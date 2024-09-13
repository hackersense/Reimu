package eternal.walnut.reimu;

import com.sun.jna.platform.win32.WinDef;
import eternal.walnut.reimu.jna.JNA;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Core {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    // define
    private static final File patterns = new File("pattern");
    public static String weapon = "";
    public static boolean isSingleMode = false;
    private static final InterruptingThreadPoolExecutor executor = new InterruptingThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public static boolean isPressed(int keyCode) {
        short state = JNA.User32Call.INSTANCE.GetAsyncKeyState(keyCode);
        return (state & 0x8000) != 0;
    }

    public static void start() {
        if (isMouseShown() || weapon.isEmpty() || isSingleMode)
            return;

        String currentWeapon = weapon;
        if (weapon.equals("HavocTurbo"))
            currentWeapon = "Havoc";
        else if (weapon.equals("Nemesis")) {
            if (DataReader.checkNemesisFullCharge())
                currentWeapon = "NemesisCharged";
            else
                currentWeapon = "Nemesis";
        }

        File patternData = new File(patterns, currentWeapon + ".txt");
        if (!patternData.exists()) {
            System.err.println("WARNING! Not found weapon " + currentWeapon + "'s pattern");
            return;
        }

        if (weapon.equals("Havoc")) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<String> patternContent = FileUtils.readList(patternData);
        if (patternContent.isEmpty()) {
            System.err.println("Failed to read " + weapon + "'s weapon pattern.");
            return;
        }

        executor.execute(() -> {
            for (String pattern : patternContent) {
                if (Thread.currentThread().isInterrupted())
                    return;

                if (!isPressed(0x01) || weapon.isEmpty())
                    return;

                String[] ptrn = pattern.split(",");
                if (ptrn.length < 3)
                    continue;

                int x = Integer.parseInt(ptrn[0]);
                int y = Integer.parseInt(ptrn[1]);
                float interval = Float.parseFloat(ptrn[2]);

                moveMouse(x, y);

                try {
                    long millis = (long) Math.floor(interval);
                    double fractional = interval - millis;
                    Thread.sleep(millis);
                    long nanos = (long) (fractional * 1000000);
                    if (nanos > 0)
                        Thread.sleep(0, (int) nanos);
                } catch (InterruptedException ignored) {
                    return;
                }
            }
        });
    }

    public static boolean isMouseShown() {
        if (!JNA.isApexLegendsActive())
            return true;

        JNA.CURSORINFO cursorInfo = new JNA.CURSORINFO();
        boolean result = JNA.User32Call.INSTANCE.GetCursorInfo(cursorInfo);
        if (result) {
            int CURSOR_SHOWING = 1;
            return (cursorInfo.flags & CURSOR_SHOWING) == CURSOR_SHOWING;
        }
        return false;
    }

    public static double getScaleFactor() {
        JNA.User32 user32 = JNA.User32.INSTANCE;
        WinDef.HWND hwnd = user32.GetForegroundWindow();
        int dpi = user32.GetDpiForWindow(hwnd);
        return dpi / 96.0; // 96 is the standard DPI (1x scaling)
    }

    public static Color getScreenPixel(int x, int y) {
        WinDef.RECT rect = new WinDef.RECT();
        JNA.User32.INSTANCE.GetWindowRect(JNA.User32.INSTANCE.GetForegroundWindow(), rect);

        x = x + rect.left;
        y = y + rect.top;

        WinDef.HDC hdc = JNA.User32.INSTANCE.GetDC(null);
        int pixelColor = JNA.GDI32.INSTANCE.GetPixel(hdc, x, y);
        int red = (pixelColor & 0x000000FF);
        int green = (pixelColor & 0x0000FF00) >> 8;
        int blue = (pixelColor & 0x00FF0000) >> 16;
        JNA.User32.INSTANCE.ReleaseDC(null, hdc);
        return new Color(red, green, blue);
    }

    public static void moveMouse(int x, int y) {
        JNA.User32.INSTANCE.mouse_event(0x01, Math.round(x * (4 / GlobalVars.reimuConfig.sens)), Math.round(y * (4 / GlobalVars.reimuConfig.sens)), 0, 0);
    }

    public static void Reset() {
        weapon = "";
    }

    public static void DetectAndSetWeapon() {
        if (isMouseShown())
            return;

        executorService.execute(() -> {
            if (DataReader.isSella()) {
                weapon = "Sella";
                return;
            }

            isSingleMode = DataReader.checkSingleMode();

            String weaponColor;
            Color weapon1 = getScreenPixel(DataReader.WEAPON_1_PIXELS[0], DataReader.WEAPON_1_PIXELS[1]);
            if (DataReader.isValidWeaponColor(weapon1)) {
                weaponColor = "0x" + String.format("%02x%02x%02x", weapon1.getBlue(), weapon1.getGreen(), weapon1.getRed()).toUpperCase();
            } else {
                Color weapon2 = getScreenPixel(DataReader.WEAPON_2_PIXELS[0], DataReader.WEAPON_2_PIXELS[1]);
                if (DataReader.isValidWeaponColor(weapon2))
                    weaponColor = "0x" + String.format("%02x%02x%02x", weapon2.getBlue(), weapon2.getGreen(), weapon2.getRed()).toUpperCase();
                else {
                    Reset();
                    return;
                }
            }

            if (weaponColor.equals(DataReader.LIGHT_WEAPON_COLOR)) {
                if (DataReader.checkWeapon(DataReader.R301_PIXELS)) {
                    weapon = "R301";
                } else if (DataReader.checkWeapon(DataReader.ALTERNATOR_PIXELS)) {
                    weapon = "Alternator";
                } else if (DataReader.checkWeapon(DataReader.RE45_PIXELS)) {
                    weapon = "RE45";
                } else if (DataReader.checkWeapon(DataReader.SPITFIRE_PIXELS)) {
                    weapon = "Spitfire";
                } else if (DataReader.checkWeapon(DataReader.G7_PIXELS)) {
                    weapon = "G7";
                }
            } else if (weaponColor.equals(DataReader.HEAVY_WEAPON_COLOR)) {
                if (DataReader.checkWeapon(DataReader.RAMPAGE_PIXELS)) {
                    weapon = "Rampage";
                } else if (DataReader.checkWeapon(DataReader.PROWLER_PIXELS)) {
                    weapon = "Prowler";
                } else if (DataReader.checkWeapon(DataReader.FLATLINE_PIXELS)) {
                    weapon = "Flatline";
                } else if (DataReader.checkWeapon(DataReader.HEMLOK_PIXELS)) {
                    weapon = "Hemlok";
                } else if (DataReader.checkWeapon(DataReader.CAR_PIXELS)) {
                    weapon = "CAR";
                } else if (DataReader.checkWeapon(DataReader.P3030_PIXELS)) {
                    weapon = "3030";
                }
            } else if (weaponColor.equals(DataReader.ENERGY_WEAPON_COLOR)) {
                if (DataReader.checkWeapon(DataReader.VOLT_PIXELS)) {
                    weapon = "Volt";
                } else if (DataReader.checkWeapon(DataReader.HAVOC_PIXELS)) {
                    if (DataReader.checkTurbocharger(DataReader.HAVOC_TURBOCHARGER_PIXELS))
                        weapon = "HavocTurbo";
                    else
                        weapon = "Havoc";
                } else if (DataReader.checkWeapon(DataReader.LSTAR_PIXELS)) {
                    weapon = "Lstar";
                } else if (DataReader.checkWeapon(DataReader.NEMESIS_PIXELS)) {
                    weapon = "Nemesis";
                } else if (DataReader.checkWeapon(DataReader.CAR_PIXELS)) {
                    weapon = "CAR";
                } else if (DataReader.checkWeapon(DataReader.G7_PIXELS)) {
                    weapon = "G7";
                }
            } else if (DataReader.checkSuppyDropColor(weaponColor)) {
                if (DataReader.checkWeapon(DataReader.DEVOTION_PIXELS)) {
                    weapon = "DevotionTurbo";
                } else if (DataReader.checkWeapon(DataReader.R99_PIXELS)) {
                    weapon = "R99";
                }
            } else Reset();
        });
    }
}
