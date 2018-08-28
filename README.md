# DSce - A DreamScreen compatible Client, CLI and Emulator

This library aims at providing an easy to use Client library written in Java that can be used in other projects to control a DreamScreen device.

An Emulator is also included, which can be extended to hook into the different methods of the emulated device. The main target of this was to emulate the DreamScreen SideKick, which is able to receive the screen colors form a DreamScreen device at 60FPS (That's what is claimed at least -> Untested), and be able to send the screen colors to annother LED device.

And finally a small CLI, for those who just want to run some commands from the terminal without tinkering with code.

**PLEASE NOTE: This library is completely unoficial, and has no relation with the original DreamScreen developers. The implementation is purely based on documentation found in the internet and my own observations.**
