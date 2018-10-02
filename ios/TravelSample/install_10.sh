#!/usr/bin/env bash

# This downloads swift4.2 compatible version of CBL. Use with Xcode 10.0 +
cd Frameworks
rm -rf iOS
rm -rf macOS
rm -rf tvOS
curl http://packages.couchbase.com/releases/couchbase-lite-ios/2.1.1/couchbase-lite-swift_enterprise_2.1.1.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
