#include <windows.h>

#include "JSerial.h"

typedef struct SerialHandle
{
	HANDLE native;
	OVERLAPPED readOv;
	OVERLAPPED writeOv;
	DCB config;
	COMMTIMEOUTS timeout;
} SerialHandle;

LPTSTR NativeGetErrorString(DWORD error)
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

VOID NativeFreeErrorString(LPTSTR message)
{
	HeapFree(GetProcessHeap(), 0, message);
}

SerialHandle* NativeOpen(LPTSTR portName)
{
	
	HANDLE nativeHandle = CreateFile(portName, GENERIC_READ | GENERIC_WRITE,
		0, NULL, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, NULL);
	if (nativeHandle == INVALID_HANDLE_VALUE)
		return NULL;
	
	SerialHandle* handle = HeapAlloc(GetProcessHeap(),
		HEAP_ZERO_MEMORY, sizeof(SerialHandle));
	if (!handle)
		goto error;
	
	handle->native = nativeHandle;
	
	if (!GetCommState(nativeHandle, &handle->config))
		goto error;

	if (!GetCommTimeouts(nativeHandle, &handle->timeout))
		goto error;

	/* Sets default timeout to infinite timeout. This should be the
	 * expected default behavior for an opened port, like a socket. */
	if (!NativeSetTimeout(handle, TIMEOUT_INFINITE))
		goto error;

	return handle;

error:
	if (nativeHandle != NULL)
		CloseHandle(nativeHandle);
	if (handle != NULL)
		HeapFree(GetProcessHeap(), 0, handle);
	return NULL;
}

BOOL NativeRead(SerialHandle* handle, LPBYTE buffer, DWORD numberOfBytes, LPDWORD readBytes)
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

BOOL NativeWrite(SerialHandle* handle, LPBYTE buffer, DWORD numberOfBytes)
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

BOOL NativeClose(SerialHandle* handle)
{
	if (!CloseHandle(handle->native))
		return FALSE;
	HeapFree(GetProcessHeap(), 0, handle);
	return TRUE;
}

DWORD NativeGetBaudRate(SerialHandle* handle)
{
	return handle->config.BaudRate;
}

DWORD NativeGetParity(SerialHandle* handle)
{
	return handle->config.Parity;
}

DWORD NativeGetStopBits(SerialHandle* handle)
{
	return handle->config.StopBits;
}

DWORD NativeGetDataBits(SerialHandle* handle)
{
	return handle->config.ByteSize;
}

VOID NativeSetBaudRate(SerialHandle* handle, DWORD baudRate)
{
	handle->config.BaudRate = baudRate;
}

VOID NativeSetParity(SerialHandle* handle, DWORD parity)
{
	handle->config.Parity = (BYTE)parity;
}

VOID NativeSetStopBits(SerialHandle* handle, DWORD stopBits)
{
	handle->config.StopBits = (BYTE)stopBits;
}

VOID NativeSetDataBits(SerialHandle* handle, DWORD dataBits)
{
	handle->config.ByteSize = (BYTE)dataBits;
}

BOOL NativeSetConfig(SerialHandle* handle)
{
	return SetCommState(handle->native, &handle->config);
}

BOOL NativeSetTimeout(SerialHandle* handle, INT timeout)
{
	if (timeout == TIMEOUT_INFINITE)
	{
		/* Waits indefinitly for the first byte, then read next bytes
		 * until no bytes is available for 10ms. */
		handle->timeout.ReadIntervalTimeout = 10;
		handle->timeout.ReadTotalTimeoutConstant = 0;
		handle->timeout.ReadTotalTimeoutMultiplier = 0;
	} else if (timeout == TIMEOUT_IMMEDIATE)
	{
		/* No timeout. All the functions returns immediatly. */
		handle->timeout.ReadIntervalTimeout = MAXDWORD;
		handle->timeout.ReadTotalTimeoutConstant = 0;
		handle->timeout.ReadTotalTimeoutMultiplier = 0;
	} else
	{
		handle->timeout.ReadIntervalTimeout = 0;
		handle->timeout.ReadTotalTimeoutConstant = timeout;
		handle->timeout.ReadTotalTimeoutMultiplier = 0;
	}
	return SetCommTimeouts(handle->native, &handle->timeout);
}

INT NativeGetTimeout(SerialHandle* handle)
{
	if (handle->timeout.ReadIntervalTimeout == 10)
		return TIMEOUT_INFINITE;
	if (handle->timeout.ReadIntervalTimeout == 0)
		return TIMEOUT_IMMEDIATE;
	return handle->timeout.ReadTotalTimeoutConstant;
}