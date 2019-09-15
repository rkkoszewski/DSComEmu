# DreamScreen Commander & Emulator (DSce) - An unofficial DreamScreen compatible Client, CLI, Emulator and Debugger

This project is formed of several components:

- **DreamScreen Client** that allows to control a DreamScreen Device (*HD, 4K and SideKick*) in a ObjectOriented way.
- **DreamScreen Emulator** that allows to Emulate several of the DreamScreen Devices (*HD, 4K and SideKick*) [WIP].
- **Network debugger** to read and decode the messages that the DreamScreen devices are broadcasting and sending to the devices (Good to discover and understand undocumented commands and works best in combination with the Emulator).
- **CLI Interface** that can be used to controll most of the features from this project without having to code anything. Just run the JAR file and explore what the command line interface is able to do.


**PLEASE NOTE: This library is completely unoficial, and has no relation with the original DreamScreen developers. The implementation is purely based on documentation found on the internet and my own observations.**

This project was born in order to understand the communication protocol between a DreamScreen SideKick and a DreamScreen HD Device. DreamScreen is theoretically able to stream screen color updates at 60FPS to a SideKick device independently of the video source. The final goal is to Emulate a SideKick good enough to be able to receive the colors in realtime from an original DreamScreen HD device and be able to forward these to another homebrew RGB device.

## Quick Start Guide: ##

The easiest way to start is to use the CLI interface by running the JAR file in a terminal (*java -jar dsce.jar*). A list of available commands with a brief description can be seen with the command " **help** ". By using " **help %command%** " you can see a more detailed description. 

Some commands:

- **list**: Shows a list of DreamScreen devices in your network.
- **connect** / connect %ip%: Conencts to a DreamScreen device and allows to control several parameters.
- **debug**: Enables the network debug mode, any broadcasted message or directly received message will be decoded and shown in the console. 
- **emulator**: Opens the emulator console, from where you can start a emulated device, control, clone and connect to it.
- **exit**: Exits the CLI, or if inside of a submenu, returns to the previous menu.


# Client: #
To connect to a DreamScreen device use the **DSClient** class.

**DSClient.getClientList()** returns an array of devices (**DSDevice**) that are available in your network.

**DSClient.getClient("%ip%")** directly connects to the device IP and returns a **DSDevice** that allows to control the device.

The **DSDevice** class represents the most basic common feature set of of the DreamScreen devices. In order to use the full feature set of each device just cast the DSDevice class to the apropiate device class (**DreamScreenHD, DreamScreen4K or SideKick**). Identification can be done by checking with **instanceof** or by using a switch with the **getDeviceType** method (The *instanceof* way is recommended).


# Emulator: #
The available emulators can be found in the package **com.robertkoszewski.dsce.emulator**. To start an emulator simply instantiate for example a **SideKickEmulator**, and run the **start()** method to start the device emulation. An example on  how to extend an Emulator to expose the color updates can be seen in **com.robertkoszewski.dsce.emulator.variant.SwingSideKickEmulator**. Please note that the Emulator is still work in progress and doesn't supports all the feature set of the real DreamScreen devices and can lead to crashes and unexpected behaviour. 


# TODOS: #
- Finish the HDMI Active Channels decoding and encoding in the DreamScreenHD and 4K client class.
- Implement thread saftyness.
- Any other unfinished tasks.


# License and Guarantee #
The project is under the MIT License and as such doesn't offer any guarantees of any kind. Use at your own risk.