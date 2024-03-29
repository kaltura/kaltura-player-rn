import React, { PureComponent } from 'react';
import { View, StyleSheet, Text } from 'react-native';
import { Navigation } from 'react-native-navigation';
import { ExpandableListView } from 'react-native-expandable-listview';

import playerTestJson from '../test/PlayerTest.json';
import { PLAYER_SCREEN } from '../../index';

const noMediaAvailable: string = 'No Media Available';

export default class PlayerTypeDetails extends PureComponent {
  constructor(props: any) {
    super(props);
    this.state = {
      clickedItem: this.props.clickedItem,
      playerType: this.props.playerType,
    };

    console.log(`this.state.clickedItem = ${this.state.clickedItem}`);
    console.log(`this.state.playerType = ${this.state.playerType}`);

    this.handleInnerItemClick = this.handleInnerItemClick.bind(this);
  }

  handleInnerItemClick({ innerItemIndex, item, itemIndex }) {
    // console.warn(`item is: ${JSON.stringify(item)}`);
    // console.warn(`innerIndex is: ${innerItemIndex}`);
    // console.warn(`itemIndex is: ${itemIndex}`);

    var categoryName = item['categoryName'];
    var subId = item['subCategory'][innerItemIndex]['id'];
    var subName = item['subCategory'][innerItemIndex]['name'];

    console.log('handleInnerItemClick Category Name: ' + categoryName);
    console.log('handleInnerItemClick sub category Id' + subId);
    console.log('handleInnerItemClick sub category name: ' + subName);
    console.log(
      'handleInnerItemClick this.state.playerType ' + this.state.playerType
    );

    if (!isMediaAvailable(subId, subName)) {
      console.log('No Media Available for this');
      return;
    }

    let listData =
      playerTestJson[this.state.playerType][this.state.clickedItem][
        categoryName
      ][subId];

    console.log('MAIN JSON IS: ' + JSON.stringify(listData));

    openPlayerScreen(this.props, listData, this.state.playerType, subName);
  }

  render() {
    let parsedJsonData: ListData[] = parseJSONData(
      this.state.playerType,
      this.state.clickedItem
    );

    return (
      <View style={styles.root}>
        {parsedJsonData.length > 0 ? (
          <ExpandableListView
            data={parsedJsonData}
            onInnerItemClick={this.handleInnerItemClick}
            onItemClick={handleItemClick}
            renderItemSeparator={true}
            renderInnerItemSeparator={true}
            innerItemContainerStyle={styles.innerItem}
          />
        ) : (
          <Text> There is no defined Player.</Text>
        )}
      </View>
    );
  }
}

function isMediaAvailable(
  subCategoryId: Number,
  subCategoryName: string
): boolean {
  if (subCategoryId == 0 && subCategoryName === noMediaAvailable) {
    return false;
  }
  return true;
}

function handleItemClick({ index }) {
  //console.log(index);
}

type ListData = {
  id: number;
  categoryName: string;
  subCategory: { id: number; name: string }[];
};

function parseJSONData(playerType: string, clickedItem: string): ListData[] {
  let subJson = playerTestJson[playerType][clickedItem];

  var subArrayUnderPlayerType = Object.getOwnPropertyNames(subJson);

  var categoryJsonArray: ListData[] = [];

  subArrayUnderPlayerType.forEach((value, index) => {
    console.log(`parsed data is: ${value}`);
    var subCategoryJsonArray = [];

    subJson[value].forEach((value: string, index: number) => {
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
        name: noMediaAvailable,
      };
      subCategoryJsonArray.push(subCategoryJson);
    }

    categoryJsonArray.push({
      id: index,
      categoryName: value,
      subCategory: subCategoryJsonArray,
    });
  });

  console.log(`categoryJsonArray is ${JSON.stringify(categoryJsonArray)}`);
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
  innerItem: {
    minHeight: 40,
  },
});
