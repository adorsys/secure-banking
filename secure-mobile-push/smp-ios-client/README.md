The standard push functionality can be achieved by incorporating the Google firebase services. However, 
this will mainly give us standard behaviour and we need functionality for more secure operations.

On iOS (for all versions up to iOS 10.x) it is neither possible to retrieve the phone number of the device
nor to parse SMS messages sent to the device. UX gudielines also demand that the user grants permissions to
the application to receive remote notifications (push messages). Therefore it is not advisable to trigger 
the registration process without asking the user for accordance anyway.

Silent Push
Since iOS 10 it is possible to send silent push notifications. However, the app and the push message need
to be set up correctly. The push message's aps dictionary must include the content-available key with a value of 1.
Furthermore, the payload’s aps dictionary must not contain the alert, sound, or badge keys.
On the other hand the app itself needs to claim the 'Remote Notification' Background mode-capability.

Encrypted Push
Since iOS 10 it is also possible for the app (or more correctly, for an app extension) to first process the 
push notification before it is displayed to the user. This enables us to send encrypted push notifications 
to the device and decrypt it for displaying them. For this to work as expected the some conditions have to be
met. The aps dictionary of the push message has to include the mutable-content key with the value set to 1.
Furthermore for the app the remote notifications have to be configured to pop up alerts.
