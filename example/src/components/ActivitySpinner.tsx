import React, { Component } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

export class ActivitySpinner extends Component<any, any> {
  constructor(props: any) {
    super(props);
    this.state = {
      isShowing: props.isShowing,
    };
    //console.log('ActivitySpinner : ' + this.state.isShowing);
  }

  componentDidUpdate(prevProps: any) {
    //console.log('ActivitySpinner in update: ' + this.props.isShowing);
    if (this.props.isShowing !== prevProps.isShowing) {
      this.setState(() => ({
        isShowing: this.props.isShowing,
      }));
    }
  }

  render() {
    return (
      <View style={styles.container}>
        <ActivityIndicator color = "blue" size="large" animating={this.state.isShowing} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center',
    position: 'absolute',
  },
});
