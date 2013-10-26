@echo off
if "x%CLASSPATH%" == "x" goto nocpath
	set xxCLASSPATH=%CLASSPATH%
	set CLASSPATH=GL4JInstaller.jar;%CLASSPATH%
	java GL4JInst
	set CLASSPATH=%xxCLASSPATH%
	goto finish
:nocpath
	set CLASSPATH=GL4JInstaller.jar
	java GL4JInst
	set CLASSPATH=
:finish
