import React, { useState, useEffect } from 'react';
import { View, ActivityIndicator, Text, Dimensions } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { LineChart } from 'react-native-chart-kit';
import { useFocusEffect } from '@react-navigation/native';
import AppConfig from './config';

const ChartActivity = () => {
  const [chartData, setChartData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [id, setId] = useState(0);

  const fetchData = async () => {
    try {
      
      const response = await axios.get(AppConfig.etudiantUrl +`/courbe?id=${id}`);
      console.log('API Response:', response.data);

      if (Object.keys(response.data).length === 0) {
        setLoading(false);
        return;
      }

      const { data } = response;
      const names = Object.keys(data);
      const nbrs = Object.values(data);

      setChartData({ names, nbrs });
      setLoading(false);
    } catch (error) {
      console.error('Error fetching data:', error);
      setLoading(false);
    }
  };

  useEffect(() => {
    const getId = async () => {
      try {
        let data = await AsyncStorage.getItem('user');
        data = JSON.parse(data);
        setId(data.id);
      } catch (error) {
        console.error('Error fetching code: ', error);
      }
    };

    getId();
  }, []);

  useFocusEffect(
    React.useCallback(() => {
      fetchData();
    }, [id]) // Call fetchData whenever 'id' changes
  );

  const screenWidth = Dimensions.get('window').width;

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#ffffff' }}>
      <Text style={{ fontSize: 20, fontWeight: 'bold', marginBottom: 30 ,marginTop: 100}}>Grades per Assignment</Text>
  
      {loading ? (
        <ActivityIndicator size="large" color="black" />
      ) : (
        chartData && chartData.names.length > 0 ? (
          <LineChart
            data={{
              labels: chartData.names,
              datasets: [{ data: chartData.nbrs }],
            }}
            width={screenWidth}
            height={700}
            chartConfig={{
              backgroundGradientFrom: '#ffffff',
              backgroundGradientTo: '#ffffff',
              color: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
              labelColor: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
              propsForDots: {
                r: '6',
                strokeWidth: '2',
                stroke: '#ffa726',
              },
              barPercentage: 0.5,
              yAxisSuffix: '', // No suffix
              yAxisInterval: 1, // Set the interval to 1
              min: Math.min(...chartData.nbrs) - 5, // Adjust the minimum value
            }}
            bezier
          />
        ) : (
          <Text>No data available</Text>
        )
      )}
    </View>
  );
}

export default ChartActivity;
