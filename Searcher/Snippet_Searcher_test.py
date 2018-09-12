#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys

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
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.jobs_3.5.200.v20120521-2346.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources.win32.x86_3.5.100.v20110423-0524.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources_3.8.0.v20120522-2034.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.core_3.8.1.v20120531-0637.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.ui_3.8.2.v20130107-165834.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jface.text_3.8.0.v20120531-0600.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.ltk.core.refactoring_3.6.100.v20130605-1748.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.osgi_3.8.0.v20120529-1548.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.text_3.5.0.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/bson-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-core-3.0.2.jar")

from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.search import IndexSearcher, SearcherManager, SearcherFactory, ReferenceManager
from org.apache.lucene.store import SimpleFSDirectory, FSDirectory
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
from org.apache.lucene.util import Version
from GitSearch.Analyzer.PorterAnalyzer import PorterAnalyzer
from GitSearch.MyUtils import arranging_query_regex
from GitSearch.DBManager import DBManager
from java.io import File
import traceback

class SnippetSearcher:
	def __init__(self, searcher, query=None):
		# self.index_path = File(index_path)
		# self.directory = None
		# self.reader = None
		self.query = query
		self.porter_analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
		self.load_index()
		self.searcher = searcher


	def load_index(self):
		a = {"code": self.porter_analyzer, "description":self.porter_analyzer, "typed_method_call": KeywordAnalyzer(),
			 "extends": KeywordAnalyzer(), "used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(),
			 "class_instance_creation": KeywordAnalyzer(), "id": KeywordAnalyzer(), "literals":self.porter_analyzer}
		self.analyzer = PerFieldAnalyzerWrapper(KeywordAnalyzer(), a)


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
