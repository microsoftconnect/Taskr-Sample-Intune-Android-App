import React, { useState } from 'react';
import { StyleSheet, View, TextInput, Button, Text, ScrollView } from 'react-native';

/**
 * Main Component of Trusted Roots Page - contains all of the trusted roots related components.
 * Handles sending requests to the server and displaying the response.
 */
const TrustedRootsPage = () => {
  const [response, setResponse] = useState('');
  const [requestUrl, setRequestUrl] = useState('');

  const handleRequest = async () => {
    try {
      const response = await fetch(requestUrl);
      const text = `Status: ${response.status}\nContent: ${await response.text()}`;
      setResponse(text);
    } catch (error) {
      setResponse(error.message);
    }
  };

  return (
    <View style={styles.pageContainer}>
      <View style={styles.requestContainer}>
        <View style={styles.container}>
          <TextInput
            inputMode='url'
            editable={true}
            multiline={true}
            numberOfLines={2}
            style={styles.textBox}
            placeholder='Request URL'
            value={requestUrl}
            onChangeText={setRequestUrl}
          />
          <Button title="Send" onPress={handleRequest} />
        </View>
        <Text style={styles.label}>Response:</Text>
        <ScrollView>
          <Text>{response}</Text>
        </ScrollView>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  pageContainer: {
    flex: 1
  },
  requestContainer: {
    padding: 10,
    flexDirection: 'column'
  },
  container: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  textBox: {
    flex: 1,
    borderBottomColor: 'black',
    borderBottomWidth: 1,
    marginRight: 10,
    fontSize: 15,
  },
  label: {
    fontWeight: 'bold'
  }
});

export default TrustedRootsPage;