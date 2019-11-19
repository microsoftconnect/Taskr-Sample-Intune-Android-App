/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.room;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.microsoft.intune.samples.taskr.R;

import java.util.List;

/**
 * Class with static accessors of the singleton design pattern that provides access to the app's
 * database. initRoom is called at the very beginning of the app's creation, so all classes can
 * safely call other methods (other than closeRoom).
 */
public final class RoomManager {
    private static TaskDatabase database;
    private static TaskDao taskDao;

    /**
     * Empty private constructor prevents an instance of RoomManager from ever being created.
     */
    private RoomManager() {
    }

    /**
     * Initializes the app's connection to the database.
     *
     * @param context the context of the app - tells the Room to stay open only as long at context
     *                is active
     */
    public static void initRoom(final Context context) {
        /* In a production app, falling back to a destructive migration would be inadvisable,
         * but for this demo it is an acceptable danger */
        database = Room.databaseBuilder(context, TaskDatabase.class, "tasks-db")
                .fallbackToDestructiveMigration().build();
        taskDao = database.taskDao();
    }

    /**
     * Closes the app's connection to the database. SHOULD ONLY BE CALLED IN TaskrApplication.
     */
    public static void closeRoom() {
        database.close();
    }

    /**
     * Queries the database for all of its Tasks and returns them in a LiveData object.
     *
     * @return a LiveData containing a List with all of the table's Tasks
     */
    public static LiveData<List<Task>> getAllTasks() {
        return taskDao.getAll();
    }

    /**
     * Inserts task into the database. Then sets its id to be the result of that
     * insertion, in case it did not already have an id.
     *
     * @param task the task to insert into the database
     */
    public static void insertTask(final Task task) {
        AsyncTask.execute(() -> {
            long id = taskDao.insert(task);
            task.setId(id);
        });
    }

    /**
     * Takes in an Task and removes the task from the table.
     *
     * @param task the Task to complete
     */
    public static void completeTask(final Task task) {
        AsyncTask.execute(() -> taskDao.delete(task));
    }

    /**
     * Deletes the entire database.
     */
    public static void deleteAll() {
        AsyncTask.execute(() -> database.clearAllTables());
    }

    /**
     * Calls observer on a String containing all the tasks in the database,
     * formatted as a CSV or HTML document.
     *
     * @param context  the context of the calling activity, so this method can access resource strings
     * @param lifespan a LifecycleOwner specifying how long the activity should run for
     * @param isCsv    true for a CSV, false for HTML
     * @param observer is called when the results of the method are available
     */
    public static void getTaskDocument(@Nullable final Context context,
                                       @NonNull final LifecycleOwner lifespan,
                                       final boolean isCsv,
                                       final Observer<String> observer) {
        final String colSeparator = ",";
        final String lineSeparator = "\n";
        String header, headerCol1, headerCol2;
        if (context == null) {
            headerCol1 = "ID";
            headerCol2 = "Task description";
        } else {
            headerCol1 = context.getString(R.string.csv_header_col_1);
            headerCol2 = context.getString(R.string.csv_header_col_2);
        }
        if (isCsv) {
            // Entries in the CSV must be wrapped in quotes to be opened by Excel
            header = "\"" + headerCol1 + "\"" + colSeparator + "\"" + headerCol2 + "\"";
        } else {
            header = headerCol1 + colSeparator + headerCol2;
        }

        /* Since database access can't be performed on the main thread, create a callback to
         * access the data. But we only want this callback to be called once. So let the observer
         * use the caller's LifeCycleOwner to instantiate itself, then remove itself after its
         * first use. */
        LiveData<List<Task>> liveTasks = getAllTasks();
        liveTasks.observe(lifespan, (List<Task> tasks) -> {
            if (tasks == null) {
                observer.onChanged(null);
                liveTasks.removeObservers(lifespan);
                return;
            }

            // Now create the document
            StringBuilder docBuilder = new StringBuilder();
            if (isCsv) {
                docBuilder.append(header);
                for (Task task : tasks) {
                    docBuilder.append(lineSeparator);
                    docBuilder.append(task.toString(colSeparator));
                }
            } else { // the user is asking for an HTML document
                // Create the document/table
                docBuilder.append("<!DOCTYPE html><html><body><table>");
                docBuilder.append(lineSeparator);
                docBuilder.append("<tr><th>");
                // Set the header
                String[] headers = header.split(colSeparator);
                docBuilder.append(headers[0]);
                docBuilder.append("</th><th>");
                docBuilder.append(headers[1]);
                docBuilder.append("</th></tr>");
                docBuilder.append(lineSeparator);

                // Add rows to the table
                for (Task task : tasks) {
                    docBuilder.append("<tr><td>");
                    docBuilder.append(task.getId());
                    docBuilder.append("</td><td>");
                    docBuilder.append(task.getDescription());
                    docBuilder.append("</td></tr>");
                    docBuilder.append(lineSeparator);
                }

                // End the document/table
                docBuilder.append(lineSeparator);
                docBuilder.append("</table></body></html>");
            }

            observer.onChanged(docBuilder.toString());
            liveTasks.removeObservers(lifespan);
        });
    }
}
