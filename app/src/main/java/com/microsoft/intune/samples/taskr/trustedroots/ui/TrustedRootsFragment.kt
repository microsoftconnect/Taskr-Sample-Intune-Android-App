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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.microsoft.intune.samples.taskr.databinding.FragmentTrustedRootsBaseBinding
import com.microsoft.intune.samples.taskr.trustedroots.TrustedRootsClientAdapter
import com.microsoft.intune.samples.taskr.trustedroots.TrustedRootsClientTypes

/**
 * A [Fragment] that contains a [ViewPager2] and [TabLayoutMediator] to display
 * the different clients.
 */
class TrustedRootsFragment : Fragment() {

    private lateinit var binding: FragmentTrustedRootsBaseBinding
    private lateinit var trustedRootsClientAdapter: TrustedRootsClientAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrustedRootsBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        trustedRootsClientAdapter = TrustedRootsClientAdapter(this)
        viewPager = binding.clientPager
        viewPager.adapter = trustedRootsClientAdapter
        val tabLayout = binding.clientTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "${TrustedRootsClientTypes.from(position)}"
        }.attach()
    }
}