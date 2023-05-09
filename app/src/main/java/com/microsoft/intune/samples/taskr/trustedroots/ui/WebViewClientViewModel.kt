/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.ui

import android.webkit.WebViewClient
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.microsoft.intune.mam.client.app.MAMCertTrustWebViewClient

/**
 * View model for the WebViewClientFragment.
 */
class WebViewClientViewModel : ViewModel() {

    val requestUrl: MutableLiveData<String> = MutableLiveData()
    val trustCustomCerts: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Returns the appropriate WebViewClient based on the value of trustCustomCerts.
     */
    fun getWebViewClient(): WebViewClient {
        return if (trustCustomCerts.value == true) {
            MAMCertTrustWebViewClient()
        } else {
            WebViewClient()
        }
    }
}