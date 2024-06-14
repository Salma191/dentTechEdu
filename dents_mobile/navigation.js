import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { View, Text, Image } from 'react-native';
import { createDrawerNavigator, DrawerContentScrollView, DrawerItemList } from '@react-navigation/drawer';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Home from './Home';
import PwActivity from './PwActivity';
import DentChoose from './DentChoose';
import Login from './Login';
import Profile from './Profile';
import TPS from './TPS';
import error from './Error';
import Forgotpassword from './Forgotpassword';
import choose from './Choose';
import DoTp from './DoTp';
import Chart from './ChartActivity';
import Token from './Token';
import ResetPassword from './ResetPassword';

const Stack = createNativeStackNavigator();
const Drawer = createDrawerNavigator();

function CustomDrawerContent(props) {
  return (
    <DrawerContentScrollView {...props}>
      <View style={{ paddingVertical: 20, marginBottom: 0 }}> 
        <Image source={require('./assets/teeth.png')} style={{ width: '100%', height: 200 }} resizeMode="cover" />
      </View>
      <DrawerItemList {...props} />
    </DrawerContentScrollView>
  );
}


function Homee() {
  return (
    <Stack.Navigator initialRouteName='Préparation' screenOptions={{ headerShown: true }}>
      <Stack.Screen name='Préparation' component={DentChoose} options={{ tabBarVisible: false, headerShown: false }} />
      <Stack.Screen name="les travaux pratiques" component={PwActivity} options={{ tabBarVisible: false, headerShown: false }} />
      <Stack.Screen name="choose" component={choose} options={{ tabBarVisible: false, headerShown: false }} />
      <Stack.Screen name="error" component={error} options={{ tabBarVisible: false, headerShown: false }} />
      <Stack.Screen name="Home" component={Home} options={{ tabBarVisible: false, headerShown: false }} />
    </Stack.Navigator>
  );
}

const SignedInStack = () => (
  <NavigationContainer>
    <Drawer.Navigator initialRouteName='PWs Of The Group' drawerContent={props => <CustomDrawerContent {...props} />}>
      <Drawer.Screen name='Treatment' component={Homee} options={{ drawerIcon: () => <Text>📝</Text> }} />
      <Drawer.Screen name='Completed Practical Works' component={TPS} options={{ drawerIcon: () => <Text>🗂️</Text> }} />
      <Drawer.Screen name='Profile' component={Profile} options={{ drawerIcon: () => <Text>🧑🏻‍🎓</Text> }} />
      <Drawer.Screen name='Practical Works' component={DoTp} options={{ drawerIcon: () => <Text>📚</Text> }} />
      <Drawer.Screen name='Progress Curve' component={Chart} options={{ drawerIcon: () => <Text>📉</Text> }} />
    </Drawer.Navigator>
  </NavigationContainer>
);

const SignedOutStack = () => (
  <NavigationContainer>
    <Stack.Navigator initialRouteName='Login' screenOptions={{ headerShown: false }}>
      <Stack.Screen name='Login' component={Login} />
      <Stack.Screen name='Forgotpassword' component={Forgotpassword} />
      <Stack.Screen name="token" component={Token} options={{ tabBarVisible: false, headerShown: false }} />
      <Stack.Screen name="ResetPassword" component={ResetPassword} options={{ tabBarVisible: false, headerShown: false }} />
    </Stack.Navigator>
  </NavigationContainer>
);

export { SignedInStack, SignedOutStack };
