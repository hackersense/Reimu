package eternal.walnut.reimu;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        try {
            if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.exitApp) {
                System.exit(0);
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.switchMode) {
                Thread.sleep(250);
                if (DataReader.isSelectiveFireWeapon(Core.weapon) && !Core.isPressed(0x01))
                    Core.DetectAndSetWeapon();
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.reloadWeapon) {
                Thread.sleep(100);
                if (!Core.isPressed(0x01))
                    Core.DetectAndSetWeapon();
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.mapKey || nativeEvent.getKeyCode() == GlobalVars.reimuConfig.bagKey) {
                Core.DetectAndSetWeapon();
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.weapon1 || nativeEvent.getKeyCode() == GlobalVars.reimuConfig.weapon2) {
                Thread.sleep(100);
                Core.DetectAndSetWeapon();
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.handKey) {
                Core.Reset();
            } else if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                Thread.sleep(150);
                Core.DetectAndSetWeapon();
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.skillKey) {
                Thread.sleep(300);
                if (DataReader.isSella())
                    Core.weapon = "Sella";
                else
                    Core.Reset();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        try {
            if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.pickWeapon) {
                Thread.sleep(300);
                Core.DetectAndSetWeapon();
            } else if (nativeEvent.getKeyCode() == GlobalVars.reimuConfig.grenadeKey) {
                Core.Reset();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
