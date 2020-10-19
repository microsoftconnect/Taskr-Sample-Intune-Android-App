/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.custom_modules;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfig;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfigManager;

import com.microsoft.intune.samples.taskr.R;
import com.microsoft.intune.samples.taskr.authentication.AuthManager;


/**
 * NativeModule for Accessing MAM-Related Actions like Config and Login
 */
public class MAMModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    // Unique Code for Config / Info Related Errors
    private static final String E_ABOUT = "E_ABOUT";

    MAMModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "CustomMAM";
    }

    /**
     * Get Targeted Application Configuration as JSON String
     * @param promise JavaScript Promise Adapter to communicate saving status to calling JS code
     */
    @ReactMethod
    public void getMAMConfig(Promise promise) {
        MAMAppConfigManager configManager = MAMComponents.get(MAMAppConfigManager.class);

        if (configManager == null) {
            promise.reject(E_ABOUT, reactContext.getString(R.string.err_unset));
            return;
        }

        // Return All MAM Config as JSON
        MAMAppConfig appConfig = configManager.getAppConfig(AuthManager.getUser());
        promise.resolve(appConfig.getFullData().toString());
    }

    /**
     * Sign Out of Microsoft MAM Account and Move to Login Screen
     */
    @ReactMethod
    public void signOut() {
        AuthManager.signOut();
        reactContext.getCurrentActivity().finish();
    }
}
