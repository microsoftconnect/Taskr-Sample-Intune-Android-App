package com.microsoft.intune.samples.taskr;

import android.app.Application;
import android.util.Log;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;

import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.Logger;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification;
import com.microsoft.intune.mam.policy.notification.MAMNotificationType;

import com.microsoft.intune.samples.taskr.authentication.AuthManager;
import com.microsoft.intune.samples.taskr.custom_modules.CustomPackage;

import java.util.List;

/**
 * Main Application Class for the React Native App
 * Handles basic init and config for React Native as well as loading custom packages
 * Also initializes MAM SDK by registering with MAM and starting MAM Auth via ADAL
 */
public class MainApplication extends Application implements ReactApplication {

    private final ReactNativeHost mReactNativeHost =
        new ReactNativeHost(this) {
            @Override
            public boolean getUseDeveloperSupport() {
                return BuildConfig.DEBUG;
            }

            @Override
            protected List<ReactPackage> getPackages() {
                List<ReactPackage> packages = new PackageList(this).getPackages();
                packages.add(new CustomPackage());
                return packages;
            }

            @Override
            protected String getJSMainModuleName() {
                return "index";
            }
        };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);

        // Registers a MAMAuthenticationCallback, which will try to acquire access tokens for MAM
        MAMEnrollmentManager mgr = MAMComponents.get(MAMEnrollmentManager.class);
        AuthenticationContext authContext =
            new AuthenticationContext(getApplicationContext(), AuthManager.AUTHORITY, true);
        mgr.registerAuthenticationCallback(
            (final String upn, final String aadId, final String resourceId) ->
                AuthManager.getAccessTokenForMAM(authContext, this, upn, aadId, resourceId));

        /* This section shows how to register a MAMNotificationReceiver, so you can perform custom
         * actions if MAM requests certain things. More information is available here:
         * https://docs.microsoft.com/en-us/intune/app-sdk-android#types-of-notifications */
        MAMComponents.get(MAMNotificationReceiverRegistry.class).registerReceiver(notification -> {
            if (notification instanceof MAMEnrollmentNotification) {
                MAMEnrollmentManager.Result result =
                    ((MAMEnrollmentNotification) notification).getEnrollmentResult();
                switch (result) {
                    case AUTHORIZATION_NEEDED:
                    case NOT_LICENSED:
                    case ENROLLMENT_SUCCEEDED:
                    case ENROLLMENT_FAILED:
                    case WRONG_USER:
                    case UNENROLLMENT_SUCCEEDED:
                    case UNENROLLMENT_FAILED:
                    case PENDING:
                    case COMPANY_PORTAL_REQUIRED:
                    default:
                        Log.d("Enrollment Receiver", result.name());
                        break;
                }
            } else {
                Log.d("Enrollment Receiver", "Unexpected notification type received");
            }
            return true;
        }, MAMNotificationType.MAM_ENROLLMENT_RESULT);

        /* ADAL logging is enabled in the app by default for troubleshooting purposes.
         * More information is available here:
         * https://github.com/AzureAD/azure-activedirectory-library-for-android/#logs */
        Logger.getInstance().setAndroidLogEnabled(true);
    }
}
