# Taskr - A [Microsoft Intune](https://www.microsoft.com/en-us/cloud-platform/microsoft-intune) Android MAM SDK Example

| MAM SDK Version | MSAL Version |
|-|-|
| 6.7.0 | 1.5.5 |

This project is a demonstration of the [Microsoft Intune SDK for Android] and contains examples
from the [SDK Guide], which is available to provide additional developer guidance.

## Important Notes Before Starting

### Configuring an Intune Subscription

- A tenant is necessary for the configuration of an Intune subscription. A free trial is sufficient for this demo and can be registered for at [Microsoft's demo site].
- Once a tenant is acquired the Intune subscription will need to be properly configured to target the user and the application. This can be accomplished by following the steps to [Set up Intune].

### Configuring App for MSAL Authentication

This sample features an [MSAL] integration to highlight MAM functionality, see [About the code]
for more information regarding MSAL.
The purpose of registering with MSAL is to acquire a unique client ID, redirect URI, and signature hash for your application.

- Perform the app registration and configuration steps by following the [Register Your Own Application] steps for an MSAL application.
- Update the MSAL values in this sample with those called out by [Using MSAL].
- Replace the included `auth-config` JSON file with the configuration for your app.

### Grant App Permission to MAM Service

- You will need to [grant your app permissions] to the Intune Mobile Application Management (MAM) service.

## Highlighted SDK Features

:warning: For policy to be applied to the application, the user will need to sign in and authenticate with [MSAL].

This project demonstrates proper integration with the MAM SDK and the [APP service] for
a *single-identity* application.

If your application is a [multi-identity application], please refer to the [multi-identity application integration guide] for the necessary modifications.

### Managed via App Participation

The following policies require app participation in order to be properly enforced.

A full breakdown of policies requiring app participation can be found in the
"[Enable features that require app participation]" section of the SDK guide.

- Prevent Android backups – The app enables managed backups in `AndroidManifest.xml`. More information is available [here](https://docs.microsoft.com/en-us/intune/app-sdk-android#protecting-backup-data).
- Prevent "Save As":
  - To User's Device - To determine if saving to the device is allowed, the app manually checks the user's policy in `fragments/TasksFragment.java`. If allowed, the save button will save a CSV containing all open tasks to the user's device. Otherwise, a notification will be displayed to the user.
- App configuration policies – The app displays the current configuration as an example on the About page in `fragments/AboutFragment.java`.

### Managed by the SDK

The following policies are automatically managed by the SDK without explicit app involvement and require no additional development.

- Require PIN for access – The MAM SDK will prompt the user for a PIN before any UI code is executed, if required by policy.
  - Allow fingerprint instead of PIN - See above.
  - Require corporate credentials for access – See above.
- Allow app to transfer data to other apps – This policy is demonstrated when the user clicks on the save button, which attempts to export a CSV containing tasks to Excel.
- Disable printing – This policy is demonstrated when the user clicks on the print button, which attempts to open the CSV in Android’s default printing view.
- Allow app to receive data from other apps – This policy is demonstrated when the app receives intents containing the text of a description to create a task.
- Restrict web content to display in the [Managed Browser](https://docs.microsoft.com/en-us/intune/app-configuration-managed-browser) – This policy is demonstrated when a user clicks on a link from the About screen.
- Encrypt app data - This policy is demonstrated when the app attempts to save a CSV file. If enabled, the file will be encrypted on disk.

## About the code

### MSAL Integration and the MAM Token

### AndroidManifest

The AndroidManifest contains the BrowserTabActivity that is required for proper MSAL integration.

``` xml
<!-- Must be specified to allow users to login via MSAL -->
<activity android:name="com.microsoft.identity.client.BrowserTabActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!--
            Add in your scheme/host from registered redirect URI
            note that the leading "/" is required for android:path
        -->
        <data
            android:host="com.intune.samples.taskr"
            android:path="/SignatureHash"
            android:scheme="msauth" />
    </intent-filter>
</activity>
```

The `SignatureHash` will need to be replaced with the MSAL
registration values for your application.

There is no specific MAM code alteration required for the `BrowserTabActivity`.

### MSALUtil class

The MSALUtil class serves as a utility for accessing the required MSAL integration code.

### AuthenticationCallback class

The AuthenticationCallback class is registered in TaskrApplication and handles acquiring the
MAM token.

**You must register this callback in order to receive a token for MAM. Without this, full MAM integration is not achievable.**

#### Callback Registration

``` java
// Registers a MAMServiceAuthenticationCallback, which will try to acquire access tokens for MAM.
// This is necessary for proper MAM integration.
MAMEnrollmentManager mgr = MAMComponents.get(MAMEnrollmentManager.class);
mgr.registerAuthenticationCallback(new AuthenticationCallback(getApplicationContext()));
```

This callback should be registered as early as possible in the `onCreate` method of your application.

#### Callback Implementation

``` java
@Nullable
@Override
public String acquireToken(@NonNull final String upn, @NonNull final String aadId, @NonNull final String resourceId) {
    try {
        // Create the MSAL scopes by using the default scope of the passed in resource id.
        final String[] scopes = {resourceId + "/.default"};
        final IAuthenticationResult result = MSALUtil.acquireTokenSilentSync(mContext, aadId, scopes);
        if (result != null)
            return result.getAccessToken();
    } catch (MsalException | InterruptedException e) {
        LOGGER.log(Level.SEVERE, "Failed to get token for MAM Service", e);
        return null;
    }

    LOGGER.warning("Failed to get token for MAM Service - no result from MSAL");
    return null;
}
```

As is noted by the comments, the resource ID that is passed to the `acquireToken` method should be
used to construct the proper scopes for the MAM token.

## Policy Enforcement

### AndroidManifest xml

The AndroidManifest file models how to utilize the MAM SDK's backup manager to block and encrypt backups, if specified by policy.

``` xml
<!-- The backupAgent here is provided by the MAM SDK. It will block/encrypt backups if necessary. -->
<application
    android:allowBackup="true"
    android:fullBackupOnly="true"
    android:fullBackupContent="true"
    android:backupAgent="com.microsoft.intune.mam.client.app.backup.MAMDefaultBackupAgent"
    ...
```

### AboutFragment class

The `AccountFragment` class models how to retrieve the app config from the MAM SDK.

``` java
String currentUser = AppSettings.getAccount(this.getContext()).getAADID();
MAMAppConfigManager configManager = MAMComponents.get(MAMAppConfigManager.class);
MAMAppConfig appConfig = configManager.getAppConfig(currentUser);
```

### SaveFragment class

The `SaveFragment` class models how to check data transfer policy for saving data to local storage.

``` java
  String currentUser = AppSettings.getAccount(view.getContext()).getAADID();

  if (MAMPolicyManager.getPolicy(getActivity())
          .getIsSaveToLocationAllowed(SaveLocation.LOCAL, currentUser)) {
            ...
```

<!-- Links -->
[Microsoft's demo site]: https://demos.microsoft.com

<!-- Intune -->
[Set up Intune]: https://docs.microsoft.com/en-us/intune/setup-steps

<!-- AAD -->
[Register Your Own Application]: https://github.com/Azure-Samples/ms-identity-android-java#register-your-own-application-optional

<!-- MAM Service -->
[grant your app permissions]: https://docs.microsoft.com/en-us/intune/app-sdk-get-started#give-your-app-access-to-the-intune-app-protection-service-optional
[APP service]: https://docs.microsoft.com/en-us/intune/app-sdk-android#app-protection-policy-without-device-enrollment

<!-- MAM SDK -->
[Microsoft Intune SDK for Android]: https://docs.microsoft.com/en-us/intune/app-sdk
[SDK Guide]: https://docs.microsoft.com/en-us/mem/intune/developer/app-sdk-android
[Enable features that require app participation]: https://docs.microsoft.com/en-us/mem/intune/developer/app-sdk-android#enable-features-that-require-app-participation

<!-- MAM Multi-Identity -->
[multi-identity application]: https://docs.microsoft.com/en-us/intune/app-sdk-android#multi-identity-optional
[multi-identity application integration guide]: https://docs.microsoft.com/en-us/intune/app-sdk-android#enabling-multi-identity

<!-- MSAL -->
[MSAL]: https://github.com/AzureAD/microsoft-authentication-library-for-android
[About the code]: https://github.com/Azure-Samples/ms-identity-android-java#about-the-code
[Using MSAL]: https://github.com/AzureAD/microsoft-authentication-library-for-android#using-msal
