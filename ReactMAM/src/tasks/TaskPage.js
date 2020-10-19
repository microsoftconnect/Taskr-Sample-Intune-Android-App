/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React, { useState } from 'react';
import { View } from 'react-native';

import TaskActions from './TaskActions';
import TaskAdder from './TaskAdder';
import TaskList from './TaskList';


/**
 * Main Component of Tasks Page - contains all of the task related components
 * Also handles all task data management via React state and custom functions 
 */
const TaskPage = () => {

  // Tasks Data Management
  const [tasks, setTasks] = useState([]);
  const addTask = text => setTasks([...tasks, text]);

  /* Because React expects state to be immutable, you can't remove tasks
   * via the Array.splice function. Hence the unusual method.
   */
  const deleteTask = index => 
    setTasks([...tasks.slice(0, index), ...tasks.slice(index + 1)]);


  return (
    <View style={{ flex: 1 }}>
      <TaskAdder addTaskFunc={addTask} />
      {tasks.length === 0 || <TaskActions tasks={tasks}/>}
      <TaskList tasks={tasks} deleteTaskFunc={deleteTask} />
    </View>
  );
};

export default TaskPage;
