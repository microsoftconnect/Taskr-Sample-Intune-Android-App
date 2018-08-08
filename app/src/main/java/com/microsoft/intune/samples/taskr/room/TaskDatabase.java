/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Contains the DAOs that provide access to the app's database.
 */
@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    /**
     * Returns the DAO that provides access to the database table containing tasks.
     *
     * @return a DAO that provides access to the database table containing tasks
     */
    public abstract TaskDao taskDao();
}
