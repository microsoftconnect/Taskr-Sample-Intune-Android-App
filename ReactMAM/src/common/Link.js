/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React from 'react';
import { Linking, StyleSheet, Text } from 'react-native';


/**
 * Custom Hyperlink Component for Adding Web URLs to App 
 * @param text Display Text for Link
 * @param href URL to go to when Link is pressed
 */
const Link = ({ text, href }) => ( 
  <Text style={styles.text} onPress={() => Linking.openURL(href)}>
    {text}
  </Text>
);

const styles = StyleSheet.create({
  text: {
    color: 'blue',
    textDecorationLine: 'underline',
  }
});

export default Link;
