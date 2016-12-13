# ParkDot

For our semester-long project for CMSC434 (Introduction to Human-Computer Interaction), we decided to create an android app that gives the users the ability to store and utilize their parking information.

ParkDot is an android app that allows you to:

1. Add a parking spot (default is your current location)
2. Set expiration time and notification settings
3. Redirect to Google Maps for directions back to your parking spot

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

* Minimum SDK Version: API 23: Android 6.0 (Marshmallow)
* Google Maps installed on your device

### Installing

1. Install [Android Studio](https://developer.android.com/studio/index.html)
2. Clone ParkDot repo into your local directory

## Deployment

You can deploy the program on your android device or Android Studio's emulator (if you do not have an android device at hand).

### Emulator

1. Click run on android studio
2. Click *Create New Virtual Device*
3. Select a hardware under the *Phone* category (Nexus 5 is recommended)
4. Select a system image with API level >= 23
5. Click Finish
6. Select your virtual device that you just created
7. Click OK
8. Virtual device window should pop up with the app open

### Android device

1. Enable *Developer Options* on your device
2. Enable debugging in *Developer Options*
3. Connect your device to your laptop/computer
4. Click run on android studio
5. Click your device under *Connected Devices*
6. Click OK
7. App should be open on your device

## Built With

* [Android Studio](https://developer.android.com/studio/index.html) - IDE used

## Authors

* [Kara Isakson](https://github.com/kisakson)
* [Hojin (Sylvia) Kang](https://github.com/hojinskang)
* [Rayna Qian](https://github.com/raynaqian)
* [Thomas Yang](https://github.com/Seiashun)

## References

### Google Maps

* https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlaces.java
* https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/BasicMapDemoActivity.java

### Push notification

* https://nnish.com/2014/12/16/scheduled-notifications-in-android-using-alarm-manager/
* http://stackoverflow.com/questions/18253482/vibrate-and-sound-defaults-on-notification
* http://stackoverflow.com/questions/15809399/android-notification-sound

### Share data across activities

* https://developers.google.com/maps/documentation/android-api/intents

### Image marker

* http://stackoverflow.com/questions/18053156/set-image-from-drawable-as-marker-in-google-map-version-2

### Edit text multiple lines

* http://stackoverflow.com/questions/7092961/edittext-maxlines-not-working-user-can-still-input-more-lines-than-set
