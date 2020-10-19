/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

 
import React from 'react';
import {
  StyleSheet,
  Text,
  View,
} from 'react-native';

import { PRIMARY_THEME_COLOR, PRIMARY_TEXT_COLOR } from '../constants';
import { displayName } from '../../app.json';


/**
 * Common Header Component with App Display Name
 */
const Header = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>{displayName}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 0,
    height: 60,
    backgroundColor: PRIMARY_THEME_COLOR,
    justifyContent: 'center',
    paddingLeft: 30
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: PRIMARY_TEXT_COLOR,
  }
});

export default Header;
