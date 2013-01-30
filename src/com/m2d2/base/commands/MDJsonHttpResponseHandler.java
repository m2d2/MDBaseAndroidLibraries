package com.m2d2.base.commands;

import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MDJsonHttpResponseHandler extends JsonHttpResponseHandler {

    private Command _command;

    public MDJsonHttpResponseHandler(Command command) {
        _command = command;
    }

    public Command getCommand() {
        return _command;
    }

    @Override
    public void onFailure(Throwable throwable, JSONObject jsonObject) {
        _command.finish(getErrorCommandResult(throwable, jsonObject));
    }

    @Override
    public void onFailure(Throwable throwable, JSONArray jsonArray) {
        _command.finish(getErrorCommandResult(throwable, jsonArray));
    }

    @Override
    protected void handleFailureMessage(java.lang.Throwable throwable, String s) {
        _command.finish(getErrorCommandResult(throwable, s));
    }

    private CommandResult getErrorCommandResult(Throwable throwable, Object o) {
        int statusCode = (throwable instanceof HttpResponseException) ?
                ((HttpResponseException)throwable).getStatusCode() : 400;

        CommandResult result = new CommandResult(null, o, statusCode);
        result.exception = throwable;
        return result;
    }
}
