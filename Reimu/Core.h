#pragma once

#include <string>

class Color;

namespace Core {
	bool isApexLegendsActive();
	bool isMouseShown();
	bool isColorInRange(const Color& color, const Color& targetColor, int tolerance);
	void moveMouse(int x, int y);
	void setMouseState(bool down);
	void setStopDetection(bool stop);
	void reset();
	void detectWeapon();
	void start();
	Color getScreenPixel(int x, int y);
	std::string getWeapon();
	void setWeapon(std::string targetWeapon);
	bool isADS();
	void setADS(bool state);
}