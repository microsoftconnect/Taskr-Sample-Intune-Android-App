/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.utils;

import android.app.Activity;
import android.content.Context;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.microsoft.intune.samples.taskr.R;
import com.microsoft.intune.samples.taskr.room.RoomManager;

/**
 * Class that will print the results of a call to RoomManager.
 *
 * Will automatically be blocked by MAM if necessary.
 */
public class Printer {
    private final Activity mActivity;
    private final LifecycleOwner mLifecycleOwner;

    public Printer(@NonNull final Activity activity, @NonNull final LifecycleOwner lifecycleOwner) {
        mActivity = activity;
        mLifecycleOwner = lifecycleOwner;
    }

    /**
     * The only ways to print from an Android app are to 1) print a photo, 2) print a website, or
     * 3) manually draw the document in a PDF. Making an HTML document to print is the easiest
     * and most appropriate option for this app.
     */
    public void printTasks() {
        // Set up a WebView to print automatically
        WebView webView = new WebView(mActivity);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return false;
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                createWebPrintJob(view);
            }
        });

        // Set the content of the view to be the HTML document we want to print
        RoomManager.getTaskDocument(mActivity.getApplicationContext(), mLifecycleOwner, false,
                (String html) -> webView.loadData(html, "text/HTML", "UTF-8"));
    }

    private void createWebPrintJob(final WebView webView) {
        // Create the printing resources
        PrintManager printManager =
                (PrintManager) mActivity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create the print job with name jobName
        String jobName = mActivity.getString(R.string.print_name);
        if (printManager != null) {
            printManager.print(jobName, printAdapter, null);
        }
    }
}
