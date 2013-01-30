MDBaseAndroidLibraries
======================

_Doco is still being written..._

Download Library from here: https://docs.google.com/file/d/0B9QV0xhni9hEUnFvQUhhSFIzeE0/edit

Android library that provides Engines to easily communicate with external APIs and Commands to carry out application operations.

There are 2 parts to this library:

## Engines

Engines are a simple way to communicate with an external API for your Android app. For example, if your app ships with a Rails API that you synchronize data with, this is the way to go!

### How to use Engines

1. Create an Abstract Engine for your app. 
Extend the 4 methods for your engine. You'll want to have a seperate abstract engine for each separate base domain you access i.e. different engines for facebook.com and yourapp.com/api.

Here's what your app's base engine should look like:

```java

public abstract class MyAppNameEngine extends Engine {

    private final static String BASE_URL = "https://myrestapi.herokuapp.com/";

    @Override
    final protected String getBaseURL() {
        return BASE_URL;
    }

    @Override
    final protected void setAuthValues() {
        Session session = ModelAccessor.getSessionDetails(); // where ever you saved your session tokens
        if (session != null) {
            getClient().addHeader("x-authentication-token", session.token); // set headers and client options here
        }
    }

    @Override
    final protected void authenticate(boolean force) {
        if (isAuthenticating()) return;

        beginAuthenticating();

        Session session = ModelAccessor.getSessionDetails();
        Log.d("Engine.authenticate()", "Authentication required");

        if (force || session == null || (session.expires_at != null && session.expires_at.before(new Date()))) {
            PostSessionCommand command = new PostSessionCommand(new CommandCompletionHandler() {
                @Override
                public void onComplete(CommandResult result) {
                    if (result.commandSuccessful()) {
                        setAuthValues();
                        finishAuthenticating();
                        resumePendingQueueOperations();
                    }
                }
            });
            CommandRunner.runCommand(command);
        }
    }

    @Override
    final protected boolean checkAuthStatus() {
        synchronized(this) {
            if (!authenticationRequired()) return true;
            Session session = ModelAccessor.getSessionDetails();
            return (!isAuthenticating() &&
                    session != null &&
                    session.expires_at != null &&
                    session.expires_at.after(new Date()));
        }
    }
}

```

2. Extend your app's engine to use for API calls.

```java

public class SessionEngine extends MyAppNameEngine {

    @Override
    protected boolean authenticationRequired() {
        return false;
    }

    public void registerSession(String facebookId, String facebookToken, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("user_id", facebookId);
        params.put("password", facebookToken);
        callService(HTTP_POST, "login", params, responseHandler); // calls POST on base_url/login
    }
}

```

## Commands

Commands is not a new concept. This is similar to commands in other Java libraries or Silverlight. A Command is basically an operation your app carries out. This may be "logging in to server", or "create a new item", or "save a file to system". Having a seperation between the operations an app has and the activities that call it gives you many benefits.

- Your code stays clean
- Good separation between controllers and business logic
- Single command can be accessed from anywhere in the app.

### How to use Commands

1. Extend the Command class
Override the execute() method. Call finish() when you're done and return a CommandResult.

2. Calling a Command

...


## Licensing

Copyright (C) 2013, M2D2 PTY LTD

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
