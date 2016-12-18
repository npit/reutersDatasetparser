#!/usr/bin/env bash

# input: folder with files with cql commands
[ $# -lt 1 ] && echo "Give input folder" && exit 1
folder="$1"
[ $# -gt 1 ] && num=$2

cqlsh  --cqlversion="3.3.1" -k bde -e "truncate news_articles_per_place;" 172.17.0.2 9042 

count=0

for f in $(ls $folder); do
	if [ $count -eq $num ] ; then exit 0; fi
	count=$(( $count + 1 ))
	echo "Incorporating file [$f]"
	cqlsh  --cqlversion="3.3.1" -k bde -f  "$folder/$f" 172.17.0.2 9042 

done
