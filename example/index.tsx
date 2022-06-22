import { Navigation } from "react-native-navigation";
import PlayerScreen from './src/screens/PlayerScreen';
import PlayerTypeDetails from './src/screens/PlayerTypeDetails';
import PlayerTypeScreen from './src/screens/PlayerTypeScreen';

export const PLAYER_TYPE_SCREEN = 'com.example.reactnativekalturaplayer.PlayerTypeScreen';
export const PLAYER_TYPE_DETAILS = 'com.example.reactnativekalturaplayer.PlayerTypeDetails';
export const PLAYER_SCREEN = 'com.example.reactnativekalturaplayer.PlayerScreen';

// Registering all the screens
Navigation.registerComponent(PLAYER_TYPE_SCREEN, () => PlayerTypeScreen);
Navigation.registerComponent(PLAYER_TYPE_DETAILS, () => PlayerTypeDetails);
Navigation.registerComponent(PLAYER_SCREEN, () => PlayerScreen);

Navigation.events().registerAppLaunchedListener(() => {
   Navigation.setRoot({
     root: {
       stack: {
         children: [
           {
             component: {
               name: PLAYER_TYPE_SCREEN,
               options: {
                 topBar: {
                   visible: true,
                   title: {
                    text: 'Kaltura Player Test App'
                  }
                 },
                 statusBar: {
                   style: 'dark'
                 }
               }
             }
           }
         ]
       }
     }
  });
});
