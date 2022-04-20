import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { KalturaPlayer } from 'react-native-kaltura-player';

export default class App extends React.Component {

  player: KalturaPlayer;

  doPlayerPause = () => {
    this.player.playOrPause(false)
  };

  doPlayerPlay = () => {
    this.player.playOrPause(true)
  };
  
  render() {
    
  return (
    <View>
      <Text style={styles.red}>Welcome to Kaltura Player RN</Text>

      <KalturaPlayer
      ref={(ref: KalturaPlayer) => { this.player = ref }}
      style = {styles.center}
      assetId = {"548576"}
      partnerId = {3009}
      baseUrl = {'https://rest-us.ott.kaltura.com/v4_5/api_v3/'}>
      </KalturaPlayer>

      <TouchableOpacity onPress={() => { this.doPlayerPlay() }}>
      <Text style={[styles.bigBlue, styles.red]}>Play Media</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => { this.doPlayerPause() }}>
        <Text style={[styles.red, styles.bigBlue]}>Pause Media</Text>
      </TouchableOpacity>
    </View>
  );
  }
}

const styles = StyleSheet.create({
  container: {
    marginTop: 50,
  },
  bigBlue: {
    color: 'blue',
    fontWeight: 'bold',
    fontSize: 30,
  },
  red: {
    color: 'red',
  },
  center: {
    flex: 1,
    padding: 100,
    height: 300,
    alignItems: 'center'
  }
});

