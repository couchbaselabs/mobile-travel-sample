#!/usr/bin/env bash

cd Frameworks
curl https://packages.couchbase.com/releases/couchbase-lite/ios/2.0DB7/couchbase-lite-swift_community_2.0DB007.zip  > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
