/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Base class for the view models used by the HTTP clients.
 */
abstract class TrustedRootsHttpViewModel : ViewModel() {

    val requestUrl: MutableLiveData<String> = MutableLiveData()
    val response: MutableLiveData<String> = MutableLiveData()
    val trustCustomCerts: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Submits the request. This method should be implemented by the child class.
     */
    abstract fun submitRequest()
}