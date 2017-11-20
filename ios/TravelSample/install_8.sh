#!/usr/bin/env bash
# This downloads swift 3.1 compatible version of CBL (Use with Xcode 8)
cd Frameworks
curl http://couchbase-docs.s3.amazonaws.com/workshop/db20-xcode8/couchbase-lite-swift_db20_xcode8_community.zip > cbl.zip
unzip -n cbl.zip
rm -rf cbl.zip
rm -rf cbl
