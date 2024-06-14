import React, { useEffect, useState } from 'react';
import { View, Text, ToastAndroid, TouchableOpacity, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import PwActivityLayout from './PwActivityLayout';
import axios from 'axios';
import { useFocusEffect } from '@react-navigation/native';

import AppConfig from './config';


      
   

const DoTp = () => {
    const [pws, setPws] = useState([]);
    const [code, setCode] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const getcode = async () => {
            try {
                let data = await AsyncStorage.getItem('user');
                data = JSON.parse(data);
                // Extraire la valeur de la clé "code"
                const groupCode = data.groupe.code;
                setCode(groupCode);
            } catch (error) {
                console.error('Error fetching code: ', error);
            }
        };

        getcode();
    }, []);

    useEffect(() => {
        if (code !== '') {
            loadProfs();
        }
    }, [code]);

    const loadProfs = async () => {
        console.log('Code:', code);
        const url = AppConfig.etudiantUrl + `/all?code=${encodeURIComponent(code)}`;
        console.log('URL:', url);
        try {
            const response = await axios.get(url);
            const data = response.data;
            if (Array.isArray(data)) {
                const parsedPws = data.map((item) => ({
                    id: item.id,
                    title: item.title,
                    docs: item.docs,
                    objectif: item.objectif,
                    nom: item.tooth.name
                }));
                setPws(parsedPws);
            } else {
                console.error('Error : An Error Occurred. Please Retry Later.', data);
                ToastAndroid.show('Error : An Error Occurred. Please Retry Later.', ToastAndroid.SHORT);
            }
        } catch (error) {
            console.error('Error : An Error Occurred. Please Retry Later.', error);
            ToastAndroid.show('Error : An Error Occurred. Please Retry Later.', ToastAndroid.SHORT);
        } finally {
            setLoading(false);
        }
    };
    
    useFocusEffect(
        React.useCallback(() => {
            if (code !== '') {
                loadProfs();
            }
        }, [code]) // Call loadProfs whenever 'code' changes
    );

    return (
        <View style={{ flex: 1 }}>
            {loading ? (
                <ActivityIndicator size="large" color="black"/>
            ) : (
                <PwActivityLayout pws={pws} />
            )}
        </View>
    );
};

export default DoTp;
