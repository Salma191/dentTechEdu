// Import the functions you need from the SDKs you need

import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries
import firebase from "firebase/compat/app";
import AsyncStorage from '@react-native-async-storage/async-storage'; // Make sure you've installed this package
import { getAuth, signInWithEmailAndPassword,getReactNativePersistence,initializeAuth } from 'firebase/auth';

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyDZ0eeK0CgR_DUdd5MBsxLPD3oeYpDtcds",
  authDomain: "dental-79a6a.firebaseapp.com",
  projectId: "dental-79a6a",
  storageBucket: "dental-79a6a.appspot.com",
  messagingSenderId: "24452162204",
  appId: "1:24452162204:web:2271d577d15c4880838359",
  measurementId: "G-FMLXH0N48N"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);

const auth = getAuth(app, {
    persistence: getReactNativePersistence(AsyncStorage),
  });
  
export  default auth