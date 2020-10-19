/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.authentication;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Represents an account that is signed in to the app.
 */
public class AppAccount {
    private final String mUPN;
    private final String mAADID;
    private final String mTenantID;
    private final String mAuthority;

    private static final String UPN_KEY = "mamsampleappaccount.upn";
    private static final String AADID_KEY = "mamsampleappaccount.aadid";
    private static final String TENANTID_KEY = "mamsampleappaccount.tenantid";
    private static final String AUTHORITY_KEY = "mamsampleappaccount.authority";


    public AppAccount(@NonNull final String upn, @NonNull final String aadid,
                          @NonNull final String tenantid, @NonNull final String authority) {
        this.mUPN = upn;
        this.mAADID = aadid;
        this.mTenantID = tenantid;
        this.mAuthority = authority;
    }

    /**
     * Get the UPN.
     *
     * @return the UPN.
     */
    public String getUPN() {
        return mUPN;
    }

    /**
     * Get the account ID.
     *
     * @return the account ID.
     */
    public String getAADID() {
        return mAADID;
    }

    /**
     * Get the tenant ID.
     *
     * @return the tenant ID.
     */
    public String getTenantID() {
        return mTenantID;
    }

    /**
     * Get the Authority used to sign in the account.
     *
     * @return the Authority.
     */
    public String getAuthority() {
        return mAuthority;
    }

    /**
     * The Account should save itself to the provided SharedPreferences object.
     *
     * @param sharedPref
     *         the preferences where the account should be written.
     */
    public void saveToSettings(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UPN_KEY, mUPN);
        editor.putString(AADID_KEY, mAADID);
        editor.putString(TENANTID_KEY, mTenantID);
        editor.putString(AUTHORITY_KEY, mAuthority);
        editor.apply();
    }

    /**
     * Reconstitute the account object from the provided settings, where it was
     * previously saved.
     *
     * @param sharedPref
     *         the preferences.
     *
     * @return the reconstituted account object, or null if insufficient data was
     * found in the settings.
     */
    public static AppAccount readFromSettings(final SharedPreferences sharedPref) {
        final String upn = sharedPref.getString(UPN_KEY, null);
        if (upn == null)
            return null;

        final String aadid = sharedPref.getString(AADID_KEY, null);
        if (aadid == null)
            return null;

        final String tenantid = sharedPref.getString(TENANTID_KEY, null);
        if (tenantid == null)
            return null;

        final String authority = sharedPref.getString(AUTHORITY_KEY, null);
        if (authority == null)
            return null;

        return new AppAccount(upn, aadid, tenantid, authority);
    }

    /**
     * Clear the saved account data from the provided settings object.
     *
     * @param sharedPref
     *         the settings from which the account data should be cleared.
     */
    public static void clearFromSettings(final SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(UPN_KEY);
        editor.remove(AADID_KEY);
        editor.remove(TENANTID_KEY);
        editor.remove(AUTHORITY_KEY);
        editor.apply();
    }
}
