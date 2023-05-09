/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots

/**
 * Enum representing the different types of HTTP clients.
 */
enum class TrustedRootsClientTypes {

    OKHTTP,
    APACHEHTTP,
    WEBVIEW;

    companion object {
        /**
         * Returns the ClientTypes enum value for the given ordinal.
         */
        infix fun from(value: Int): TrustedRootsClientTypes? = TrustedRootsClientTypes.values().firstOrNull { it.ordinal == value }
    }
}