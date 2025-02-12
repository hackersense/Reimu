#include "Core.h"
#include "DataReader.h"
#include "GlobalVars.h"

#include <windows.h>
#include <vector>
#include <sstream>
#include <chrono>
#include <thread>
#include <mmsystem.h>

#pragma comment(lib, "winmm.lib")

static std::string weapon = "";
static bool isSingleMode;
static bool adsState;
static bool pressingLeftMouse;
static bool stopDetection;

bool Core::isApexLegendsActive() {
    wchar_t buffer[1024];

    HWND hwnd = GetForegroundWindow();

    int length = GetWindowTextW(hwnd, buffer, 1024);
    if (length == 0)
        return false;

    std::wstring windowTitle(buffer);

    return windowTitle == L"Apex Legends";
}

bool Core::isMouseShown() {
    if (!isApexLegendsActive())
        return true;

    CURSORINFO ci{};
    ci.cbSize = sizeof(CURSORINFO);

    if (GetCursorInfo(&ci))
        return ci.flags & CURSOR_SHOWING;
    return true;
}

bool Core::isColorInRange(const Color& color, const Color& targetColor, int tolerance) {
    return std::abs(color.getRed() - targetColor.getRed()) <= tolerance &&
        std::abs(color.getGreen() - targetColor.getGreen()) <= tolerance &&
        std::abs(color.getBlue() - targetColor.getBlue()) <= tolerance;
}

void Core::moveMouse(int x, int y) {
    float sens = 4.0f / GlobalVars::reimuConfig.sens * (adsState ? GlobalVars::reimuConfig.zoom_sens : 1.0f);
    int sensX = static_cast<int>(std::round(x * sens));
    int sensY = static_cast<int>(std::round(y * sens));
    if (GlobalVars::reimuConfig.debug)
        std::cout << sensX << " / " << sensY << " / isADS: " << adsState << std::endl;
    INPUT input = {0};
    input.type = INPUT_MOUSE;
    input.mi.dx = sensX;
    input.mi.dy = sensY;
    input.mi.dwFlags = MOUSEEVENTF_MOVE;
    SendInput(1, &input, sizeof(input));
}

void Core::setMouseState(bool down) {
    pressingLeftMouse = down;
}

void Core::setStopDetection(bool stop) {
    stopDetection = stop;
}

void Core::reset() {
    weapon = "";
    adsState = false;
}

static void CoordToScreen(int& aX, int& aY) {
    HWND active_window = GetForegroundWindow();
    if (active_window && !IsIconic(active_window))
    {
        POINT pt = { 0 };
        if (ClientToScreen(active_window, &pt))
        {
            aX += pt.x;
            aY += pt.y;
        }
    }
}

void Core::detectWeapon() {
    if (isMouseShown()) {
        reset();
        return;
    }

    std::string oldResolution = DataReader::resolution;
    HWND foregroundHwnd = GetForegroundWindow();
    RECT targetRect;
    GetClientRect(foregroundHwnd, &targetRect);

    POINT topLeft = { targetRect.left, targetRect.top };
    POINT bottomRight = { targetRect.right, targetRect.bottom };

    ClientToScreen(foregroundHwnd, &topLeft);
    ClientToScreen(foregroundHwnd, &bottomRight);

    RECT resultRect{ topLeft.x, topLeft.y, bottomRight.x, bottomRight.y };

    HDC hdc = GetDC(nullptr);
    int dpi = GetDeviceCaps(hdc, LOGPIXELSX) / 96.0f;
    ReleaseDC(nullptr, hdc);

    DataReader::resolution = (std::to_string(static_cast<int>(std::round((resultRect.right - resultRect.left) * dpi))) + "x" +
        std::to_string(static_cast<int>(std::round((resultRect.bottom - resultRect.top) * dpi))));
    if (oldResolution != DataReader::resolution) {
        if (!oldResolution.empty() && GlobalVars::reimuConfig.debug)
            std::cout << "Resolution changed! New resolution: " << DataReader::resolution << std::endl;
        if (!DataReader::reload()) {
            std::cout << "Resolution " << DataReader::resolution << " Pixel file is not found!" << std::endl;
            weapon = "";
            return;
        }
    }

    stopDetection = false;

    if (DataReader::isSella()) {
        weapon = "Sella";
        return;
    }

    isSingleMode = DataReader::checkSingleMode();

    std::string weaponColorHex;
    Color weaponColor = COLOR_EMPTY;
    Color weapon1 = getScreenPixel(DataReader::WEAPON_1_PIXELS[0], DataReader::WEAPON_1_PIXELS[1]);
    if (DataReader::isValidWeaponColor(weapon1)) {
        weaponColorHex = DataReader::toHex(weapon1);
        weaponColor = weapon1;
    }
    else {
        Color weapon2 = getScreenPixel(DataReader::WEAPON_2_PIXELS[0], DataReader::WEAPON_2_PIXELS[1]);
        if (DataReader::isValidWeaponColor(weapon2)) {
            weaponColorHex = DataReader::toHex(weapon2);
            weaponColor = weapon2;
        }
        else {
            weapon = "";
            return;
        }
    }

    if (weaponColorHex == DataReader::LIGHT_WEAPON_COLOR) {
        if (DataReader::checkWeapon(DataReader::R301_PIXELS)) {
            weapon = "R301";
        }
        else if (DataReader::checkWeapon(DataReader::ALTERNATOR_PIXELS)) {
            weapon = "Alternator";
        }
        else if (DataReader::checkWeapon(DataReader::CAR_PIXELS)) {
            weapon = "CAR";
        }
        else if (DataReader::checkWeapon(DataReader::G7_PIXELS)) {
            weapon = "G7";
        }
        else if (DataReader::checkWeapon(DataReader::RE45_PIXELS)) {
            weapon = "RE45";
        }
        else if (DataReader::checkWeapon(DataReader::SPITFIRE_PIXELS)) {
            weapon = "Spitfire";
        }
        else if (DataReader::checkWeapon(DataReader::R99_PIXELS)) {
            weapon = "R99";
        }
        else weapon = "";
    }
    else if (weaponColorHex == DataReader::HEAVY_WEAPON_COLOR) {
        if (DataReader::checkWeapon(DataReader::RAMPAGE_PIXELS)) {
            weapon = "Rampage";
        }
        else if (DataReader::checkWeapon(DataReader::PROWLER_PIXELS)) {
            if (!isSingleMode)
                weapon = "Prowler";
            weapon = "ProwlerFullAuto";
        }
        else if (DataReader::checkWeapon(DataReader::FLATLINE_PIXELS)) {
            weapon = "Flatline";
        }
        else if (DataReader::checkWeapon(DataReader::HEMLOK_PIXELS)) {
            weapon = "Hemlok";
        }
        else if (DataReader::checkWeapon(DataReader::CAR_PIXELS)) {
            weapon = "CAR";
        }
        else if (DataReader::checkWeapon(DataReader::P3030_PIXELS)) {
            weapon = "3030";
        }
        else weapon = "";
    }
    else if (weaponColorHex == DataReader::ENERGY_WEAPON_COLOR) {
        if (DataReader::checkWeapon(DataReader::VOLT_PIXELS)) {
            weapon = "Volt";
        }
        else if (DataReader::checkWeapon(DataReader::DEVOTION_PIXELS)) {
            weapon = "Devotion";
        }
        else if (DataReader::checkWeapon(DataReader::LSTAR_PIXELS)) {
            weapon = "Lstar";
        }
        else if (DataReader::checkWeapon(DataReader::NEMESIS_PIXELS)) {
            weapon = "Nemesis";
        }
        else weapon = "";
    }
    else if (DataReader::checkSuppyDropColor(weaponColor)) {
        if (DataReader::checkWeapon(DataReader::HAVOC_PIXELS)) {
            //if (DataReader::checkTurbocharger(DataReader::HAVOC_TURBOCHARGER_PIXELS))
            //    weapon = "HavocTurbo";
            //else
            weapon = "HavocTurbo";
        }
        else weapon = "";
    }
    else weapon = "";
}

