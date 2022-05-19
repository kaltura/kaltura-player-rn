import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { Slider } from '@miblanchard/react-native-slider';

export default function SeekBar(props) {
  return (
    <>
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
      <View
        style={[
          styles.flex_container,
          {
            flexDirection: 'row',
          },
        ]}
      >
        <Text style={{ flex: 1 }}>{convertToHHMMSS(props.position)}</Text>

        <Text>{convertToHHMMSS(props.duration)}</Text>
      </View>
    </>
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
  },
  flex_container: {
    flex: 1,
    flexWrap: 'wrap',
    marginStart: 5,
    marginEnd: 5,
  },
});

function convertToHHMMSS(value: number) {
  var sec_num = value; // don't forget the second param
  var hours = Math.floor(sec_num / 3600);
  var minutes = Math.floor((sec_num - hours * 3600) / 60);
  var seconds = Math.floor(sec_num - hours * 3600 - minutes * 60);

  if (hours < 10) {
    hours = '0' + hours;
  }
  if (minutes < 10) {
    minutes = '0' + minutes;
  }
  if (seconds < 10) {
    seconds = '0' + seconds;
  }

  if (hours > 0) {
    return hours + ':' + minutes + ':' + seconds;
  } else {
    return minutes + ':' + seconds;
  }
}
