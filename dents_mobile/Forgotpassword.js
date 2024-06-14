import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ToastAndroid, Image } from 'react-native';
import axios from 'axios';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import AppConfig from './config';



const Forgotpassword = () => {
  const [email, setEmail] = useState('');
  const navigation = useNavigation();

  const sendEmail = async () => {
    const url = AppConfig.etudiantUrl + '/send-email';
    const randomToken = generateRandomToken(); // Generate a random token
    await AsyncStorage.setItem('token', randomToken);
    await AsyncStorage.setItem('email', email);
    try {
      const response = await axios.post(url, {
        to: email,
        subject: 'Password Reset', // Update subject if needed
        text: `Your password reset token is: ${randomToken}`, // Include the random token in the email body
      });
    
      if (response.status === 200) {
        ToastAndroid.show('Email Sent Successfully', ToastAndroid.SHORT);
        navigation.navigate('token');
      } else {
        console.log('Failed to send email.');
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        ToastAndroid.show('Email Is Not Valid', ToastAndroid.SHORT);
      } else {
        ToastAndroid.show('An error occurred', ToastAndroid.SHORT);
      }
    }
    
  };

  // Function to generate a random token
  const generateRandomToken = () => {
    const length = 8; // Adjust the length of the token as needed
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let token = '';
    for (let i = 0; i < length; i++) {
      token += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return token;
  };

  return (
    <View style={styles.container}>
      <Image
        style={{width:200,height:200,marginBottom:40}}
        source={require('./android/app/src/main/res/mipmap-hdpi/snina.png')}
        resizeMode="contain"
      />
      <View style={styles.inputView}>
        <TextInput
          style={styles.inputText}
          placeholder="Email"
          placeholderTextColor="#003f5c"
          value={email}
          onChangeText={text => setEmail(text)}
        />
      </View>
      <TouchableOpacity
        style={styles.loginBtn}
        onPress={sendEmail}>
        <Text style={styles.loginText}>Send Email</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
    alignItems: 'center',
    justifyContent: 'center',
  },
  inputView: {
    width: "80%",
    backgroundColor: "#dcdcdc",
    borderRadius: 25,
    height: 50,
    marginBottom: 20,
    justifyContent: "center",
    padding: 20
  },
  inputText: {
    height: 50,
    color: "black"
  },
  loginBtn: {
    width: 150,
    backgroundColor: "#a9a9a9",
    borderRadius: 25,
    height: 50,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 20,
    marginBottom: 10
  },
  loginText: {
    color: "white"
  }
});

export default Forgotpassword;
