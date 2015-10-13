#ifndef JSERIAL_JSERIAL_H
#define JSERIAL_JSERIAL_H

typedef struct serial_handle serial_handle_t;

char** NativeGetAvailablePortsNames();
void NativeFreeAvailablePortsNames(char **result);

serial_handle_t* NativeOpen(char* device);

#endif
