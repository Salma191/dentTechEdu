import React, { useState, useRef } from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import AuthNavigation from './AuthNavigation';
import { AsyncStorageEventProvider } from './AsyncStorageEventContext';
import 'react-native-gesture-handler';
import {
  NavigationContainer,
  useNavigation,
  DrawerActions,
} from '@react-navigation/native';
import { createDrawerNavigator } from '@react-navigation/drawer';
import Profile from './Profile';
import TPS from './TPS';
import PwActivity from './PwActivity';
const drawer = createDrawerNavigator();


const App = () => {

  return (


    <AsyncStorageEventProvider>
      <AuthNavigation>

      </AuthNavigation>


      { }
    </AsyncStorageEventProvider>



  );
};


export default App;

