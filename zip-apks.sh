#!/bin/bash
zip -r generated-apks.zip secure-device-storage/sds-android/app/build/outputs/apk/*.apk secure-mobile-push/smp-android-client/app/build/outputs/apk/*.apk sms-parser/android-sms-parser/app/build/outputs/apk/*.apk
