MDBaseAndroidLibraries
======================

_Doco is still being written..._

Android library that provides Engines to easily communicate with external APIs and Commands to carry out application operations.

There are 2 parts to this library:

## Engines

Engines are a simple way to communicate with an external API for your Android app. For example, if your app ships with a Rails API that you synchronize data with, this is the way to go!

### How to use Engines

1. Create an Abstract Engine for your app. 

Extend the 4 methods for your engine. You'll want to have a seperate abstract engine for each separate base domain you access i.e. different engines for facebook.com and yourapp.com/api.

2. Extend your app's engine to use for API calls.


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
