import React from 'react';
import { StyleSheet } from 'react-native';
import { Dropdown } from 'react-native-element-dropdown';

export default function TrackList(props) {
  let trackType = props.trackType;
  let labelFieldName = 'bitrate';

  let incomingTrackList = props.trackList;

  if (trackType === 'video') {
    incomingTrackList.map((val, idx) => val.isAdaptive === true ? val.bitrate = 'Adaptive' : val);
    labelFieldName = 'bitrate';
  } else if (trackType === 'audio') {
    incomingTrackList.map((val, idx) => {
      val.isAdaptive === true ? val.bitrate = 'Adaptive' : val
      if (val.label == null) {
        labelFieldName = 'language';
      } else {
        labelFieldName = 'label';
      }
    });
    
  } else if (trackType === 'text') {
    incomingTrackList.map((val, idx) => {
      if (val.label == null) {
        labelFieldName = 'language';
      } else {
        labelFieldName = 'label';
      }
    });
  }

  return (
    <Dropdown
      style={styles.dropdown}
      selectedTextStyle={styles.selectedTextStyle}
      iconStyle={styles.iconStyle}
      maxHeight={200}
      data={incomingTrackList}
      valueField="id"
      labelField={labelFieldName}
      placeholder={props.title}
      onChange={(track) => {
        {
          props.onTrackChangeListener(track.id);
        }
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
