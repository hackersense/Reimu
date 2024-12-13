#pragma once

#include <Windows.h>
#include "json.hpp"
#include "FileUtils.h"

#define VK_A 0x41
#define VK_B 0x42
#define VK_C 0x43
#define VK_D 0x44
#define VK_E 0x45
#define VK_F 0x46
#define VK_G 0x47
#define VK_H 0x48
#define VK_I 0x49
#define VK_J 0x4A
#define VK_K 0x4B
#define VK_L 0x4C
#define VK_M 0x4D
#define VK_N 0x4E
#define VK_O 0x4F
#define VK_P 0x50
#define VK_Q 0x51
#define VK_R 0x52
#define VK_S 0x53
#define VK_T 0x54
#define VK_U 0x55
#define VK_V 0x56
#define VK_W 0x57
#define VK_X 0x58
#define VK_Y 0x59
#define VK_Z 0x5A
#define VK_0 0x30
#define VK_1 0x31
#define VK_2 0x32
#define VK_3 0x33
#define VK_4 0x34
#define VK_5 0x35
#define VK_6 0x36
#define VK_7 0x37
#define VK_8 0x38
#define VK_9 0x39

struct ReimuConfig {
    float sens = 4.0f;
    float zoom_sens = 1.0f;
    int adsType = 0;
    bool debug = false;
    bool hideConsole = false;
    int exitApp = VK_END;
    int switchMode = VK_B;
    int reloadWeapon = VK_R;
    int mapKey = VK_M;
    int bagKey = VK_H;
    int weapon1 = VK_1;
    int weapon2 = VK_2;
    int handKey = VK_3;
    int skillKey = VK_Z;
    int pickWeapon = VK_E;
    int grenadeKey = VK_G;

    NLOHMANN_DEFINE_TYPE_INTRUSIVE(ReimuConfig, sens, zoom_sens, adsType, debug, hideConsole, exitApp, switchMode, reloadWeapon, mapKey, bagKey, weapon1, weapon2, handKey, skillKey, pickWeapon, grenadeKey)
};

class GlobalVars {
public:
    static ReimuConfig reimuConfig;

    static void read() {
        try {
            std::string jsonContent = FileUtils::read("Config.json");
            if (!jsonContent.empty()) {
                reimuConfig = nlohmann::json::parse(jsonContent).get<ReimuConfig>();
            }
            else {
                reimuConfig = ReimuConfig();
            }
        }
        catch (...) {
            reimuConfig = ReimuConfig();
        }
    }

    static void save() {
        std::string jsonContent = nlohmann::json(reimuConfig).dump(4);
        FileUtils::save("Config.json", jsonContent, false);
    }
};