package eternal.walnut.reimu;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class DataReader {
    private static final File resolutions = new File("resolution", GlobalVars.reimuConfig.resolution + ".ini");
    // Weapon Slot Pixels
    public static final Integer[] WEAPON_1_PIXELS = loadPixel("weapon1");
    public static final Integer[] WEAPON_2_PIXELS = loadPixel("weapon2");
    // Weapon Type Pixels

    // Light
    public static final Integer[] R301_PIXELS = loadPixel("r301");
    public static final Integer[] ALTERNATOR_PIXELS = loadPixel("alternator");
    public static final Integer[] RE45_PIXELS = loadPixel("re45");
    public static final Integer[] SPITFIRE_PIXELS = loadPixel("spitfire");
    public static final Integer[] G7_PIXELS = loadPixel("g7");
    // Heavy
    public static final Integer[] FLATLINE_PIXELS = loadPixel("flatline");
    public static final Integer[] CAR_PIXELS = loadPixel("car");
    public static final Integer[] HEMLOK_PIXELS = loadPixel("hemlok");
    public static final Integer[] RAMPAGE_PIXELS = loadPixel("rampage");
    public static final Integer[] P3030_PIXELS = loadPixel("p3030");
    public static final Integer[] PROWLER_PIXELS = loadPixel("prowler");
    // ENERGY
    public static final Integer[] VOLT_PIXELS = loadPixel("volt");
    public static final Integer[] HAVOC_PIXELS = loadPixel("havoc");
    public static final Integer[] LSTAR_PIXELS = loadPixel("lstar");
    public static final Integer[] NEMESIS_PIXELS = loadPixel("nemesis");
    // SUPPY DROP
    public static final Integer[] DEVOTION_PIXELS = loadPixel("devotion");
    public static final Integer[] R99_PIXELS = loadPixel("r99");
    // Rampage
    public static final Integer[] RAMPAGE_AMP_PIXELS = loadPixel("rampage_amp");
    // NEMESIS
    public static final Integer[] NEMESIS_FULL_CHARGE_PIXELS = loadPixel("nemesis_full_charge");
    // TURBOCHARGER
    public static final Integer[] HAVOC_TURBOCHARGER_PIXELS = loadPixel("havoc_turbocharger");
    // SELECTIVE
    public static final Integer[] SINGLE_MODE_PIXELS = loadPixel("single_mode");
    public static final Integer[] SELECTIVE_FIRE_CAN_FIRE_PIXELS = loadPixel("selective_fire_weapon_can_fire");

    // Color
    private static final File colorData = new File("weapon_color.ini");
    public static final String LIGHT_WEAPON_COLOR = loadColor("light");
    public static final String HEAVY_WEAPON_COLOR = loadColor("heavy");
    public static final String ENERGY_WEAPON_COLOR = loadColor("energy");
    public static final String SUPPY_DROP_COLOR_NORMAL = loadColor("suppy_drop_normal");
    public static final String SUPPY_DROP_COLOR_PROTANOPIA = loadColor("suppy_drop_protanopia");
    public static final String SUPPY_DROP_COLOR_DEUTERANOPIA = loadColor("suppy_drop_deuteranopia");
    public static final String SUPPY_DROP_COLOR_TRITANOPIA = loadColor("suppy_drop_tritanopia");
    public static final String SHOTGUN_WEAPON_COLOR = loadColor("shotgun");
    public static final String SNIPER_WEAPON_COLOR = loadColor("sniper");
    public static final String SELLA_WEAPON_COLOR = loadColor("sella");
    public static final String SELECTIVE_FIRE_CAN_FIRE_COLOR = loadColor("selective_fire_weapon_can_fire");

    private static String read(File file, String key) {
        String data = FileUtils.read(file);
        if (data.contains(key))
            return StringUtils.getSubString(data, key + " = \"", "\"");
        return "";
    }

    private static Integer[] loadPixel(String key) {
        String data = read(resolutions, key.toLowerCase());
        if (data.isEmpty()) {
            System.out.println("Pixel " + key + " Not found!");
            return new Integer[0];
        }
        return Arrays.stream(read(resolutions, key.toLowerCase()).split(",")).map(Integer::valueOf).toArray(Integer[]::new);
    }

    private static String loadColor(String key) {
        return read(colorData, key);
    }

    public static boolean checkSuppyDropColor(String weaponColorHex) {
        return SUPPY_DROP_COLOR_NORMAL.equals(weaponColorHex) || SUPPY_DROP_COLOR_PROTANOPIA.equals(weaponColorHex) || SUPPY_DROP_COLOR_DEUTERANOPIA.equals(weaponColorHex) || SUPPY_DROP_COLOR_TRITANOPIA.equals(weaponColorHex);
    }

    public static boolean checkSuppyDropColor(Color weaponColor) {
        return checkSuppyDropColor("0x" + String.format("%02x%02x%02x", weaponColor.getBlue(), weaponColor.getGreen(), weaponColor.getRed()).toUpperCase());
    }

    public static boolean isValidWeaponColor(Color weaponColor) {
        String hex = "0x" + String.format("%02x%02x%02x", weaponColor.getBlue(), weaponColor.getGreen(), weaponColor.getRed()).toUpperCase();
        return LIGHT_WEAPON_COLOR.equals(hex) || HEAVY_WEAPON_COLOR.equals(hex) || ENERGY_WEAPON_COLOR.equals(hex) || checkSuppyDropColor(weaponColor) || SHOTGUN_WEAPON_COLOR.equals(hex);
    }

    public static boolean checkWeapon(Integer[] weaponPixels) {
        try {
            if (weaponPixels == null)
                return false;

            Color targetColor = Color.WHITE;
            int i = 0;
            for (int loop = 0; loop < 3; loop++) {
                Color checkPointColor = Core.getScreenPixel(weaponPixels[i], weaponPixels[i + 1]);
                if (checkPointColor.equals(targetColor) != (weaponPixels[i + 2] == 1))
                    return false;
                i += 3;
            }

            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean checkTurbocharger(Integer[] turbocharger_pixels)
    {
        Color checkPointColor = Core.getScreenPixel(turbocharger_pixels[0], turbocharger_pixels[1]);
        return checkPointColor.equals(Color.WHITE);
    }

    public static boolean checkNemesisFullCharge()
    {
        Color checkPointColor = Core.getScreenPixel(NEMESIS_FULL_CHARGE_PIXELS[0], NEMESIS_FULL_CHARGE_PIXELS[1]);
        return checkPointColor.equals(new Color(98, 189, 214));
    }

    public static boolean checkSingleMode()
    {
        Color checkPointColor = Core.getScreenPixel(SINGLE_MODE_PIXELS[0], SINGLE_MODE_PIXELS[1]);
        return checkPointColor.equals(Color.WHITE);
    }

    public static boolean isSella()
    {
        Color check_weapon2_color = Core.getScreenPixel(WEAPON_2_PIXELS[0], WEAPON_2_PIXELS[1]);
        String hex = "0x" + String.format("%02x%02x%02x", check_weapon2_color.getBlue(), check_weapon2_color.getGreen(), check_weapon2_color.getRed()).toUpperCase();
        return hex.equals(SELLA_WEAPON_COLOR);
    }

    public static boolean checkSelectiveFire()
    {
        Color check_point_color = Core.getScreenPixel(SELECTIVE_FIRE_CAN_FIRE_PIXELS[0], SELECTIVE_FIRE_CAN_FIRE_PIXELS[1]);
        String hex = "0x" + String.format("%02x%02x%02x", check_point_color.getBlue(), check_point_color.getGreen(), check_point_color.getRed()).toUpperCase();
        return hex.equals(SELECTIVE_FIRE_CAN_FIRE_COLOR);
    }

    public static boolean isSelectiveFireWeapon(String weapon_type)
    {
        return !weapon_type.isEmpty() && (weapon_type.equals("Hemlok") || weapon_type.equals("Flatline") || weapon_type.equals("R301"));
    }
}
