#pragma once

#include <string>
#include <unordered_map>
#include <vector>

class Color {
public:
    Color(int r, int g, int b) : red(r), green(g), blue(b) {}

    int getRed() const { return red; }
    int getGreen() const { return green; }
    int getBlue() const { return blue; }

    bool operator==(const Color& other) const {
        return red == other.red && green == other.green && blue == other.blue;
    }

    bool operator!=(const Color& other) const {
        return !(*this == other);
    }
private:
    int red;
    int green;
    int blue;
};

static Color COLOR_EMPTY = Color(-1, -1, -1);
static Color WHITE = Color(255, 255, 255);

class DataReader {
public:
    static std::string resolution;
    // Weapon Slot Pixels
    static std::vector<int> WEAPON_1_PIXELS;
    static std::vector<int> WEAPON_2_PIXELS;
    // Weapon Type Pixels
    // Light
    static std::vector<int> R301_PIXELS;
    static std::vector<int> ALTERNATOR_PIXELS;
    static std::vector<int> RE45_PIXELS;
    static std::vector<int> SPITFIRE_PIXELS;
    static std::vector<int> G7_PIXELS;
    // Heavy
    static std::vector<int> FLATLINE_PIXELS;
    static std::vector<int> CAR_PIXELS;
    static std::vector<int> HEMLOK_PIXELS;
    static std::vector<int> RAMPAGE_PIXELS;
    static std::vector<int> P3030_PIXELS;
    static std::vector<int> PROWLER_PIXELS;
    // ENERGY
    static std::vector<int> VOLT_PIXELS;
    static std::vector<int> HAVOC_PIXELS;
    static std::vector<int> LSTAR_PIXELS;
    static std::vector<int> NEMESIS_PIXELS;
    // SUPPY DROP
    static std::vector<int> DEVOTION_PIXELS;
    static std::vector<int> R99_PIXELS;
    // Rampage
    static std::vector<int> RAMPAGE_AMP_PIXELS;
    // NEMESIS
    static std::vector<int> NEMESIS_FULL_CHARGE_PIXELS;
    // TURBOCHARGER
    static std::vector<int> HAVOC_TURBOCHARGER_PIXELS;
    // SELECTIVE
    static std::vector<int> SINGLE_MODE_PIXELS;
    static std::vector<int> SELECTIVE_FIRE_CAN_FIRE_PIXELS;
    static std::unordered_map<std::string, std::vector<std::string>> PATTERNS;
    // Color
    static std::string LIGHT_WEAPON_COLOR;
    static std::string HEAVY_WEAPON_COLOR;
    static std::string ENERGY_WEAPON_COLOR;
    static std::string SUPPY_DROP_COLOR_NORMAL;
    static std::string SUPPY_DROP_COLOR_PROTANOPIA;
    static std::string SUPPY_DROP_COLOR_DEUTERANOPIA;
    static std::string SUPPY_DROP_COLOR_TRITANOPIA;
    static std::string SHOTGUN_WEAPON_COLOR;
    static std::string SNIPER_WEAPON_COLOR;
    static std::string SELLA_WEAPON_COLOR;
    static std::string SELECTIVE_FIRE_CAN_FIRE_COLOR;

    static bool reload();
    static bool checkSuppyDropColor(const Color& weaponColor);
    static bool isValidWeaponColor(const Color& weaponColor);
    static bool checkWeapon(const std::vector<int>& weaponPixels);
    static bool checkTurbocharger(const std::vector<int>& turbocharger_pixels);
    static bool checkNemesisFullCharge();
    static bool checkSingleMode();
    static bool isSella();
    static bool checkSelectiveFire();
    static bool isSelectiveFireWeapon(const std::string& weapon_type);
    static std::string toHex(const Color& color);

private:
    static std::string makeResolutionFile();
    static std::unordered_map<std::string, std::vector<std::string>> readPatterns();
    static std::string read(const std::string& fileName, const std::string& key);
    static std::vector<int> loadPixel(const std::string& key);
    static std::string loadColor(const std::string& key);
};