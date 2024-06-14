import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Image, Modal, Dimensions } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Pdf from 'react-native-pdf';
import RNFetchBlob from 'rn-fetch-blob';

const PwAdapter = ({ pw }) => {
    const [isPdfFullscreen, setIsPdfFullscreen] = useState(false);
    const togglePdfFullscreen = () => {
        setIsPdfFullscreen(!isPdfFullscreen);
    };

    return (
        <TouchableOpacity style={styles.container}>
            <View >
                <Image
                    source={require('./android/app/src/main/res/mipmap-hdpi/snina.png')}
                    style={{ width: 130, height: 130 }}
                    resizeMode="contain"
                />
            </View>
            <View style={styles.fieldsContainer}>
                <View style={styles.row}>
                    <Text style={styles.label}>Title           : </Text>
                    <Text style={styles.value}>{pw.title}</Text>
                </View>
                <View style={styles.row}>
                    <Text style={styles.label}>Objective   : </Text>
                    <Text style={[styles.value, { flexWrap: 'wrap', width: '60%' }]} numberOfLines={2}>{pw.objectif}</Text>
                </View>
                <View style={styles.row}>
                    <TouchableOpacity style={styles.seePdfButton} onPress={togglePdfFullscreen}>
                        <Text style={styles.seePdfButtonText}>See Documents</Text>
                    </TouchableOpacity>
                    {pw.docs ? (

                        <Modal visible={isPdfFullscreen} transparent={true}>
                            <View style={styles.modalContainer}>
                                <TouchableOpacity style={styles.closeButton} onPress={togglePdfFullscreen}>
                                    <Text style={styles.closeButtonText}>Fermer</Text>
                                </TouchableOpacity>
                                <Pdf
                                    source={{ uri: `data:application/pdf;base64,${pw.docs}` }}
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

                </View>
            </View>
        </TouchableOpacity>
    );
};

const styles = StyleSheet.create({
    container: {
        borderWidth: 1,
        borderColor: 'black',
        borderRadius: 10,
        backgroundColor:'#f2f2f2',
        padding: 5,
        margin:10,
        flexDirection: 'row', // Pour aligner les éléments horizontalement
        alignItems: 'center', // Pour aligner les éléments verticalement
    },

    fieldsContainer: {
        flex: 1,
        marginLeft: 20 // Pour que les champs occupent tout l'espace restant
    },
    row: {
        flexDirection: 'row',
        justifyContent: 'flex-start', // Aligner les éléments horizontalement
        alignItems: 'center', // Aligner les éléments verticalement au centre
        marginBottom: 5, // Espacement entre chaque paire label-valeur
    },
    label: {
        fontWeight: 'bold',
        fontSize: 16,
        // Espacement entre les étiquettes et leurs valeurs
        color: 'black',
    },
    value: {
        fontSize: 16,
        color: 'black',
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
    label: {
        fontWeight: 'bold',
        fontSize: 16,
        color: 'black',
        marginRight: 5,
    },
    value: {
        fontSize: 16,
        color: 'black',
    },
    button: {
        backgroundColor: '#007bff',
        paddingHorizontal: 20,
        paddingVertical: 10,
        marginTop: 20,
        borderRadius: 20,
    },
    buttonText: {
        color: 'white',
        fontSize: 18,
        fontWeight: 'bold',
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
        backgroundColor: 'black',
        paddingHorizontal: 10,
        paddingVertical: 10,
        marginTop: 10,
        borderRadius: 20,
    },
    seePdfButtonText: {
        color: 'white',
        fontSize: 12,
        fontWeight: 'bold',
    },
});

export default PwAdapter;