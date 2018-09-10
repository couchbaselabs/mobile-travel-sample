#!/usr/bin/env bash

# This downloads swift4.1 compatible version of CBL. Use with Xcode 9.3 +
cd Frameworks
rm -rf iOS
rm -rf macOS
rm -rf tvOS
curl http://packages.couchbase.com/releases/couchbase-lite-ios/2.1.0/couchbase-lite-swift_enterprise_2.1.0.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
