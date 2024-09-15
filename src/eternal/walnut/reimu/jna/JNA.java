package eternal.walnut.reimu.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.Arrays;
import java.util.List;

public class JNA {
    public static boolean isApexLegendsActive() {
        char[] buffer = new char[2048];

        User32.INSTANCE.GetWindowTextW(User32.INSTANCE.GetForegroundWindow(), buffer, 1024);

        return Native.toString(buffer).equals("Apex Legends");
    }

    public static class CURSORINFO extends Structure {
        public int cbSize;
        public int flags;
        public WinDef.HCURSOR hCursor;
        public WinDef.POINT ptScreenPos;

        public CURSORINFO() {
            cbSize = size();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("cbSize", "flags", "hCursor", "ptScreenPos");
        }
    }

    public interface User32Call extends StdCallLibrary {
        User32Call INSTANCE = Native.load("user32", User32Call.class, W32APIOptions.DEFAULT_OPTIONS);

        WinUser.HHOOK SetWindowsHookEx(int idHook, WinUser.HOOKPROC lpfn, WinDef.HINSTANCE hMod, int dwThreadId);
        WinDef.LRESULT CallNextHookEx(WinUser.HHOOK hhk, int nCode, WinDef.WPARAM wParam, WinDef.LPARAM lParam);
        WinDef.LRESULT UnhookWindowsHookEx(WinUser.HHOOK hhk);
        int GetMessage(WinUser.MSG lpMsg, WinDef.HWND hWnd, int wMsgFilterMin, int wMsgFilterMax);
        boolean TranslateMessage(WinUser.MSG lpMsg);
        WinDef.LRESULT DispatchMessage(WinUser.MSG lpMsg);
        boolean GetCursorInfo(CURSORINFO cursorInfo);
        short GetKeyState(int nVirtKey);
        short GetAsyncKeyState(int nVirtKey);
    }

    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);

        boolean GetCursorPos(WinDef.POINT lpPoint);
        WinDef.HWND GetForegroundWindow();
        int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);
        int mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);
        boolean GetWindowRect(WinDef.HWND hWnd, WinDef.RECT lpRect);
        boolean ClientToScreen(WinDef.HWND hWnd, WinDef.POINT lpPoint);
        boolean GetClientRect(WinDef.HWND hWnd, WinDef.RECT lpRect);
        WinDef.HDC GetDCEx(WinDef.HWND hWnd, WinDef.HRGN hrgnClip, WinDef.DWORD flags);
        WinDef.HDC GetDC(WinDef.HWND hWnd);
        int ReleaseDC(WinDef.HWND hWnd, WinDef.HDC hDC);
        WinDef.HDC GetWindowDC(WinDef.HWND hWnd);
        int GetDpiForWindow(WinDef.HWND hwnd);
    }

    public interface GDI32 extends Library {
        GDI32 INSTANCE = Native.load("gdi32", GDI32.class);

        int GetPixel(WinDef.HDC hdc, int nXPos, int nYPos);
        boolean TextOutW(WinDef.HDC hdc, int x, int y, WString lpString, int nCount);
    }
}