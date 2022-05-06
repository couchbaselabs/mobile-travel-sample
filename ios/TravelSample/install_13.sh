#!/usr/bin/env bash

# This downloads swift5.1.x compatible version of CBL. Use with Xcode 12+
cd Frameworks
rm -rf iOS
rm -rf macOS
rm -rf tvOS
curl https://packages.couchbase.com/releases/couchbase-lite-ios/3.0.0/couchbase-lite-swift_xc_enterprise_3.0.0.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
