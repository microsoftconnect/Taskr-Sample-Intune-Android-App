/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.ui

import androidx.lifecycle.viewModelScope
import com.microsoft.intune.samples.taskr.trustedroots.network.TrustedRootsNetworkHandler
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.net.MalformedURLException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A [TrustedRootsHttpViewModel] that handles sending requests using OkHttp Client.
 */
class OkHttpClientViewModel : TrustedRootsHttpViewModel() {

    private val LOGGER = Logger.getLogger(OkHttpClientViewModel::class.java.name)

    override fun submitRequest() {
        response.postValue("")

        viewModelScope.launch {
            try {
                val result = TrustedRootsNetworkHandler.submitOkHttpClientRequest(
                    trustCustomCerts.value ?: false,
                    requestUrl.value ?: ""
                )
                response.postValue("Status: ${result.code} ${result.message} \n ${result.body?.string()}")
            } catch (ex: Exception) {
                when (ex) {
                    is MalformedURLException, is IllegalArgumentException, is IOException -> {
                        response.postValue(ex.message)
                        LOGGER.log(Level.SEVERE, ex.message, ex)
                    }
                }
            }
        }
    }
}