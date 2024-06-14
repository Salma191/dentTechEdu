import React from 'react';
import { View, Text, StyleSheet, Image ,TouchableOpacity} from 'react-native';
import { useNavigation } from '@react-navigation/native';

const Error = () => {
  const navigation = useNavigation();
  const handleGoBack = () => {
    navigation.navigate('Préparation');
  };
  return (
    <View style={styles.container}>
      <View style={styles.imageContainer}>
        <Image
          style={styles.image}
          source={require('./assets/aie.png')} 
        />
      </View>
      <Text style={styles.errorText}>No practical work available for this tooth and preparation type.</Text>
      <TouchableOpacity style={styles.button} onPress={handleGoBack}>
        <Text style={styles.buttonText}>Go Back</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#FFFFFF', // background color of the container
  },
  imageContainer: {
    position: 'absolute',
    top: 200, // adjust as needed
    left: 150, // adjust as needed
  },
  image: {
    width: 150, // adjust as needed
    height: 150, 
    // adjust as needed
  },
  errorText: {
    color: '#444444', // dark gray color
    fontSize: 16, // adjust as needed
    marginTop: 100, // adjust as needed
    textAlign: 'center', // center the text horizontally
    marginBottom:20
  },
  button: {
    backgroundColor: 'black', 
    borderRadius: 5,
    alignSelf: 'center',
    marginTop:10 // align the button horizontally to center
  },
  buttonText: {
    color: '#FFFFFF', // white color
    fontSize: 16,
    paddingHorizontal: 20, // add padding to the button text
    paddingVertical: 10, // add padding to the button text
  },
});

export default Error;
