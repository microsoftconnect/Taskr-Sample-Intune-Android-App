/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.utils;

import androidx.core.content.FileProvider;

/**
 * This FileProvider allows the app to export files to other apps.
 *
 * Will automatically be blocked by MAM if necessary.
 */
public class CustomFileProvider extends FileProvider { }
