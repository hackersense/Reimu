package eternal.walnut.reimu;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

public class GlobalMouseListener implements NativeMouseListener {
    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (nativeEvent.getButton() == 1)
            Core.start();
    }
}
