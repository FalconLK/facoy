#!/usr/bin/env python
# -*- coding: utf-8 -*-
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

from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.index import Term, DirectoryReader
from org.apache.lucene.search import IndexSearcher, SearcherManager, SearcherFactory, ReferenceManager
from org.apache.lucene.store import SimpleFSDirectory, FSDirectory
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
from org.apache.lucene.util import Version

from GitSearch.Analyzer.PorterAnalyzer import PorterAnalyzer
from GitSearch.MyUtils import arranging_query_regex
import traceback
from GitSearch.DBManager import DBManager
from java.io import File

INDICES_PATH = "/Indices/"


class SnippetSearcher:
	def __init__(self, index_path, query=None):
		self.index_path = File(index_path)
		# self.index_path = index_path
		self.directory = None
		self.reader = None
		self.query = query
		self.porter_analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
		self.load_index()


	def load_index(self):
		a = {"code": self.porter_analyzer, "description":self.porter_analyzer, "typed_method_call": KeywordAnalyzer(),
			 "extends": KeywordAnalyzer(), "used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(),
			 "class_instance_creation": KeywordAnalyzer(), "id": KeywordAnalyzer(), "literals":self.porter_analyzer}
		self.analyzer = PerFieldAnalyzerWrapper(KeywordAnalyzer(), a)
		self.directory = SimpleFSDirectory(self.index_path)


		self.searchermgr = SearcherManager(self.directory, SearcherFactory())
		self.searchermgr.maybeRefresh()
		self.searcher = self.searchermgr.acquire()
		# self.searchermgr.tryIncRef(self.searcher)
		# self.reader = DirectoryReader.open(self.directory)
		# self.searcher = IndexSearcher(self.reader)

		# index = SimpleFSDirectory(indexDir)
		# self.reader = IndexReader.open(index)
		# self.searcher = SearcherFactory.newSearcher(self.reader)



	def get_matched_keywords(self, query, doc):
		matched_terms = []
		weight_expl = self.searcher.explain(query, doc).toString().split("weight(")
		for expl in weight_expl:
			if " in " in expl:
				field_val = expl.split(" in ")[0]
				val = field_val.split(":")[-1]
				matched_terms.append(val)
		return matched_terms

	def more_like_this(self, result_num, query):
		result = []
		queryparser = QueryParser(Version.LUCENE_CURRENT, "methods_called", self.porter_analyzer)
		if query:
			try:
				query = arranging_query_regex(query=query)
				# print '4. Right after the regex handling : ', query
				like_query = queryparser.parse(query)
				# print '5. Right after the Lucene parser : ', like_query

				hits = self.searcher.search(like_query, result_num).scoreDocs
				# filterScoreDosArray = hits.topDocs().scoreDocs;

				for i, hit in enumerate(hits):
					doc = self.searcher.doc(hit.doc)
					# matched_terms = self.get_matched_keywords(like_query, hit.doc)
					result.append(doc.get("answer_id"))

			except Exception as e:
				print "AnswerSearcher: Error: %s" % e
				print(traceback.format_exc())

		# self.searchermgr.decRef(self.searcher)
		self.searchermgr.release(self.searcher)
		self.searcher = None
		self.directory.close()
		self.directory = None
		return result

	def find_question_ids(self, answer_ids):
		result_list = []
		for id in answer_ids:
			# print "Answer id : ", id, " /// ", ;
			query = "SELECT parentID from posts where id = %s" % id
			question_id = DBManager.requestOneColumnQuery(query)
			result_list.append(question_id[0])
			# print "Question id : ", question_id[0]
		return result_list

# if __name__ == '__main__':
# 	query = """
# 	typed_method_call:FTPClient.setControlEncoding typed_method_call:FTPClient.login typed_method_call:FTPClient.disconnect typed_method_call:FTPClient.enterLocalPassiveMode typed_method_call:FTPClient.isConnected typed_method_call:FTPClient.setFileType typed_method_call:FTPClient.connect typed_method_call:FTPClient.storeFile typed_method_call:FTPClient.logout typed_method_call:FTPClient.changeWorkingDirectory typed_method_call:Log.e typed_method_call:File.getName typed_method_call:FTPClient.makeDirectory typed_method_call:FileInputStream.close used_classes:FTP used_classes:Log used_classes:FTPClient used_classes:FileInputStream used_classes:boolean class_instance_creation:FTPClient class_instance_creation:FileInputStream methods:uploadFile methods:login methods:FTPConnector methods_called:disconnect methods_called:makeDirectory methods_called:setFileType methods_called:getName methods_called:e methods_called:isConnected methods_called:login methods_called:storeFile methods_called:enterLocalPassiveMode methods_called:logout methods_called:changeWorkingDirectory methods_called:close methods_called:setControlEncoding methods_called:connect literals:LOGIN ERROR literals:UTF-8 literals:Artbit3 literals:FTP_UPLOAD literals:artbit123 literals:FTP_CONNECT literals:music_upload
# 	"""
#
# 	answer = SnippetSearcher("%sstackoverflow" % (INDICES_PATH), query)
#
# 	#유저 Code Query와 유사한 Snippet들을 가진 Answer Posts 도출
# 	answer_ids = answer.more_like_this(10, query=query)
# 	print answer_ids
#
# 	#도출된 Answer Posts에 각각 해당되는 Question Posts Id들 찾기
# 	question_ids = answer.find_question_ids(answer_ids)
# 	print question_ids