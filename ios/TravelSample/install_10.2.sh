#!/usr/bin/env bash

# This downloads swift4.2 compatible version of CBL. Use with Xcode 10.2 +
cd Frameworks
rm -rf iOS
rm -rf macOS
rm -rf tvOS
curl https://packages.couchbase.com/releases/couchbase-lite-ios/2.5.0/couchbase-lite-swift_enterprise_2.5.0.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
