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
	# Import all required jar files to perform AST construction
	paths = [jar for jar in glob("/Libs/*.jar")]

	classpath = "%s:%s" % (":".join(paths), os.environ.get("CLASSPATH"))
	write_classpath(classpath)
else:
	classpath = read_classpath()

os.environ["CLASSPATH"] = classpath
current_dir = os.getcwd()
os.environ["JYTHONPATH"] = current_dir + ":" + ":".join([abspath(folder) for folder in glob("*") if isdir(folder)])

if sys.argv[1] == "clean":
	os.remove("/tmp/classpath.txt")
	print "Cleaned"
else:
	subprocess.call(["jython", sys.argv[1]])
