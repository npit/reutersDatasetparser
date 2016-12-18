#!/usr/bin/env bash

# input: folder with files with cql commands
[ $# -lt 1 ] && echo "Give input folder" && exit 1
folder="$1"
dest="./cqlFiles"
mkdir -p $dest
echo "Copying from [$folder] to  [$dest]"
for f in $(ls $folder | grep -i ".cql"); do
	echo "    Copying [$f]"
	cp $folder/$f $dest/

done