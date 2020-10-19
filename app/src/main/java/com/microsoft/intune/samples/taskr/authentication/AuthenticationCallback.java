/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.authentication;

import android.content.Context;

import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Implementation of the required callback for MAM integration.
 */
public class AuthenticationCallback implements MAMServiceAuthenticationCallback {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationCallback.class.getName());

    private final Context mContext;

    public AuthenticationCallback(@NonNull final Context context) {
        this.mContext = context.getApplicationContext();
    }

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
}
