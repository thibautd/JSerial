#include <stdlib.h>
#include <stdio.h>
#include <termios.h>
#include <glob.h>
#include <fcntl.h>
#include <string.h>
#include <unistd.h>

#include <jni.h>

#include "JSerial.h"

struct serial_handle {
    int fd;
    struct termios termios;
};

/**
 * Opens the specified device and returns an opaque handle, or NULL.
 */
//serial_handle_t* NativeOpen(char* device) {
//    int fd = open(device, O_RDWR | O_NONBLOCK | O_NOCTTY);
//    if (fd == -1)
//        return NULL;
//    struct termios termios;
//    if(tcgetattr(fd, &termios) != 0) {
//        close(fd);
//        return NULL;
//    }
//    serial_handle_t* handle = malloc(sizeof(serial_handle_t));
//    handle->fd = fd;
//    handle->termios = termios;
//    return handle;
//}

/**
 * Get a list of available serial ports.
 *
 * This function try to open every device matching /dev/tty* to get
 * some serial-specific information on the file descriptor.
 *
 * If it succeeds (tcgetattr does not return an error) we consider
 * the device to be a valid serial port.
 *
 * This solution does not perform very well, and works only if the
 * current user have enough permissions to open the device.
 *
 * Returns an null-terminated array of serial devices paths.
 */
char** get_available_ports_names() {

    glob_t glob_results;

    if (glob("/dev/tty*", 0, NULL, &glob_results) != 0)
        return NULL;

    size_t devices_count = glob_results.gl_pathc;
    size_t found = 0;

    /* Allocate enough memory for the worst case: if every device
     * under /dev/tty* is a valid serial device. We clear the
     * memory with null bytes, so the array is null-terminated. */
    size_t result_size = sizeof(char*) * devices_count + 1;
    char** result = malloc(result_size);
    memset(result, 0, result_size);

    struct termios termios;
    char* device_name;
    int fd;

    for (int i = 0; i < devices_count; i++) {
        device_name = glob_results.gl_pathv[i];
        fd = open(device_name, O_RDWR | O_NONBLOCK | O_NOCTTY);
        if (fd == -1)
            continue;
        if(tcgetattr(fd, &termios) == 0)
            result[found++] = strdup(device_name);
        close(fd);
    }

    globfree(&glob_results);

    return result;
}

/**
 * Free the string array allocated by NativeGetAvailablePortsNames().
 */
void free_available_ports_names(char **result) {
    char **temp = result, *portName;
    while ((portName = *result) != NULL) {
        free(portName);
        result++;
    }
    free(temp);
}
