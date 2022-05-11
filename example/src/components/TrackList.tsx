import React from 'react';
import { StyleSheet } from 'react-native';
import { Dropdown } from 'react-native-element-dropdown';

export default function TrackList(props) {
  return (
    <Dropdown
      style={styles.dropdown}
      selectedTextStyle={styles.selectedTextStyle}
      iconStyle={styles.iconStyle}
      maxHeight={200}
      data={props.trackList}
      valueField="id"
      labelField="bitrate"
      placeholder={props.title}
      onChange={(track) => {
        {props.onTrackChangeListener(track.id)}
        console.log('Selected Track in TrackList component is: ' + track.id);
      }}
    />
  );
}

const styles = StyleSheet.create({
  dropdown: {
    margin: 5,
    height: 30,
    width: 140,
    backgroundColor: '#EEEEEE',
    borderRadius: 210,
    paddingHorizontal: 8,
  },
  selectedTextStyle: {
    fontSize: 16,
    marginLeft: 8,
  },
  iconStyle: {
    width: 20,
    height: 20,
  },
});
