import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { Slider } from '@miblanchard/react-native-slider';

export default function SeekBar(props) {
  return (
    <View style={styles.container}>
      <Slider
        value={props.position * 1000}
        maximumValue={props.duration * 1000}
        onSlidingComplete={(newPosition) => {
          props.onSeekBarScrubbing(false);
          props.onSeekBarScrubbed(newPosition);
        }}
        onSlidingStart={(newPosition) => {
          props.onSeekBarScrubbing(true);
        }}
      />
      <Text>Position: {props.position}</Text>

      <Text>Duration: {props.duration}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginLeft: 10,
    marginRight: 10,
    alignItems: 'stretch',
    justifyContent: 'center',
  },
});
