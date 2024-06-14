import React from 'react';
import { View, StyleSheet, FlatList } from 'react-native';
import PwAdapter from './PwAdapter';

const PwActivityLayout = ({ pws }) => {
    return (
        <View style={styles.container}>
            <FlatList
                data={pws}
                renderItem={({ item }) => <PwAdapter pw={item} />}
                keyExtractor={(item) => item.id.toString()}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#FFF',
        paddingTop: 10,
    },
});

export default PwActivityLayout;