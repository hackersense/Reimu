package eternal.walnut.reimu;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class DataReader {
    public static String resolution = "";
    // Weapon Slot Pixels
    public static Integer[] WEAPON_1_PIXELS;
    public static Integer[] WEAPON_2_PIXELS;
    // Weapon Type Pixels
    // Light
    public static Integer[] R301_PIXELS;
    public static Integer[] ALTERNATOR_PIXELS;
    public static Integer[] RE45_PIXELS;
    public static Integer[] SPITFIRE_PIXELS;
    public static Integer[] G7_PIXELS;
    // Heavy
    public static Integer[] FLATLINE_PIXELS;
    public static Integer[] CAR_PIXELS;
    public static Integer[] HEMLOK_PIXELS;
    public static Integer[] RAMPAGE_PIXELS;
    public static Integer[] P3030_PIXELS;
    public static Integer[] PROWLER_PIXELS;
    // ENERGY
    public static Integer[] VOLT_PIXELS;
    public static Integer[] HAVOC_PIXELS;
    public static Integer[] LSTAR_PIXELS;
    public static Integer[] NEMESIS_PIXELS;
    // SUPPY DROP
    public static Integer[] DEVOTION_PIXELS;
    public static Integer[] R99_PIXELS;
    // Rampage
    public static Integer[] RAMPAGE_AMP_PIXELS;
    // NEMESIS
    public static Integer[] NEMESIS_FULL_CHARGE_PIXELS;
    // TURBOCHARGER
    public static Integer[] HAVOC_TURBOCHARGER_PIXELS;
    // SELECTIVE
    public static Integer[] SINGLE_MODE_PIXELS;
    public static Integer[] SELECTIVE_FIRE_CAN_FIRE_PIXELS;
    // Weapon Pattern
    public static Map<String, List<String>> PATTERNS = readPatterns();

    // Color
    private static final File colorData = new File("weapon_color.ini");
    public static String LIGHT_WEAPON_COLOR ;
    public static String HEAVY_WEAPON_COLOR;
    public static String ENERGY_WEAPON_COLOR;
    public static String SUPPY_DROP_COLOR_NORMAL;
    public static String SUPPY_DROP_COLOR_PROTANOPIA ;
    public static String SUPPY_DROP_COLOR_DEUTERANOPIA;
    public static String SUPPY_DROP_COLOR_TRITANOPIA;
    public static String SHOTGUN_WEAPON_COLOR;
    public static String SNIPER_WEAPON_COLOR;
    public static String SELLA_WEAPON_COLOR;
    public static String SELECTIVE_FIRE_CAN_FIRE_COLOR;

    private static Map<String, List<String>> readPatterns() {
        File[] patterns = new File("pattern").listFiles();
        if (patterns == null || patterns.length == 0) {
            System.err.println("Failed to find patterns!");
            return null;
        }

        Map<String, List<String>> patternMap = new HashMap<>();

        for (File pattern : patterns) {
            List<String> patternContent = FileUtils.readList(pattern);
            if (patternContent.isEmpty())
                continue;

            patternMap.put(pattern.getName().replace(".txt", ""), patternContent);
        }

        return patternMap;
    }

    public static void reload() {
        // Weapon Slot Pixels
        WEAPON_1_PIXELS = loadPixel("weapon1");
        WEAPON_2_PIXELS = loadPixel("weapon2");
        // Weapon Type Pixels
        // Light
        R301_PIXELS = loadPixel("r301");
        ALTERNATOR_PIXELS = loadPixel("alternator");
        RE45_PIXELS = loadPixel("re45");
        SPITFIRE_PIXELS = loadPixel("spitfire");
        G7_PIXELS = loadPixel("g7");
        // Heavy
        FLATLINE_PIXELS = loadPixel("flatline");
        CAR_PIXELS = loadPixel("car");
        HEMLOK_PIXELS = loadPixel("hemlok");
        RAMPAGE_PIXELS = loadPixel("rampage");
        P3030_PIXELS = loadPixel("p3030");
        PROWLER_PIXELS = loadPixel("prowler");
        // ENERGY
        VOLT_PIXELS = loadPixel("volt");
        HAVOC_PIXELS = loadPixel("havoc");
        LSTAR_PIXELS = loadPixel("lstar");
        NEMESIS_PIXELS = loadPixel("nemesis");
        // SUPPY DROP
        DEVOTION_PIXELS = loadPixel("devotion");
        R99_PIXELS = loadPixel("r99");
        // Rampage
        RAMPAGE_AMP_PIXELS = loadPixel("rampage_amp");
        // NEMESIS
        NEMESIS_FULL_CHARGE_PIXELS = loadPixel("nemesis_full_charge");
        // TURBOCHARGER
        HAVOC_TURBOCHARGER_PIXELS = loadPixel("havoc_turbocharger");
        // SELECTIVE
        SINGLE_MODE_PIXELS = loadPixel("single_mode");
        SELECTIVE_FIRE_CAN_FIRE_PIXELS = loadPixel("selective_fire_weapon_can_fire");
        // Color
        LIGHT_WEAPON_COLOR = loadColor("light");
        HEAVY_WEAPON_COLOR = loadColor("heavy");
        ENERGY_WEAPON_COLOR = loadColor("energy");
        SUPPY_DROP_COLOR_NORMAL = loadColor("suppy_drop_normal");
        SUPPY_DROP_COLOR_PROTANOPIA = loadColor("suppy_drop_protanopia");
        SUPPY_DROP_COLOR_DEUTERANOPIA = loadColor("suppy_drop_deuteranopia");
        SUPPY_DROP_COLOR_TRITANOPIA = loadColor("suppy_drop_tritanopia");
        SHOTGUN_WEAPON_COLOR = loadColor("shotgun");
        SNIPER_WEAPON_COLOR = loadColor("sniper");
        SELLA_WEAPON_COLOR = loadColor("sella");
        SELECTIVE_FIRE_CAN_FIRE_COLOR = loadColor("selective_fire_weapon_can_fire");
    }

    private static File makeResolutionFile() {
        return new File("resolution", resolution + ".ini");
    }

    private static String read(File file, String key) {
        String data = FileUtils.read(file);
        if (data.contains(key))
            return StringUtils.getSubString(data, key + " = \"", "\"");
        return "";
    }

    private static Integer[] loadPixel(String key) {
        String data = read(makeResolutionFile(), key.toLowerCase());
        if (data.isEmpty()) {
            System.out.println("Pixel " + key + " Not found!");
            return new Integer[0];
        }
        return Arrays.stream(read(makeResolutionFile(), key.toLowerCase()).split(",")).map(Integer::valueOf).toArray(Integer[]::new);
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
        if (WEAPON_2_PIXELS == null)
            return false;

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
