/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React from 'react';
import { Button, StyleSheet, ToastAndroid, View } from 'react-native';

import { saveTasks, printTasks } from '../modules';


/**
 * Component Containing Save and Printing Task Buttons and Actions
 * @param tasks Tasks to process in some way (save or print)
 */
const TaskActions = ({ tasks }) => {
  /**
   * Wrapper to Android saveTasks Native Function
   */
  const saveFunc = async () => {
    try {
      await saveTasks(tasks);
      ToastAndroid.show('Saved Tasks to tasks.csv', ToastAndroid.SHORT);
    } catch (e) {
      ToastAndroid.show(
        'Error Saving Tasks: ' + e.message, ToastAndroid.LONG);
    }
  };

  /**
   * Wrapper to Android printTasks Native Function
   */
  const printFunc = async () => {
    try {
      await printTasks(tasks);
    } catch (e) {
      ToastAndroid.show(
        'Error Printing Tasks: ' + e.message, ToastAndroid.LONG);
    }
  };


  return (
    <View style={styles.container}>
      <Button title='    Save    ' onPress={saveFunc} />
      <Button title='    Print    ' onPress={printFunc} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 10,
    flexDirection: 'row',
    justifyContent: 'space-evenly'
  }
});

export default TaskActions;
