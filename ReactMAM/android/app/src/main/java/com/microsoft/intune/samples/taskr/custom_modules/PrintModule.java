/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.custom_modules;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;

import com.microsoft.intune.samples.taskr.R;


/**
 * NativeModule for Printing Documents formatted as a HTML Page
 * Communicates with Android Printing protocol to print document
 * Will be automatically blocked by MAM if necessary
 */
public class PrintModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    // Unique Code for Printing Related Errors
    private static final String E_PRINTING = "E_PRINTING";

    PrintModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "CustomPrint";
    }

    /**
     * Print any HTML Document as a String By Using WebView and Printing Pane
     * @param htmlDoc String representing HTML document to print
     * @param promise JS Promise to return to calling JS code with printing status
     */
    @ReactMethod
    public void printDocument(String htmlDoc, Promise promise) {
        UiThreadUtil.runOnUiThread(() -> {  // Important Since Printing Window Affects App UI
            WebView webView = new WebView(reactContext);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    return false;
                }

                @Override
                public void onPageFinished(final WebView view, final String url) {
                    setupPrintingHandler(view, promise);
                }
            });

            webView.loadData(htmlDoc, "text/HTML", "UTF-8");
        });
    }

    /**
     * Sets up Printing Service to Print WebView with HTML Doc
     * @param view WebView with HTML Document to Print
     * @param promise JS Promise to return to calling JS code with printing status
     */
    private void setupPrintingHandler(final WebView view, Promise promise) {
        // Create the printing resources
        PrintManager printManager =
            (PrintManager) reactContext.getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
            private final PrintDocumentAdapter wrapped =
                view.createPrintDocumentAdapter("Taskr Document");

            @Override
            public void onLayout(PrintAttributes attrs, PrintAttributes attrs1,
                                 CancellationSignal signal, LayoutResultCallback callback,
                                 Bundle bundle) {
                wrapped.onLayout(attrs, attrs1, signal, callback, bundle);
            }

            @Override
            public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor descriptor,
                                CancellationSignal signal, WriteResultCallback callback) {
                wrapped.onWrite(pageRanges, descriptor, signal, callback);
            }

            @Override
            public void onFinish() {
                wrapped.onFinish();
                promise.resolve(null);
            }
        };

        // Create the print job
        if (printManager == null)
            promise.reject(E_PRINTING, reactContext.getString(R.string.err_no_print));
        else printManager.print("Print Taskr Doc", printAdapter, null);
    }
}
