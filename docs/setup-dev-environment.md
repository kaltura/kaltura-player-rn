## Steps to setup dev environment

1. Install NodeJS on you machine. [Download Link](https://nodejs.org/en/download/)
2. Clone [kaltura-player-rn](https://github.com/kaltura/kaltura-player-rn) in your machine or download the source code.
3. We have Example app which contains various test medias and you can **'test your media'** as well in this app.
4. We are using YARN package manager in the document. Feel free to use your favourite.
5. Install **XCode** and **Android Studio (Version Dolphin)** for the development.

### About Kaltura Player

Kaltura Player is based on Google ExoPlayer in Android and AVPlayer in iOS. Along with the content playback, it supports Advertisements playback, VR/360 media playback, Chromecast(Not Supported Currently), Offline Media Playback(Not Supported Currently) and reknowned Analytics plugin (NPAW Youbora). If you are a Kaltura BE customer then you can use our Providers plugins which helps in getting the media details from Kaltura BE.

- **Kaltura Player Android contains following Native SDK**
  - [Kaltura Player](https://github.com/kaltura/kaltura-player-android)
  - [Ad Plugin (IMA)](https://github.com/kaltura/playkit-android-ima)
  - [Youbora](https://github.com/kaltura/playkit-android-youbora)
  - [Providers (For Kaltura BE only)](https://github.com/kaltura/playkit-android-providers)
  - [Googlecast](https://github.com/kaltura/playkit-android-googlecast)

- **Kaltura Player iOS contains following Native SDK**
  - [Kaltura Player](https://github.com/kaltura/kaltura-player-ios)
  - [Ad Plugin (IMA)](https://github.com/kaltura/playkit-ios-ima)
  - [Youbora](https://github.com/kaltura/playkit-ios-youbora)
  - [Providers (For Kaltura BE only)](https://github.com/kaltura/playkit-ios-providers)
  - [Googlecast](https://github.com/kaltura/playkit-ios-googlecast)


Kaltura Player RN SDK gives the media playback, VR/360 media playback and Offline media playback(Not Supported Currently). If you want to use other plugins like IMA, Youbora, Googlecast then you need to add it as dependency in your FE app's `build.gradle` or `pod` file for Android and iOS. Latest versions are there in Maven or Cocoapods and mentioned on the Github repos as well.

### Folder Structure

#### `.ios` and `./android` 

It contains the native source code for iOS and Android platforms. We are using our Native SDKs [Kaltura Player Android](https://github.com/kaltura/kaltura-player-android) and [Kaltura Player iOS](https://github.com/kaltura/kaltura-player-ios) for this react native SDK.

#### `./src` 

It contains the react native bridge code which constains the APIs which as a React Native FE apps will eventually be calling. It communicates to the relevant platform to call the underlying Native Player (iOS and Android).

#### `./example`

It contains a test app which contains a Test JSON. Currently this JSON has our in house or open media streams. You can pass your media stream URLs or IDs in this JSON and it will be visible in the mobile screen.

### Building the environment (React Native RN)

1. Open the project folder in your favourite IDE. We are giving example here with [Visual Studio Code](https://code.visualstudio.com/download) IDE.
2. Stand on the root directory and call `yarn install`. It will fetch all the relevant dependencies in your local machine in `node_modules` root folder.

### Building the environment (Example Test App)

  1. Navigate to `example` folder and call `yarn install`. It will fetch all the relevant dependencies in your local machine under `node_modules` example folder.
  2. Source code for the test app is in `./src` folder.

- #### Setup for Android

  1. Open Android Studio and click on **File** -> **Open** then choose `./example/android` folder.
  2. Let it build. After some time, on left side pane, you should see project files. If there are any build errors, please try to fix it. These build errors could be related to the Android Studio and Gradle version you are using. We suggest to use Android Studio (Dolphin or later version) and Gradle version 7+.


- #### Setup for iOS

  1. Native to `example -> ios` folder and run the following,

    ```js
    cd example/ios
    pod install
    ```

    If you are using M1 mac then do the following,

    ```js
    cd example
    sudo arch -x86_64 gem install ffi
    cd ios  
    arch -x86_64 pod install 
    ```

  2. Now you can open the workspace in XCode and can build the project.

### Build Kaltura-Player-RN SDK locally with 'example' app

In case if you are want to develop Kaltura Player RN SDK then you can setup locally in 'example' app. 

  - #### Link example app with local `kaltura-player-rn`.

      ```js
      cd root_folder OR path to `kaltura-player-rn` in you system 
      yarn link

      cd root_folder/example OR path to `example` app
      yarn link "kaltura-player-rn"
      ```

    There is a chance that linking will not work the try adding `kaltura-player-rn` manually using.

      ```js
      cd root_folder/example OR path to `example` app
      yarn add path to `kaltura-player-rn` in you system
      ```

  - #### Setup for Android

    1. If you want to change the Native Android code then inorder to show `kaltura-player-rn` in Android Studio. You need to open project level `settings.gradle` in Android Studio, add the following.

        ```gradle
        include ':kaltura-player-rn'
        project(':kaltura-player-rn').projectDir = new File(rootProject.projectDir, '../node_modules/kaltura-player-rn/android')
        ```
    
    2. Please make sure that Android Studio Java version is on Java-11. You can check in **Preference -> Build,Execution,Deployment -> Build Tools -> Gradle**. Then choose Gradle SDK. Choose Java-11 from the dropdown if not selected then **press Apply and OK.**. In case if Java-11 is not installed in your machine, please try installing it from [here](https://www.oracle.com/downloads/).

  - #### Setup for iOS

    1. Open 'Podfile' in 'example -> ios'.
    2. Uncomment `pod 'RNKalturaPlayer', :path => '../..'` from this file. 


### How to test my media in the 'example' app.

  1. Open [Test JSON](https://github.com/kaltura/kaltura-player-rn/blob/develop/example/src/test/PlayerTest.json).
  2. You can add your test media [here](https://github.com/kaltura/kaltura-player-rn/blob/50e7f3e88ca9212c7478969f3f900515339e7517/example/src/test/PlayerTest.json#L75).
  3. Use 'dash' instead of 'hls' if you want to test mpd of Dash media [here](https://github.com/kaltura/kaltura-player-rn/blob/50e7f3e88ca9212c7478969f3f900515339e7517/example/src/test/PlayerTest.json#L85).

