**Secure Mobile Push (Android)**

This project is meant to create a mobile push module
* which enables a server to register / unregister mobile devices of an authorized user to a Notification service (very likely Firebase but exchangeable),
* which enables the authorized user to modify his / her subscribed devices,
* which enables the authorized user to modify his / her subscriptions (topics),
* which is able to receive small payloads of data containing strings
* handle multiple device subscriptions per user & multiple user per device

It should be as easy for the user and secure as it could be so the following things should be considered:

* touchless sms based registration (extra module)
* login via qr code (extra module)
* login via long-living access token
* secure storage should be possible locally (Android Keystore / extra module)


The following concept is now planned:

* Setup Firebase Notifications on the client (each device has its own Android deviceId or advertisingId)
* Generate public key on client --> per device
* Exchange public key (server to client) via push notification
* Send public key (client to server) via server endpoint
* Push Notification Service is setup for sending encrypted messages with the public key

Unregistration for devices is not an issue as described here:
https://developers.google.com/cloud-messaging/registration#how-uninstalled-client-app-unregistration-works