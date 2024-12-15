#include <iostream>
#include <thread>

#include "Core.h"
#include "GlobalVars.h"
#include "DataReader.h"

#define isKeyPressed(VK) ((GetAsyncKeyState(VK) & 0x8000) != 0)
constexpr auto REIMU_VERSION = "v8.5";

HHOOK hKeyboardHook;
HHOOK hMouseHook;

static void keyDown(int vkCode) {
    try {
        if (vkCode == GlobalVars::reimuConfig.exitApp) {
            exit(EXIT_SUCCESS);
        }
        else if (vkCode == GlobalVars::reimuConfig.switchMode) {
            Sleep(250);
            if (DataReader::isSelectiveFireWeapon(Core::getWeapon()) && !isKeyPressed(VK_LBUTTON))
                Core::detectWeapon();
        }
        else if (vkCode == GlobalVars::reimuConfig.reloadWeapon) {
            Sleep(100);
            if (!isKeyPressed(VK_LBUTTON))
                Core::detectWeapon();
        }
        else if (vkCode == GlobalVars::reimuConfig.mapKey || vkCode == GlobalVars::reimuConfig.bagKey) {
            Core::detectWeapon();
        }
        else if (vkCode == GlobalVars::reimuConfig.weapon1 || vkCode == GlobalVars::reimuConfig.weapon2) {
            Sleep(100);
            Core::detectWeapon();
        }
        else if (vkCode == GlobalVars::reimuConfig.handKey) {
            Core::setStopDetection(true);
            Core::reset();
        }
        else if (vkCode == VK_ESCAPE) {
            Sleep(150);
            Core::detectWeapon();
        }
        else if (vkCode == GlobalVars::reimuConfig.skillKey) {
            if (Core::isApexLegendsActive()) {
                Sleep(300);
                if (DataReader::isSella())
                    Core::setWeapon("Sella");
                else
                    Core::reset();
            }
        }

        if (Core::getWeapon().empty())
            Core::setADS(false);
    }
    catch (...) {

    }
}

static void keyUp(int vkCode) {
    try {
        if (vkCode == GlobalVars::reimuConfig.pickWeapon) {
            Sleep(300);
            Core::detectWeapon();
        }
        else if (vkCode == GlobalVars::reimuConfig.grenadeKey) {
            Core::setStopDetection(true);
            Core::reset();
        }
    }
    catch (...) {

    }
}

static void rightButtonDown() {
    if (Core::getWeapon().empty() || !Core::isApexLegendsActive())
        Core::setADS(false);
    else
        Core::setADS(!Core::isADS());
}

static void rightButtonUp() {
    if (Core::getWeapon().empty() || GlobalVars::reimuConfig.adsType == 2)
        Core::setADS(false);
}

LRESULT CALLBACK KeyboardProc(int nCode, WPARAM wParam, LPARAM lParam) {
    if (nCode >= 0) {
        std::thread([](WPARAM wParam, LPARAM lParam) {
            KBDLLHOOKSTRUCT* kbd = (KBDLLHOOKSTRUCT*)lParam;
            if (wParam == WM_KEYDOWN)
                keyDown(kbd->vkCode);
            else if (wParam == WM_KEYUP)
                keyUp(kbd->vkCode);
        }, wParam, lParam).detach();
    }
    return CallNextHookEx(hKeyboardHook, nCode, wParam, lParam);
}

LRESULT CALLBACK MouseProc(int nCode, WPARAM wParam, LPARAM lParam) {
    if (nCode >= 0) {
        std::thread([](WPARAM wParam, LPARAM lParam) {
            if (wParam == WM_LBUTTONDOWN)
                Core::setMouseState(true);
            else if (wParam == WM_LBUTTONUP)
                Core::setMouseState(false);
            else if (GlobalVars::reimuConfig.adsType != 0) {
                if (wParam == WM_RBUTTONDOWN)
                    rightButtonDown();
                else if (wParam == WM_RBUTTONUP)
                    rightButtonUp();
            }
        }, wParam, lParam).detach();
    }
    return CallNextHookEx(hMouseHook, nCode, wParam, lParam);
}

