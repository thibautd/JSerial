#include <windows.h>

#define DllExport __declspec(dllexport)

typedef struct SerialHandle
{
	HANDLE native;
	OVERLAPPED readOv;
	OVERLAPPED writeOv;
} SerialHandle;

typedef struct SerialConfig
{
	DWORD BaudRate;
	DWORD Parity;
	DWORD StopBits;
	BYTE DataBits;
} SerialConfig;

DllExport SerialHandle* NativeOpen(LPTSTR portName)
{
	return NULL;
}

DllExport BOOL NativeRead(SerialHandle* handle, LPBYTE buffer, DWORD numberOfBytes, LPDWORD readBytes)
{
	if (!ReadFile(handle->native, buffer, numberOfBytes, readBytes, &handle->readOv))
	{
		DWORD error = GetLastError();
		if (error != ERROR_IO_PENDING)
			return FALSE;
		if (!GetOverlappedResult(handle->native, &handle->readOv, readBytes, TRUE))
			return FALSE;
	}
	return TRUE;
}

DllExport BOOL NativeWrite(SerialHandle* handle, LPBYTE buffer, DWORD numberOfBytes)
{
	DWORD toWrite = numberOfBytes;
	while (toWrite != 0)
	{
		DWORD written = 0;
		if (!WriteFile(handle->native, buffer + (numberOfBytes - toWrite), toWrite, &written, &handle->writeOv))
		{
			DWORD error = GetLastError();
			if (error != ERROR_IO_PENDING)
				return FALSE;
			if (!GetOverlappedResult(handle->native, &handle->writeOv, &written, TRUE))
				return FALSE;
		}
		toWrite -= written;
	}
	return TRUE;
}