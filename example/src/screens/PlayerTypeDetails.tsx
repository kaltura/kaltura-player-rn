import React, { PureComponent } from 'react';
import { View, StyleSheet, Text } from 'react-native';
import { Navigation } from 'react-native-navigation';
import { ExpandableListView } from 'react-native-expandable-listview';

import playerTestJson from '../test/PlayerTest.json';
import { PLAYER_SCREEN } from '../../index';

export default class PlayerTypeDetails extends PureComponent {
  constructor(props: any) {
    super(props);
    this.state = {
      playerType: this.props.id,
    };

    this.handleInnerItemClick = this.handleInnerItemClick.bind(this);
  }

  handleInnerItemClick({ innerIndex, item, itemIndex }) {
    var categoryName = item['categoryName'];
    var subId = item['subCategory'][0]['id'];
    var subName = item['subCategory'][0]['name'];

    console.log('handleInnerItemClick Category Name: ' + categoryName);
    console.log('handleInnerItemClick sub category Id' + subId);
    console.log('handleInnerItemClick sub category name: ' + subName);
    console.log(
      'handleInnerItemClick this.state.playerType ' + this.state.playerType
    );

    console.log(
      'MAIN JSON IS: ' +
        JSON.stringify(
          playerTestJson[this.state.playerType][categoryName][subId]
        )
    );

    openPlayerScreen(
      this.props,
      playerTestJson[this.state.playerType][categoryName][subId],
      this.state.playerType,
      subName
    );
  }

  render() {
    let parsedJsonData: ListData[] = parseJSONData(this.state.playerType);

    return (
      <View style={styles.root}>
        {parsedJsonData.length > 0 ? (
          <ExpandableListView
            data={parsedJsonData}
            onInnerItemClick={this.handleInnerItemClick}
            onItemClick={handleItemClick}
          />
        ) : (
          <Text> No Player Details Type found.</Text>
        )}
      </View>
    );
  }
}

function handleItemClick({ index }) {
  //console.log(index);
}

type ListData = {
  id: number;
  categoryName: string;
  subCategory: { id: number; name: string }[];
};

function parseJSONData(id: string): ListData[] {
  var subArrayUnderPlayerType = Object.getOwnPropertyNames(playerTestJson[id]);

  var categoryJsonArray: ListData[] = [];

  subArrayUnderPlayerType.forEach((value, index) => {
    console.log(`parsed data is: ${value}`);
    var subCategoryJsonArray = [];

    playerTestJson[id][value].forEach((value: string, index: number) => {
      var playerDetailsElement = value.id;

      console.log(`Internal parsed data is: ${playerDetailsElement}`);

      var subCategoryJson = {
        id: index,
        name: playerDetailsElement,
      };
      subCategoryJsonArray.push(subCategoryJson);
    });

    if (subCategoryJsonArray.length == 0) {
      var subCategoryJson = {
        id: 0,
        name: 'No Media Available',
      };
      subCategoryJsonArray.push(subCategoryJson);
    }

    categoryJsonArray.push({
      id: index,
      categoryName: value,
      subCategory: subCategoryJsonArray,
    });
  });

  console.log(`categoryJsonArray is ${categoryJsonArray}`);
  return categoryJsonArray;
}

// Open the Next Screen for the specific Player Type
function openPlayerScreen(
  props,
  mediaObject: object,
  playerType: string,
  title: string
) {
  Navigation.push(props.componentId, {
    component: {
      name: PLAYER_SCREEN,
      id: PLAYER_SCREEN,
      passProps: {
        incomingJson: mediaObject,
        playerType: playerType,
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
});
