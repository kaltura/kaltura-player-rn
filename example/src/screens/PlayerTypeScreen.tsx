import { PLAYER_TYPE_DETAILS, PLAYER_TYPE_SCREEN_TWO } from '../../index';
import React, { PureComponent } from 'react';
import { FlatList, View, Text, StyleSheet } from 'react-native';
import { Navigation } from 'react-native-navigation';

import playerTestJson from '../test/PlayerTest.json';

export default class PlayerTypeScreen extends PureComponent {
  // Gets the name of the JSON Key objects
  names: string[] = Object.getOwnPropertyNames(playerTestJson);

  // Renders a line seperator
  renderSeparator = () => {
    return (
      <View
        style={{
          height: 1,
          width: '100%',
          backgroundColor: '#000',
        }}
      />
    );
  };

  // Renders the Flat List
  render() {
    return (
      <View style={styles.root}>
        {this.names.length > 0 ? (
          <FlatList
            data={this.names}
            ItemSeparatorComponent={this.renderSeparator}
            renderItem={({ item }) => (
              <Text
                style={styles.item}
                onPress={itemOnClick.bind(this, item, this.props)}
              >
                {item}
              </Text>
            )}
          />
        ) : (
          <Text> No Player Type found.</Text>
        )}
      </View>
    );
  }
}

// Item on click of Flat List
const itemOnClick = (item: string, props) => {
  console.log(`Clicked item is: ${item}`);
  openPlayerTypeScreenTwo(props, item);
};

// Open the Next Screen for the specific Player Type
function openPlayerTypeScreenTwo(props, title: string) {
  Navigation.push(props.componentId, {
    component: {
      name: PLAYER_TYPE_SCREEN_TWO,
      id: PLAYER_TYPE_SCREEN_TWO,
      passProps: {
        playerType: title,
      },
      options: {
        topBar: {
          title: {
            text: title,
          },
        },
      },
    },
  });
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: 'whitesmoke',
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
});
