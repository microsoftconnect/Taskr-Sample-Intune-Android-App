/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

/*
 * Set of Functions as the Interface Between JS and Platform-Specific Code
 * Communicates with Native Modules on Android and Potentially iOS in future
 */

 
import { NativeModules, PermissionsAndroid } from 'react-native';


/**
 * Access Intune MAM SDK Configuration
 */
const getMAMConfig = async () => await NativeModules.CustomMAM.getMAMConfig(); 

/**
 * Sign out from Microsoft Account
 */
const signOut = () => NativeModules.CustomMAM.signOut();


const HEADER_COL_ID = 'ID';
const HEADER_COL_DESC = 'Task Description';
const CSV_HEADER = `"${HEADER_COL_ID}","${HEADER_COL_DESC}"\n`;

/**
 * Requests Runtime Permissions for Writing to External Storage on Android
 * @return Promise of configuration (as string) if available
 */
const requestSavePermissions = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
      {
        title: 'Taskr Save Permission',
        message: 
          'Taskr needs access to write to external storage to save tasks.',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      }
    );

    if (granted !== PermissionsAndroid.RESULTS.GRANTED) 
      throw 'Save Permissions Denied';

  } catch (err) {
    throw err;
  }
};

/**
 * Save Tasks into formatted CSV File in Android Documents folder
 * Uses Native Modules to handled file system communication on Android
 * Requires Runtime Permissions for writing to external storage
 * @param tasks List of string of tasks to format and save
 * @return Promise of saving status and any errors with permissions or saving
 */
const saveTasks = async tasks => {
  // Request Save Permissions (SDK 23 and Above)
  try {
    await requestSavePermissions();
  } catch (err) {
    throw err;
  }

  // Format tasks into CSV file format
  const csv = CSV_HEADER 
    + tasks.map((task, index) => `${index},${task}\n`).join('');
  return NativeModules.CustomSave.saveString(csv, 'tasks.csv');
};
  

const HTML_HEADER = 
`<!DOCTYPE html><html><body><table>
<tr><th>${HEADER_COL_ID}</th><th>${HEADER_COL_DESC}</th></tr>
`;

const HTML_FOOTER = '</table></body></html>';

/**
 * Print Tasks by sending to Android Printing Service formatted in HTML Doc
 * Uses Native Modules to handle communication with Android printing
 * @param tasks List of string of tasks to format and print
 * @return Promise of printing status and any potential errors
 */
const printTasks = tasks => {
  // Format Tasks into HTML Doc
  const html = HTML_HEADER 
    + tasks.map((task, index) => 
      `<tr><td>${index + 1}</td><td>${task}</td></tr>\n`).join('')
    + HTML_FOOTER;

  return NativeModules.CustomPrint.printTasks(html);
};


export { getMAMConfig, signOut, saveTasks, printTasks };
