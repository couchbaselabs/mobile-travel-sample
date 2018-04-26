#!/usr/bin/env bash

# This downloads swift 3.2/ swift4 compatible version of CBL. Use with Xcode 9
cd Frameworks
rm -rf iOS
rm -rf macOS
rm -rf tvOS
curl https://s3.amazonaws.com/cbmobile-travelsample-demo/couchbase-lite-swift_xcode9.2_community.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