void HookThread() {
    hKeyboardHook = SetWindowsHookEx(WH_KEYBOARD_LL, KeyboardProc, NULL, 0);
    if (hKeyboardHook == NULL) {
        std::cerr << "Failed to install keyboard hook! Error: " << GetLastError() << std::endl;
        return;
    }

    hMouseHook = SetWindowsHookEx(WH_MOUSE_LL, MouseProc, NULL, 0);
    if (hMouseHook == NULL) {
        std::cerr << "Failed to install mouse hook! Error: " << GetLastError() << std::endl;
        UnhookWindowsHookEx(hKeyboardHook);
        exit(EXIT_FAILURE);
        return;
    }

    MSG msg;
    while (GetMessage(&msg, NULL, 0, 0)) {
        if (PeekMessageA(&msg, NULL, NULL, NULL, PM_REMOVE)) {
            TranslateMessage(&msg);
            DispatchMessage(&msg);
        } else Sleep(0);
    }
    
    UnhookWindowsHookEx(hKeyboardHook);
    UnhookWindowsHookEx(hMouseHook);
}

void HideConsoleThread() {
    FreeConsole();
}

void adjustPrivileges()
{
    HANDLE token;
    TOKEN_PRIVILEGES tp;
    tp.PrivilegeCount = 1;
    tp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
    tp.Privileges[0].Luid.LowPart = 20; // 20 = SeDebugPrivilege
    tp.Privileges[0].Luid.HighPart = 0;

    if (OpenProcessToken((HANDLE)-1, TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &token)) {
        AdjustTokenPrivileges(token, FALSE, &tp, 0, NULL, 0);
        CloseHandle(token);
    }
}

ReimuConfig GlobalVars::reimuConfig;

