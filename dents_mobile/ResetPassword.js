import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ToastAndroid, Image } from 'react-native';
import axios from 'axios';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Ionicons } from '@expo/vector-icons';
import AppConfig from './config';

const Forgotpassword = () => {
  const [email, setEmail] = useState('');
  const [email2, setEmail2] = useState('');
  const [token, setToken] = useState('');
  const navigation = useNavigation();

  useEffect(() => {
    const getCode = async () => {
      try {
        let data = await AsyncStorage.getItem('email');
        if (data) {
          setToken(data);
        }
      } catch (error) {
        console.error('Error fetching email: ', error);
      }
    };

    getCode();
  }, []);

  const verify = async () => {
    if (email2 === email) {
      sendEmail();
    } else {
      ToastAndroid.show('Password Does Not Match !', ToastAndroid.SHORT);
    }
  };

  const sendEmail = async () => {
    const url = AppConfig.etudiantUrl  +'/changePassword';

    try {
      const response = await axios.post(url, {
        email: token,
        password: email,
      });
      
      if (response.status === 200) {
        ToastAndroid.show('Password Reset Successfully', ToastAndroid.SHORT);
        navigation.navigate('Login');
      } else {
        console.log('Failed to Reset password.');
      }
    } catch (error) {
      console.error('Error:', error);
    }
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
          required
          placeholder="Password"
          placeholderTextColor="#003f5c"
          value={email}
          onChangeText={text => setEmail(text)}
          secureTextEntry={true} 
        />
      </View>
      <View style={styles.inputView}>
        <TextInput
          style={styles.inputText}
          required
          placeholder="Confirm Password"
          placeholderTextColor="#003f5c"
          value={email2}
          onChangeText={text => setEmail2(text)}
          secureTextEntry={true} 
        />
      </View>
      <TouchableOpacity
        style={styles.loginBtn}
        onPress={verify}>
        <Text style={styles.loginText}>Change Password</Text>
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
    width: '80%',
    backgroundColor: '#dcdcdc',
    borderRadius: 25,
    height: 50,
    marginBottom: 20,
    justifyContent: 'center',
    padding: 20,
  },
  inputText: {
    height: 50,
    color: 'black',
  },
  loginBtn: {
    width: 150,
    backgroundColor: '#a9a9a9',
    borderRadius: 25,
    height: 50,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 20,
    marginBottom: 10,
  },
  loginText: {
    color: 'white',
  },
});

export default Forgotpassword;
