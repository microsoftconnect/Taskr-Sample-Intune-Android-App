/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.custom_modules;

import android.os.Environment;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.microsoft.intune.mam.client.identity.MAMPolicyManager;
import com.microsoft.intune.mam.policy.SaveLocation;

import com.microsoft.intune.samples.taskr.R;
import com.microsoft.intune.samples.taskr.authentication.AuthManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * NativeModule for Saving any String into a Specified File
 * First manually checks MAM policy to see if saving to disk is allowed
 * Communicates with Android storage libraries to write file to Documents folder
 * NOTE: If the user's policy encrypt files, the output will only be readable by other managed apps
 */
public class SaveModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    // Unique Code for Saving Related Errors
    private static final String E_SAVING = "E_SAVING";

    SaveModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "CustomSave";
    }

    /**
     * Saves Specified Text to a New File in Documents if Allowed by MAM Policy
     * @param text String to text to save to a file
     * @param fileName Name of new file to create to store text
     * @param promise JavaScript Promise Adapter to communicate saving status to calling JS code
     */
    @ReactMethod
    public void saveString(String text, String fileName, Promise promise) {
        // Check MAM Policy if Saving is Allowed to Disk
        if (!MAMPolicyManager.getPolicy(reactContext)
            .getIsSaveToLocationAllowed(SaveLocation.LOCAL, AuthManager.getUser())) {
            promise.reject(E_SAVING, reactContext.getString(R.string.err_not_allowed));
            return;
        }

        // Checks and Creates Document Folder if Nonexistent
        File docDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!docDir.exists() && !docDir.mkdirs())
            promise.reject(E_SAVING, reactContext.getString(R.string.err_no_folder));

        // Write CSV Text to Disk
        File file = new File(docDir, fileName);
        try (final FileWriter writer = new FileWriter(file)) {
            writer.write(text);
            writer.flush();
            promise.resolve(null);
        } catch (IOException e) {
            promise.reject(E_SAVING, e);
        }
    }
}
