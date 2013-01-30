package com.m2d2.base.commands;

// dineth: 24/12/12 - 9:52 AM

public class CommandResult {

    public final static CommandResult OK = new CommandResult(null, null, 200);
    public final static CommandResult ERROR = new CommandResult(null, "There was an error", 400);

    public Object result;
    public int statusCode;
    public Object error;
    public Throwable exception = null;

    public CommandResult(Object result, Object error, int statusCode) {
        this.result = result;
        this.error = error;
        this.statusCode = statusCode;
    }

    public boolean commandFailed() {
        return (error != null || statusCode >= 400);
    }

    public boolean commandSuccessful() {
        return !commandFailed();
    }
}
