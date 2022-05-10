import React from 'react';
import { StyleSheet } from 'react-native';
import { Dropdown } from 'react-native-element-dropdown';

export default function TrackList(props) {
    let data = [{
        value: 'Banana',
        lable: 'Country 5'
      }, {
        value: 'Mango',
        lable: 'Country 5'
      }, {
        value: 'Pear',
        lable: 'Country 5'
      }];
  return (
    <Dropdown
    style={styles.dropdown}
    selectedTextStyle={styles.selectedTextStyle}
    iconStyle={styles.iconStyle}
    maxHeight={200}
    data={data}
    valueField="value"
    labelField="lable"
    placeholder= {props.title}
    onChange={e => {
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
