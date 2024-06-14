import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Image } from 'react-native';

const TPAdapter = ({ pw }) => {

    return (
        <TouchableOpacity style={styles.container} >
            {/* { <View >
        <Image
                  source={{ uri: `data:image/jpeg;base64,${pw.image}` }}
                  style={{ width: 130, height: 130 }}
                  resizeMode="contain"
                />
        </View> } */}

            <View style={styles.fieldsContainer}>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>🖋️</Text>
                    <Text style={styles.label}>Title                 : </Text>
                    <Text style={styles.value}>{pw.pwtitle}</Text>
                </View>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>📏</Text>
                    <Text style={styles.label}>Internal           : </Text>
                    <Text style={[styles.value, { flexWrap: 'wrap', width: '50%' }]}>{pw.internes} degree</Text>
                </View>

                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>📏</Text>
                    <Text style={styles.label}>External          : </Text>
                    <Text style={[styles.value, { flexWrap: 'wrap', width: '50%' }]}>{pw.externes} degree</Text>
                </View>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>📐</Text>
                    <Text style={styles.label}>draft angles   : </Text>
                    <Text style={[styles.value, { flexWrap: 'wrap', width: '50%' }]}>{pw.depouilles} degree</Text>
                </View>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>✔️</Text>
                    <Text style={styles.label}>convergence  : </Text>
                    <Text style={styles.value}>{pw.convergence}</Text>
                </View>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>📅</Text>
                    <Text style={styles.label}>Date                 : </Text>
                    <Text style={styles.value}>{pw.date}</Text>
                </View>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>🔢</Text>
                    <Text style={styles.label}>Mark                : </Text>
                    <Text style={styles.value}>{pw.note}</Text>
                </View>
                <View style={styles.row}>
                    <Text style={{ marginRight: 10 }}>📝</Text>
                    <Text style={styles.label}>Remark           : </Text>
                    <Text style={styles.value}>{pw.remarque}</Text>
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
        fontSize: 18,
        // Espacement entre les étiquettes et leurs valeurs
        color: 'black',
    },
    value: {
        fontSize: 16,
        color: 'black',
    },
});
export default TPAdapter;