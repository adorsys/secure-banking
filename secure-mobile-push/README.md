# Secure Mobile Push (SMP)

## Introduction
This project deals with all issues surrounding a clean and secure implementation of push notifications to mobile user devices 

Yes I know we have implemented plenty of projects with push notifications. But still my remark is that we always react to problems we encouter and fix up the problems we encounter on the version of that very project, forgetting everything we have done for other customer.

We even have a module [https://github.com/adorsys/amp] that we use to send messages to GCM. But this is so far not push, it is just a protocole layer. 

The primary intention of this project is to make a thorough analysis of the mobile push process and derived components that can be reused, both on client and server side.

## Following process are found in the mobile push world

### Device Registration

The device registration process generally seems to be proprietary. This because we do not have any standard process we offer to our customer. The purpose of this project is to define standards device registration processes we can implements as components and use to enrich our customer projects.

Below are some of my attempts to define registration processes.

#### Touchless SMS Based Registration

This process is use to associate the user of an SMS Terminal device this a push notification process. This process sets the following preconditions:

* We are able to read an SMS sent to a user give that user phone number (required)
* We are able to read the phone number of a device after we have installed our Secure Mobile Push App (SMPApp) on that device (Option).
	* If reading the phone number associated with the device is not possible, the SMPApp will require the user to enter his mobile phone number.
	* If we can read the mobile phone number of this device automatically, the SMPApp will automatically send a message to our SMPEndpoint request an SMS to the user phone number. One this SMS gets there, we can user the first feature to automatically parse the message and associate the push of the this device with that phone number.
* We might want to consider the case where a user has multiple SIM Cards.

##### TODO
* Define and implement server interfaces for a touchless SMS based device registration
* Define and implement android component with a touchless device based registration
* Define and implement ios component with a touchless device based registration


#### Registration with User Credential

This is the natural registration process, that request the user ot login in the SMPApp. The SMPApp will then user the access token provided by the IDP to send a device id to the server. The server will associate that device id with the user account.

##### TODO
* Define and implement Server Endpoint for user credential based device registration
  * With bearer token
  * With basic auth
* Define and implement android component for user credential device based registration
* Define and implement ios component for user credential device based registration


#### Registration with Code Scan

The registration with code scan assumes that the user is logged into a web site. The registration page of this web site shows a login page to the user. The user uses the SMPApp to scan the code and trigger the registration process.

In oder to guaranty the user is the one sending the code, the SMPApp must display a 4 digit PIN that the user must enter in that web site to complete the registration process.

##### TODO
* Define and implement Server Endpoint for code scan based device registration
* Define and implement android component for code scan device based registration
* Define and implement ios component for code scan device based registration

#### Storing device id on a client device

Do we need to store the device id on the client device. If yes:

* Why and what for?
* How and where can we securely store a device id on the client device? 
   

### Updating Device Id

Updating the server environment with the user device id is something else we have to deal with. They are many situations where the Cloud Notification Backend (like GCM) assigns a new device id to the user device (like SMPApp). In this case, the server environment hat to be updated with the new device id, if not the user wont be able to receive any new notification. Following situation are:

* The user has installed a new version of the SMPApp
* The device has updated the android/ios version
* The user has a new Device and has newly installed the SMPApp

In any of those cases, the SMPAp will have to update the server environment with the new device id. In order to have a consistent association, we need to store more than just the device id on the user device. We can use following properties to track or/and identify the user device:

* Use telephone number(s) SMS on client device to legitimate update 
* Store some sort of persistent token in the user keychain and use id as a app2device identifier.

#### Touchless SMS Based Update of Device Id

* Preconditions
  * We are able to read an SMS sent to a user give that user phone number (required)
  * We are able to read the phone number of a device after we have installed our Secure Mobile Push App (SMPApp) on that device (Option).
	* If reading the phone number associated with the device is not possible, the SMPApp will require the user to enter his mobile phone number.
	* If we can read the mobile phone number of this device automatically, the SMPApp will automatically send a message to our SMPEndpoint request an SMS to the user phone number. One this SMS gets there, we can user the first feature to automatically parse the message and associate the push of the this device with that phone number.
  * We might want to consider the case where a user has multiple SIM Cards.

##### TODO
* Define and implement server interfaces for a touchless SMS based update of device id
* Define and implement android component with a touchless SMS based update of device id
* Define and implement ios component with a touchless SMS based update of device id

#### Registration with User Credential

We assume in this case that the SMPApp will promp the user for his credentials. The SMPApp will then user the access token provided by the IDP to send a device id to the server. The server will associate that device id with the user account.

If the IDP is able to provide the SMPApp with the persistent long term refresh token, this token could be stored in the key chain an used to update the server with the new device id without the need to promp the user for any type of credentials.

##### TODO
* Define and implement Server Endpoint for user credential based update of device id
  * With bearer token
  * With basic auth
* Define and implement android component for user credential device update of device id
* Define and implement ios component for user credential device update of device id


### Managing Many User Devices per Account

We can also have the situation where we have many user devices registered with the same account. In that case, we wil have to finde a way of identifying the single devices. This is we have following possibilities:

* Use the phone number(s) on the device to identify the device
* Give a name to each device. particularly if the device does not have a phone number. Like Tablets.

#### TODO
* Define a server data model with the possibility of managing multiple user devices.
  * Based on phone number(s)
  * based on given device names.

### Pushing Messages to Client Device

Pushing messages to user device might involve more than just having a GCM send a chunk of bytes to the device. We are concern with:

* Do we encrypt the connection between our server and GMC
* does GCM encrypt connection to user device
* What is intruder or code mistake leads to mismatch of our device id?
* Is there any business requirement to document all messages sent to the client device? 

Below are some propositions on how to manage messages sent to client.  

#### Push Payload to Client
This approcah whan us to push the payload to the client device. We have the following options:
* Push the Plain Text to the client
* Push encrypted version of the payload to the client
  * Assume a client preshared key
  * or client sent a pulbic key to server together with registration and stored private in keychain.

#### Push message id
This is the case when the server onluy pushes a message id to the client. The cient then use a persisten access token to fetch the message.

### SMP Device Management and Event Channel

In order to keep control on which devices is registered for which client, the server environment can provide:

* An interface for the notification of user on device events
  * Registration
  * Update
* An interface for user to discover whoch devices are registered with the user account. 



