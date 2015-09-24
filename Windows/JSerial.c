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

DllExport LPTSTR NativeGetErrorString(DWORD error)
{
	LPTSTR message = NULL;
	FormatMessage(
		FORMAT_MESSAGE_ALLOCATE_BUFFER |
		FORMAT_MESSAGE_FROM_SYSTEM |
		FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL, error, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
		(LPTSTR)&message, 0, NULL);
	return message;
}

DllExport VOID NativeFreeErrorString(LPTSTR message)
{
	HeapFree(GetProcessHeap(), 0, message);
}

DllExport SerialHandle* NativeOpen(LPTSTR portName)
{
	HANDLE nativeHandle = CreateFile(portName, GENERIC_READ | GENERIC_WRITE,
		0, NULL, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, NULL);
	if (nativeHandle == INVALID_HANDLE_VALUE)
		return NULL;
	SerialHandle* handle = HeapAlloc(GetProcessHeap(),
		HEAP_ZERO_MEMORY, sizeof(SerialHandle));
	handle->native = nativeHandle;
	return handle;
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

DllExport BOOL NativeClose(SerialHandle* handle)
{
	if (!CloseHandle(handle->native))
		return FALSE;
	HeapFree(GetProcessHeap(), 0, handle);
	return TRUE;
}