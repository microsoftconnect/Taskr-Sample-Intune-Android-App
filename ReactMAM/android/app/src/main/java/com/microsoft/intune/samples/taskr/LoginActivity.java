package com.microsoft.intune.samples.taskr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.PromptBehavior;

import com.microsoft.intune.samples.taskr.authentication.AuthListener;
import com.microsoft.intune.samples.taskr.authentication.AuthManager;

public class LoginActivity extends AppCompatActivity implements AuthListener {
    private Handler mHandler;
    private AuthenticationContext mAuthContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* If the app has already started, the user has signed in, and the activity was just
         * restarted, skip the rest of this initialization and open the main UI */
        if (savedInstanceState != null && AuthManager.shouldRestoreSignIn(savedInstanceState)) {
            onSignedIn();
            return;
        }

        // Start by making a sign in window to show instead of the main view
        setContentView(R.layout.sign_in);
        findViewById(R.id.sign_in_button).setOnClickListener(signInListener);

        mAuthContext = new AuthenticationContext(this, AuthManager.AUTHORITY, true);
        // Will make sign in attempts that are allowed to access/modify the UI (prompt)
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == AuthManager.MSG_PROMPT_AUTO) {
                    AuthManager.signInWithPrompt(mAuthContext, LoginActivity.this,
                        LoginActivity.this, PromptBehavior.Auto, mHandler);
                } else if (msg.what == AuthManager.MSG_PROMPT_ALWAYS) {
                    AuthManager.signInWithPrompt(mAuthContext, LoginActivity.this,
                        LoginActivity.this, PromptBehavior.Always, mHandler);
                }
            }
        };

        /* We only need to change/set the view and sign in if this is the first time the app
         * has opened, which is when savedInstanceState is null */
        if (savedInstanceState == null) {
            AuthManager.signInSilent(mAuthContext, this, mHandler);
        }
    }

    private final View.OnClickListener signInListener = (View view) ->
        mHandler.sendEmptyMessage(AuthManager.MSG_PROMPT_ALWAYS);

    @Override
    public void onSignedIn() {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        Toast.makeText(this, getString(R.string.auth_success), Toast.LENGTH_SHORT).show();
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
        mAuthContext.onActivityResult(requestCode, resultCode, data);  // Required by ADAL
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        AuthManager.onSaveInstanceState(outState);
    }
}