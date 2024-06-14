import React, { useState, useRef, useEffect } from 'react';
import { ScrollView, View, Button, Image, StyleSheet, Animated, Text, TouchableOpacity, Modal, Dimensions, useWindowDimensions, ToastAndroid } from 'react-native';
import * as ImagePicker from 'react-native-image-picker';
import { NativeModules } from 'react-native';
import Svg, { Circle, Line } from 'react-native-svg';
import RNFS from 'react-native-fs';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { PinchGestureHandler, GestureHandlerRootView, State } from 'react-native-gesture-handler';
import AsyncStorage from '@react-native-async-storage/async-storage';
import AsyncStorageManager from './AsyncStorageManager';
import ViewShot from 'react-native-view-shot';
import axios from 'axios';
import AppConfig from './config';

const Home = () => {
  const [images, setImages] = useState([])
  const [imageURI, setImageURI] = useState('')
  const [color_text_1, setColorText_1] = useState('green')
  const [confirmVsible, setConfirmVisible] = useState(false)
  const [color_text_2, setColorText_2] = useState('green')
  const [color_text_3, setColorText_3] = useState('green')
  const [zoomVisible, setZoomVisible] = useState(false);
  const [imagesForViewer, setImagesForViewer] = useState([]);
  const [processedImageToDisplay, setProcessedImage] = useState(null);
  const [countourPoints, setCountourPoints] = useState(null)
  const [points, setPoints] = useState([]);
  const [angles, setAngles] = useState([])
  const [counter, setConter] = useState(0)
  const { ProcessImage } = NativeModules;
  const { height: screenHeight } = useWindowDimensions();
  const [imageDimensions, setImageDimensions] = useState({ width: 0, height: 0 });
  const [visible, setVisible] = useState(true)
  const [visible_before, setVisibleBefore] = useState(true)
  const [visible_send, setSendVisible] = useState(true)
  const [popupvisible, setPopupVisible] = useState(false)
  const [isFinishVisible, setFinishVisible] = useState(true)
  const [imagepopup, setImagePopup] = useState(false)
  const [deptext, setDepText] = useState('')
  const [excessif, setExcissif] = useState(false)
  const scaleValue = useRef(new Animated.Value(1)).current;
  const scaleValue_reset = useRef(new Animated.Value(1)).current;
  const scaleValue_next = useRef(new Animated.Value(1)).current;
  const scaleValue_before = useRef(new Animated.Value(1)).current;
  const scaleValue_analytics = useRef(new Animated.Value(1)).current;
  const scaleValue_logout = useRef(new Animated.Value(1)).current;
  const scaleValue_image = useRef(new Animated.Value(1)).current;
  const scaleValue_camera = useRef(new Animated.Value(1)).current;
  const scaleValue_send = useRef(new Animated.Value(1)).current;
  const [iscloseddep, setClosedDep] = useState(false)
  const baseScale = useRef(new Animated.Value(1)).current;
  const pinchScale = useRef(new Animated.Value(1)).current;
  const panState = useRef(0).current;
  const translateX = useRef(new Animated.Value(0)).current;
  const translateY = useRef(new Animated.Value(0)).current;
  const lastOffset = useRef({ x: 0, y: 0 }).current;
  const [cvg, setConvergence] = useState('');
  const viewRef = useRef();
  const [idtp, setCode] = useState('');
  const currentDate = new Date();
  useEffect(() => {
    const getcode = async () => {
      try {
        let data = await AsyncStorage.getItem('tpID');
        let data2 = await AsyncStorage.getItem('pw');
        data2 = JSON.parse(data2);
        setConvergence(data2.convergence)
        console.log(cvg);
        setCode(data);
      } catch (error) {
        console.error('Error fetching code: ', error);
      }
    };

    getcode();
  }, []);
  const [id, setId] = useState('');

  useEffect(() => {
    const getId = async () => {
      try {
        let data = await AsyncStorage.getItem('user');
        data = JSON.parse(data);
        // Extraire la valeur de la clé "code"
        const Id = data.id;
        setId(Id);
      } catch (error) {
        console.error('Error fetching code: ', error);
      }
    };

    getId();
  }, []);

  const placeholder = {
    label: 'Select an option...',
    value: null,
  };
  const onSend = async () => {


    Animated.sequence([
      Animated.timing(scaleValue_send, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_send, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(async () => {
        setWichtButton('send')
        setConfirmVisible(true)

      }, 50); // Adjust the delay duration as needed
    });





  }
  const options = [
    { label: 'Option 1', value: 'option1' },
    { label: 'Option 2', value: 'option2' },
    { label: 'Option 3', value: 'option3' },
  ];

  const onPinchGestureEvent = Animated.event(
    [
      {
        nativeEvent: {

          scale: pinchScale,


        },
      },
    ],
    { useNativeDriver: true }
  );
  const onPinchHandlerStateChange = (event) => {
    if (event.nativeEvent.oldState === State.ACTIVE) {
      // Ensure that baseScale._value is a number or fallback to 1
      const lastScale = baseScale._value || 1; // Adjustment here

      // Prevent NaN values by constraining scale within a valid range
      const scale = Math.max(0.1, Math.min(lastScale * event.nativeEvent.scale, 3)); // Adjustment here
      pinchScale.setValue(1);
      baseScale.setValue(scale);
      panState.current = 1;
    }
  };
  const onLogout = async () => {
    Animated.sequence([
      Animated.timing(scaleValue_logout, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_logout, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(async () => {
        await AsyncStorageManager.signOut()
      });
    })
  }

  const onPanGestureEvent = Animated.event(
    [
      {
        nativeEvent: {
          translationX: translateX,
          translationY: translateY,
        },
      },
    ],
    { useNativeDriver: true }
  );

  const convertToBase64 = async (imageUri) => {
    try {
      const imageBase64 = await RNFS.readFile(imageUri, 'base64');
      return imageBase64;
    } catch (error) {
      console.error('Error converting image to base64:', error);
      throw error;
    }
  };
  // Function to handle pan gesture state change
  const onPanHandlerStateChange = event => {
    if (event.nativeEvent.oldState === State.ACTIVE) {
      lastOffset.x += event.nativeEvent.translationX;
      lastOffset.y += event.nativeEvent.translationY;

      translateX.setOffset(lastOffset.x);
      translateX.setValue(0);
      translateY.setOffset(lastOffset.y);
      translateY.setValue(0);
      panState.current = 0;
    }
  };
  const onPanGestureEnd = () => {
    translateX.flattenOffset();
    translateY.flattenOffset();
  };
  const transformStyle = {
    transform: [{ scale: Animated.multiply(baseScale, pinchScale).interpolate({ inputRange: [1, 2], outputRange: [1, 2] }) }],
  }; // Access the animated value

  const takePhoto = () => {
    Animated.sequence([
      Animated.timing(scaleValue_camera, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_camera, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(() => {
        const options = {
          mediaType: 'photo',
          quality: 1,
          maxWidth: 1000,
          maxHeight: 1000,
        };


        try {
          if (processedImageToDisplay) {
            setWichtButton('Take Photo')
            setConfirmVisible(true)

          } else {
            ImagePicker.launchCamera(options, (response) => {

              if (!response.didCancel) {
                // Handle the response...
                setImageURI(response.assets[0].uri);
                setImagesForViewer([{ url: response.assets[0].uri }]);
                setConter(1)
                convertImageToBase64(response.assets[0].uri);
                setPoints([]);
              }
            });
          }

        } catch (error) {
          console.error('Error launching cameraa', error);
        }
      }, 50); // Adjust the delay duration as needed
    });


  };
  const selectImage = () => {
    setAngles([])
    setPoints([])
    Animated.sequence([
      Animated.timing(scaleValue, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(() => {

        const options = {
          mediaType: 'photo',
          quality: 1,
          maxWidth: 1000,
          maxHeight: 1000,
        };
        if (processedImageToDisplay) {
          setWichtButton('selectimage')
          setConfirmVisible(true)

        } else {
          ImagePicker.launchImageLibrary(options, (response) => {
            if (!response.didCancel && !response.error) {
              setImageURI(response.assets[0].uri);
              setImagesForViewer([{ url: response.assets[0].uri }]);
              setConter(1)
              convertImageToBase64(response.assets[0].uri);
              setPoints([]);
            }
          });
        }
      }, 50); // Adjust the delay duration as needed
    });

  };
  const onreset = async () => {

    Animated.sequence([
      Animated.timing(scaleValue_reset, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_reset, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(() => {
        setWichtButton('reset')
        setConfirmVisible(true)

      }, 50); // Adjust the delay duration as needed
    });



  }

  const convertImageToBase64 = (uri) => {
    fetch(uri)
      .then((res) => res.blob())
      .then((blob) => {
        const reader = new FileReader();
        reader.onloadend = () => {
          const base64data = reader.result.split(',')[1];
          ProcessImage.applyGrayscaleFilter(base64data, (result) => {



            setProcessedImage(result['processedImage']);
            setCountourPoints(result['tracedPoints'])

          });
        };
        reader.readAsDataURL(blob);
      });
  };
  const [name, setName] = useState('')
  const getDetail = async () => {
    let data = await AsyncStorage.getItem('user')
    data = JSON.parse(data)

    return setName(data["userName"])
  }
  getDetail()
  const renderDrawings = () => {

    const extendedLines = calculateExtendedLines();
    const verticalLines = calculateVerticalLines();
    const intersectionpointrender = findIntersectionPoint();
    const individualintersectionpoint = intersectionpointrender[0]

    if (!angles.some(item => item.id === counter) && points.length == 4) {
      // If the ID doesn't exist, add a new element
      const left_taper_push_1 = 90 - intersectionpointrender[3]
      const right_taper_push_1 = 90 - intersectionpointrender[4]
      const left_taper_push = intersectionpointrender[3]
      const right_taper_push = intersectionpointrender[4]

      if (counter == 1) {

        angles.push({ id: intersectionpointrender[1], 'intersection_angle': intersectionpointrender[2], 'left_taper': left_taper_push_1.toFixed(2), 'right_taper': right_taper_push_1.toFixed(2) })

      } else {

        angles.push({ id: intersectionpointrender[1], 'intersection_angle': intersectionpointrender[2], 'left_taper': left_taper_push.toFixed(2), 'right_taper': right_taper_push.toFixed(2) })

      }

    }





    return (

      <View>
        {points.map((point, index) => (
          <Circle key={index} cx={point.x} cy={point.y} r={2} fill="blue" />
        ))}




        <View style={{ left: points[0]?.x, top: points[0]?.y }}>
          <Text style={{ color: 'red' }}> {angles[counter - 1]?.left_taper} </Text>
        </View>

        <View style={{ left: points[2]?.x, top: points[2]?.y }}>
          <Text style={{ color: 'red' }}> {angles[counter - 1]?.right_taper} </Text>
        </View>
        {intersectionpointrender[0].map((intersection, index) => (

          <Circle key={index} cx={intersection.x} cy={intersection.y} r={2} fill="blue" />
        ))}

        {counter == 3 && intersectionpointrender[0].map((intersection, index) => (
          <View key={index} style={{ left: intersection.x, top: intersection.y }}>
            <Text style={{ color: 'red' }}> {angles[counter - 1]?.intersection_angle} </Text>
          </View>

        ))}
        {extendedLines.map((line, index) => (
          <Line
            key={index}
            x1={line.x1}
            y1={line.y1}
            x2={line.x2}
            y2={line.y2}
            stroke="orange"
            strokeWidth="1"
          />
        ))}
      </View>

    );
  };
  const calculateAngle = (slope1, slope2) => {
    const angleRad = Math.atan(Math.abs((slope2 - slope1) / (1 + slope1 * slope2)));
    const angleDeg = (angleRad * 180) / Math.PI;
    return angleDeg;
  };
  const calculateIntersectionAngle = (slope1, slope2) => {
    if (slope1 !== slope2) {
      // Calculate angle between non-parallel lines
      return calculateAngle(slope1, slope2);
    } else {
      // Lines are parallel, angle between them is 0 degrees
      return 0;
    }
  };
  const calculateVerticalLines = () => {
    const verticalLines = [];
    if (points.length >= 4) {
      // Check if the first three points have been drawn
      // Assume the first point is (x1, y1), the second point is (x2, y2), and the third point is (x3, y3)
      const [point1, , point3] = points;

      if (point3.x !== point1.x) {
        // Calculate x-coordinate for the vertical lines
        const xCoord = point1.x;
        const XCoord1 = point3.x
        // Draw lines passing through the first and third points
        verticalLines.push({
          x1: xCoord,
          y1: 0,
          x2: xCoord,
          y2: point1.y, // Height of the screen or any desired value
        });
        verticalLines.push({
          x1: XCoord1,
          y1: 0,
          x2: XCoord1,
          y2: point3.y, // Height of the screen or any desired value
        });


        return verticalLines;
      }
    }
    return verticalLines;
  };


  const findIntersectionPoint = () => {
    const intersectionspoint = [];
    let angleDegleft = 0
    let angleDegRight = 0
    let sommeTaper = 0
    let interangle = 0
    if (points.length >= 4) {

      const m1 = (points[1]['y'] - points[0]['y']) / (points[1]['x'] - points[0]['x']); // Slope of line 1
      const m2 = (points[3]['y'] - points[2]['y']) / (points[3]['x'] - points[2]['x']); // Slope of line 2

      angleDegleft = 90 - Math.abs(Math.atan(m1)) * (180 / Math.PI);
      angleDegRight = 90 - Math.abs(Math.atan(m2)) * (180 / Math.PI);
      sommeTaper = angleDegRight + angleDegleft
      interangle = calculateIntersectionAngle(m1, m2).toFixed(2)



      if (Math.abs(m1 - m2) < 0.0001) {
        return 'Paralle - No intersection point';
      }

      const c1 = points[0]['y'] - m1 * points[0]['x']; // y-intercept of line 1
      const c2 = points[2]['y'] - m2 * points[2]['x']; // y-intercept of line 2

      const x = (c2 - c1) / (m1 - m2); // x-coordinate of the intersection point
      const y = m1 * x + c1; // y-coordinate of the intersection point
      intersectionspoint.push({ 'x': x, 'y': y })

    }
    return [intersectionspoint, counter, interangle, angleDegleft, angleDegRight, sommeTaper]
    // Intersection point coordinates
  };

  const calculateExtendedLines = () => {


    const extendedLines = [];
    if (points.length >= 2) {
      extendedLines.push({
        x1: points[0].x,
        y1: points[0].y,
        x2: points[1].x,
        y2: points[1].y,
      });
    }
    if (points.length >= 4) {

      const pointofintersection = findIntersectionPoint()[0];

      extendedLines.push({
        x1: points[2].x,
        y1: points[2].y,
        x2: points[3].x,
        y2: points[3].y,
      });
      extendedLines.push({
        x1: points[1].x,
        y1: points[1].y,
        x2: pointofintersection[0].x,
        y2: pointofintersection[0].y,
      });
      extendedLines.push({
        x1: points[3].x,
        y1: points[3

        ].y,
        x2: pointofintersection[0].x,
        y2: pointofintersection[0].y,
      });

    }


    return extendedLines;
  };

  const handlePress = (event) => {
    const { locationX, locationY } = event.nativeEvent;
    if (processedImageToDisplay && points.length < 4) {


      const distances = countourPoints.map((point, index) => {

        const distance = Math.sqrt((point.x - locationX) ** 2 + (point.y - locationY) ** 2);
        return { distance, index };
      });

      const nearestPointIndex = distances.reduce((nearestIndex, current, index) => {
        if (current.distance < distances[nearestIndex].distance) {
          return index;
        }
        return nearestIndex;
      }, 0);

      const updatedPoints = countourPoints.map((point, index) => {
        if (index === nearestPointIndex) {
          return { x: locationX, y: locationY };
        }
        return point;
      });



      setPoints([...points, { x: countourPoints[nearestPointIndex]['x'], y: countourPoints[nearestPointIndex]['y'] }]);
    }


  };

  const onImageLoad = (event) => {
    const { width, height } = event.nativeEvent.source;
    setImageDimensions({ width, height });
  };
  const calculateIntersections = () => {
    const intersections = [];

    for (let i = 0; i < points.length - 1; i++) {
      for (let j = i + 1; j < points.length - 1; j++) {
        const p1 = points[i];
        const p2 = points[i + 1];
        const p3 = points[j];
        const p4 = points[j + 1];

        const intersection = findIntersection(p1, p2, p3, p4);

        if (intersection) {
          intersections.push(intersection);
        }
      }
    }

    return intersections;
  };
  const goBeforeAngle = () => {
    Animated.sequence([
      Animated.timing(scaleValue_before, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_before, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(async () => {
        setConter(counter - 1)

      }, 50); // Adjust the delay duration as needed
    });


  }
  const goNextAngle = () => {
    Animated.sequence([
      Animated.timing(scaleValue_next, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_next, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {

      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(async () => {
        setWichtButton('nextangle')
        setConfirmVisible(true)



      }, 50); // Adjust the delay duration as needed
    });


  }
  const closeModal = () => {
    setPopupVisible(false)

  }
  const closeModalDep = () => {

    setClosedDep(false)

  }
  const opennModalDep = () => {
    setPopupVisible(false)
    setClosedDep(true)

  }

  const closeImageModal = () => {
    setImagePopup(false)

  }
  const closeConfirmModal = () => {
    setConfirmVisible(false)

  }
  const openCofirmModal = () => {
    setConfirmVisible(true)
  }
  const [wichbutton, setWichtButton] = useState('')
  const onconfirmed = async () => {
    if (wichbutton == 'nextangle') {
      try {
        const uri = await viewRef.current.capture();

        // Convert the captured image to base64
        const base64 = await convertToBase64(uri);
        images.push(base64)

      } catch (error) {
        console.error('Error while capturing image:', error);
      }
      setPoints([])
      setConter(counter + 1)
      setConfirmVisible(false)
    } else if (wichbutton == 'reset') {
      setPoints([])
      const updatedData = angles.filter(item => item.id !== counter);
      setAngles(updatedData);
      setConfirmVisible(false)
    } else if (wichbutton == 'Take Photo') {
      ImagePicker.launchCamera(options, (response) => {

        if (!response.didCancel) {
          // Handle the response...
          setImageURI(response.assets[0].uri);
          setImagesForViewer([{ url: response.assets[0].uri }]);
          setConter(1)
          convertImageToBase64(response.assets[0].uri);
          setPoints([]);
        }
      });
      setConfirmVisible(false)
    } else if (wichbutton == 'selectimage') {
      ImagePicker.launchImageLibrary(options, (response) => {
        if (!response.didCancel && !response.error) {
          setImageURI(response.assets[0].uri);
          setImagesForViewer([{ url: response.assets[0].uri }]);
          setConter(1)
          convertImageToBase64(response.assets[0].uri);
          setPoints([]);
        }
      });
      setConfirmVisible(false)
    } else {
      setConfirmVisible(false);
      
      const endpoint = AppConfig.etudiantUrl + '/saveStudentPW';
      const url = endpoint;
      try {
        try {
          const uri = await viewRef.current.capture();

          // Convert the captured image to base64
          const base64 = await convertToBase64(uri);
          images.push(base64)

        } catch (error) {
          console.error('Error while capturing image:', error);
        }
        let internes = ''
        let externes = ''
        let depouille = ''
        let image1 = ''
        let image2 = ''
        let image3 = ''
        let convergence = ''
        angles?.map(item => {
          if (item?.id == 1) {
            internes = 'left: ' + item.left_taper + '  right: ' + item.right_taper
            image1 = images[0]
          } else if (item?.id == 2) {
            externes = 'left: ' + item.left_taper + '  right: ' + item.right_taper
            image2 = images[1]
          } else {
            depouille = 'left: ' + item.left_taper + '  right: ' + item.right_taper
            convergence = parseFloat(item.left_taper) + parseFloat(item.right_taper)
            image3 = images[2]
          }
        })

        let note;
        let convint = parseInt(convergence, 10);
        let cvgint = parseInt(cvg, 10);

        if (convint >= cvgint - 2 && convint <= cvgint + 2) {
          note = 18;
        } else if (convint >= cvgint - 4 && convint <= cvgint + 4) {
          note = 14;
        } else if (convint >= cvgint - 6 && convint <= cvgint + 6) {
          note = 10;
        } else {
          note = 8;
        }

        const response = await axios.post(url, {
          "internes": internes,
          "externes": externes,
          "depouilles": depouille,
          "convergence": convergence,
          "image1": image1,
          "image2": image2,
          "image3": image3,
          "date": currentDate.toISOString().split('T')[0],
          "time": currentDate.toISOString().split('T')[1].split('.')[0],
          "note": note,
          "student": {
            "id": id
          },
          "pw": {
            "id": idtp
          }
        });


        ToastAndroid.show('Lab Sent Succesfully', ToastAndroid.SHORT);
      } catch (error) {
        // Handle errors or show an error message
        console.error('Error:', error);

        // Show a toast or error message to the user
        ToastAndroid.show('Error: Authentication failed', ToastAndroid.SHORT);
      }
    }



  }

  const openImageModal = () => {
    Animated.sequence([
      Animated.timing(scaleValue_image, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_image, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(() => {
        setImagePopup(true)

      }, 50); // Adjust the delay duration as needed
    });

  }
  const openModal = () => {
    Animated.sequence([
      Animated.timing(scaleValue_analytics, {
        toValue: 0.8,
        duration: 50,
        useNativeDriver: true,
      }),
      Animated.timing(scaleValue_analytics, {
        toValue: 1,
        duration: 50,
        useNativeDriver: true,
      }),
    ]).start(() => {
      // Logic executed after animation completion (using setTimeout for delay)
      setTimeout(() => {
        let sum = 0
        for (i = 0; i < 3; i++) {

          sum = parseFloat(angles[i]?.left_taper) + parseFloat(angles[i]?.right_taper)
          if (sum > 16 && sum <= 21) {
            if (i == 0) {
              setColorText_1('orange');
            } else if (i == 1) {
              setColorText_2('orange');
            } else if (i == 2) {
              setColorText_3('orange');
            }

          } else if (sum > 21 || (sum >= 4 && sum < 6) || sum < 4) {
            if (i == 0) {
              setColorText_1('red');
            } else if (i == 1) {
              setColorText_2('red');
            } else if (i == 2) {
              setColorText_3('red');
            }

          } else {
            if (i == 0) {
              setColorText_1('green');
            } else if (i == 1) {
              setColorText_2('green');
            } else if (i == 2) {
              setColorText_3('green');
            }
          }

        }
        if (sum >= 6 && sum <= 16) {
          setDepText('Excellente performance');
          setExcissif(false)
        } else if (sum > 0 && sum < 6) {
          setDepText('Performance insatisfaisante');
          setExcissif(false)
        } else if (sum > 16) {
          setDepText('Performance insatisfaisante : Dépouille excessive');
          setExcissif(true)
        } else {
          setDepText('Médiocre');
          setExcissif(false)
        }

        setPopupVisible(true)

      }, 50); // Adjust the delay duration as needed
    });


  }
  const onNextAngle = () => {
    if (counter < 3) {
      if (points.length >= 4) {
        setVisible(false)
      } else {
        setVisible(true)
      }
    } else {
      setVisible(true)
    }


  }

  React.useEffect(() => {
    onNextAngle();
  }, [points]);

  const onBeforeAngle = () => {
    if (counter > 1) {

      setVisibleBefore(false)

    } else {
      setVisible(true)
    }

  }

  React.useEffect(() => {
    onBeforeAngle();
  }, [counter]);

  const findIntersection = (p1, p2, p3, p4) => {
    const x1 = p1.x;
    const y1 = p1.y;
    const x2 = p2.x;
    const y2 = p2.y;
    const x3 = p3.x;
    const y3 = p3.y;
    const x4 = p4.x;
    const y4 = p4.y;

    const denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

    if (denominator === 0) {
      return null;
    }

    const xNumerator = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
    const yNumerator = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);

    const x = xNumerator / denominator;
    const y = yNumerator / denominator;

    return { x, y };
  };
  return (

    <GestureHandlerRootView style={styles.container}>
      <ScrollView>
        <View style={{ flexDirection: 'row', justifyContent: 'center' }}>

          <View style={{ marginRight: 10, alignItems: 'center' }}>
            <Animated.View style={{ transform: [{ scale: scaleValue_camera }] }}>
              <Icon name="photo-camera" size={40} color="brown" onPress={takePhoto} />
              <Text style={styles.fancyText}>Camera</Text>
            </Animated.View>
          </View>
          <View style={{ marginRight: 10, marginLeft: 10, alignItems: 'center' }}>
            <Animated.View style={{ transform: [{ scale: scaleValue }] }}>
              <Icon name="image" size={40} color="blue" onPress={selectImage} />
              <Text style={styles.fancyText}>Gallery</Text>
            </Animated.View>
          </View>
          <Animated.View style={{ marginLeft: 10, marginRight: 10, alignItems: 'center', transform: [{ scale: scaleValue_reset }] }}>
            <Icon name="replay" size={40} color="red" onPress={onreset} />
            <Text style={styles.fancyText}>Reset</Text>
          </Animated.View>

          {visible ? (
            <View style={{ marginLeft: 10, alignItems: 'center' }}>
              <Icon disabled={visible} name="navigate-next" size={40} color='gray' />
              <Text style={styles.fancyText} >Next</Text>
            </View>
          ) : (
            <Animated.View style={{ marginLeft: 10, alignItems: 'center', transform: [{ scale: scaleValue_next }] }}>


              <Icon disabled={visible} name="navigate-next" size={40} onPress={goNextAngle} color="green" />
              <Text style={styles.fancyText}>Next</Text>

            </Animated.View>
          )}


        </View>

        <View style={styles.imageContainer} >
          {processedImageToDisplay && (

            <PinchGestureHandler
              onGestureEvent={onPinchGestureEvent}
              onHandlerStateChange={onPinchHandlerStateChange}>

              <Animated.View style={transformStyle}>
                <ViewShot ref={viewRef}>




                  <TouchableOpacity activeOpacity={0.95} onPress={handlePress} style={{ overflow: 'hidden' }}>

                    <Image
                      onLoad={onImageLoad}
                      resizeMode="contain"
                      source={{ uri: `data:image/png;base64,${processedImageToDisplay}` }}
                      style={{
                        width: imageDimensions.width,
                        height: imageDimensions.height,

                      }}

                    />
                    <Svg
                      style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 }}
                    >
                      {renderDrawings()}
                    </Svg>

                  </TouchableOpacity>
                </ViewShot>
              </Animated.View>
            </PinchGestureHandler>
          )}
        </View>

        {!imageURI && !processedImageToDisplay && (
          <View style={styles.placeholder}>
            <Text style={styles.fancyText}>No image selected</Text>
          </View>
        )}
        <View style={{ marginTop: 10, marginBottom: 12, alignItems: 'center' }}>
          <Text style={styles.fancyText}>
            {counter === 1 ?
              'Step 1. Internal (G-D)'
              : counter == 2 ? 'Step 2. External (G-D)' : counter == 3 ? 'Step 3. draft angles (G-D)' : ''}</Text>
        </View>

        {imageURI &&
          <View style={{ flexDirection: 'row', justifyContent: 'center' }}>

            <Animated.View style={{ transform: [{ scale: scaleValue_logout }], marginRight: 30 }}>
              <View style={{ alignItems: 'center' }}>
                <Icon name="logout" size={40} color="green" onPress={onLogout} />
                <Text style={styles.fancyText}>Logout</Text>
              </View>
            </Animated.View>
            <Animated.View style={{ transform: [{ scale: scaleValue_analytics }], marginRight: 30 }}>
              <View style={{ alignItems: 'center' }}>
                <Icon name="analytics" size={40} color="green" onPress={openModal} />
                <Text style={styles.fancyText}>Results</Text>
              </View>
            </Animated.View>

            <Animated.View style={{ transform: [{ scale: scaleValue_image }] }}>
              <View style={{ alignItems: 'center' }}>
                <Icon name="image" size={40} color="blue" onPress={openImageModal} />
                <Text style={styles.fancyText}>Original Image</Text>
              </View>
            </Animated.View>
            {counter == 3 && points.length == 4 ? (
              <Animated.View style={{ marginLeft: 10, marginRight: 10, alignItems: 'center', transform: [{ scale: scaleValue_send }] }}>
                <Icon name="check" size={40} color="green" onPress={onSend} />
                <Text style={styles.fancyText}>Send</Text>
              </Animated.View>
            ) : (

              <View style={{ marginLeft: 10, alignItems: 'center' }}>
                <Icon name="check" size={40} color="gray" />
                <Text style={styles.fancyText}>Send</Text>
              </View>


            )}


          </View>
        }

        <Modal
          animationType="fade" // Animation type for the modal (slide, fade, etc.)
          transparent={true} // Whether the modal is transparent or not
          visible={confirmVsible} // Controls the visibility of the modal


        >
          <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0, 0, 0, 0.5)' }}>
            {/* Content of the modal */}
            <View style={{ backgroundColor: 'white', padding: 20, alignContent: 'center', alignItems: 'center' }}>
              <Text style={{ marginBottom: 10 }}>Are you sure to{wichbutton == 'Take Photo' ? ' change the picture you will lose all acheivements' :
                wichbutton == 'selectimage' ? ' change the picture you will lose all acheivements' : wichbutton == 'send' ? ' send the PW' : wichbutton == 'nextangle' ? ' go to next angle' : ' reset'}</Text>

              <View style={{ flexDirection: 'row' }} >
                <TouchableOpacity style={styles.button} onPress={onconfirmed} >
                  <Text style={styles.buttonText}>Proceed</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.button} onPress={onconfirmed} >
                  <Text style={styles.buttonText} onPress={closeConfirmModal}>Cancel</Text>
                </TouchableOpacity>
              </View>

            </View>
          </View>

        </Modal>


        <Modal
          animationType="fade" // Animation type for the modal (slide, fade, etc.)
          transparent={true} // Whether the modal is transparent or not
          visible={popupvisible} // Controls the visibility of the modal


        >
          <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0, 0, 0, 0.5)' }}>
            {/* Content of the modal */}
            <View style={{ backgroundColor: 'white', padding: 20 }}>

              {angles.map((item, index) => (

                <View key={index} style={{ marginTop: 10, marginBottom: 10 }}>

                  {item.id == 1 && (

                    <View>

                      <Text style={{ color: color_text_1 }}>Les versants internes :</Text>
                      <Text style={{ color: color_text_1 }}>Lingual à 15 de l'horizontal : {item.left_taper}</Text>
                      <Text style={{ color: color_text_1 }} >Vestibulaire à 45 de l'horizontal : {item.right_taper}</Text>
                    </View>

                  )}
                  {item.id == 2 && (
                    <View>

                      <Text style={{ color: color_text_2 }} >Les versants externes :</Text>
                      <Text style={{ color: color_text_2 }}>Lingual à 15 de l'horizontal : {item.left_taper}</Text>
                      <Text style={{ color: color_text_2 }}>Vestibulaire à 45 de l'horizontal : {item.right_taper}</Text>
                    </View>

                  )}

                  {item.id == 3 && (

                    <View>

                      {(parseFloat(item.left_taper) + parseFloat(item.right_taper)).toFixed(2) != item.intersection_angle &&
                        <View style={{ flexDirection: 'row', justifyContent: 'flex-start', alignContent: 'center', alignItems: 'center' }}>

                          <Text style={{ color: 'red', marginRight: 10 }}  >Contre dépouille</Text>
                          <TouchableOpacity onPress={opennModalDep} >
                            <Icon name="search" size={30} color="red" />
                          </TouchableOpacity>

                        </View>}
                      {excessif && !((parseFloat(item.left_taper) + parseFloat(item.right_taper)).toFixed(2) != item.intersection_angle) && <TouchableOpacity onPress={opennModalDep} >
                        <Icon name="search" size={30} color="red" />
                      </TouchableOpacity>
                      }

                      <Text style={{ color: color_text_3 }}>{deptext} </Text>
                      <Text style={{ color: color_text_3 }}>Dépouille des parois vestibulo-linguales : {item.left_taper} </Text>
                      <Text style={{ color: color_text_3 }}>Dépouille des parois mésio-distale {item.right_taper}</Text>

                      <Text style={{ color: color_text_3 }}>Somme des angles : {item.intersection_angle}</Text>
                      <Text style={{ color: color_text_3 }}>Angles de convergence : {(parseFloat(item.left_taper) + parseFloat(item.right_taper)).toFixed(2)}</Text>
                    </View>

                  )}

                </View>
              ))}
              <Button  style={styles.button} title="Close" onPress={closeModal} />
            </View>
          </View>
        </Modal>
        <Modal
          animationType="fade" // Animation type for the modal (slide, fade, etc.)
          transparent={true} // Whether the modal is transparent or not
          visible={imagepopup} // Controls the visibility of the modal
          onRequestClose={closeImageModal}

        >
          <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0, 0, 0, 0.5)' }}>
            {/* Content of the modal */}
            <View style={{ padding: 20 }} >

              {imageURI && (
                <TouchableOpacity onPress={closeImageModal}>
                  <Image
                    source={{ uri: imageURI }}
                    style={[styles.selectedImage]} // Adjusted style here
                    resizeMode="contain"
                  />
                </TouchableOpacity>
              )}

            </View>
          </View>
        </Modal>
        <Modal
          animationType="fade" // Animation type for the modal (slide, fade, etc.)
          transparent={true} // Whether the modal is transparent or not
          visible={iscloseddep} // Controls the visibility of the modal
          onRequestClose={closeModalDep}

        >
          <TouchableOpacity onPress={closeModalDep} style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0, 0, 0, 0.5)' }}>
            {/* Content of the modal */}

            <Image
              source={require('./assets/schema1.png')}
              style={[styles.depouilleimage]} // Adjusted style here
              resizeMode="contain"
            />
            <Image
              source={require('./assets/schema2.png')}
              style={[styles.depouilleimage]} // Adjusted style here
              resizeMode="contain"
            />

            <Image
              source={require('./assets/schema3.png')}
              style={[styles.depouilleimage]} // Adjusted style here
              resizeMode="contain"
            />



          </TouchableOpacity>
        </Modal>
      </ScrollView>
    </GestureHandlerRootView>

  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: 'lightgray',
    paddingTop: Dimensions.get('window').height * 0.035,
    paddingRight: Dimensions.get('window').width * 0.035,
    paddingLeft: Dimensions.get('window').width * 0.035,
    paddingBottom: Dimensions.get('window').width * 0.035,
  },
  button: {
    backgroundColor: 'black',
    paddingVertical: 10,
    paddingHorizontal: 20,
    marginRight:5,
    borderRadius: 5,
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    textAlign: 'center',
  },
  top: {
    alignSelf: 'stretch',
    alignItems: 'center',
    marginBottom: 20,
  },
  imageContainer: {
    alignItems: 'center',
    marginTop: 20,


  },
  processedImage: {
    width: '50%',

    borderWidth: 1,
    padding: 0, // Remove padding
    margin: 0, // Remove margin
    borderColor: 'black',

    marginBottom: 20,
    paddingLeft: 0,
    position: 'relative',
  },
  selectedImage: {

    height: '80%',
    aspectRatio: 1,

  },
  depouilleimage: {

    height: '22%',
    marginTop: 10
  },
  loupimage: {
    margin: 0,
    padding: 0,
    height: '100%',
    width: '50%'
  },
  placeholder: {
    alignItems: 'center',
    justifyContent: 'center',
    alignContent: 'center',
    width: '90%',
    height: '20%',
    backgroundColor: 'lightgray',
    marginTop: 0,
    marginBottom: 10
  },
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
  },
  modalImage: {
    width: '90%',

    borderWidth: 1,
    borderColor: 'white',
  },
  fancyText: {
    fontSize: 14,
    fontFamily: 'Arial', // Change the font family as desired
    color: 'gray', // Change the text color
    fontStyle: 'italic', // Apply italic style
    fontWeight: 'bold', // Apply bold weight

  },
  scrollViewContent: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 20,
  },

  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  dropdown: {
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 4,
    marginBottom: 10,
    padding: 10,
  },
  dropdownOptions: {
    borderRadius: 4,
    borderWidth: 1,
    borderColor: 'gray',
  },
  buttonsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 20,
  },
});


export default Home;
