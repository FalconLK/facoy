# -*- coding: utf-8 -*-
############BigCloneBench(IJaData) Indexing Code by Functions############

import sys
import os
import codecs
import time

sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/jsoup-1.8.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-analyzers-common-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-core-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-queries-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-queryparser-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/jython-standalone-2.7.0.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mysql-connector-java-5.1.22-bin.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/py4j-0.9.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.apache.commons.lang_2.6.0.v201205030909.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.cdt.core_5.6.0.201402142303.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.jobs_3.5.200.v20120521-2346.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources.win32.x86_3.5.100.v20110423-0524.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources_3.8.0.v20120522-2034.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.core_3.8.1.v20120531-0637.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.ui_3.8.2.v20130107-165834.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jface.text_3.8.0.v20120531-0600.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.ltk.core.refactoring_3.6.100.v20130605-1748.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.osgi_3.8.0.v20120529-1548.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.text_3.5.0.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/bson-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-core-3.0.2.jar")


from java.io import IOException, File, StringReader
from org.apache.lucene.document import Document, Field, StringField
from org.apache.lucene.index import IndexWriter, IndexWriterConfig, CorruptIndexException
from org.apache.lucene.store import SimpleFSDirectory, LockObtainFailedException, RAMDirectory, FSDirectory
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.util import Version
from GitSearch.Indexer.NewJavaParser import parse
from GitSearch.Indexer.Indexer_Counter import Counter
from GitSearch.Analyzer.PorterAnalyzer import PorterAnalyzer

# source_path = "/Users/Falcon/Desktop/test/test_result/"
# dest_path = '/Users/Falcon/Desktop/New_Indices/IJA_Indices'
source_path = "/Users/Falcon/Desktop/IJA/Dataset_by_func/"
dest_path = '/Users/Falcon/Desktop/New_Indices/IJA_Indices'

hashes = set()

def java_files_from_dir(directory):
	print "java files from the directory"
	javafiles = (os.path.join(dirpath, f)
		for dirpath, dirnames, files in os.walk(directory)
		for f in files if f.endswith('.java'))
	return javafiles

def generate_indices_from_benchmark(writer, counter):
	javafiles = java_files_from_dir(source_path)
	i = 0
	j = 0
	for javafile in javafiles:
		# print javafile
		i += 1
		if i % 1000 == 0:	#1000개 마다 프린트
			print("Counter: %s" % i)
			# print "typed_method_call" + str(counter.typed_method_call_count)
		document = Document()
		document.add(Field("file", javafile, Field.Store.YES, Field.Index.NO))
		try:
			with codecs.open(javafile, "r", encoding='utf-8', errors='ignore') as f:
				file_content = f.read().encode("utf-8", errors='ignore')
			f.close()

			ast = parse(file_content, resolve=False)
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
	# document.add(Field("line_numbers", str(dict(node["line_numbers"])), Field.Store.YES, Field.Index.NO))
	# document.add(Field("hash", str(md5(file_content)), Field.Store.YES, Field.Index.NO))
	# document.add(Field("code", so_tokenizer(file_content, False), Field.Store.YES, Field.Index.ANALYZED))

	for m in node["typed_method_call"]:
		if m:
			document.add( Field("word", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.typed_method_call_count += 1
			flag = True

	for e in node["extends"]:
		if e:
			document.add(Field("word", e, Field.Store.NO, Field.Index.ANALYZED))
			counter.extends_count += 1

	for c in node["used_classes"]:
		if c:
			document.add( Field("word", str(c), Field.Store.YES, Field.Index.ANALYZED))
			counter.used_classes_count += 1

	for i in node["class_instance_creation"]:
		if i:
			document.add( Field("word", i, Field.Store.YES, Field.Index.ANALYZED) )
			counter.class_instance_creation_count += 1
			flag = True

	for m in node["methods"]:
		if m:
			document.add(Field("word", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.methods_count += 1

	for m in node["methods_called"]:
		if m:
			document.add(Field("word", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.methods_called_count += 1
			flag = True

	for m in node["unresolved_method_calls"]:
		if m:
			document.add(Field("word", m, Field.Store.YES, Field.Index.ANALYZED))
			counter.unresolved_method_calls_count += 1

	for l in node["literals"]:
		if l:
			document.add( StringField("word", l, Field.Store.YES))
			counter.literals_count += 1
			flag = True

	return flag

def main():
	try:
		indicesDestination = File(dest_path)
		analyzer = KeywordAnalyzer()
		porter_analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
		a = {"code": porter_analyzer, "description": porter_analyzer, "typed_method_call": KeywordAnalyzer(),
			 "extends": KeywordAnalyzer(), "used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(),
			 "class_instance_creation": KeywordAnalyzer(), "id": KeywordAnalyzer(), "literals": porter_analyzer,
             "word":KeywordAnalyzer()}
		wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a)
		config = IndexWriterConfig(Version.LUCENE_CURRENT, wrapper_analyzer)

		writer = IndexWriter(SimpleFSDirectory(indicesDestination), config)
		counter = Counter()
		generate_indices_from_benchmark(writer, counter)
		writer.close()

		print "All jobs are done.."
		print str(counter)

	except CorruptIndexException as e:		#when index is corrupt
			e.printStackTrace()
	except LockObtainFailedException as e:	#when other writer is using the index
			e.printStackTrace()
	except IOException as e:	#when directory can't be read/written
			e.printStackTrace()

if __name__ == '__main__':
	print("*** Indexing IJaDataset by func. ***\n")
	start_time = time.time()
	main()
	print("--- %s seconds ---" % (time.time() - start_time))
