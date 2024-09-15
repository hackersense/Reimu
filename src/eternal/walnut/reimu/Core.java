package eternal.walnut.reimu;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import eternal.walnut.reimu.jna.JNA;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Core {
    public static String weapon = "";
    public static boolean isSingleMode = false;
    public static boolean stopDetect = false;
    public static boolean pressingLeftMouse = false;

    public static boolean isPressed(int keyCode) {
        short state = JNA.User32Call.INSTANCE.GetAsyncKeyState(keyCode);
        return (state & 0x8000) != 0;
    }

    public static void start() {
        if (isMouseShown() || weapon.isEmpty() || isSingleMode)
            return;

        String originalWeapon = weapon;
        String currentWeapon = weapon;
        if (weapon.equals("HavocTurbo"))
            currentWeapon = "Havoc";
        else if (weapon.equals("Nemesis") && DataReader.checkNemesisFullCharge())
            currentWeapon = "NemesisCharged";

        if (GlobalVars.reimuConfig.debug)
            System.out.println("[" + new Date() + "] Detected weapon: " + weapon);

        if (weapon.equals("Havoc")) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<String> patternContent = DataReader.PATTERNS.get(currentWeapon);
        if (patternContent == null || patternContent.isEmpty()) {
            System.err.println("Failed to read " + currentWeapon + "'s weapon pattern.");
            return;
        }

        for (String pattern : patternContent) {
            if (!pressingLeftMouse && !isPressed(0x01))
                break;

            if (weapon.isEmpty() || !originalWeapon.equals(weapon))
                break;

            String[] ptrn = pattern.split(",");
            if (ptrn.length < 3) {
                System.out.println("[" + new Date() + "] Error pattern");
                continue;
            }

            int x = Integer.parseInt(ptrn[0]);
            int y = Integer.parseInt(ptrn[1]);
            BigDecimal interval = BigDecimal.valueOf(Float.parseFloat(ptrn[2]));

            moveMouse(x, y);

            try {
                long millis = interval.longValue();
                long nanos = (long) ((interval.doubleValue() - millis) * 1000000);
                Thread.sleep(millis, (int) nanos);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isMouseShown() {
        if (!JNA.isApexLegendsActive())
            return true;

        JNA.CURSORINFO cursorInfo = new JNA.CURSORINFO();
        boolean result = JNA.User32Call.INSTANCE.GetCursorInfo(cursorInfo);
        if (result)
            return cursorInfo.flags >= 1;

        return true;
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
        if (GlobalVars.reimuConfig.debug)
            System.out.println("[" + new Date() + "] " + Math.round(x * (4 / GlobalVars.reimuConfig.sens)) + " / " + Math.round(y * (4 / GlobalVars.reimuConfig.sens)));

        try {
            int sensX = Math.round(x * (4 / GlobalVars.reimuConfig.sens));
            int sensY = Math.round(y * (4 / GlobalVars.reimuConfig.sens));
            User32.INPUT input = new User32.INPUT();
            input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE);
            input.input.setType("mi");
            input.input.mi.dx = new WinDef.LONG(sensX);
            input.input.mi.dy = new WinDef.LONG(sensY);
            input.input.mi.dwFlags = new WinDef.DWORD(0x0001 | 0x8000);
            if (!Objects.equals(User32.INSTANCE.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) input.toArray(1), input.size()), new WinDef.DWORD(1)))
                System.err.println("Failed to move mouse!");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //JNA.User32.INSTANCE.mouse_event(0x01, Math.round(x * (4 / GlobalVars.reimuConfig.sens)), Math.round(y * (4 / GlobalVars.reimuConfig.sens)), 0, 0);
    }

    public static void Reset() {
        weapon = "";
    }

    public static void DetectAndSetWeapon() {
        if (isMouseShown()) {
            Reset();
            return;
        }

        final String oldResolution = DataReader.resolution;
        WinDef.RECT rect = new WinDef.RECT();
        JNA.User32.INSTANCE.GetWindowRect(JNA.User32.INSTANCE.GetForegroundWindow(), rect);
        DataReader.resolution = ((rect.right - rect.left) + "x" + (rect.bottom - rect.top));
        if (!oldResolution.equals(DataReader.resolution)) {
            if (!oldResolution.isEmpty() && GlobalVars.reimuConfig.debug)
                System.out.println("[" + new Date() + "] Resolution changed! New resolution: " + DataReader.resolution);
            DataReader.reload();
        }

        stopDetect = false;

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
    }
}
