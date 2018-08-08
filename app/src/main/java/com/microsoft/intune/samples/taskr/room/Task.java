/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

/**
 * Java representation of a task. Standard object with privates with getters and setters.
 */
@Entity
public class Task {
    @PrimaryKey(autoGenerate = true)
    private long mId;

    private final String mDescription;

    public Task(final String description) {
        this.mDescription = description;
    }

    /**
     * Sets the id of the task.
     * @param id the new id to set
     */
    public void setId(final long id) {
        this.mId = id;
    }

    /**
     * Gets the id of the task.
     * @return the task's id
     */
    public long getId() {
        return this.mId;
    }

    /**
     * Gets the description of the task.
     * @return the task's description
     */
    public String getDescription() {
        return this.mDescription;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Task)) {
            return false;
        }
        Task task = (Task) obj;
        return mId == task.getId() && mDescription.equals(task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mDescription);
    }

    /**
     * Returns a representation of this task with fields separated by separator.
     * @param separator the string to separate the fields of the task
     * @return a string representation of this task
     */
    public String toString(final String separator) {
        return quote("" + mId) + separator + quote(mDescription);
    }

    /**
     * Wraps str in quotes.
     * @param str the String to wrap
     * @return str surrounded by " characters
     */
    private String quote(final String str) {
        return "\"" + str + "\"";
    }
}