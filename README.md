# p2pkit.io React Native module for iOS & Android

#### p2pkit is a peer-to-peer proximity SDK for iOS and Android

p2pkit is an easy to use SDK that bundles together several proximity technologies kung-fu style! With p2pkit apps immediately understand their proximity to nearby devices and users, estimate their range and broadcast information to them.


## Get Started

1. Using p2pkit requires an application key, start by creating a p2pkit account here:
[Create p2pkit account](http://p2pkit.io/signup.html)

2. Once you have an account you can log-in to the console and create an application key: [Create your Application Key](https://p2pkit-console.uepaa.ch/login). p2pkit validates Bundle/Application ID so <strong>don't forget to</strong> add ``your.package.name`` to the known Bundle/Application ID when creating your application key

3. Add the module to your project
```
react-native install react-native-p2pkit
react-native link react-native-p2pkit
```
4. Afterwards you need to replace the react-native-p2pkit dependency in the package.json file with "react-native-p2pkit": "git://github.com/Uepaa-AG/react-native-p2pkit.git". Then run ``npm install`` to update the dependencies.

5. Setup and link the p2pkit framework on your native environments:
  * <strong>iOS</strong>: Follow the [CocoaPods setup](http://p2pkit.io/developer/get-started/ios/#signup)
> Currently the module is configured to use CocoaPods for fetching and linking the P2PKit.framework. If you are getting "header search paths" errors, make sure to compile the Pods project at least once so that the p2pkit headers will be exported to ./Pods/Headers/Public, the module is configured to search for them there. Note that Bitcode is not supported, you would need to disable Bitcode in your iOS project.

  * <strong>Android</strong>: Follow the [setup here](http://p2pkit.io/developer/get-started/android/)
> Don't forget to add the p2pkit maven repository to your app's gradle file (as mentioned in the link above)

6. Once you have configured p2pkit on each platform, you can go ahead and build your react-native app.

## Example

Here is an example that implements p2pkit functionality:

```
import p2pkit from 'react-native-p2pkit';

class MyComponent extends Component {
  onException = (exceptionMessage) => {
    console.log(exceptionMessage.message)
  }

  onEnabled = () => {
    console.log('p2pkit is enabled')
    p2pkit.enableProximityRanging()
    p2pkit.startDiscovery('', p2pkit.HIGH_PERFORMANCE) //base64 encoded Data (bytes)
  }

  onDisabled = () => {
    console.log('p2pkit is disabled')
  }

  // Refer to platform specific API for error codes
  onError = (errorObject) => {
    console.log('p2pkit failed to enable on platform ' + errorObject.platform + ' with error code ' + errorObject.errorCode)
  }

  onDiscoveryStateChanged = (discoveryStateObject) => {
    console.log('discovery state updated on platform ' + discoveryStateObject.platform + ' with error code ' + discoveryStateObject.state)
  }

  onPeerDiscovered = (peer) => {
    console.log('peer discovered ' + peer.peerID)
  }

  onPeerLost = (peer) => {
    console.log('peer lost ' + peer.peerID)
  }

  onPeerUpdatedDiscoveryInfo = (peer) => {
    console.log('discovery info updated for peer ' + peer.peerID + ' info ' + peer.discoveryInfo)
  }

  onProximityStrengthChanged = (peer) => {
    console.log('proximity strength changed for peer ' + peer.peerID + ' proximity strength ' + peer.proximityStrength)
  }

  onGetMyPeerId = (reply) => {
    console.log(reply.myPeerId)
  }

  onGetDiscoveryPowerMode = (reply) => {
  console.log(reply.discoveryPowerMode)
  }

  componentWillMount() {
    p2pkit.addListener('onEnabled', this.onEnabled)
    p2pkit.addListener('onPeerDiscovered', this.onPeerDiscovered)
  }

  componentWillUnmount() {
    p2pkit.removeListener('onEnabled', this.onEnabled)
    p2pkit.removeListener('onPeerDiscovered', this.onPeerDiscovered)
  }

  componentDidMount() {
    p2pkit.enable('<YOUR APPLICATION KEY>')
  }

  render() {
    // render code
  }
}
```

## Documentation
The full API for the module is available at <code>index.js</code>.

In general, a tutorial as well as more documentation for p2pkit is available on our website at:
[http://p2pkit.io/developer](http://p2pkit.io/developer)

### p2pkit License
* By using P2PKit you agree to abide by our Terms of Service, License Agreement and Policies which are available here: http://p2pkit.io/policy.html
* Please refer to "Third_party_licenses.txt" included with P2PKit.framework for 3rd party software that P2PKit.framework may be using - You will need to abide by their licenses as well
