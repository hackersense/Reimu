#pragma once

#include <Windows.h>
#include "DataReader.h"
Color PixelGetColor(int x, int y)
{
	HWND foregroundHwnd = GetForegroundWindow();
	if (foregroundHwnd == nullptr)
		return COLOR_EMPTY;

	RECT targetRect;
	GetClientRect(foregroundHwnd, &targetRect);

	POINT topLeft = { targetRect.left, targetRect.top };
	POINT bottomRight = { targetRect.right, targetRect.bottom };

	ClientToScreen(foregroundHwnd, &topLeft);
	ClientToScreen(foregroundHwnd, &bottomRight);

	RECT resultRect{ topLeft.x, topLeft.y, bottomRight.x, bottomRight.y };

	float dpi = GetDpiForWindow(foregroundHwnd) / 96.0f;

	int windowX = x + (resultRect.left * dpi);
	int windowY = y + (resultRect.top * dpi);

	HDC hdc = CreateDC(L"DISPLAY", NULL, NULL, NULL);// : GetDC(NULL);
	if (!hdc)
		return COLOR_EMPTY;

	COLORREF color = GetPixel(hdc, x, y);
	int red = GetRValue(color);
	int green = GetGValue(color);
	int blue = GetBValue(color);
	//if (use_alt_mode)
	DeleteDC(hdc);
	//else
	//	ReleaseDC(NULL, hdc);
	return Color(red, green, blue);
}