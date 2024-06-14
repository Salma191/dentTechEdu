import React from 'react';
import { View, StyleSheet, FlatList } from 'react-native';
import TPAdapter from './TPAdapter';

const TPActivityLayout = ({ pws }) => {
    return (
        <View style={styles.container}>
            <FlatList
                data={pws}
                renderItem={({ item }) => <TPAdapter pw={item} />}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#FFF', // Vous pouvez le remplacer par votre couleur d'arrière-plan
        paddingTop: 10,
    },
});

export default TPActivityLayout;