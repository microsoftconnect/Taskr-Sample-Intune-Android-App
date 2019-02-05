# Taskr - A [Microsoft Intune](https://www.microsoft.com/en-us/cloud-platform/microsoft-intune) Android MAM SDK Example
This project is a demonstration of the [Microsoft Intune SDK for Android](https://docs.microsoft.com/en-us/intune/app-sdk). A developer guide to the SDK is available [here](https://docs.microsoft.com/en-us/intune/app-sdk-android). This project implements some commonly used features so developers making their own apps have an example to follow. IT administrators who want to create apps with similar functionality can even use this as a template.

Taskr allows users to keep a list of to-do items, or tasks. Users can view their open tasks and mark tasks as complete, print them, or save them to their phone. Tasks are kept in a database implemented using the [Android Room persistence library](https://developer.android.com/topic/libraries/architecture/room). Users' actions are managed by policy, so not all actions may be available.

## Important Notes Before Starting
### Configuring an Intune Subscription
- A tenant is necessary for the configuration of an Intune subscription. A free trial is sufficient for this demo and can be registered for at [Microsoft's demo site](https://demos.microsoft.com).
- Once a tenant is acquired the Intune subscription will need to be properly configured to target the user and the application. Follow the set up steps found [here](https://docs.microsoft.com/en-us/intune/setup-steps).
### Configuring App for ADAL Authentication
- Perform the app registration and configuration steps found [here](https://github.com/Azure-Samples/active-directory-android#register--configure-your-app). 
  - The purpose of registering with ADAL is to acquire a client ID and redirect URI for your application. Once you have registered your app, replace `AuthManager#CLIENT_ID` with the client ID and `AuthManager#REDIRECT_URI` with the redirect URI.
### Grant App Permission to MAM Service
- You will need to [grant your app permissions](https://docs.microsoft.com/en-us/intune/app-sdk-get-started#give-your-app-access-to-the-intune-app-protection-service-optional) to the Intune Mobile Application Management (MAM) service.

### Authentication Flow
1. When the user first launches the app, before `TaskrApplication#onCreate` is called, the MAM SDK will initialize itself and check if the user is signed in and needs to provide a PIN. If they do, it will open a PIN screen.
1. Once this process has completed (the user provides a PIN, a PIN is not required, or the user is not signed in), `TaskrApplication#onCreate` registers an AuthenticationCallback with MAM. This will be called by the MAM SDK when it needs to acquire an access token.
1. Next, `MainActivity#onCreate` will be called. This checks if the user is already authenticated in a recently run state of the app, and signs them in if they are. If not, it will try to silently acquire an access token from ADAL's cache by calling `authentication/AuthManager#signInSilent`.
1. If a token is still in the cache and valid, `authentication/AuthManager#handleSignInSuccess` will be called. If it is not and there was an error, the error callback provided will be called, which will display an error and the user will have to click the sign in button to try again. If there was no token in the cache but there was no error searching for it, the `Handler` provided will be sent a message telling it to try to sign in with a prompt. The prompt will be created and managed by ADAL. The possible outcomes of this action are the same (and will be handled the same) as described in this step. So to progress to the full app, `authentication/AuthManager#handleSignInSuccess` must be called.
1. `authentication/AuthManager#handleSignInSuccess` registers the user's token with MAM and calls the `AuthListener#onSignedIn` callback. It registers with MAM so MAM knows to enforce any policies. It will also notify MAM that it has received a token for the user only if MAM has looked for the token in the past and not found one. If MAM needs this newly acquired token at some point in the future, it will invoke the callback provided in `TaskrApplication#onCreate`.

## Highlighted SDK Features
This project demonstrates proper integration with the MAM SDK and the [MAM-WE service](https://docs.microsoft.com/en-us/intune/app-sdk-android#app-protection-policy-without-device-enrollment). However, it does not show how to properly handle [multi-identity](https://docs.microsoft.com/en-us/intune/app-sdk-android#multi-identity-optional) protection. If your application needs to be multi-identity aware please refer to the [implementation documentation](https://docs.microsoft.com/en-us/intune/app-sdk-android#enabling-multi-identity).

__! NOTE__ For policy to be applied to the application, the user will need to sign in and authenticate with ADAL. 

### Actively Managed
The following policies require explicit app involvement in order to be properly enforced. 

- Prevent Android backups – The app enables managed backups in `AndroidManifest.xml`. More information is available [here](https://docs.microsoft.com/en-us/intune/app-sdk-android#protecting-backup-data).
- Prevent "Save As":
  - To User's Device - To determine if saving to the device is allowed, the app manually checks the user's policy in `fragments/TasksFragment.java`. If allowed, the save button will save a CSV containing all open tasks to the user's device. Otherwise, a notification will be displayed to the user.
- App configuration policies – The app displays the current configuration as an example on the About page in `fragments/AboutFragment.java`.

### Automatically Managed
The following policies are automatically managed by the SDK without explicit app involvement and require no additional development.

- Require PIN for access – The MAM SDK will prompt the user for a PIN before any UI code is executed, if required by policy.
  - Allow fingerprint instead of PIN - See above.
  - Require corporate credentials for access – See above.
- Allow app to transfer data to other apps – This policy is demonstrated when the user clicks on the save button, which attempts to export a CSV containing tasks to Excel.
- Disable printing – This policy is demonstrated when the user clicks on the print button, which attempts to open the CSV in Android’s default printing view.
- Allow app to receive data from other apps – This policy is demonstrated when the app receives intents containing the text of a description to create a task.
- Restrict web content to display in the [Managed Browser](https://docs.microsoft.com/en-us/intune/app-configuration-managed-browser) – This policy is demonstrated when a user clicks on a link from the About screen.
- Encrypt app data - This policy is demonstrated when the app attempts to save a CSV file. If enabled, the file will be encrypted on disk.

## Relevant Files
- `authentication/AuthManager.java` handles the bulk of ADAL sign in and MAM registration. Every method is highly relevant, and should be used as an example.
- `MainActivity.java` and `TaskrApplication.java` handle a lot of mandatory registrations. `TaskrApplication` explicitly registers an authentication context with MAM, while `MainActivity` calls methods in `AuthManager` that perform relevant actions. `MainActivity#onCreate` handles the authentication flow on start-up and instantiates the `Handler` that will be used across the file.
- `AndroidManifest.xml` requests the necessary permissions and sets up the MAM SDK's backup manager.
- `fragments/TasksFragment.java` explicitly checks MAM policies to see if saving files to a user's device is allowed.
- `fragments/AboutFragment.java` attempts to retrieve and print the user's Application Configuration JSON object.
- `app/MAMSDK/` contains the MAM SDK binaries.

## Troubleshooting
- Do not use "Instant Run" in Android Studio when developing with the MAM SDK build plugin. It will result in build issues.