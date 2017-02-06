**Secure Mobile Push (Android)**

This project is meant to create a mobile push module
* which enables a server to register / unregister mobile devices of an authorized user to a Notification service (very likely Firebase but exchangeable),
* which enables the authorized user to modify his / her subscribed devices,
* which enables the authorized user to modify his / her subscriptions (topics),
* which is able to receive small payloads of data containing message ids
* and which is able to fetch these messages via another endpoint in combination with the access token / authorization,
* handle multiple device subscriptions per user & multiple user per device

It should be as easy for the user and secure as it could be so the following things should be considered:

* touchless sms based registration
* login via qr code
* login via long-living access token
* secure storage should be possible locally (Android Keystore)
