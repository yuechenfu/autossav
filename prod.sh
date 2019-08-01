#!/bin/sh
./gradlew bootJar

scp build/libs/autossav-0.0.1-SNAPSHOT.jar root@64.188.16.34:/root/autossav/server/autossav/autossav.jar
ssh root@64.188.16.34 << remotessh
cd /root/autossav/server/autossav
docker stop autossav_home
docker run --rm -d -it --name autossav_home --network net -p 9981:8080 -v /root/autossav/server/autossav:/autossav_home -w /autossav_home openjdk java -jar autossav.jar
exit
remotessh