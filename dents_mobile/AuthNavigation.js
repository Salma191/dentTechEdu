import React, { useState, useEffect } from 'react';
import { SignedInStack } from './navigation';
import { SignedOutStack } from './navigation';
import AsyncStorageManager from './AsyncStorageManager';

const AuthNavigation = () => {
  const [currentUser, setCurrentUser] = useState(null);
  const [shouldRenderLogin, setShouldRenderLogin] = useState(false); // Introduce shouldRenderLogin state

  useEffect(() => {
    const fetchData = async () => {
      const user = await AsyncStorageManager.getItem('user');
      setCurrentUser(user);
      setShouldRenderLogin(true); // Set shouldRenderLogin to true once data is fetched
    };

    const fetchDataTimeout = setTimeout(() => {
      fetchData();
    }, 500); // Adjust the delay time as needed

    const listenerCallback = () => {
      clearTimeout(fetchDataTimeout); // Clear the timeout if the listener callback is triggered before the timeout finishes
      fetchData();
    };

    AsyncStorageManager.addListener('user', listenerCallback);

    return () => {
      AsyncStorageManager.removeListener('user', listenerCallback);
      clearTimeout(fetchDataTimeout); // Clear the timeout on component unmount
    };
  }, []);

  const handleSignIn = async (userData) => {
    await AsyncStorageManager.signIn(userData);
    setCurrentUser(userData);
  };

  const handleSignOut = async () => {
    await AsyncStorageManager.signOut();
    setCurrentUser(null);
  };

  if (!shouldRenderLogin) {
    return null; // Return null or loading indicator before rendering the login page
  }

  return (
    <>
      {currentUser !== null ? (
        <SignedInStack signOut={handleSignOut} />
      ) : (
        <SignedOutStack signIn={handleSignIn} />
      )}
    </>
  );
};

export default AuthNavigation;
