#!/bin/sh
./gradlew bootJar

scp build/libs/autossav-0.0.1-SNAPSHOT.jar root@172.168.2.16:/root/autossav/server/autossav/autossav.jar
ssh root@172.168.2.16 << remotessh
cd /root/autossav/server/autossav
docker stop autossav_home
docker run --privileged --rm -d -it --name autossav_home --network mynetwork -p 9981:8080 -v /root/autossav/server/autossav:/autossav_home -w /autossav_home openjdk java -jar autossav.jar --logging.config=config/log4j2.xml
exit
remotessh