#!/usr/bin/env python
# -*- coding: utf-8 -*-
############BigCloneBench(IJaData) Indexing Code############

import sys
sys.path.append("/Libs/jsoup-1.8.2.jar")
sys.path.append("/Libs/lucene-analyzers-common-4.10.4.jar")
sys.path.append("/Libs/lucene-core-4.10.4.jar")
sys.path.append("/Libs/lucene-queries-4.10.4.jar")
sys.path.append("/Libs/lucene-queryparser-4.10.4.jar")
sys.path.append("/Libs/jython-standalone-2.7.0.jar")
sys.path.append("/Libs/mysql-connector-java-5.1.22-bin.jar")
sys.path.append("/Libs/py4j-0.9.jar")
sys.path.append("/Libs/org.apache.commons.lang_2.6.0.v201205030909.jar")
sys.path.append("/Libs/org.eclipse.cdt.core_5.6.0.201402142303.jar")
sys.path.append("/Libs/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar")
sys.path.append("/Libs/org.eclipse.core.jobs_3.5.200.v20120521-2346.jar")
sys.path.append("/Libs/org.eclipse.core.resources.win32.x86_3.5.100.v20110423-0524.jar")
sys.path.append("/Libs/org.eclipse.core.resources_3.8.0.v20120522-2034.jar")
sys.path.append("/Libs/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar")
sys.path.append("/Libs/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar")
sys.path.append("/Libs/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar")
sys.path.append("/Libs/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar")
sys.path.append("/Libs/org.eclipse.jdt.core_3.8.1.v20120531-0637.jar")
sys.path.append("/Libs/org.eclipse.jdt.ui_3.8.2.v20130107-165834.jar")
sys.path.append("/Libs/org.eclipse.jface.text_3.8.0.v20120531-0600.jar")
sys.path.append("/Libs/org.eclipse.ltk.core.refactoring_3.6.100.v20130605-1748.jar")
sys.path.append("/Libs/org.eclipse.osgi_3.8.0.v20120529-1548.jar")
sys.path.append("/Libs/org.eclipse.text_3.5.0.jar")
sys.path.append("/Libs/bson-3.0.2.jar")
sys.path.append("/Libs/mongodb-driver-3.0.2.jar")
sys.path.append("/Libs/mongodb-driver-core-3.0.2.jar")

import os
import codecs
import time
from java.io import File
from java.io import IOException
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.en import EnglishAnalyzer
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.document import Document, Field, StringField
from org.apache.lucene.index import IndexWriter, IndexWriterConfig, CorruptIndexException
from org.apache.lucene.store import SimpleFSDirectory, LockObtainFailedException
from org.apache.lucene.util import Version
from GitSearch.MyUtils import md5, so_tokenizer
from GitSearch.Analyzer.JavaCodeAnalyzer import JavaCodeAnalyzer
from GitSearch.Indexer.NewJavaParser import parse
from GitSearch.Indexer.Indexer_Counter import Counter

hashes = set()

def java_files_from_dir(directory):
	print "java files from the directory"
	javafiles = (os.path.join(dirpath, f)
		for dirpath, dirnames, files in os.walk(directory)
		for f in files if f.endswith('.java'))
	return javafiles

def generate_indices_from_projects(writer, counter):
	HOME = "/Users/Falcon/Desktop/IJA/dataset/"
	javafiles = java_files_from_dir(HOME)	#자바 파일들만 뽑아내는 함수
	i = 0
	j = 0
	for javafile in javafiles:
		#print javafile
		i += 1
		if i % 1000 == 0:	#1000개 될때마다 프린트 한번씩
			print("Counter: %s" % i)
			print "typed_method_call" + str(counter.typed_method_call_count)
		document = Document()	#루씬 Document 객체

		################################################################################################################
		splits = javafile.split("/")[6:]
		project_path = ""
		for names in splits:
			project_path += "/" + names

		changed_path = "/Users/Falcon/Desktop/IJA/dataset" + project_path
		document.add(Field("file", changed_path, Field.Store.YES, Field.Index.NO))
		################################################################################################################
		# document.add(Field("file", javafile, Field.Store.YES, Field.Index.NO))
		try:
			with codecs.open(javafile, "r", encoding='utf-8', errors='ignore') as f:
				file_content = f.read().encode("utf-8", errors='ignore')

			ast = parse(file_content, resolve=False)	#newJavaParser를 사용하여 자바 코드 파싱
			if add_code_keyword_into_document(document, file_content, ast, counter):
				writer.addDocument(document)
				j += 1
				if j % 1000 == 0:
					print "Wrote:: %s files" % j

		except Exception as e:
			print("Error: %s" % e)
			continue
	print "Number of files: %s" % i
	print "Number of duplicates: %s" % len(hashes)
	print "%s files has been indexed" % j

def add_code_keyword_into_document(document, file_content, node, counter):
	# Flag is set when at least 1 code characteristics has been stored
	flag = False
	document.add(Field("line_numbers", str(dict(node["line_numbers"])), Field.Store.YES, Field.Index.NO))
	document.add(Field("hash", str(md5(file_content)), Field.Store.YES, Field.Index.NO))
	document.add(Field("code", so_tokenizer(file_content, False), Field.Store.YES, Field.Index.ANALYZED))

	for m in node["typed_method_call"]:
		if m:
			document.add( Field("typed_method_call", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.typed_method_call_count += 1
			flag = True

	for e in node["extends"]:
		if e:
			document.add(Field("extends", e, Field.Store.NO, Field.Index.ANALYZED))
			counter.extends_count += 1

	for c in node["used_classes"]:
		if c:
			document.add( Field("used_classes", str(c), Field.Store.YES, Field.Index.ANALYZED))
			counter.used_classes_count += 1

	for i in node["class_instance_creation"]:
		if i:
			document.add( Field("class_instance_creation", i, Field.Store.YES, Field.Index.ANALYZED) )
			counter.class_instance_creation_count += 1
			flag = True

	for m in node["methods"]:
		if m:
			document.add(Field("methods", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.methods_count += 1

	for m in node["methods_called"]:
		if m:
			document.add(Field("methods_called", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.methods_called_count += 1
			flag = True

	for m in node["unresolved_method_calls"]:
		if m:
			document.add(Field("unresolved_method_calls", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.unresolved_method_calls_count += 1

	for l in node["literals"]:
		if l:
			document.add( StringField("literals", l, Field.Store.YES))
			counter.literals_count += 1
			flag = True

	return flag

def main():
	try:
		print "Indexing starts..."
		indicesDestination = File("/Users/Falcon/Desktop/New_Indices/IJA_Indices")

		analyzer = KeywordAnalyzer()  
		a = {"code": JavaCodeAnalyzer(), "comments": EnglishAnalyzer(Version.LUCENE_CURRENT)}
		wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a) 				
		config = IndexWriterConfig(Version.LUCENE_CURRENT, wrapper_analyzer)

		writer = IndexWriter(SimpleFSDirectory(indicesDestination), config)
		counter = Counter()
		generate_indices_from_projects(writer, counter)
		writer.close()

		print "Done"
		print str(counter)

	except CorruptIndexException as e:		#when index is corrupt
			e.printStackTrace()
	except LockObtainFailedException as e:	#when other writer is using the index
			e.printStackTrace()
	except IOException as e:	#when directory can't be read/written
			e.printStackTrace()

if __name__ == '__main__':
	print("*** Indexing IJaDataset ***\n")
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))
