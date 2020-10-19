/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React from 'react';
import {
  Button, 
  FlatList,
  StyleSheet,
  Text,
  ToastAndroid,
  View, 
} from 'react-native';


/**
 * Individual List Element for A Task - Can finish single tasks
 * @param text Task Description
 * @param index Task ID for Deleting
 * @param deleteTaskFunc Function for deleting / completing individual task
 */
const ListElem = ({ text, index, deleteTaskFunc }) => {
  const deleteTask = () => {
    deleteTaskFunc(index);
    ToastAndroid.show('Deleted Task!', ToastAndroid.SHORT);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.text}>{text}</Text>
      <Button title='Complete' onPress={deleteTask}/>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  text: { flex: 1 }
});


/**
 * Divider Between Individual List Elements
 */
const ListSeparator = () => (
  <View style={{
    borderBottomColor: 'lightgrey',
    borderBottomWidth: 1,
    marginTop: 10,
    marginBottom: 10,
  }} />
);


/**
 * List Display for All Tasks
 * @param tasks All tasks to display
 * @param deleteTaskFunc Function to delete single tasks based on ID or index
 */
const TaskList = ({ tasks, deleteTaskFunc }) => {
  return (
    <View style={{ padding: 20 }}>
      <FlatList data={tasks}
        ItemSeparatorComponent={ListSeparator}
        keyExtractor={(_, index) => index.toString()}
        renderItem={({item, index}) => (
          <ListElem text={item} index={index} deleteTaskFunc={deleteTaskFunc}/>
        )}
      />
    </View>
  );
};

export default TaskList;
