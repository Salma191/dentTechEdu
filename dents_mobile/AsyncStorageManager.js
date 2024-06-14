// AsyncStorageManager.js
import AsyncStorage from '@react-native-async-storage/async-storage';

const AsyncStorageManager = {
  async setItem(key, value) {
    try {
      await AsyncStorage.setItem(key, value);
      this.notifyListeners(key);
    } catch (error) {
      console.error('Error setting AsyncStorage item:', error);
    }
  },

  async getItem(key) {
    try {
      const value = await AsyncStorage.getItem(key);
      return value;
    } catch (error) {
      console.error('Error getting AsyncStorage item:', error);
      return null;
    }
  },

  listeners: {},

  addListener(key, callback) {
    if (!this.listeners[key]) {
      this.listeners[key] = [];
    }
    this.listeners[key].push(callback);
  },

  removeListener(key, callback) {
    if (this.listeners[key]) {
      this.listeners[key] = this.listeners[key].filter(
        (listener) => listener !== callback
      );
    }
  },

  notifyListeners(key) {
    if (this.listeners[key]) {
      this.listeners[key].forEach((listener) => listener());
    }
  },
  
  async signOut() {
    try {
      await AsyncStorage.removeItem('user');
      this.notifyListeners('user'); // Notify about user data change (sign-out)
    } catch (error) {
      console.error('Error clearing user data:', error);
    }
  },
  async signIn(userData) {
    try {
      await AsyncStorage.setItem('user', JSON.stringify(userData));
      this.notifyListeners('user'); // Notify about user data change (sign-in)
    } catch (error) {
      console.error('Error storing user data:', error);
    }
  },
};

export default AsyncStorageManager;
