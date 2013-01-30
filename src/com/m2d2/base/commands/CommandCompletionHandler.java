package com.m2d2.base.commands;


public abstract class CommandCompletionHandler {

    public abstract void onComplete(CommandResult result);

    public void finish(CommandResult result) {
        onComplete(result);
    }
}
