#JSerial

JSerial aims to be an *easy to use*, well *documented* and *cross-platform* Serial library for Java.

Currently, the following platforms are supported :

* Windows x86
* Windows x64

The code is easy to understand, documented and unit tested. If you want to contribute, please get in touch
if you need help, contributions are really welcome (especially new platforms support !).

#Features

* List, read and write to native Serial Ports (or USB CDC virtual ports)
* Configure port (baudrate, parity, stop bits, data bits)
* Supports Java NIO Buffers
* Supports Java Streams

#Example

Here is a simple example of use. This example will work with all the supported platforms.

    // Get a list of available ports names (COM2, COM4, ...)
    List<String> portsNames = SerialPort.getAvailablePorts();
    
    // Get a new instance of SerialPort by opening a port.
    SerialPort port = SerialPort.open("COM2");
    
    // Configure the connection
    port.setTimeout(100)
    port.setConfig(BaudRate.B115200, Parity.NONE, StopBits.ONE, DataBits.B8);
    
    // You have the choice, you can either use the Java NIO channels
    // or classic Input/Ouput streams to read and write data.
    SerialChannel channel = port.getChannel();
    InputStream istream = port.getInputStream()
    
    // Read some data using your preferred solution
    byte[] byteBuffer = new byte[4096];
    // Will timeout after 100ms, returning 0 if no bytes were available.
    int n = istream.read(byteBuffer);
    
    ByteBuffer buffer = ByteBuffer.allocate(4096);
    int c = channel.read(buffer);
    
    port.close();

