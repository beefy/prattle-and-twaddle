#!/bin/sh

if [ -z ${CLASSPATH:=""} ] ; then
	CLASSPATH=GL4JInstaller.jar
else
	CLASSPATH=GL4JInstaller.jar:${CLASSPATH}
fi
export CLASSPATH
exec java -Dhttp.proxyHost=192.168.0.1 -Dhttp.proxyPort=3128 GL4JInst
