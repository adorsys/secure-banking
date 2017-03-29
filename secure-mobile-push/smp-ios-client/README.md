Secure-Mobile-Push on iOS

The first idea that the app requests the push-service, the server sends an SMS for verification 
which the app captures in the background and verifies the request without user interaction is 
not possible on iOS (up to iOS 10). Even with CallKit we are not able to get access to SMS or
incoming calls. Thus we need an SMS with a 'deep link'(/url reference) into the app. As a result,
on iOS, user interaction is mandatory at the moment.

PushService-implementation (lessons learned from previous projects)
1) App requests a push token from Apple. This token changes from time to time. However, 
we can request the token at each start-up - we either receive the previous token or a 
new token.
2) This token (the one we receive von Apple) is sent to Google service and we receive a
token with which our server can issue a push notification, sending the message and the token 
to the Google service (which then uses the Apple service for iOS devices)
3) We save the Apple-Token locally therefore we can identify when the token has changed. At that time
we need to update the token with the Google service and with our server if necessary.

