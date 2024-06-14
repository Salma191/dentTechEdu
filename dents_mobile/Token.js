import React, {   useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ToastAndroid, Image } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';



  

const Forgotpassword = () => {
  const navigation = useNavigation();
  const [email, setEmail] = useState('');
  const [token,setToken]=useState('');


  useEffect(() => {
    const getCode = async () => {
      try {
        let data = await AsyncStorage.getItem('token');
        if (data) {
          setToken(data);
        }
      } catch (error) {
        console.error('Error fetching token: ', error);
      }
    };

    getCode();
  }, []);

  const sendEmail = async () => {
    if(token === email){
      navigation.navigate('ResetPassword');
    }else{
        ToastAndroid.show('Token Is Not Valid !', ToastAndroid.SHORT);
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
          placeholder="Token"
          placeholderTextColor="#003f5c"
          value={email}
          onChangeText={text => setEmail(text)}
        />
      </View>
      <TouchableOpacity
        style={styles.loginBtn}
        onPress={sendEmail}>
        <Text style={styles.loginText}>Validate Token</Text>
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
