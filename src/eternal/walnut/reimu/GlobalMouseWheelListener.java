package eternal.walnut.reimu;

import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

public class GlobalMouseWheelListener implements NativeMouseWheelListener {
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        try {
            Thread.sleep(100);
            Core.DetectAndSetWeapon();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
