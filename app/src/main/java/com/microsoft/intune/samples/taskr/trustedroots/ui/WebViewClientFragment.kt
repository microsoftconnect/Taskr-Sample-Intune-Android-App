/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.microsoft.intune.samples.taskr.databinding.FragmentWebViewClientBinding

/**
 * A [Fragment] that handles sending requests using WebView.
 */
class WebViewClientFragment : Fragment() {

    private lateinit var binding: FragmentWebViewClientBinding
    private val viewModel: WebViewClientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewClientBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        handleRequest()
        return binding.root
    }

    /**
     * Handles the request to the WebView. This is done by clearing the cache and SSL preferences,
     * setting the WebViewClient to the one provided by the ViewModel,
     * and then loading the URL.
     */
    private fun handleRequest() {
        binding.submitRequestButton.setOnClickListener {
            binding.webview.clearCache(true)
            binding.webview.clearSslPreferences()
            binding.webview.webViewClient = viewModel.getWebViewClient()
            binding.webview.loadUrl(viewModel.requestUrl.value ?: "")
        }
    }
}