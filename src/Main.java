import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import eternal.walnut.reimu.*;
import eternal.walnut.reimu.jna.JNA;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final String version = "v4.0";

    public static void main(String[] args) throws IOException, InterruptedException {
        if (!System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            System.out.println("Your system is not supported! Now only support windows system.");
            return;
        }

        System.out.println("""
                %@@@@%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%*=-::--=+++**++++++++*#%%%**-.*@@=:@@@@@
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
                %@@@@@@@@@@@@%: -#@%%##*++********: -=====-----::::.......::::--=====: :+****************+..#@@@@@@@""");
        System.out.println("Welcome to use Reimu, Code by HackerSense Team @Eternal_Walnut (Also known as Sakion_Sakura)");
        System.out.println("Our Github: https://github.com/hackersense");
        System.out.println("Current version: " + version);
        if (!new File("config.cfg").exists()) {
            System.out.println("Enter your Apex Legends game sens: (example 4.0)");
            Scanner scanner = new Scanner(System.in);
            GlobalVars.reimuConfig.sens = scanner.nextFloat();
            GlobalVars.save();
        }
        GlobalVars.read();
        System.out.println("Sens: " + GlobalVars.reimuConfig.sens);

        ProcessBuilder processBuilder = new ProcessBuilder("wmic", "process", "where", "ProcessId=" + ProcessHandle.current().pid(), "call", "setpriority", "256");
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode == 0)
            System.out.println("Successfully set process priority.\nStart running...");
        else
            System.out.println("Failed to set process priority.\nStart running...");

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        GlobalScreen.addNativeMouseListener(new GlobalMouseListener());
        GlobalScreen.addNativeMouseWheelListener(new GlobalMouseWheelListener());

        WinUser.LowLevelMouseProc mouseHook = (nCode, wParam, lParam) -> {
            if (nCode >= 0) {
                switch (wParam.intValue()) {
                    case 0x0202:
                        Core.pressingLeftMouse = false;
                        break;
                    default:
                        break;
                }
            }
            return JNA.User32Call.INSTANCE.CallNextHookEx(null, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
        };

        User32.HHOOK hook = JNA.User32Call.INSTANCE.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseHook, Kernel32.INSTANCE.GetModuleHandle(null), 0);
        if (hook == null) {
            System.err.println("Failed to install mouse hook");
            System.exit(1);
        }

        WinUser.MSG msg = new WinUser.MSG();
        while (JNA.User32Call.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            JNA.User32Call.INSTANCE.TranslateMessage(msg);
            JNA.User32Call.INSTANCE.DispatchMessage(msg);
        }

        JNA.User32Call.INSTANCE.UnhookWindowsHookEx(hook);
    }

    private static void shutdown() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}