int main()
{
    SetConsoleTitle(L"Reimu😋");
    adjustPrivileges();

    if (!SetPriorityClass(GetCurrentProcess(), REALTIME_PRIORITY_CLASS))
        std::cerr << "Failed to set process priority.\n";

    std::cout << R"(%@@@@%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%*=-::--=+++**++++++++*#%%%**-.*@@=:@@@@@
@@@@@@@@@@@%%##***+++++++++****##%%%@@@@@@@@@@@@@@@@@@@@%+-::-=++*******++++++++++++*%#*= +@*.+@@@@@
=====**+--::::::------------------::---=+**#%%%%%#####*-.:-+*********++++========+++#***=.=@#=-#@@@@
.+##*: :=*###*********************+**++=:.. ....... ... ..:.::--=++++====--=======+*+++*=.+@@%:*@@@@
::#@@+ =*###%*++=============+++++++=-:...............::............::--===-----===++**+- +@#-+@@@@@
.:#@@*.:*###*+=======-===========-:...........:...............::........:--==----=++=+**-.+@*:+@@@@@
%= =@%:.=+**=+=--=----=--====--:. ....................................... .:--==-==+++**-.*%@=:@@@@@
%-:*@@+ :++++=+====---==-==-:.. ..........................................  ..:-==++++++:.*%=.*@@@@@
+ +@@@#. -=-=-=++==---==--:. .................. ................  .:. .......  ..-++-=+=.:#%+.#@@@@@
%+:-%@@+ :==+-=+===-=-==-.  ..............  -++:. ..............  -*+:   .......  :++==- -#%*.#@@@@@
@@=.%@@%- :===++=+=-===:. ................ -****=: ............. .+*-.-:  ........ .=+=: =%*.=@@@@@@
@%:.#@@@#..===*+===-=-:  . .............  .=*****+-. ........... -+: -**=. ........  :-. *@%:-@@@@@@
@@*:.:*@@+ .++*++++--.     ............ .-. :=+****=. ......... :+: -*+**+: ......... . .%@%:-@@@@@@
@@@@+ +@@@- -+++=++-.    ............  :+*+=: .-+***+: ......  :+- -******+: ......... .+@@=.*@@@@@@
@@@@= +@@@#. -+*+*+:   ............. .=******=:..-+**+:  .... :+: -*+=-:...-: ........ -%@%.=@@@@@@@
@@@@%-:-:+@* .=+=*=    ............ :+**++*****+-..=**+: .. .-*- --..  ... -+. ......  =%@+.#@@@@@@@
@@@@@@@#.=@@+ .=+=. ............   .::.....::-=+*+- -+*+:  .=**:.... .:::. -*-  ...    -%* +@@@@@@@@
@@@@@@@# =%%@+ .=- ....    .... :=.:=+++-. ..   .:===++*+--+**+. =*=  :::. -*+. ---. ...:.=@@@@@@@@@
@@@@@@@@+--:+@+... ...  :--==-.:#*..:.::..::::::.  =********+*=  ::..::::. -*+. -==. ... +@@@@@@@@@@
@@@@@@@@@@%:=@@#-  ... .-++++- =%*..:::::::::::::: -**********- .::::::::. -*+. -==: ... +@@@@@@@@@@
@@@@@@@@@@*.:**%@: .:.. :++++-.-##:.::::::::::::::.-**********- .::::::::. -*=..-++: ... :%@@@@@@@@@
@@@@@@@@@=.:-: :%- .:....+***=.-##=..:::::::::::::.=**********= .::::::::..+*- .::-. .... +@@@@@@@@@
@@@@@@@#:.=**+:.:  .:... :=--: :***-..:::::::::::.:***********+. :::::::. -*=. .    ..... .+@@@@@@@@
@@@@@@*.:+***+=-:.. ........    =***=...:::::::. :+************+-...::.  =*+. .....  .....  :#@@@@@@
@@@@@+ -**+++=====- .... ......  =***+=:.......:-****++******+-+*=:...:-+*=.  .....   .... :: -%@@@@
@@@@* -%%#+===---=- ...  .......  :=****+==--==-:=*********+=..+***+++**=:  ........  .... -%+..=%@@
@@@%:.##*#+=++=--=-. .   .........  :=*********=: .--====-:..-+**+***+=:    ........  .... .*#*=..-=
@#+= .+#++*==+++=--.     ......       .-=+*******+-::....:-=******+=:.     ..    .  ...  ...=***: -=
-.=**=..:-=++==++=-.  ..  .....  ..    ...:--=+++***********+++=::..        ...   .:-+=-=--+*+-.:#@@
*++:*@%*-:..:-=+++=::-==-:. .   ... .:==: :::.....::::::::::.....  =+-.     :=+=====+*==+==-:.:*@@*+
@@@:-#*%#*+-:..--=*+++++++=-. ..  .=**-.:.:+***+++-:  .-.  :=+**: ..:.:-=*+..++=====+==-::..-*@@@+ .
%@@#++:=%###*+=:..:-=====+*+. *%#=:.:..-=: .+*+=-:.. .+*++-:.:-- .=: .**#@# .====+=--:...:=#@@#:-:-+
@@@@@@+.=*+=+**#*=-:...-*+==. *@@%*=. -+==: .:...:-..=*+++*+=. .:=++: :+*%%*-..:--:..:=+*%#===:-*%@@
@@@@@@@#+++-:====*****=. .:=+*#%#**- :=====-::--==: .-=+**+=+=..-===+- :+*#%%#+-.  -*#*=-=:.=*%@@@@@
@@@@@@@@@@@@*===.:------+#@@@%#***=.:=====-=====-=::::..:-.....:-===+=:.=****#%%#+-::-.:+#%@@@@@@@@@
%@@@@@@@@@@@@@@%%+: .-*@@@%#*****+: -========----=====-:.::-=--====--.:=*****+*##%%#+-::=#@@@@@@@@@@
%@@@@@@@@@@@@@@#=..=%@%%#********+......::----=================--::.. =*************###+:..+@@@@@@@@
%@@@@@@@@@@@@%=.:*#**##***********=..::::........::::::::::::....::-- .+******************- +@@@@@@@
%@@@@@@@@@@@@%: -#@%%##*++********: -=====-----::::.......::::--=====: :+****************+..#@@@@@@@)" << std::endl;
    std::cout << "Welcome to use Reimu, Code by HackerSense Team @Eternal_Walnut (Also known as Sakion_Sakura)" << std::endl;
    std::cout << "Our Github: https://github.com/hackersense" << std::endl;
    std::cout << "Current version: " << REIMU_VERSION << std::endl;

    if (!FileUtils::exists("Config.json")) {
        std::cout << "Enter your Apex Legends game sens: (example 4.0)" << std::endl;
        float sens;
        std::cin >> sens;
        GlobalVars::reimuConfig.sens = sens;
        GlobalVars::save();
    }

    GlobalVars::read();
    GlobalVars::save();
    std::cout << "Sens: " << GlobalVars::reimuConfig.sens << std::endl;
    if (GlobalVars::reimuConfig.adsType != 0)
        std::cout << "Zoom Sens: " << GlobalVars::reimuConfig.zoom_sens << std::endl;

    std::thread hookThread(HookThread);

    std::cout << "Start running..." << std::endl;
    if (GlobalVars::reimuConfig.hideConsole) {
        std::cout << "Hiding Console..." << std::endl;
        Sleep(1000);
        HideConsoleThread();
    }

    boolean pressed = false;
    while (true) {
        bool keyDown = isKeyPressed(VK_LBUTTON);
        if (!keyDown || pressed) {
            if (!keyDown) {
                Core::setMouseState(false);
                pressed = false;
            }
            Sleep(1);
            continue;
        }

        pressed = true;
        Core::setMouseState(true);
        try {
            Core::start();
        }
        catch (...) {

        }
    }

    PostThreadMessage(GetThreadId(hookThread.native_handle()), WM_QUIT, 0, 0);
    hookThread.join();
    return EXIT_SUCCESS;
}