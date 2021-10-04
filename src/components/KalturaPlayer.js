import React from "react";
import { View, Text, NativeModules, Button } from "react-native";

const { CalendarModule } = NativeModules;

const KalturaPlayer = () => {
  console.log(CalendarModule);
  const onPress = () => {
    CalendarModule.createCalendarEvent("testName", "testLocation");
  };

  return (
    <View>
      <Text>Player</Text>
      <Button onPress={onPress} title="calendar event" />
    </View>
  );
};

export default KalturaPlayer;
