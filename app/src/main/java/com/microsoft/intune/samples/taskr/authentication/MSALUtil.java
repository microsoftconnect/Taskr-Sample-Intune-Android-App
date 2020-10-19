/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.authentication;

import android.app.Activity;
import android.content.Context;

import com.microsoft.identity.client.AcquireTokenParameters;
import com.microsoft.identity.client.AcquireTokenSilentParameters;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.intune.samples.taskr.R;

import java.util.Arrays;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

/**
 * A utility class for methods required by MSAL.
 */
public final class MSALUtil {

    private static final Logger LOGGER = Logger.getLogger(MSALUtil.class.getName());

    private static IPublicClientApplication mMsalClientApplication;

    private MSALUtil() { }

    /**
     * Acquire a token for the requested scopes.  Will be interactive.
     *
     * @param fromActivity
     *         the Activity from which the auth request is made.
     * @param scopes
     *         Scopes for the requested token.
     * @param loginHint
     *         a prompt for the login dialog, can be null if unused.
     * @param callback
     *         callback to receive the result of the auth attempt.
     *
     * @throws MsalException
     *         MSAL error occurred.
     * @throws InterruptedException
     *         Thread was interrupted.
     */
    @WorkerThread
    public static void acquireToken(@NonNull final Activity fromActivity, @NonNull final String[] scopes,
                                    final String loginHint, @NonNull final AuthenticationCallback callback)
            throws MsalException, InterruptedException {
        initializeMsalClientApplication(fromActivity.getApplicationContext());

        AcquireTokenParameters params = new AcquireTokenParameters.Builder()
                .withScopes(Arrays.asList(scopes))
                .withCallback(callback)
                .startAuthorizationFromActivity(fromActivity)
                .withLoginHint(loginHint)
                .build();

        mMsalClientApplication.acquireToken(params);
    }

    /**
     * Acquire a token for the requested scopes.  Will not be interactive.
     *
     * @param appContext
     *         A Context used to initialize the MSAL context, if needed.
     * @param aadId
     *         Id of the user.
     * @param scopes
     *         Scopes for the requested token.
     * @param callback
     *         callback to receive the result of the auth attempt.
     *
     * @throws MsalException
     *         MSAL error occurred.
     * @throws InterruptedException
     *         Thread was interrupted.
     */
    @WorkerThread
    public static void acquireTokenSilent(@NonNull final Context appContext, @NonNull final String aadId, @NonNull final String[] scopes,
                                          @NonNull final AuthenticationCallback callback)
            throws MsalException, InterruptedException {
        initializeMsalClientApplication(appContext.getApplicationContext());

        final IAccount account = getAccount(aadId);
        if (account == null) {
            LOGGER.severe("Failed to acquire token: no account found for " + aadId);
            callback.onError(
                    new MsalUiRequiredException(MsalUiRequiredException.NO_ACCOUNT_FOUND, "no account found for " + aadId));
            return;
        }

        AcquireTokenSilentParameters params = new AcquireTokenSilentParameters.Builder()
                .forAccount(account)
                .fromAuthority(account.getAuthority())
                .withScopes(Arrays.asList(scopes))
                .withCallback(callback)
                .build();

        mMsalClientApplication.acquireTokenSilentAsync(params);
    }

    /**
     * Acquire a token silently for the given resource and user.  This is synchronous.
     *
     * @param appContext
     *         the application context.
     * @param aadId
     *         Id of the user.
     * @param scopes
     *         Scopes to request the token for.
     *
     * @return the authentication result, or null if it fails.
     *
     * @throws MsalException
     *         MSAL error occurred.
     * @throws InterruptedException
     *         Thread was interrupted.
     */
    @WorkerThread
    public static IAuthenticationResult acquireTokenSilentSync(@NonNull final Context appContext, @NonNull final String aadId, @NonNull final String[] scopes)
            throws MsalException, InterruptedException {

        initializeMsalClientApplication(appContext);
        final IAccount account = getAccount(aadId);
        if (account == null) {
            LOGGER.severe("Failed to acquire token: no account found for " + aadId);
            throw new MsalUiRequiredException(MsalUiRequiredException.NO_ACCOUNT_FOUND, "no account found for " + aadId);
        }

        AcquireTokenSilentParameters params =
                new AcquireTokenSilentParameters.Builder()
                        .forAccount(account)
                        .fromAuthority(account.getAuthority())
                        .withScopes(Arrays.asList(scopes))
                        .build();

        return mMsalClientApplication.acquireTokenSilent(params);
    }

    /**
     * Sign out the given account from MSAL.
     *
     * @param appContext
     *         the application context.
     * @param aadId
     *         Id of the user.
     *
     * @throws MsalException
     *         MSAL error occurred.
     * @throws InterruptedException
     *         Thread was interrupted.
     */
    public static void signOutAccount(@NonNull final Context appContext, @NonNull final String aadId) throws MsalException, InterruptedException {

        initializeMsalClientApplication(appContext);
        final IAccount account = getAccount(aadId);

        if (account == null) {
            LOGGER.warning("Failed to sign out account: No account found for " + aadId);
            return;
        }

        if (mMsalClientApplication instanceof IMultipleAccountPublicClientApplication) {
            IMultipleAccountPublicClientApplication multiAccountPCA =
                    (IMultipleAccountPublicClientApplication) mMsalClientApplication;

            multiAccountPCA.removeAccount(account);
        } else {
            ISingleAccountPublicClientApplication singleAccountPCA =
                    (ISingleAccountPublicClientApplication) mMsalClientApplication;

            singleAccountPCA.signOut();
        }
    }

    private static IAccount getAccount(String aadId) throws InterruptedException, MsalException {
        IAccount account = null;

        if (mMsalClientApplication instanceof IMultipleAccountPublicClientApplication) {
            IMultipleAccountPublicClientApplication multiAccountPCA =
                    (IMultipleAccountPublicClientApplication) mMsalClientApplication;

            account = multiAccountPCA.getAccount(aadId);
        } else {
            ISingleAccountPublicClientApplication singleAccountPCA =
                    (ISingleAccountPublicClientApplication) mMsalClientApplication;

            ICurrentAccountResult accountResult = singleAccountPCA.getCurrentAccount();
            if (accountResult != null) {
                account = accountResult.getCurrentAccount();
                // make sure this is the correct user
                if (account != null && !account.getId().equals(aadId))
                    account = null;
            }
        }
        return account;
    }

    private static synchronized void initializeMsalClientApplication(final Context appContext)
            throws MsalException, InterruptedException {
        if (mMsalClientApplication == null) {
            com.microsoft.identity.client.Logger msalLogger
                    = com.microsoft.identity.client.Logger.getInstance();
            msalLogger.setEnableLogcatLog(true);
            msalLogger.setLogLevel(com.microsoft.identity.client.Logger.LogLevel.VERBOSE);
            msalLogger.setEnablePII(true);

            mMsalClientApplication = PublicClientApplication.create(appContext, R.raw.auth_config);
        }
    }
}
