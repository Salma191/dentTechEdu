import React, { useState, useRef, useEffect } from 'react';

import { View, Button, Image, StyleSheet, Animated, Text, TouchableOpacity, Modal, Dimensions, useWindowDimensions, TouchableWithoutFeedback } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage'; 


const Splash = ({navigation}) => {

useEffect(()=>{
    setTimeout(()=>{
        const status =  AsyncStorage.getItem('connectionStatus');

        if(status._h){
            navigation.navigate('PwActivity')
        }else{
            navigation.navigate('Login')
        }
        
    },3000)
},[])

 return(
    <View style={{backgroundColor:'lightblue',flex:1,justifyContent:'center',alignItems:'center'}}>
                       <Image
                  source={require('./android/app/src/main/res/mipmap-hdpi/ic_launcher_foreground.png') }
                 
                  resizeMode="contain"
                />
                <Text>MyPrep</Text>
    </View>
 );
};


export default Splash;
