/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */


import React, { useState, useEffect } from 'react';
import { Text, View, StyleSheet } from 'react-native';

import Link from '../common/Link';
import { getMAMConfig } from '../modules';


const INTUNE_LINK = 'https://www.microsoft.com/en-us/cloud-platform/microsoft-intune';
const SDK_LINK = 'https://docs.microsoft.com/en-us/intune/app-sdk';
const DEV_LINK = 'https://docs.microsoft.com/en-us/intune/app-sdk-android';
const MICROSOFT_LINK = 'https://microsoft.com';

const CONFIG = 'Your Intune application configuration is: ';
const NO_CONFIG = 'Your application configuration JSON is not set.';


/**
 * General App About Page with Developer Info for Intune
 */
const AboutPage = () => {
  const [mamConfigStr, setConfig] = useState(NO_CONFIG);
  useEffect(() => {
    const wrapper = async () => {
      try {
        setConfig(CONFIG + (await getMAMConfig()));
      } catch (e) {}
    };

    wrapper();
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>About Taskr</Text>
      <Text style={{ paddingBottom: 15 }}>
        Taskr is a line-of-business app that allows employees to keep a personal
        to-do list. It is a demonstration of Microsoft's Android MAM SDK, 
        an <Link text='Intune' href={INTUNE_LINK} /> product.
      </Text>
      <Text style={{ paddingBottom: 15 }}>
        More information about the SDK is 
        available <Link text='here' href={SDK_LINK} /> and 
        a developer guide is available <Link text='here' href={DEV_LINK} />.
      </Text>
      <Text style={{ flex: 1, fontWeight: 'bold' }}>{mamConfigStr}</Text>
      <Text style={styles.devTag}>
        Developed By <Link text='Microsoft' href={MICROSOFT_LINK} />
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  title: {
    fontSize: 24,
    paddingBottom: 15,
  },
  devTag: {
    fontSize: 16,
    fontWeight: 'bold',
    textAlign: 'center',
  },
});

export default AboutPage;
