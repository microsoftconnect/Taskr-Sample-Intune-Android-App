/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Manage settings and account storage.
 */
public class AppSettings {
    private static final String SETTINGS_PATH = "com.microsoft.intune.samples.taskr.appsettings";

    private AppSettings() { }

    /**
     * Save the given account in settings.  Currently, only a single account is supported,
     * so saving an account will overwrite an existing account.
     *
     * @param appContext
     *         application Context.
     * @param account
     *         the account to save.
     */
    public static void saveAccount(@NonNull final Context appContext, @NonNull final AppAccount account) {
        final SharedPreferences prefs = getPrefs(appContext);
        account.saveToSettings(prefs);
    }

    /**
     * Reconstitute and return the saved account from settings.
     *
     * @param appContext
     *         application Context.
     *
     * @return the account, if one is saved, otherwise null.
     */
    public static AppAccount getAccount(@NonNull final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        return AppAccount.readFromSettings(prefs);
    }

    /**
     * Delete the saved account from the settings.
     *
     * @param appContext
     *         application Context.
     */
    public static void clearAccount(final Context appContext) {
        final SharedPreferences prefs = getPrefs(appContext);
        AppAccount.clearFromSettings(prefs);
    }

    private static SharedPreferences getPrefs(final Context appContext) {
        return appContext.getSharedPreferences(SETTINGS_PATH, Context.MODE_PRIVATE);
    }
}
