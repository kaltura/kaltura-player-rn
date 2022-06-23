import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { Slider } from '@miblanchard/react-native-slider';

export default function SeekBar(props) {
  return (
    <View>
      <Slider
        disabled={props.isAdPlaying ? true : false}
        animateTransitions={true}
        value={props.position}
        maximumValue={props.duration}
        minimumTrackTintColor={'#ed0037'}
        thumbStyle={styles.thumb}
        trackStyle={styles.track}
        onSlidingComplete={(newPosition) => {
          props.onSeekBarScrubbing(false);
          props.onSeekBarScrubbed(parseInt(newPosition, 10));
        }}
        onSlidingStart={(_) => {
          props.onSeekBarScrubbing(true);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  thumb: {
    backgroundColor: '#eb6e1b',
  },
  track: {
    marginLeft: 5,
    marginRight: 5,
    backgroundColor: '#d0d0d0',
    borderRadius: 3,
    height: 5,
  }
});


