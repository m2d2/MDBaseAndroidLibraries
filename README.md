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
        Session session = ModelAccessor.getSessionDetails();
        if (session != null) {
            getClient().addHeader("x-auth-token", session.token);
        }
    }

    @Override
    final protected void authenticate(boolean force) {
        if (isAuthenticating()) return;

        beginAuthenticating();

        Session session = ModelAccessor.getSessionDetails();
        Log.d(MomentoJarApplication.appTag(this), "Authentication required");

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
