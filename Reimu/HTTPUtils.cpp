#include "HTTPUtils.h"

std::string HTTPUtils::get(std::string url)
{
	HINTERNET hINet, hHttpFile;

	char szSizeBuffer[32];

	DWORD dwLengthSizeBuffer = sizeof(szSizeBuffer);

	hINet = InternetOpenA("IE6.0", INTERNET_OPEN_TYPE_PRECONFIG, NULL, NULL, 0);

	if (!hINet)
		return "";

	hHttpFile = InternetOpenUrlA(hINet, url.c_str(), NULL, 0, 0, 0);

	if (!hHttpFile)
		return "";

	BOOL bQuery = HttpQueryInfoA(hHttpFile, HTTP_QUERY_CONTENT_LENGTH, szSizeBuffer, &dwLengthSizeBuffer, NULL);

	if (!bQuery)
		return "";

	long fileSize = atol(szSizeBuffer);

	std::string revData;

	revData.resize(fileSize);

	DWORD dwBytesRead;

	BOOL bRead = InternetReadFile(hHttpFile, &revData[0], fileSize, &dwBytesRead);

	if (!bRead)
		return "";

	InternetCloseHandle(hHttpFile);

	InternetCloseHandle(hINet);

	return revData;
}