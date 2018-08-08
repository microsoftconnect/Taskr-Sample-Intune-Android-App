# Taskr - An Intune Android MAM SDK Example
This project is a demonstration of the [Microsoft Intune](https://www.microsoft.com/en-us/cloud-platform/microsoft-intune) [SDK for Android](https://docs.microsoft.com/en-us/intune/app-sdk). A developer guide to the SDK is available [here](https://docs.microsoft.com/en-us/intune/app-sdk-android). This project implements some commonly used features so developers making their own apps have an example to follow. IT administrators who want to create apps with similar functionality can even use this as a template.

Taskr allows users to keep a list of to-do items, or tasks. Users can view their open tasks and mark tasks as complete, print them, save them to their phone, or upload them to OneDrive. Tasks are kept in a database implemented using the [Android Room persistence library](https://developer.android.com/topic/libraries/architecture/room). Users' actions are managed by policy, so not all actions may be available.

## Important Notes Before Starting
- Do not use "Instant Run" in Android Studio when developing with the MAM SDK build plugin. It will result in build issues.
- You will need to register with [Azure Active Directory](https://github.com/AzureAD/azure-activedirectory-library-for-android#how-to-use-this-library). The goal of this registration is to acquire a client ID, which you should replace the current `AuthManager#CLIENT_ID` with.

### Authentication Flow
1. When the user first launches the app, before `TaskrApplication#onCreate` is called, the MAM SDK will initialize itself and check if the user is signed in and needs to provide a PIN. If they do, it will open a PIN screen.
1. Once this process has completed (the user provides a PIN, a PIN is not required, or the user is not signed in), `TaskrApplication#onCreate` registers an AuthenticationCallback with MAM. This will be called by the MAM SDK when it needs to acquire an access token.
1. Next, `MainActivity#onCreate` will be called. This checks if the user is already authenticated in a recently run state of the app, and signs them in if they are. If not, it will try to silently acquire an access token from ADAL's cache by calling `authentication/AuthManager#signInSilent`.
1. If a token is still in the cache and valid, `authentication/AuthManager#handleSignInSuccess` will be called. If it is not and there was an error, the error callback provided will be called, which will display an error and the user will have to click the sign in button to try again. If there was no token in the cache but there was no error searching for it, the `Handler` provided will be sent a message telling it to try to sign in with a prompt. The prompt will be created and managed by ADAL. The possible outcomes of this action are the same (and will be handled the same) as described in this step. So to progress to the full app, `authentication/AuthManager#handleSignInSuccess` must be called.
1. `authentication/AuthManager#handleSignInSuccess` registers the user's token with MAM and calls the `AuthListener#onSignedIn` callback. It registers with MAM so MAM knows to enforce any policies. It will also notify MAM that it has received a token for the user only if MAM has looked for the token in the past and not found one. If MAM needs this newly acquired token at some point in the future, it will invoke the callback provided in `TaskrApplication#onCreate`.

## Highlighted SDK Features
This project demonstrates proper integration with the MAM SDK and the [MAM-WE service](https://docs.microsoft.com/en-us/intune/app-sdk-android#app-protection-policy-without-device-enrollment). However, it does not show how to properly handle [multi-identity](https://docs.microsoft.com/en-us/intune/app-sdk-android#multi-identity-optional) protection.

The rest of this section lists most Intune App Protection Policy Settings, and what Taskr does to address that feature. Any policy not mentioned here can be assumed to be managed automatically.

### Actively Managed
- Prevent Android backups – The app enables managed backups in `AndroidManifest.xml`. More information is available [here](https://docs.microsoft.com/en-us/intune/app-sdk-android#protecting-backup-data).
- Prevent "Save As" – The save button tries to save a CSV containing all open tasks to the user's device. In `fragments/TasksFragment.java` the app manually checks if the user’s policy allows this, and notifies the user if it is not allowed. 
  - Prevent "Save As" to OneDrive - The same scheme described above is used for OneDrive.
- App configuration policies – The app displays the current configuration as an example on the About page in `fragments/AboutFragment.java`.

### Automatically Managed
The following policies are automatically managed by the SDK, with no additional development required, and demonstrated by specific features. The remaining policies not mentioned are also automatically managed, with no features in this app needed to highlight them.

- Require PIN for access – The first UI entry point of the app (`MainActivity`'s `onCreate` method) signs the user in using ADAL. But if the user is already signed in, the MAM SDK will prompt the user for a PIN before any UI code is executed, if required.
  - Allow fingerprint instead of PIN - See above.
  - Require corporate credentials for access – See above.
- Allow app to transfer data to other apps – This policy is demonstrated when the user clicks on the save button, which attempts to export a CSV containing tasks to Excel.
- Disable printing – This policy is demonstrated when the user clicks on the print button, which attempts to open the CSV in Android’s default printing view.
- Allow app to receive data from other apps – This policy is demonstrated when the app receives intents containing the text of a description to create a task.
- Restrict web content to display in the Managed Browser – This policy is demonstrated when a user clicks on a link from the about screen.
- Encrypt app data - This policy is demonstrated when the app attempts to save a CSV file. If enabled, the file will be not be plaintext.

## Relevant Files
- `authentication/AuthManager.java` handles the bulk of ADAL sign in and MAM registration. Every method is highly relevant, and should be used as an example.
- `MainActivity.java` and `TaskrApplication.java` handle a lot of mandatory registrations. `TaskrApplication` explicitly registers an authentication context with MAM, while `MainActivity` calls methods in `AuthManager` that perform relevant actions. `MainActivity#onCreate` handles the authentication flow on start-up and instantiates the `Handler` that will be used across the file.
- `AndroidManifest.xml` requests the necessary permissions and sets up the MAM SDK's backup manager.
- `fragments/TasksFragment.java` explicitly checks MAM policies to see if saving files to a user's device or OneDrive is allowed.
- `fragments/AboutFragment.java` attempts to retrieve and print the user's Application Configuration JSON object.
- `app/MAMSDK/` contains the MAM SDK binaries.