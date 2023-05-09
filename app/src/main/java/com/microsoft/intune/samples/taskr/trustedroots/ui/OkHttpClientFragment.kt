/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.trustedroots.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.microsoft.intune.samples.taskr.databinding.FragmentOkhttpClientBinding

/**
 * A [Fragment] that handles sending requests using OkHttp Client.
 */
class OkHttpClientFragment : Fragment() {

    private lateinit var binding: FragmentOkhttpClientBinding
    private val okHttpClientViewModel: OkHttpClientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOkhttpClientBinding.inflate(inflater, container, false)
        binding.viewModel = okHttpClientViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}