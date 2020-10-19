/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React, { useState } from 'react';
import {
  Button,
  StyleSheet,
  ToastAndroid,
  TextInput,
  View,
} from 'react-native';


/**
 * Basic Form to Add New Tasks
 * @param addTaskFunc Caller func to add new task when submitted
 */
const TaskAdder = ({ addTaskFunc }) => {
  const [text, setText] = useState('');
  
  const pressFunc = () => {
    addTaskFunc(text);
    setText('');
    ToastAndroid.show('Added Task!', ToastAndroid.SHORT);
  };


  return (
    <View style={styles.container}>
      <TextInput
        style={styles.textBox}
        placeholder='Enter New Task ...'
        value={text}
        onChangeText={setText}
      />
      <Button title='Submit' onPress={pressFunc}/>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 15,
    flexDirection: 'row',
    alignItems: 'center'
  },
  textBox: {
    flex: 1,
    borderBottomColor: 'black',
    borderBottomWidth: 1,
    marginRight: 15,
    fontSize: 18,
  },
});

export default TaskAdder;
