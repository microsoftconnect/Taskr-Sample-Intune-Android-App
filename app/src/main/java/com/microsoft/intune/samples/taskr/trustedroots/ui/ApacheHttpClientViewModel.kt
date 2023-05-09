/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.ui

import androidx.lifecycle.viewModelScope
import com.microsoft.intune.samples.taskr.trustedroots.network.TrustedRootsNetworkHandler
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.MalformedURLException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A [TrustedRootsHttpViewModel] that handles sending requests using Apache Http Client.
 */
class ApacheHttpClientViewModel : TrustedRootsHttpViewModel() {

    private val LOGGER = Logger.getLogger(OkHttpClientViewModel::class.java.name)

    override fun submitRequest() {
        response.postValue("")

        viewModelScope.launch {
            try {
               val result = TrustedRootsNetworkHandler.submitApacheHttpClient5Request(
                    trustCustomCerts.value ?: false,
                    requestUrl.value ?: ""
                )
                response.postValue(result)
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