import { ScrollView, View, Alert, Button, ToastAndroid, Image, StyleSheet, Animated, Text, TouchableOpacity, Modal, Dimensions, useWindowDimensions, TouchableWithoutFeedback } from 'react-native';
import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import DropDownPicker from 'react-native-dropdown-picker';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';

export default DentChoose = () => {

  const navigation = useNavigation();



  const [couleur, setCouleur] = useState('rgba(255, 255, 255, 0)');
  const [dent, setDent] = useState(0);
  const [idtp, setCode] = useState('');
  const [PW,setPws]=useState([]);

  useEffect(() => {
    const getcode = async () => {
      try {
        let data = await AsyncStorage.getItem('user');
        data = JSON.parse(data);
        // Extraire la valeur de la clé "code"
        const groupCode = data.groupe.id;
        setCode(groupCode);
      } catch (error) {
        console.error('Error fetching code: ', error);
      }
    };

    getcode();
  }, []);
  const [open, setOpen] = useState(false);
  const [toothId, setToothId] = useState(null);
  const [coloredTooth, setColoredTooth] = useState([]);
  const [value, setValue] = useState(null);
  const [go, setGo] = useState(false);
  const [items, setItems] = useState([]);

  useEffect(() => {
    const fetchPreparationTypes = async () => {
      try {
        const response = await axios.get('http://192.168.0.92:5050/etudiant/allpw');
        if (response.status === 200) {
          // Assuming the response data is an array of preparation types
          const preparationTypes = response.data.map((type) => ({
            label: type,
            value: type,
          }));
          setItems(preparationTypes);
        } else {
          console.error('Failed to fetch preparation types');
        }
      } catch (error) {
        console.error('Error fetching preparation types:', error);
      }
    };

    fetchPreparationTypes();
  }, []);


  const onPressLogin = async (item) => {
   setToothId([]);
   setColoredTooth([]);
    try {
      const endpoint = `http://192.168.0.92:5050/etudiant/checkpw?params=${encodeURIComponent(item.label)},${encodeURIComponent(idtp)}`;
      const response = await axios.post(endpoint);
      if (response.status === 200) {
        const data = response.data;
        
        setPws(data);
        // Créer une liste de tous les IDs de dents et des documents correspondants
        const toothIds = data.map(pw => parseInt(pw.tooth.code, 10));
        const documents = data.map(pw => decodeURIComponent(pw.docs));
        // Mettre à jour les états avec les listes créées
        console.log(toothIds);
        setToothId(toothIds);
        setColoredTooth(toothIds);
        setGo(true);
      } else {
        ToastAndroid.show('Error: An Error Occurred', ToastAndroid.SHORT);
      }
    } catch (error) {
      ToastAndroid.show('No PW for this preparation type', ToastAndroid.SHORT);
    }
  };
 
  const handleButtonClick = async (dent) => {
    setDent(dent)
    if (!go) {
      navigation.navigate('choose');
    }
    else if (coloredTooth.includes(dent)) {
      // Recherche du TP correspondant au tooth.code dans la liste des PW
      const matchingPW = PW.find(pw => pw.tooth.code === dent.toString());
      
      if (matchingPW) {
        // Stockage du TP correspondant dans AsyncStorage
        await AsyncStorage.setItem('pw', JSON.stringify(matchingPW));
        navigation.navigate('les travaux pratiques');
      } else {
        // Si aucun TP correspondant n'est trouvé, afficher un message d'erreur
        ToastAndroid.show('No matching TP found', ToastAndroid.SHORT);
      }
    }    
    else {
      navigation.navigate('error');
    }
  }





  return (
    <View style={{ flexDirection: 'column', alignContent: 'center', justifyContent: 'center' }}>
      <Text style={{ color: 'black', fontSize: 16, marginLeft: 20, marginBottom: 20, marginTop: 15 }}>Choose The Preparation Type : </Text>

      <DropDownPicker
        style={{ backgroundColor: 'lightgray' }}
        containerStyle={{ marginLeft: 20, width: 350 }}
        open={open}
        value={value}
        items={items}
        setOpen={setOpen}
        setValue={setValue}
        setItems={setItems}
        onOpen={() => {
          console.log('Dropdown opened');
        }}
        onClose={() => {
          console.log('Dropdown closed');
        }}
        onSelectItem={(item) => {
          onPressLogin(item);
        }}
      />


      <View style={{ alignItems: 'center', alignContent: 'center', position: 'relative' ,marginTop:70}}>
        <Image
          style={{ width: '89%' }}
          source={require('./assets/fix2.png')} // Replace with the actual image source

        />
        <TouchableOpacity //28
          style={{ position: 'absolute', top: 180, left: 285 }}
          onPress={() => handleButtonClick(28)}
        >
          <View
            id='28'
            style={{
              backgroundColor: coloredTooth.includes(28) ? 'blue' : couleur,
              padding: 10,
              width: 10,
              height: 5
            }}
          />

        </TouchableOpacity>
        <TouchableOpacity //27
          style={{ position: 'absolute', top: 144, left: 280 }}
          onPress={() => handleButtonClick(27)}
        >

          <View id='27' style={{ backgroundColor: coloredTooth.includes(27) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>

        <TouchableOpacity //26
          style={{ position: 'absolute', top: 112, left: 270 }}
          onPress={() => handleButtonClick(26)}
        >
          <View id='27' style={{ backgroundColor: coloredTooth.includes(27) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>

        <TouchableOpacity //25
          style={{ position: 'absolute', top: 91, left: 268 }}
          onPress={() => handleButtonClick(25)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(25) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //24
          style={{ position: 'absolute', top: 60, left: 255 }}
          onPress={() => handleButtonClick(24)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(24) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //23
          style={{ position: 'absolute', top: 30, left: 250 }}
          onPress={() => handleButtonClick(23)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(23) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //22
          style={{ position: 'absolute', top: 20, left: 225 }}
          onPress={() => handleButtonClick(22)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(22) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>
        <TouchableOpacity //21
          style={{ position: 'absolute', top: 10, left: 198 }}
          onPress={() => handleButtonClick(21)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(21) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>
        <TouchableOpacity //11
          style={{ position: 'absolute', top: 10, left: 170 }}
          onPress={() => handleButtonClick(11)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(11) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //12
          style={{ position: 'absolute', top: 25, left: 148 }}
          onPress={() => handleButtonClick(12)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(12) ? 'blue' : couleur, width: 11, height: 11 }} />

        </TouchableOpacity>
        <TouchableOpacity //13
          style={{ position: 'absolute', top: 40, left: 125 }}
          onPress={() => handleButtonClick(13)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(13) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //14
          style={{ position: 'absolute', top: 60, left: 110 }}
          onPress={() => handleButtonClick(14)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(14) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //15
          style={{ position: 'absolute', top: 86, left: 100 }}
          onPress={() => handleButtonClick(15)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(15) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>
        <TouchableOpacity //16
          style={{ position: 'absolute', top: 110, left: 85 }}
          onPress={() => handleButtonClick(16)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(16) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>
        <TouchableOpacity //17
          style={{ position: 'absolute', top: 140, left: 78 }}
          onPress={() => handleButtonClick(17)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(17) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>

        <TouchableOpacity //18
          style={{ position: 'absolute', top: 178, left: 80 }}
          onPress={() => handleButtonClick(18)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(18) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>

        <TouchableOpacity //48
          style={{ position: 'absolute', top: 250, left: 78 }}
          onPress={() => handleButtonClick(48)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(48) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>

        <TouchableOpacity //47
          style={{ position: 'absolute', top: 290, left: 83 }}
          onPress={() => handleButtonClick(47)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(47) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>

        <TouchableOpacity //46
          style={{ position: 'absolute', top: 331, left: 93 }}
          onPress={() => handleButtonClick(46)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(46) ? 'blue' : couleur, padding: 10, width: 5, height: 5 }} />

        </TouchableOpacity>
        <TouchableOpacity //45
          style={{ position: 'absolute', top: 364, left: 110 }}
          onPress={() => handleButtonClick(45)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(45) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>

        <TouchableOpacity //44
          style={{ position: 'absolute', top: 383, left: 118 }}
          onPress={() => handleButtonClick(44)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(44) ? 'blue' : couleur, width: 15, height: 15 }} />

        </TouchableOpacity>
        <TouchableOpacity //43
          style={{ position: 'absolute', top: 398, left: 135 }}
          onPress={() => handleButtonClick(43)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(43) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>
        <TouchableOpacity //42
          style={{ position: 'absolute', top: 407, left: 152 }}
          onPress={() => handleButtonClick(42)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(42) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>

        <TouchableOpacity //41
          style={{ position: 'absolute', top: 420, left: 170 }}
          onPress={() => handleButtonClick(41)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(41) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>
        <TouchableOpacity //31
          style={{ position: 'absolute', top: 420, left: 190 }}
          onPress={() => handleButtonClick(31)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(31) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>
        <TouchableOpacity //32
          style={{ position: 'absolute', top: 420, left: 218 }}
          onPress={() => handleButtonClick(32)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(32) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>
        <TouchableOpacity //33
          style={{ position: 'absolute', top: 400, left: 227 }}
          onPress={() => handleButtonClick(33)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(33) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>

        <TouchableOpacity //34
          style={{ position: 'absolute', top: 386, left: 245 }}
          onPress={() => handleButtonClick(34)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(34) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>
        <TouchableOpacity //35
          style={{ position: 'absolute', top: 368, left: 252 }}
          onPress={() => handleButtonClick(35)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(35) ? 'blue' : couleur, width: 13, height: 13 }} />

        </TouchableOpacity>
        <TouchableOpacity //36
          style={{ position: 'absolute', top: 318, left: 260 }}
          onPress={() => handleButtonClick(36)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(36) ? 'blue' : couleur, width: 20, height: 20 }} />

        </TouchableOpacity>
        <TouchableOpacity //37
          style={{ position: 'absolute', top: 290, left: 272 }}
          onPress={() => handleButtonClick(37)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(37) ? 'blue' : couleur, width: 20, height: 20 }} />

        </TouchableOpacity>
        <TouchableOpacity //38
          style={{ position: 'absolute', top: 250, left: 280 }}
          onPress={() => handleButtonClick(38)}
        >
          <View style={{ backgroundColor: coloredTooth.includes(38) ? 'blue' : couleur, width: 20, height: 20 }} />

        </TouchableOpacity>
      </View>


    </View>

  )

}