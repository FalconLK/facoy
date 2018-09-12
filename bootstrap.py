#!/usr/local/bin/jython
# -*- coding: utf-8 -*-

import sys
import os
from os.path import isfile, isdir, abspath
import subprocess
from glob import glob

def read_classpath():
	with open("/tmp/classpath.txt", "r") as f:
		return f.read()

def write_classpath(classpath):
	with open("/tmp/classpath.txt", "w") as f:
		f.write(classpath)

if not isfile("/tmp/classpath.txt"):
	# Import all required jar files to perform AST contstruction
	paths = [jar for jar in glob("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/*.jar")]
	#paths.append("/Users/Raphael/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.6.jar")	######Where is it..
	# Directory with class files
	#paths.append("/Users/Raphael/Google Drive/GithubSearch/GitSearch/bin")
	#paths.append("/Users/Raphael/Google Drive/GitSearchScala/out/production/GitSearchScala")	######Where is it..

	classpath = "%s:%s" % (":".join(paths), os.environ.get("CLASSPATH"))
	write_classpath(classpath)
else:
	classpath = read_classpath()

os.environ["CLASSPATH"] = classpath
#print(os.environ["CLASSPATH"])
current_dir = os.getcwd()
os.environ["JYTHONPATH"] = current_dir + ":" + ":".join([abspath(folder) for folder in glob("*") if isdir(folder)])
#print os.environ["JYTHONPATH"]

if sys.argv[1] == "clean":
	os.remove("/tmp/classpath.txt")
	print "Cleaned"
else:
	subprocess.call(["jython", sys.argv[1]])
