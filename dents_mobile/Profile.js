import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ToastAndroid, ScrollView, Image } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { launchImageLibrary } from 'react-native-image-picker';
import AppConfig from './config';

const Profile = () => {
  const [fname, setFname] = useState('');
  const [lname, setLname] = useState('');
  const [uname, setUname] = useState('');
  const [number, setNumber] = useState('');
  const [email, setEmail] = useState('');
  const [groupe, setGroupe] = useState('');
  const [photoBase64, setPhotoBase64] = useState(null);

  useEffect(() => {
    const getData = async () => {
      let data = await AsyncStorage.getItem('user');
      userdata = JSON.parse(data);
      const url = AppConfig.etudiantUrl + '/getstudent';

      const response = await axios.post(url, {
        userName: userdata.userName,
      });

      // Si le statut de la réponse est "FORBIDDEN", cela signifie que l'authentification a échoué
      if (response.status === 200) {
        const data = response.data;
        setGroupe(data.groupe.code);
        setFname(data.firstName);
        setLname(data.lastName);
        setUname(data.userName);
        setNumber(data.number);
        setEmail(data.email);
        if (data.photo) {
          const decodedPhoto = decodeURIComponent(data.photo);
          setPhotoBase64(decodedPhoto);
        }

      }
    }; getData();
  }, []);


  const handleImagePicker = () => {
    let options = {
      mediaType: 'photo', // Spécifier le type de média comme une photo
      includeBase64: true,
      maxHeight: 200,
      maxWidth: 200,
    };

    launchImageLibrary(options, (response) => {
      if (!response.didCancel && !response.errorCode) {
        setPhotoBase64(response.assets[0].base64);
      }
    });
  };




  const handleSubmit = async () => {
    const url = AppConfig.etudiantUrl + `/changeProfil?email=${encodeURIComponent(email)}`;
    const data = {
      email: email,
      firstName: fname,
      lastName: lname,
      userName: uname,
      number: number,
      photo: photoBase64
    };

    try {
      const response = await axios.post(url, data);
      if (response.status === 200) {
        ToastAndroid.show('Pofile Changed Successfully', ToastAndroid.SHORT);
      }
    } catch (error) {
      console.error('Error fetching data: ', error);
      ToastAndroid.show('Erreur : An Error Occurred. Please Retry Later.', ToastAndroid.SHORT);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.scrollViewContent}>
      <View style={styles.container}>
        <View style={styles.detailsContainer}>
          <View style={styles.imageContainer}>
            {photoBase64 && (
              <Image
                style={styles.profileImage}
                source={{ uri: `data:image/jpeg;base64,${photoBase64}` }}
              />
            )}
            <TouchableOpacity onPress={handleImagePicker}>
              <Text style={[styles.change, { textDecorationLine: 'underline' }]}>Change Image</Text>
            </TouchableOpacity>
          </View>
          <View style={styles.detailsRow}>
            <Image
              source={require('./android/app/src/main/res/mipmap-hdpi/i1.jpeg')}
              style={{ width: 20, height: 20, marginRight: 10 }}
            />
            <Text style={styles.label}>First Name:</Text>
            <TextInput
              style={styles.input}
              value={fname}
              onChangeText={(text) => setFname(text)}
            />
          </View>
          <View style={styles.detailsRow}>
            <Image
              source={require('./android/app/src/main/res/mipmap-hdpi/i1.jpeg')}
              style={{ width: 20, height: 20, marginRight: 10 }}
            />
            <Text style={styles.label}>Last Name:</Text>
            <TextInput
              style={styles.input}
              value={lname}
              onChangeText={(text) => setLname(text)}
            />
          </View>
          <View style={styles.detailsRow}>
            <Image
              source={require('./android/app/src/main/res/mipmap-hdpi/i3.jpeg')}
              style={{ width: 20, height: 20, marginRight: 10 }}
            />
            <Text style={styles.label}>Username :</Text>
            <TextInput
              style={styles.input}
              value={uname}
              onChangeText={(text) => setUname(text)}
            />
          </View>
          <View style={styles.detailsRow}>
            <Image
              source={require('./android/app/src/main/res/mipmap-hdpi/i4.jpeg')}
              style={{ width: 20, height: 20, marginRight: 10 }}
            />
            <Text style={styles.label}>Email          :</Text>
            <TextInput
              style={styles.input}
              value={email}
              onChangeText={(text) => setEmail(text)}
            />
          </View>
          <View style={styles.detailsRow}>
            <Image
              source={require('./android/app/src/main/res/mipmap-hdpi/i5.jpeg')}
              style={{ width: 20, height: 20, marginRight: 10 }}
            />
            <Text style={styles.label}>Number     :</Text>
            <TextInput
              style={styles.input}
              value={number}
              onChangeText={(text) => setNumber(text)}
            />
          </View>
          <View style={styles.detailsRow}>
            <Image
              source={require('./android/app/src/main/res/mipmap-hdpi/i6.jpeg')}
              style={{ width: 20, height: 20, marginRight: 10 }}
            />
            <Text style={styles.label}>Group        :</Text>
            <Text style={styles.value}>{groupe}</Text>
          </View>
          <TouchableOpacity style={styles.button} onPress={handleSubmit}>
            <Text style={styles.buttonText}>Submit</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  scrollViewContent: {
    flexGrow: 1,
  },
  container: {
    flex: 1,
    padding: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },
  detailsContainer: {
    alignItems: 'center',
  },
  imageContainer: {
    alignItems: 'center',
    marginBottom: 20,
  },
  change: {
    color: 'black',
    fontWeight: 'bold'
  },

  profileImage: {
    width: 200,
    height: 200,
    borderRadius: 100, // pour rendre l'image circulaire
    marginBottom: 10,
  },
  detailsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
    marginTop: 6
  },
  label: {
    fontSize: 18,
    fontWeight: 'bold',
    marginRight: 10,
  },
  value: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 120,
    padding: 10,
    fontSize: 15,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 120,
    padding: 10,
    fontSize: 15,

  },
  button: {
    backgroundColor: 'black',
    padding: 10,
    borderRadius: 20,
    marginTop: 25,
    width: 120,
    height: 40
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
  },
});

export default Profile;
