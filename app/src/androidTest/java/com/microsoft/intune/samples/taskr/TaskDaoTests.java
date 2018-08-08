/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.microsoft.intune.samples.taskr.room.Task;
import com.microsoft.intune.samples.taskr.room.TaskDao;
import com.microsoft.intune.samples.taskr.room.TaskDatabase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device. Verifies Room usage.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TaskDaoTests {
    private static TaskDatabase mDb;
    private static TaskDao mDao;

    @BeforeClass
    public static void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, TaskDatabase.class).build();
        mDao = mDb.taskDao();
    }

    @AfterClass
    public static void closeDb() {
        mDb.close();
    }

    @After
    public void clearDb() {
        mDb.clearAllTables();
    }

    @Test
    public void writeAndGetById() {
        Task task = new Task("test");

        long id = mDao.insert(task);
        task.setId(id);

        Task taskBack = mDao.get(id);
        assertTrue(task.equals(taskBack));
    }

    @Test
    public void writeManyAndGetById() {
        Task task0 = new Task("test0");
        Task task1 = new Task("test1");
        Task task2 = new Task("test2");
        Task task3 = new Task("test3");

        long[] ids = mDao.insertAll(task0, task1, task2, task3);
        task0.setId(ids[0]);
        task1.setId(ids[1]);
        task2.setId(ids[2]);
        task3.setId(ids[3]);

        Task taskBack = mDao.get(ids[2]);
        assertTrue(task2.equals(taskBack));
    }

    @Test
    public void writeManyAndGetList() {
        Task task0 = new Task("test0");
        Task task1 = new Task("test1");
        Task task2 = new Task("test2");
        Task task3 = new Task("test3");

        long[] ids = mDao.insertAll(task0, task1, task2, task3);
        task0.setId(ids[0]);
        task1.setId(ids[1]);
        task2.setId(ids[2]);
        task3.setId(ids[3]);

        List<Task> list = mDao.getAllSync();
        assertNotNull(list);
        assertTrue(list.size() == 4);
        assertTrue(list.contains(task0));
        assertTrue(list.contains(task1));
        assertTrue(list.contains(task2));
        assertTrue(list.contains(task3));
    }

    @Test
    public void writeManyAndDelete() {
        Task task0 = new Task("test0");
        Task task1 = new Task("test1");
        Task task2 = new Task("test2");
        Task task3 = new Task("test3");

        long[] ids = mDao.insertAll(task0, task1, task2, task3);
        task0.setId(ids[0]);
        task1.setId(ids[1]);
        task2.setId(ids[2]);
        task3.setId(ids[3]);
        mDao.delete(task1, task2, task3);

        List<Task> tasksBack = mDao.getAllSync();
        assertNotNull(tasksBack);
        assertTrue(tasksBack.size() == 1);
        assertTrue(tasksBack.get(0).equals(task0));
    }
}
