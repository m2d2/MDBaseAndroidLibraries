package com.m2d2.base.commands;

import java.util.ArrayList;

public class CommandRunner {
    private static ArrayList<Command> _pendingCommands;

    static {
        _pendingCommands = new ArrayList<Command>();
    }

    public static void runCommand(Command c) {
        if (!_pendingCommands.contains(c)) {
            _pendingCommands.add(c);
            c.execute();
        }
    }

    public static void removeCommand(Command c) {
        _pendingCommands.remove(c);
    }

    public static void killAllCommands() {
        for (Command c : _pendingCommands) {
            c.kill();
        }
        _pendingCommands.clear();
    }

    public static boolean isCommandRunning(Class commandClass) {
        for (Command c : _pendingCommands) {
            if (commandClass.isInstance(c)) {
                return true;
            }
        }
        return false;
    }
}
