#pragma once

#include <Windows.h>
#include <string>
#include <wininet.h>
#pragma comment(lib,"WinInet.lib")

namespace HTTPUtils
{
	std::string get(std::string url);
}