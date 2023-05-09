/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.microsoft.intune.samples.taskr.trustedroots.ui.ApacheHttpClientFragment
import com.microsoft.intune.samples.taskr.trustedroots.ui.OkHttpClientFragment
import com.microsoft.intune.samples.taskr.trustedroots.ui.WebViewClientFragment

/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the tabs.
 */
class TrustedRootsClientAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = TrustedRootsClientTypes.values().size

    /**
     * Create a new instance of the fragment for the given position.
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            TrustedRootsClientTypes.OKHTTP.ordinal -> OkHttpClientFragment()
            TrustedRootsClientTypes.APACHEHTTP.ordinal -> ApacheHttpClientFragment()
            TrustedRootsClientTypes.WEBVIEW.ordinal -> WebViewClientFragment()
            else -> throw IllegalArgumentException("Unsupported client type")
        }
    }
}