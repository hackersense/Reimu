#include "FileUtils.h"
#include "DataReader.h"
#include "Core.h"

#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <algorithm>
#include <iomanip>
#include <cstdint>
#include <cstdio>
#include <windows.h>
#include <io.h>

std::string DataReader::resolution = "";
// Weapon Slot Pixels
std::vector<int> DataReader::WEAPON_1_PIXELS;
std::vector<int> DataReader::WEAPON_2_PIXELS;
// Weapon Type Pixels
// Light
std::vector<int> DataReader::R301_PIXELS;
std::vector<int> DataReader::ALTERNATOR_PIXELS;
std::vector<int> DataReader::RE45_PIXELS;
std::vector<int> DataReader::SPITFIRE_PIXELS;
std::vector<int> DataReader::G7_PIXELS;
// Heavy
std::vector<int> DataReader::FLATLINE_PIXELS;
std::vector<int> DataReader::CAR_PIXELS;
std::vector<int> DataReader::HEMLOK_PIXELS;
std::vector<int> DataReader::RAMPAGE_PIXELS;
std::vector<int> DataReader::P3030_PIXELS;
std::vector<int> DataReader::PROWLER_PIXELS;
// ENERGY
std::vector<int> DataReader::VOLT_PIXELS;
std::vector<int> DataReader::HAVOC_PIXELS;
std::vector<int> DataReader::LSTAR_PIXELS;
std::vector<int> DataReader::NEMESIS_PIXELS;
// SUPPY DROP
std::vector<int> DataReader::DEVOTION_PIXELS;
std::vector<int> DataReader::R99_PIXELS;
// Rampage
std::vector<int> DataReader::RAMPAGE_AMP_PIXELS;
// NEMESIS
std::vector<int> DataReader::NEMESIS_FULL_CHARGE_PIXELS;
// TURBOCHARGER
std::vector<int> DataReader::HAVOC_TURBOCHARGER_PIXELS;
// SELECTIVE
std::vector<int> DataReader::SINGLE_MODE_PIXELS;
std::vector<int> DataReader::SELECTIVE_FIRE_CAN_FIRE_PIXELS;
std::unordered_map<std::string, std::vector<std::string>> DataReader::PATTERNS = readPatterns();
// Color
std::string DataReader::LIGHT_WEAPON_COLOR;
std::string DataReader::HEAVY_WEAPON_COLOR;
std::string DataReader::ENERGY_WEAPON_COLOR;
std::string DataReader::SUPPY_DROP_COLOR_NORMAL;
std::string DataReader::SUPPY_DROP_COLOR_PROTANOPIA;
std::string DataReader::SUPPY_DROP_COLOR_DEUTERANOPIA;
std::string DataReader::SUPPY_DROP_COLOR_TRITANOPIA;
std::string DataReader::SHOTGUN_WEAPON_COLOR;
std::string DataReader::SNIPER_WEAPON_COLOR;
std::string DataReader::SELLA_WEAPON_COLOR;
std::string DataReader::SELECTIVE_FIRE_CAN_FIRE_COLOR;

std::unordered_map<std::string, std::vector<std::string>> DataReader::readPatterns() {
    std::unordered_map<std::string, std::vector<std::string>> patternMap;
    const std::string patternDir = "pattern";

    std::string searchPath = patternDir + "\\*.txt";
    _finddata_t fileInfo;
    intptr_t handle = _findfirst(searchPath.c_str(), &fileInfo);
    if (handle == -1) {
        std::cerr << "Failed to open patterns directory!" << std::endl;
        exit(EXIT_FAILURE);
        return patternMap;
    }

    do {
        std::string fileName = fileInfo.name;
        std::string filePath = patternDir + "\\" + fileName;

        std::vector<std::string> patternContent = FileUtils::readList(filePath);
        if (!patternContent.empty()) {
            patternMap[fileName.substr(0, fileName.size() - 4)] = patternContent;
        }
    } while (_findnext(handle, &fileInfo) == 0);

    _findclose(handle);
    return patternMap;
}

std::string DataReader::read(const std::string& fileName, const std::string& key) {
    if (!FileUtils::exists(fileName)) {
        std::cout << fileName << " not found!" << std::endl;
        exit(EXIT_FAILURE);
        return "";
    }

    std::ifstream file(fileName);
    std::string data;
    std::string line;

    if (file.is_open()) {
        std::ostringstream oss;
        while (getline(file, line)) {
            oss << line << "\n";
        }
        file.close();
        data = oss.str();
    }

    std::size_t key_pos = data.find(key);
    if (key_pos != std::string::npos) {
        std::size_t start_pos = data.find("= \"", key_pos);
        if (start_pos != std::string::npos) {
            start_pos += 3;
            std::size_t end_pos = data.find("\"", start_pos);
            if (end_pos != std::string::npos) {
                return data.substr(start_pos, end_pos - start_pos);
            }
        }
    }

    return "";
}

inline std::string DataReader::makeResolutionFile() {
    return "resolution/" + resolution + ".ini";
}

std::vector<int> DataReader::loadPixel(const std::string& key) {
    std::string data = read(makeResolutionFile(), key);
    if (data.empty()) {
        std::cout << "Pixel " << key << " Not found!" << std::endl;
        return {};
    }
    std::vector<int> pixels;
    std::istringstream stream(data);
    std::string item;
    while (std::getline(stream, item, ',')) {
        pixels.push_back(std::stoi(item));
    }
    return pixels;
}

