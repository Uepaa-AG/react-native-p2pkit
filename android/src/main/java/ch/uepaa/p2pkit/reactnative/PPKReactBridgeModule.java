package ch.uepaa.p2pkit.reactnative;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import ch.uepaa.p2pkit.*;
import ch.uepaa.p2pkit.discovery.*;
import ch.uepaa.p2pkit.discovery.Peer;

import android.util.Base64;
import android.util.Log;


public class PPKReactBridgeModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    public static final String REACT_CLASS = "PPKReactBridge";

    private ReactApplicationContext mApplicationContext;

    @Override
    public String getName() {
      return REACT_CLASS;
    }

    public PPKReactBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        mApplicationContext = reactContext;

    }

    @ReactMethod
    public void enable(String appKey) {

        try {
            P2PKit.enable(mApplicationContext, appKey, p2pKitStatusListener);
        } catch (AlreadyEnabledException e) {
            invokePluginResultError("Failed to enable p2pkit with error "+e.toString());
        }
    }

    @ReactMethod
    private void disable() {
        if (P2PKit.isEnabled()) {
            P2PKit.disable();
        }
    }

    @ReactMethod
    private void getMyPeerId() {

        if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        WritableMap map = Arguments.createMap();
        map.putString("myPeerId",  P2PKit.getMyPeerId().toString());
        invokePluginResult("onGetMyPeerId", map);
    }

    @ReactMethod
    private void startDiscovery(String discoveryInfoBase64String, ReadableMap options) {

        if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        byte [] discoveryInfo = null;

        if (discoveryInfoBase64String != null) {
            discoveryInfo = Base64.decode(discoveryInfoBase64String,Base64.DEFAULT);
        }

        String discoveryPowerMode = options.getString("discoveryPowerMode");

        DiscoveryPowerMode powerModeToUse = getDiscoveryPowerModeFromString(discoveryPowerMode);

        if (powerModeToUse == null) {
        	invokePluginResultError("Unknown DiscoveryPowerMode: " + discoveryPowerMode);
        } else {

        	try {
            	P2PKit.startDiscovery(discoveryInfo, powerModeToUse, mDiscoveryListener);
        	} catch (DiscoveryInfoTooLongException e) {
            	invokePluginResultError("Failed to start discovery with exception " +e.toString());
        	}
        }
    }

    @ReactMethod
    private void stopDiscovery() {

        if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        P2PKit.stopDiscovery();
    }

    @ReactMethod
    private void enableProximityRanging() {

        if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        P2PKit.enableProximityRanging();
    }

    @ReactMethod
    private void pushNewDiscoveryInfo(String discoveryInfoBase64String) {

        if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        byte [] discoveryInfo = null;

        if (discoveryInfoBase64String != null) {

            try {
                discoveryInfo = Base64.decode(discoveryInfoBase64String,Base64.DEFAULT);
            }catch (Exception e) {
                invokePluginResultError("Failed to extract base64 encoded discovery info with exception " +e.toString());
                return;
            }
        }

        try {
            P2PKit.pushDiscoveryInfo(discoveryInfo);
        } catch (DiscoveryInfoTooLongException e) {
            invokePluginResultError("Failed to update discovery info with exception " +e.toString());
        } catch (DiscoveryInfoUpdatedTooOftenException e) {
            invokePluginResultError("Failed to update discovery info with exception " +e.toString());
        }

    }

    @ReactMethod
    private void getDiscoveryPowerMode() {

    	if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        WritableMap map = Arguments.createMap();
        map.putString("discoveryPowerMode",  P2PKit.getDiscoveryPowerMode().name());
        invokePluginResult("onGetDiscoveryPowerMode", map);
    }

    @ReactMethod
    private void setDiscoveryPowerMode(String discoveryPowerMode) {

    	if (!P2PKit.isEnabled()) {
            invokePluginResultError("p2pkit is not enabled");
            return;
        }

        DiscoveryPowerMode powerModeToUse = getDiscoveryPowerModeFromString(discoveryPowerMode);
        if (powerModeToUse == null) {
        	invokePluginResultError("Unknown DiscoveryPowerMode: " + discoveryPowerMode);
        } else {
        	P2PKit.setDiscoveryPowerMode(powerModeToUse);
        }
    }

    private final DiscoveryListener mDiscoveryListener = new DiscoveryListener() {

        @Override
        public void onStateChanged(final int state) {

            WritableMap map = Arguments.createMap();
            map.putString("platform", "android");
            map.putString("state",String.valueOf(state));
            invokePluginResult("onDiscoveryStateChanged", map);

        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            invokePluginResult("onPeerDiscovered", createMapFromPeer(peer));
        }

        @Override
        public void onPeerLost(final Peer peer) {
            invokePluginResult("onPeerLost", createMapFromPeer(peer));
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            invokePluginResult("onPeerUpdatedDiscoveryInfo", createMapFromPeer(peer));
        }

        @Override
        public void onProximityStrengthChanged(Peer peer) {

            invokePluginResult("onProximityStrengthChanged", createMapFromPeer(peer));
        }
    };

    private final P2PKitStatusListener p2pKitStatusListener = new P2PKitStatusListener() {

        @Override
        public void onEnabled() {
            invokePluginResult("onEnabled", null);
        }

        @Override
        public void onDisabled() {
            invokePluginResult("onDisabled", null);
        }

        @Override
        public void onError(StatusResult statusResult) {

            WritableMap map = Arguments.createMap();
            map.putString("platform", "android");
            map.putString("errorCode",String.valueOf(statusResult.getStatusCode()));
            invokePluginResult("onError", map);
        }

        @Override
        public void onException(Throwable throwable) {

        	WritableMap map = Arguments.createMap();
        	map.putString("platform", "android");
        	map.putString("message", Log.getStackTraceString(throwable));
        	invokePluginResult("onException", map);
        }
    };

    private void invokePluginResultError(String errorString) {

        if (errorString == null) {
            return;
        }

        WritableMap map = Arguments.createMap();
        map.putString("platform", "android");
        map.putString("message",errorString);

        invokePluginResult("onException", map);
    }

    private void invokePluginResult(String methodName, WritableMap parms) {

      if (mApplicationContext == null) {
          P2PKit.disable();
          return;
      }

        if (methodName == null) {
            return;
        }

        WritableMap moduleResponse = Arguments.createMap();

        moduleResponse.putString("methodName",methodName);
        if (parms != null) moduleResponse.putMap("parms",parms);

        mApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("callback", moduleResponse);
    }

    private WritableMap createMapFromPeer(Peer peer) {

        String peerID = peer.getPeerId().toString();
        String discoveryInfo = Base64.encodeToString(peer.getDiscoveryInfo(),Base64.DEFAULT);
        String proximityStrength = String.valueOf(peer.getProximityStrength());

        WritableMap mapPeer = Arguments.createMap();

        mapPeer.putString("peerID",peerID);
        mapPeer.putString("discoveryInfo",discoveryInfo);
        mapPeer.putString("proximityStrength",proximityStrength);

        return mapPeer;
    }

    private DiscoveryPowerMode getDiscoveryPowerModeFromString(String discoveryPowerMode) {

    	if (DiscoveryPowerMode.LOW_POWER.name().equals(discoveryPowerMode)) {
    		return DiscoveryPowerMode.LOW_POWER;
    	} else if (DiscoveryPowerMode.HIGH_PERFORMANCE.name().equals(discoveryPowerMode)) {
    		return DiscoveryPowerMode.HIGH_PERFORMANCE;
    	} else {
    		return null;
    	}
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (P2PKit.isEnabled()) {
            P2PKit.disable();
        }
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }
}
