# JSerial

JSerial aims to be an **easy to use**, well **documented** and **cross-platform** serial library for Java. JSerial is distributed under the term of the MIT licence (you can use it in closed source applications).

Currently, only the following platforms are supported :

* Windows x86
* Windows x64

The code is easy to understand, documented and unit tested. If you want to contribute, please get in touch if you need help, contributions are really welcome (especially new platforms support !).

# Installation

You can download the latest available JAR, including all-supported platforms on the [releases](https://github.com/thibautd/JSerial/releases) page.

If you want to build from source, you will need gradle. Simply run ``gradle installDist`` in the ``JSerial`` directory to generate a jar file.

Please note that JSerial uses [JNA](https://github.com/java-native-access/jna) (Java Native Access) to make native functions calls.

**Windows Users :** The native DLL has been compiled with Visual Studio 2015, so you needto install [Visual C++ Redistributable for Visual Studio 2015](https://www.microsoft.com/en-US/download/details.aspx?id=48145) ! Don't forget to install it, else it will not work.

# Features

* Supports listing, reading and writing to serial ports
* Configure port (baudrate, parity, stop bits, data bits)
* Configure read timeout (milliseconds, infinite or non-blocking)
* Setting RTS/DTR, and reading CTS/DSR status.
* Supports Java NIO Buffers
* Supports Java Streams

# Documentation

The Javadoc's documentation is available on http://thibautd.github.io/JSerial/.

# Example

Here is a simple example of use. This example will work with all the supported platforms.

``` java
import dk.thibaut.serial.SerialPort;
import dk.thibaut.serial.enums.*;

// Get a list of available ports names (COM2, COM4, ...)
List<String> portsNames = SerialPort.getAvailablePortsNames();

// Get a new instance of SerialPort by opening a port.
SerialPort port = SerialPort.open("COM2");

// Configure the connection
port.setTimeout(100):
port.setConfig(BaudRate.B115200, Parity.NONE, StopBits.ONE, DataBits.B8);

// You have the choice, you can either use the Java NIO channels
// or classic Input/Ouput streams to read and write data.
SerialChannel channel = port.getChannel();
InputStream istream = port.getInputStream();

// Read some data using a stream
byte[] byteBuffer = new byte[4096];
// Will timeout after 100ms, returning 0 if no bytes were available.
int n = istream.read(byteBuffer);

// Read some data using a ByteBuffer.
ByteBuffer buffer = ByteBuffer.allocate(4096);
int c = channel.read(buffer);

port.close();
```

# Compiling native code

If you want to rebuild the native libraries, all the source code is available. For Windows, you need to open the Visual Studio 2015 solution, and that's all. When you'll build the project, the output DLL will automatically be copied into the correct folder in the JSerial directory.

# Contributing

If you want to contribute, you can open the project with IntelliJ Community Edition IDE (for the Java part) by importing the project as a Gradle project.

Contributions are welcome, and I can help you to get in touch with the code if you need help.
