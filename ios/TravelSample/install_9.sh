#!/usr/bin/env bash

# This downloads swift 3.2/ swift4 compatible version of CBL. Use with Xcode 9
cd Frameworks
curl https://packages.couchbase.com/releases/couchbase-lite/ios/2.0DB18/couchbase-lite-swift_community_2.0.0-434.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
