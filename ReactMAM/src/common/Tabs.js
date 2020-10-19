/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

 
import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import { signOut } from '../modules';
import { 
  PRIMARY_THEME_COLOR, 
  PRIMARY_LIGHT_COLOR,
  PRIMARY_TEXT_COLOR,
} from '../constants';


/**
 * Common Tab Component to Switch Between Pages
 * @param index Currently selected Tab (True for Tasks, False for About)
 * @param onTabChange Function to pass selected tab when pressed
 */
const Tabs = ({ index, onTabChange }) => {
  return (
    <View style={styles.container}>
      <TouchableOpacity 
        style={[styles.tab, index && styles.selected]} 
        onPress={() => onTabChange(true)}>
          <Text style={styles.tabText}>Tasks</Text>
      </TouchableOpacity>
      
      <TouchableOpacity 
        style={[styles.tab, !index && styles.selected]} 
        onPress={() => onTabChange(false)}>
          <Text style={styles.tabText}>About</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.tab} onPress={signOut}>
        <Text style={styles.tabText}>Sign Out</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 0,
    height: 60,
    backgroundColor: PRIMARY_THEME_COLOR,
    flexDirection: 'row',
  },
  tab: {
    flex: 1,
    height: '100%',
  },
  tabText: {
    height: '100%',
    textAlign: 'center',
    textAlignVertical: 'center',
    fontSize: 18,
    fontWeight: 'bold',
    color: PRIMARY_TEXT_COLOR,
  },
  selected: {
    borderTopColor: PRIMARY_LIGHT_COLOR,
    borderTopWidth: 5,
  }
});

export default Tabs;
