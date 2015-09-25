#ifndef JSERIAL_H
#define JSERIAL_H

#include <windows.h>

#ifdef __cplusplus
extern "C" {
#endif

#ifdef JSERIAL_LIBRARY
	#define DllExport __declspec(dllexport)
#else
	#define DllExport
#endif

#define TIMEOUT_INFINITE -1
#define TIMEOUT_IMMEDIATE 0

typedef struct SerialHandle SerialHandle;

DllExport LPTSTR NativeGetErrorString(DWORD error);
DllExport VOID NativeFreeErrorString(LPTSTR message);

DllExport SerialHandle* NativeOpen(LPTSTR portName);
DllExport BOOL NativeRead(SerialHandle* handle, LPBYTE buffer, DWORD numberOfBytes, LPDWORD readBytes);
DllExport BOOL NativeWrite(SerialHandle* handle, LPBYTE buffer, DWORD numberOfBytes);
DllExport BOOL NativeClose(SerialHandle* handle);

DllExport DWORD NativeGetBaudRate(SerialHandle* handle);
DllExport DWORD NativeGetParity(SerialHandle* handle);
DllExport DWORD NativeGetStopBits(SerialHandle* handle);
DllExport DWORD NativeGetDataBits(SerialHandle* handle);

DllExport VOID NativeSetBaudRate(SerialHandle* handle, DWORD baudRate);
DllExport VOID NativeSetParity(SerialHandle* handle, DWORD parity);
DllExport VOID NativeSetStopBits(SerialHandle* handle, DWORD stopBits);
DllExport VOID NativeSetDataBits(SerialHandle* handle, DWORD dataBits);
DllExport BOOL NativeSetConfig(SerialHandle* handle);

DllExport BOOL NativeSetTimeout(SerialHandle* handle, INT timeout);
DllExport INT NativeGetTimeout(SerialHandle* handle);

#ifdef __cplusplus
}
#endif

#endif