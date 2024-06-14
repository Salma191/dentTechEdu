
import React, { useState } from 'react';
import AsyncStorageManager from './AsyncStorageManager';
import AppConfig from './config';
import axios from 'axios';
import {

    StyleSheet,
    Text,
    Image,
    View,
    TextInput,
    TouchableOpacity,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage'; // Make sure you've installed this package

const Login = ({ navigation }) => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    // Assuming this code runs in a JavaScript environment (e.g., Node.js, browser with Axios library imported)

    // Import Axios (Node.js) or include Axios library (browser)


    // Assuming userName and password are variables holding the input values // Replace with actual value

    // Assuming APIConfig.BASE_URL is defined somewhere, replace it with the actual base URL


    const onPressLogin = async () => {

        const endpoint =AppConfig.etudiantUrl + '/register';

        try {
            const response = await axios.post(endpoint, {
                userName: email,
                password: password,
            });

            // Si le statut de la réponse est "FORBIDDEN", cela signifie que l'authentification a échoué
            if (response.status === 403) {
                console.log('Authentification échouée: Nom d\'utilisateur ou mot de passe incorrect');
                ToastAndroid.show('Error : Authentification Failed', ToastAndroid.SHORT);
            } else {
                // Gérer la réponse réussie
                const data = response.data;
                const user = { status: 'connected' };
                await AsyncStorageManager.signIn(user);
                await AsyncStorage.setItem('user', JSON.stringify(data));
                console.log('Connexion réussie !');
            }
        } catch (error) {
            ToastAndroid.show('Error : Authentification Failed', ToastAndroid.SHORT);
        }



    };

    const onPressForgot = () => {
        navigation.navigate('Forgotpassword');
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
                    placeholder="User Name"
                    placeholderTextColor="black"
                    onChangeText={text => setEmail(text)} />
            </View>
            <View style={styles.inputView}>
                <TextInput
                    style={styles.inputText}
                    secureTextEntry
                    placeholder="Password"
                    placeholderTextColor="black"
                    onChangeText={text => setPassword(text)} />
            </View>
            <TouchableOpacity
                onPress={onPressForgot}>
                <Text style={styles.forgotAndSignUpText}>Forgot Password?</Text>
            </TouchableOpacity>
            <TouchableOpacity
                onPress={onPressLogin}
                style={styles.loginBtn}>
                <Text style={styles.loginText}>LOGIN </Text>
            </TouchableOpacity>

        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'white',
        alignItems: 'center',
        justifyContent: 'center',
    },
    title: {
        fontWeight: "bold",
        fontSize: 50,
        color: 'black',
        marginBottom: 40,
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
    forgotAndSignUpText: {
        color: "black",
        fontSize: 11
    },
    loginBtn: {
        width: 150,
        backgroundColor: "#a9a9a9",
        borderRadius: 25,
        height: 50,
        alignItems: "center",
        justifyContent: "center",
        marginTop: 40,
        marginBottom: 10
    },
    loginText :{
        color: "white",
    }
});
export default Login;