#!/usr/bin/env bash

cd Frameworks
curl https://packages.couchbase.com/releases/couchbase-lite/ios/2.0DB18/couchbase-lite-swift_community_2.0.0-434.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
