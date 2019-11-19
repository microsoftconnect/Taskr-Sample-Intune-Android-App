/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.Observer;

import com.microsoft.intune.samples.taskr.R;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * An observer to be called when a String is ready to be saved to a user's device.
 *
 * Assumes that calling code has checked on the MAM policy to confirm that this is allowed. For example,
 * see {@link com.microsoft.intune.samples.taskr.fragments.TasksFragment#saveListener}.
 */
public class SaveObserver implements Observer<String> {
    private final Context mContext;
    private final Activity mActivity;
    private final int mRequestCode;

    public SaveObserver(@NonNull final Context context,
                        @NonNull final Activity activity,
                        final int requestCode) {
        mContext = context;
        mActivity = activity;
        mRequestCode = requestCode;
    }

    @Override
    public void onChanged(@Nullable final String doc) {
        if (doc == null) {
            Toast.makeText(mContext, R.string.err_no_body, Toast.LENGTH_LONG).show();
            return;
        }
        // Confirm we're allowed to save to this device, ask for permission if not
        confirmWritePermission();
        // Get the default location for the export
        File exportFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "tasks.csv");

        // Now try to write the document to their device
        boolean didWrite = true;
        try (final PrintWriter writer = new PrintWriter(exportFile)) {
            writer.append(doc);
            writer.flush();
        } catch (IOException e) {
            didWrite = false;
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        // And try to open it. Will be blocked by MAM if necessary
        if (didWrite) {
            Toast.makeText(mContext,
                    mContext.getString(R.string.save_success, exportFile.getPath()),
                    Toast.LENGTH_SHORT).show();
            openFile(exportFile);
        }
    }

    /**
     * Opens file as a CSV in an editor on the user's device, if one exists.
     *
     * @param file the file to open
     */
    private void openFile(final File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(mContext,
                CustomFileProvider.class.getName(), file);
        intent.setDataAndType(uri, "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        /* Confirm the user has at least one app that can open a CSV before trying
         * to open it for them */
        PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            mContext.startActivity(intent);
        }
    }

    /**
     * Confirm we can write the user's device, and if we currently can't, ask to.
     */
    private void confirmWritePermission() {
        if (PermissionChecker.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    mRequestCode);
        }
    }
}
