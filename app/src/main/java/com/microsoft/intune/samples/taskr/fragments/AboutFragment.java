/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.fragments;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfig;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfigManager;
import com.microsoft.intune.samples.taskr.R;
import com.microsoft.intune.samples.taskr.authentication.AppSettings;


/**
 * A {@link Fragment} subclass that handles the creation of a view of the about screen.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Needed to make the links active
        TextView body1 = view.findViewById(R.id.about_nav_body_1);
        body1.setMovementMethod(LinkMovementMethod.getInstance());
        TextView body2 = view.findViewById(R.id.about_nav_body_2);
        body2.setMovementMethod(LinkMovementMethod.getInstance());
        TextView footer = view.findViewById(R.id.about_nav_footer);
        footer.setMovementMethod(LinkMovementMethod.getInstance());

        TextView configText = view.findViewById(R.id.about_nav_config_text);

        // Get and show the targeted application configuration
        String currentUser = AppSettings.getAccount(this.getContext()).getAADID();
        MAMAppConfigManager configManager = MAMComponents.get(MAMAppConfigManager.class);
        MAMAppConfig appConfig = configManager.getAppConfig(currentUser);

        configText.setText(appConfig == null ? getString(R.string.err_unset)
                : getString(R.string.about_nav_config_text, appConfig.getFullData().toString()));

        return view;
    }
}
