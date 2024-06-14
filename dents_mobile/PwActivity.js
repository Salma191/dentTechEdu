import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Image, Dimensions, ScrollView, Share, Platform, Modal } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';
import Pdf from 'react-native-pdf';
import RNFetchBlob from 'rn-fetch-blob';

const PwActivity = () => {
  const [title, setTitle] = useState(null);
  const [objectif, setObjectif] = useState(null);
  const [docs, setDocs] = useState(null);
  const [Dent, setDent] = useState('');
  const [Prep, setPreparation] = useState('');
  const [id, setId] = useState(0);
  const [isPdfFullscreen, setIsPdfFullscreen] = useState(false);
  const navigation = useNavigation();

  useEffect(() => {
    const getCode = async () => {
      try {
        let data = await AsyncStorage.getItem('pw');
        if (data) {
          data = JSON.parse(data);
          setTitle(data.title);
          setObjectif(data.objectif);
          setDocs(data.docs);
          setDent(data.tooth.name);
          setPreparation(data.preparation.type);
          setId(data.id);
        }
      } catch (error) {
        console.error('Error fetching code: ', error);
      }
    };

    getCode();
  }, []);

  const handleGoHome = async () => {
    try {
      await AsyncStorage.setItem('tpID', id.toString());
      navigation.navigate("Home");
    } catch (error) {
      console.error('Error saving TP ID:', error);
    }
  };

  const togglePdfFullscreen = () => {
    setIsPdfFullscreen(!isPdfFullscreen);
  };

  return (
    <ScrollView>
      {docs ? (

        <Modal visible={isPdfFullscreen} transparent={true}>
          <View style={styles.modalContainer}>
            <TouchableOpacity style={styles.closeButton} onPress={togglePdfFullscreen}>
              <Text style={styles.closeButtonText}>Fermer</Text>
            </TouchableOpacity>
            <Pdf
              source={{ uri: `data:application/pdf;base64,${docs}` }}
              onLoadComplete={(numberOfPages, filePath) => {
                console.log(`Number of pages: ${numberOfPages}`);
              }}
              onError={(error) => {
                console.log(error);
              }}
              style={styles.pdf}
            />
          </View>
        </Modal>
      ) : (
        <Text>Loading PDF...</Text>
      )}
      <View style={styles.container}>
        <Image
          source={{ uri: 'https://th.bing.com/th/id/R.9d16a68596a60ad188ba62d060395614?rik=hdk2ObTaZWhpXA&riu=http%3a%2f%2fwww.ensaj.ucd.ac.ma%2fwp-content%2fthemes%2fBuntington%2flogosite.jpg&ehk=h8yhY6y3aq3q211EJ2773mfCIpjJ4dZodZJr7whUkL0%3d&risl=&pid=ImgRaw&r=0' }}
          style={styles.logo}
        />
        <Text style={styles.valueTitle}>Statement Of {title}</Text>
        <Text style={styles.label}>Objective:</Text>
        <Text style={styles.value}>{objectif}</Text>
        <Text style={styles.label}>Tooth:</Text>
        <Text style={styles.value}>{Dent}</Text>
        <Text style={styles.label}>Preparation:</Text>
        <Text style={styles.value}>{Prep}</Text>
        <TouchableOpacity style={styles.button} onPress={togglePdfFullscreen}>
          <Text style={styles.seePdfButtonText}>See Document</Text>
        </TouchableOpacity>
      </View>
      <View style={styles.buttonContainer}>
        <TouchableOpacity style={styles.button2} onPress={handleGoHome}>
          <Text style={styles.buttonText}>Do The Lab</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    borderWidth: 2,
    height: 500,
    borderColor: 'black',
    margin: 20,
    backgroundColor: 'white',
    alignItems: 'center',
    marginTop: 80,
  },
  logo: {
    width: 340,
    height: 100,
    resizeMode: 'contain',
  },
  label: {
    textDecorationLine: 'underline',
    fontWeight: 'bold',
    fontSize: 16,
    color: 'black',
    marginRight: 5,
  },
  valueTitle: {
    marginTop: 20,
    marginBottom: 40,
    fontSize: 30,
    color: 'black',
  },
  value: {
    fontSize: 16,
    color: 'black',
    marginBottom: 20,
  },
  buttonContainer: {
    alignItems: 'center',
    marginTop: 20,
  },
  button2: {
    backgroundColor: 'black',
    paddingHorizontal: 20,
    paddingVertical: 10,
    alignItems: 'center',
    width: 170,
    height: 45,
    borderRadius: 20,
  },
  button: {
    backgroundColor: 'black',
    paddingHorizontal: 20,
    paddingVertical: 10,
    alignItems: 'center',
    width: 170,
    height: 45,
    marginTop: 30,
    borderRadius: 20,
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    textAlign: 'center',
    fontWeight: 'bold',
  },
  pdfContainer: {
    flex: 1,
    width: Dimensions.get('window').width,
    height: Dimensions.get('window').height * 0.5, // Reducing the height to half of the window height
  },
  pdf: {
    flex: 1,
    width: '100%',
    height: '100%',
  },
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
  },
  closeButton: {
    position: 'absolute',
    top: 20,
    right: 20,
    zIndex: 1,
  },
  closeButtonText: {
    color: 'black',
    fontSize: 16,
  },
  seePdfButton: {
    backgroundColor: '#007bff',
    paddingHorizontal: 20,
    paddingVertical: 10,
    marginTop: 10,
    borderRadius: 20,
  },
  seePdfButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});

export default PwActivity;
