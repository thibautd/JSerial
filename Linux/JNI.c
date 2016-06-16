#include <jni.h>

#include "JSerial.h"

JNIEXPORT jobjectArray JNICALL
Java_dk_thibaut_serial_SerialNativeLinux_getAvailablePortsNames
    (JNIEnv *env, jobject serialPort)
{
    jobjectArray result = (*env)->NewObjectArray(env, 10,
        (*env)->FindClass(env, "java/lang/String"), NULL);

    char **ports = get_available_ports_names();
    char **tmp = ports;
    char  *port = *ports;

    for (int i = 0; port != NULL; i++) {
        (*env)->SetObjectArrayElement(env, result, i,
            (*env)->NewStringUTF(env, port));
        port = *(ports++);
    }

    free_available_ports_names(tmp);

    return result;
}
