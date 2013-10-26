@echo off
if "x%CLASSPATH%" == "x" goto nocpath
	set xxCLASSPATH=%CLASSPATH%
	set CLASSPATH=GL4JInstaller.jar;%CLASSPATH%
	java -Dhttp.proxyHost=192.168.0.1 -Dhttp.proxyPort=3128 GL4JInst
	set CLASSPATH=%xxCLASSPATH%
	goto finish
:nocpath
	set CLASSPATH=GL4JInstaller.jar
	java -Dhttp.proxyHost=192.168.0.1 -Dhttp.proxyPort=3128 GL4JInst
	set CLASSPATH=
:finish