std::string DataReader::loadColor(const std::string& key) {
    return read("weapon_color.ini", key);
}

bool DataReader::reload() {
    // Weapon Slot Pixels
    WEAPON_1_PIXELS = loadPixel("weapon1");
    WEAPON_2_PIXELS = loadPixel("weapon2");
    if (WEAPON_1_PIXELS.empty() || WEAPON_2_PIXELS.empty())
        return false;
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
    return true;
}

static Color GBRToRGB(const std::string& gbrStr) {
    std::string colorStr = gbrStr;
    if (colorStr.substr(0, 2) == "0x")
        colorStr = colorStr.substr(2);
    unsigned int gbr;
    std::stringstream ss;
    ss << std::hex << colorStr;
    ss >> gbr;
    return Color(gbr & 0xFF, (gbr >> 8) & 0xFF, (gbr >> 16) & 0xFF);
}

bool DataReader::checkSuppyDropColor(const Color& weaponColor) {
    return Core::isColorInRange(GBRToRGB(SUPPY_DROP_COLOR_NORMAL), weaponColor, 10) ||
        Core::isColorInRange(GBRToRGB(SUPPY_DROP_COLOR_PROTANOPIA), weaponColor, 10) ||
        Core::isColorInRange(GBRToRGB(SUPPY_DROP_COLOR_DEUTERANOPIA), weaponColor, 10) ||
        Core::isColorInRange(GBRToRGB(SUPPY_DROP_COLOR_TRITANOPIA), weaponColor, 10);
}

bool DataReader::isValidWeaponColor(const Color& weaponColor) {
    return Core::isColorInRange(GBRToRGB(LIGHT_WEAPON_COLOR), weaponColor, 10) ||
        Core::isColorInRange(GBRToRGB(HEAVY_WEAPON_COLOR), weaponColor, 10) ||
        Core::isColorInRange(GBRToRGB(ENERGY_WEAPON_COLOR), weaponColor, 10) ||
        checkSuppyDropColor(weaponColor) ||
        Core::isColorInRange(GBRToRGB(SHOTGUN_WEAPON_COLOR), weaponColor, 10);
}

int rgb_to_int(int r, int g, int b) {
    return (r << 16) + (g << 8) + b;
}

Color int_to_rgb(int int_value) {
    int r = (int_value >> 16) & 0xFF;
    int g = (int_value >> 8) & 0xFF;
    int b = int_value & 0xFF;
    return Color(r, g, b);
}

bool DataReader::checkWeapon(const std::vector<int>& weaponPixels) {
    if (weaponPixels.empty())
        return false;

    int i = 0;
    for (int loop = 0; loop < 3; loop++) {
        Color checkPointColor = Core::getScreenPixel(weaponPixels[i], weaponPixels[i + 1]);
        int check = weaponPixels[i + 2];
        if (check <= 1) {
            if (Core::isColorInRange(checkPointColor, WHITE, 10) != (weaponPixels[i + 2] == 1))
                return false;
        } else {
            if (!Core::isColorInRange(checkPointColor, int_to_rgb(check), 10))
                return false;
        }
        i += 3;
    }

    return true;
}

bool DataReader::checkTurbocharger(const std::vector<int>& turbocharger_pixels) {
    Color checkPointColor = Core::getScreenPixel(turbocharger_pixels[0], turbocharger_pixels[1]);
    return checkPointColor == WHITE;
}

bool DataReader::checkNemesisFullCharge() {
    Color checkPointColor = Core::getScreenPixel(NEMESIS_FULL_CHARGE_PIXELS[0], NEMESIS_FULL_CHARGE_PIXELS[1]);
    return checkPointColor == Color(98, 189, 214);
}

bool DataReader::checkSingleMode() {
    Color checkPointColor = Core::getScreenPixel(SINGLE_MODE_PIXELS[0], SINGLE_MODE_PIXELS[1]);
    return Core::isColorInRange(checkPointColor, WHITE, 10);
}

bool DataReader::isSella() {
    if (WEAPON_2_PIXELS.empty())
        return false;
    Color check_weapon2_color = Core::getScreenPixel(WEAPON_2_PIXELS[0], WEAPON_2_PIXELS[1]);
    return toHex(check_weapon2_color) == SELLA_WEAPON_COLOR;
}

bool DataReader::checkSelectiveFire() {
    Color check_point_color = Core::getScreenPixel(SELECTIVE_FIRE_CAN_FIRE_PIXELS[0], SELECTIVE_FIRE_CAN_FIRE_PIXELS[1]);
    return toHex(check_point_color) == SELECTIVE_FIRE_CAN_FIRE_COLOR;
}

bool DataReader::isSelectiveFireWeapon(const std::string& weapon_type) {
    return !weapon_type.empty() && (weapon_type == "Hemlok" || weapon_type == "Flatline" || weapon_type == "R301");
}

std::string DataReader::toHex(const Color& color) {
    std::stringstream ss;
    ss << std::hex << std::uppercase << "0x"
        << std::setw(2) << std::setfill('0') << color.getBlue()
        << std::setw(2) << std::setfill('0') << color.getGreen()
        << std::setw(2) << std::setfill('0') << color.getRed()
        << std::nouppercase;
    return ss.str();
}