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
 * @param selectedIndex Index of the currently selected tab
 * @param tabs Array of tab names
 * @param onTabChange Function to call when a tab is selected
 */
const Tabs = ({ selectedIndex, tabs, onTabChange }) => {
  return (
    <View style={styles.container}>
      {tabs.map((tab, index) => (
        <TouchableOpacity 
          key={index}
          style={[styles.tab, selectedIndex === index && styles.selected]} 
          onPress={() => onTabChange(index)}>
            <Text style={styles.tabText}>{tab}</Text>
        </TouchableOpacity>
      ))}

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
