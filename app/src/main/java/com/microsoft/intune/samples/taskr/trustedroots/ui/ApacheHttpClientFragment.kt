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
import com.microsoft.intune.samples.taskr.databinding.FragmentApacheHttpClientBinding

/**
 * A [Fragment] that handles sending requests using Apache HTTP Client.
 */
class ApacheHttpClientFragment : Fragment() {

    private lateinit var binding: FragmentApacheHttpClientBinding
    private val apacheHttpClientViewModel: ApacheHttpClientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApacheHttpClientBinding.inflate(inflater, container, false)
        binding.viewModel = apacheHttpClientViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}