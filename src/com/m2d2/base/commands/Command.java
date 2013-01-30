package com.m2d2.base.commands;

import android.util.Log;
import com.m2d2.base.constants.Constants;
import com.m2d2.base.engines.Engine;

public abstract class Command {

    protected CommandCompletionHandler _completionHandler;
    private Engine _engine;
    private int _retires;

    public Command() {
        _retires = 0;
        _completionHandler = null;
        _engine = null;
    }

    public Command(CommandCompletionHandler handler, Engine engine) {
        _retires = 0;
        _completionHandler = handler;
        _engine = engine;
    }

    public abstract void execute();

    protected Engine getEngine() {
        return _engine;
    }

    protected void finish(CommandResult result) {
        finish(result, false);
    }

    protected void finish(CommandResult result, boolean forceClose) {
        if (result.commandFailed() &&
                result.statusCode == Constants.HTTP_CODE_UNAUTHORIZED &&
                _retires < Constants.OPTION_COMMAND_MAX_RETRIES) {
            synchronized (this) {
                _retires++;
                Log.d("M2D2::Command",
                        "Execution failed due to session unauthorized exception. Retrying (" + _retires + ")...");
                execute();
            }
        } else {
            if (_completionHandler != null) {
                _completionHandler.finish(result);
            }

            if (forceClose || !isPersistentCommand()) {
                CommandRunner.removeCommand(this);
            }
        }
    }

    public boolean isPersistentCommand() {
        return false;
    }

    public void kill() {

    }
}
