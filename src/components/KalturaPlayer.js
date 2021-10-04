import React from "react";
import { View, Text, NativeModules, Button } from "react-native";

const { KalturaPlayerModule } = NativeModules;

const KalturaPlayer = () => {
  console.log(KalturaPlayerModule);
  const onPress = () => {
    KalturaPlayerModule.createKalturaPlayerEvent("testName", "testLocation");
  };

  return (
    <View>
      <Text>Player</Text>
      <Button onPress={onPress} title="Kaltura Player event" />
    </View>
  );
};

export default KalturaPlayer;
