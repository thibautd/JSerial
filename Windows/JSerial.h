#ifndef JSERIAL_H
#define JSERIAL_H

#include <windows.h>

#ifdef JSERIAL_LIB
#define DllExport __declspec(dllexport)
#else
#define DllExport
#endif

typedef struct SerialHandle SerialHandle;


SerialHandle* NativeSerialOpen(LPTSTR portName)
{
	
}

#endif