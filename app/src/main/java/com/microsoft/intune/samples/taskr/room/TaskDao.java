/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * The DAO (data access object) for Tasks. Defines relevant database queries and methods.
 */
@Dao
public interface TaskDao {
    /**
     * The name of the table tasks are stored in.
     */
    String TABLE = "task";

    /**
     * Queries the database for all of its Tasks and returns them in a LiveData object.
     *
     * @return a LiveData containing a List with all of the table's Tasks
     */
    @Query("SELECT * FROM " + TABLE)
    LiveData<List<Task>> getAll();

    /**
     * Queries the database for all of its Tasks and returns them in a List, synchronously.
     * Should never be used in the UI, only in tests.
     *
     * @return a List containing all of the table's Tasks
     */
    @Query("SELECT * FROM " + TABLE)
    List<Task> getAllSync();

    /**
     * Queries the database for a Task with id id.
     *
     * @param id the id to search for
     * @return a Task with id id or null if none was found
     */
    @Query("SELECT * FROM " + TABLE + " WHERE mId == :id")
    @Nullable
    Task get(long id);

    /**
     * Inserts task into the database. Returns its id, which is automatically assigned
     * if it didn't already have one.
     *
     * @param task the task to insert into the database
     * @return the id of the task that has been inserted
     */
    @Insert
    long insert(Task task);

    /**
     * Inserts all of the tasks into the database.
     *
     * @param tasks the tasks to insert
     * @return an array containing the ids of the tasks that have been inserted
     */
    @Insert
    long[] insertAll(Task... tasks);

    /**
     * Deletes all of the Tasks passed in from the database.
     *
     * @param task the tasks to delete
     */
    @Delete
    void delete(Task... task);
}