static std::vector<std::string> split(const std::string& str, char delimiter) {
    std::vector<std::string> tokens;
    std::string token;
    std::stringstream tokenStream(str);

    while (std::getline(tokenStream, token, delimiter)) {
        tokens.push_back(token);
    }

    return tokens;
}

static void highPrecisionSleep(long long nanoseconds) {
    timeBeginPeriod(1);
    LARGE_INTEGER frequency;
    LARGE_INTEGER start, end;
    QueryPerformanceFrequency(&frequency);
    QueryPerformanceCounter(&start);
    int64_t target = nanoseconds;
    while (true) {
        QueryPerformanceCounter(&end);
        int64_t elapsed = (end.QuadPart - start.QuadPart) * 1000000000 / frequency.QuadPart;
        if (elapsed >= target)
            break;
    }
    timeEndPeriod(1);
}

void Core::start() {
    if (isMouseShown() || weapon.empty() || (isSingleMode && weapon != "ProwlerFullAuto"))
        return;

    std::string originalWeapon = weapon;
    std::string currentWeapon = weapon;
    if (weapon == "HavocTurbo")
        currentWeapon = "Havoc";
    else if (weapon == "Nemesis" && DataReader::checkNemesisFullCharge())
        currentWeapon = "NemesisCharged";

    if (GlobalVars::reimuConfig.debug)
        std::cout << "Detected weapon: " << weapon << std::endl;

    if (weapon == "Havoc")
        highPrecisionSleep(400000000);

    std::vector<std::string> patternContent = DataReader::PATTERNS.at(currentWeapon);
    if (patternContent.empty()) {
        std::cout << "Failed to read " << currentWeapon << "'s weapon pattern." << std::endl;
        return;
    }

    for (const std::string& pattern : patternContent) {
        if (!pressingLeftMouse)
            break;

        if (weapon.empty() || originalWeapon != weapon)
            break;

        if (pattern.empty())
            continue;

        std::vector<std::string> ptrn = split(pattern, ',');
        if (ptrn.size() < 3) {
            std::cout << "Weapon " << weapon << ": Error pattern";
            continue;
        }

        int x = atoi(ptrn[0].c_str());
        int y = atoi(ptrn[1].c_str());
        double interval = std::stod(ptrn[2].c_str());

        moveMouse(x, y);

        highPrecisionSleep(static_cast<long long>(interval * 1e6));
    }
}

Color Core::getScreenPixel(int x, int y) {
    HWND foregroundHwnd = GetForegroundWindow();
    if (!foregroundHwnd)
        return COLOR_EMPTY;

    //HDC hdc = GetDC(nullptr);
    HDC hdc = CreateDC(L"DISPLAY", NULL, NULL, NULL);
    if (!hdc)
        return COLOR_EMPTY;

    CoordToScreen(x, y);

    COLORREF pixelColor = GetPixel(hdc, x, y);

    int red = GetRValue(pixelColor);
    int green = GetGValue(pixelColor);
    int blue = GetBValue(pixelColor);

    //ReleaseDC(nullptr, hdc);
    DeleteDC(hdc);

    return Color(red, green, blue);
}

std::string Core::getWeapon() {
    return weapon;
}

void Core::setWeapon(std::string targetWeapon) {
    weapon = targetWeapon;
}

bool Core::isADS() {
    return adsState;
}

void Core::setADS(bool state) {
    adsState = state;
}
