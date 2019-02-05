/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.intune.mam.client.identity.MAMPolicyManager;
import com.microsoft.intune.mam.policy.SaveLocation;
import com.microsoft.intune.samples.taskr.R;
import com.microsoft.intune.samples.taskr.authentication.AuthManager;
import com.microsoft.intune.samples.taskr.room.TaskListAdapter;
import com.microsoft.intune.samples.taskr.room.RoomManager;
import com.microsoft.intune.samples.taskr.utils.Printer;
import com.microsoft.intune.samples.taskr.utils.SaveObserver;


/**
 * A {@link Fragment} subclass that handles the creation of a view of the tasks screen.
 */
public class TasksFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Make a view adapter that will update the list as data updates
        TaskListAdapter adapter = new TaskListAdapter(getContext());
        ListView displayList = rootView.findViewById(R.id.tasks_nav_list_view);
        displayList.setAdapter(adapter);

        // Define the observer that will notify the adapter of changes
        RoomManager.getAllTasks().observe(this, adapter::setList);

        // Set up the click handlers
        rootView.findViewById(R.id.tasks_nav_save_icon).setOnClickListener(saveListener);
        rootView.findViewById(R.id.tasks_nav_print_icon).setOnClickListener(printListener);

        return rootView;
    }

    private void toastErrorStr(final int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_LONG).show();
    }

    /* Example of MAM policy - allow printing.
     * Will be automatically blocked by MAM if necessary. */
    private final View.OnClickListener printListener = (final View view) -> {
        Activity activity = getActivity();
        if (activity != null) {
            Printer printer = new Printer(activity, this);
            printer.printTasks();
        } else {
            toastErrorStr(R.string.err_not_active);
        }
    };

    /* Example of MAM policy - allow saving to device.
     * Manually checks whether or not this is allowed. NOTE: if the user's policy asks the app to
     * encrypt files, the output of this process will be useless*/
    private final View.OnClickListener saveListener = (final View view) -> {
        if (MAMPolicyManager.getPolicy(getActivity())
                .getIsSaveToLocationAllowed(SaveLocation.LOCAL, AuthManager.getUser())) {
            Activity activity = getActivity();
            Context context = getContext();
            if (activity != null && context != null) {
                RoomManager.getTaskDocument(context, this, true,
                        new SaveObserver(context, activity, getTargetRequestCode()));
            } else {
                toastErrorStr(R.string.err_not_active);
            }
        } else {
            toastErrorStr(R.string.err_not_allowed);
        }
    };
}
