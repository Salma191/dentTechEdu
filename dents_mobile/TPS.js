import React, { useEffect, useState } from 'react';
import { View, ToastAndroid, ActivityIndicator, Text } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import TPActivityLayout from './TPActivityLayout';
import axios from 'axios';
import { useFocusEffect } from '@react-navigation/native';
import AppConfig from './config';

const TPS = () => {
    const [pws, setPws] = useState([]);
    const [id, setId] = useState('');
    const [loading, setLoading] = useState(true);

    const loadProfs = async (userId) => {
        const url = AppConfig.etudiantUrl  +`/studentpw?id=${encodeURIComponent(userId)}`;
        console.log('URL:', url);
        try {
            const response = await axios.get(url);
            const data = response.data;
            if (Array.isArray(data)) {
                const parsedPws = data.map((item) => ({
                    internes: item.internes,
                    externes: item.externes,
                    image: item.image1 ? decodeURIComponent(item.image1) : null,
                    date: item.date,
                    remarque: item.remarque,
                    note: item.note,
                    depouilles: item.depouilles,
                    convergence: item.convergence,
                    pwtitle: item.pw.title
                }));
                setPws(parsedPws);
            } 
        } catch (error) {
            console.log('An Error Occurred. Please Retry Later.: ', error);
            ToastAndroid.show('Error : An Error Occurred. Please Retry Later.', ToastAndroid.SHORT);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const getIdAndLoadProfs = async () => {
            try {
                let data = await AsyncStorage.getItem('user');
                data = JSON.parse(data);
                setId(data.id);
                await loadProfs(data.id);
            } catch (error) {
                console.error('Error fetching code: ', error);
            }
        };

        getIdAndLoadProfs();
    }, []);

    useFocusEffect(
        React.useCallback(() => {
            loadProfs(id);
        }, [id]) // Call loadProfs whenever 'id' changes
    );

    return (
        <View style={{ flex: 1 }}>
            {loading ? (
                <ActivityIndicator size="large" color="black"/>
            ) : (
                <TPActivityLayout pws={pws} />
            )}
        </View>
    );
};

export default TPS;
