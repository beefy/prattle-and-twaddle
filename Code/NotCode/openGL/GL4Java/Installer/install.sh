#!/bin/sh

if [ -z ${CLASSPATH:=""} ] ; then
	CLASSPATH=GL4JInstaller.jar
else
	CLASSPATH=GL4JInstaller.jar:${CLASSPATH}
fi
export CLASSPATH
exec java GL4JInst
