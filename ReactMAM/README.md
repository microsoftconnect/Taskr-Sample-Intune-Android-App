# Taskr - A [Microsoft Intune](https://www.microsoft.com/en-us/cloud-platform/microsoft-intune) React Native + Android MAM SDK Example
This project is a demonstration of the [Microsoft Intune SDK for Android](https://docs.microsoft.com/en-us/intune/app-sdk) in projects that primarily use [React Native](https//reactnative.dev).
A developer guide to the SDK is available [here](https://docs.microsoft.com/en-us/intune/app-sdk-android).
This project implements some commonly used features so developers making their own apps have an example to follow.
IT administrators who want to create apps with similar functionality can even use this as a template.

Taskr allows users to keep a list of to-do items, or tasks. Users can view their open tasks and mark tasks as complete, print them, or save them to their phone. Users' actions are managed by policy, so not all actions may be available.


## Important Notes Before Starting (React Native Specific)
### Create a Debug Keystore
In the android/app folder, create a new keystore named `debug.keystore`. React Native expects a keystore to sign the generated app during the build process.