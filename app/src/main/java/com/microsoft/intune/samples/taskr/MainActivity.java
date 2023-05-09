/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalIntuneAppProtectionPolicyRequiredException;
import com.microsoft.identity.client.exception.MsalUserCancelException;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.samples.taskr.authentication.AppAccount;
import com.microsoft.intune.samples.taskr.authentication.AppSettings;
import com.microsoft.intune.samples.taskr.authentication.MSALUtil;
import com.microsoft.intune.samples.taskr.fragments.AboutFragment;
import com.microsoft.intune.samples.taskr.fragments.TasksFragment;
import com.microsoft.intune.samples.taskr.fragments.SubmitFragment;
import com.microsoft.intune.samples.taskr.trustedroots.ui.TrustedRootsFragment;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main activity of the app - runs when the app starts.
 *
 * Handles authentication, explicitly interacting with MSAL and implicitly with MAM.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private AppAccount mUserAccount;
    private MAMEnrollmentManager mEnrollmentManager;

    public static final String[] MSAL_SCOPES = {"https://graph.microsoft.com/User.Read"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEnrollmentManager = MAMComponents.get(MAMEnrollmentManager.class);

        // Get the account info from the app settings.
        // If a user is not signed in, the account will be null.
        mUserAccount = AppSettings.getAccount(getApplicationContext());

        if (mUserAccount == null) {
            displaySignInView();
        } else {
            displayMainView();
        }
    }

    private void displaySignInView() {
        setContentView(R.layout.sign_in);
    }

    private void displayMainView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        changeNavigationView(R.id.nav_submit);
        Toast.makeText(this, R.string.auth_success, Toast.LENGTH_SHORT).show();
    }

    public void onClickSignIn(final View view) {
        // initiate the MSAL authentication on a background thread
        Thread thread = new Thread(() -> {
            LOGGER.info("Starting interactive auth");

            try {
                String loginHint = null;
                if (mUserAccount != null) {
                    loginHint = mUserAccount.getUPN();
                }
                MSALUtil.acquireToken(MainActivity.this, MSAL_SCOPES, loginHint, new AuthCallback());
            } catch (MsalException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, getString(R.string.err_auth), e);
                showMessage("Authentication exception occurred - check logcat for more details.");
            }
        });
        thread.start();
    }

    private void signOutUser() {
        // Initiate an MSAL sign out on a background thread.
        final AppAccount effectiveAccount = mUserAccount;

        Thread thread = new Thread(() -> {
            try {
                MSALUtil.signOutAccount(this, effectiveAccount.getAADID());
            } catch (MsalException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Failed to sign out user " + effectiveAccount.getAADID(), e);
            }

            mEnrollmentManager.unregisterAccountForMAM(effectiveAccount.getUPN(), effectiveAccount.getAADID());
            AppSettings.clearAccount(getApplicationContext());
            mUserAccount = null;

            runOnUiThread(this::displaySignInView);
        });
        thread.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        return changeNavigationView(item.getItemId());
    }

    /**
     * Changes the sidebar view of the app. Used when a user clicks on a menu item or to
     * manually change the view
     *
     * @param id the id of the fragment that should be displayed
     */
    private boolean changeNavigationView(final int id) {
        Fragment frag = null;

        switch (id) {
            case R.id.nav_tasks:
                frag = new TasksFragment();
                break;
            case R.id.nav_about:
                frag = new AboutFragment();
                break;
            case R.id.nav_trusted_roots:
                frag = new TrustedRootsFragment();
                break;
            case R.id.nav_sign_out:
                signOutUser();
                break;
            default: // If we don't recognize the id, go to the default (submit) rather than crashing
            case R.id.nav_submit:
                frag = new SubmitFragment();
                break;
        }

        boolean didChangeView = frag != null;
        if (didChangeView) {
            try {
                // Display the fragment
                FragmentManager fragManager = getSupportFragmentManager();
                fragManager.beginTransaction().replace(R.id.flContent, frag).commit();
            } catch (NullPointerException e) {
                e.printStackTrace();
                didChangeView = false;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return didChangeView;
    }

    private void showMessage(final String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private class AuthCallback implements AuthenticationCallback {
        @Override
        public void onError(final MsalException exc) {
            LOGGER.log(Level.SEVERE, "authentication failed", exc);

            if (exc instanceof MsalIntuneAppProtectionPolicyRequiredException) {
                MsalIntuneAppProtectionPolicyRequiredException appException = (MsalIntuneAppProtectionPolicyRequiredException) exc;

                // Note: An app that has enabled APP CA with Policy Assurance would need to pass these values to `remediateCompliance`.
                // For more information, see https://docs.microsoft.com/en-us/mem/intune/developer/app-sdk-android#app-ca-with-policy-assurance
                final String upn = appException.getAccountUpn();
                final String aadid = appException.getAccountUserId();
                final String tenantId = appException.getTenantId();
                final String authorityURL = appException.getAuthorityUrl();

                // The user cannot be considered "signed in" at this point, so don't save it to the settings.
                mUserAccount = new AppAccount(upn, aadid, tenantId, authorityURL);

                final String message = "Intune App Protection Policy required.";
                showMessage(message);

                LOGGER.info("MsalIntuneAppProtectionPolicyRequiredException received.");
                LOGGER.info(String.format("Data from broker: UPN: %s; AAD ID: %s; Tenant ID: %s; Authority: %s",
                        upn, aadid, tenantId, authorityURL));
            } else if (exc instanceof MsalUserCancelException) {
                showMessage("User cancelled sign-in request");
            } else {
                showMessage("Exception occurred - check logcat");
            }
        }

        @Override
        public void onSuccess(final IAuthenticationResult result) {
            IAccount account = result.getAccount();

            final String upn = account.getUsername();
            final String aadId = account.getId();
            final String tenantId = account.getTenantId();
            final String authorityURL = account.getAuthority();

            String message = "Authentication succeeded for user " + upn;
            LOGGER.info(message);

            // Save the user account in the settings, since the user is now "signed in".
            mUserAccount = new AppAccount(upn, aadId, tenantId, authorityURL);
            AppSettings.saveAccount(getApplicationContext(), mUserAccount);

            // Register the account for MAM.
            mEnrollmentManager.registerAccountForMAM(upn, aadId, tenantId, authorityURL);

            displayMainView();
        }

        @Override
        public void onCancel() {
            showMessage("User cancelled auth attempt");
        }
    }
}
