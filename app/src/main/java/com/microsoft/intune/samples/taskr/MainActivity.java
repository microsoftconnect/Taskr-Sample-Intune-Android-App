/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.PromptBehavior;
import com.microsoft.intune.samples.taskr.authentication.AuthListener;
import com.microsoft.intune.samples.taskr.fragments.AboutFragment;
import com.microsoft.intune.samples.taskr.fragments.TasksFragment;
import com.microsoft.intune.samples.taskr.fragments.SubmitFragment;
import com.microsoft.intune.samples.taskr.authentication.AuthManager;
import com.microsoft.intune.samples.taskr.room.RoomManager;

/**
 * The main activity of the app - runs when the app starts.
 *
 * Handles authentication, explicitly interacting with ADAL and implicitly with MAM.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AuthListener {
    private Handler mHandler;
    private AuthenticationContext mAuthContext;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* If the app has already started, the user has signed in, and the activity was just restarted,
         * skip the rest of this initialization and open the main UI */
        if (savedInstanceState != null && AuthManager.shouldRestoreSignIn(savedInstanceState)) {
            onSignedIn();
            return;
        }

        // Start by making a sign in window to show instead of the main view
        openSignInView();

        mAuthContext = new AuthenticationContext(this, AuthManager.AUTHORITY, true);
        // Will make sign in attempts that are allowed to access/modify the UI (prompt)
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == AuthManager.MSG_PROMPT_AUTO) {
                    AuthManager.signInWithPrompt(mAuthContext, MainActivity.this,
                            MainActivity.this, PromptBehavior.Auto, mHandler);
                } else if (msg.what == AuthManager.MSG_PROMPT_ALWAYS) {
                    AuthManager.signInWithPrompt(mAuthContext, MainActivity.this,
                            MainActivity.this, PromptBehavior.Always, mHandler);
                }
            }
        };

        /* We only need to change/set the view and sign in if this is the first time the app
         * has opened, which is when savedInstanceState is null */
        if (savedInstanceState == null) {
            AuthManager.signInSilent(mAuthContext, this, mHandler);
        }
    }

    private void openMainView() {
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

    private void openSignInView() {
        setContentView(R.layout.sign_in);
        findViewById(R.id.sign_in_button).setOnClickListener(signInListener);
    }

    private final View.OnClickListener signInListener = (View view) ->
            mHandler.sendEmptyMessage(AuthManager.MSG_PROMPT_ALWAYS);

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

    @Override
    public void onSignedIn() {
        // Must be run on the UI thread because it is modifying the UI
        runOnUiThread(this::openMainView);
    }

    /**
     * Called when the user signs out, puts up a blocker window and deletes the database. In a real
     * LOB app the tasks would have been forwarded to a server and wouldn't be stored locally
     * anyway, so they are cleared from the cache here to prevent data leaks from user to user.
     */
    @Override
    public void onSignedOut() {
        Toast.makeText(this, getString(R.string.auth_out_success), Toast.LENGTH_SHORT).show();
        runOnUiThread(this::openSignInView);
        RoomManager.deleteAll();
    }

    @Override
    public void onError(final Exception e) {
        Toast.makeText(this, getString(R.string.err_auth, e.getLocalizedMessage()),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Required by ADAL
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        AuthManager.onSaveInstanceState(outState);
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
            case R.id.nav_sign_out:
                AuthManager.signOut(this);
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
}
