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


import traceback
from java.io import File, StringReader
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.index import Term, DirectoryReader
from org.apache.lucene.search import IndexSearcher, TermQuery, SearcherManager, SearcherFactory, ReferenceManager
from org.apache.lucene.store import SimpleFSDirectory
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.queryparser.classic import QueryParser
from org.apache.lucene.util import Version
from org.apache.lucene.analysis.tokenattributes import CharTermAttribute
from collections import namedtuple
from GitSearch.DBManager import DBManager
from stemming.porter2 import stem
from GitSearch.MyUtils import english_stop_words
from GitSearch.Analyzer.PorterAnalyzer import PorterAnalyzer

INDICES_PATH = "/Indices/"
ResultItem = namedtuple("ResultItem", "doc score title question_id answer_id description")


def find_answer_ids(question_ids):
	strIDs = ', '.join(str(id) for id in question_ids)
	query = "SELECT AcceptedAnswerId from posts where id in (%s)" % strIDs
	answer_ids = DBManager.requestOneColumnQuery(query)
	return answer_ids


class GettingAnswerDocs:
	def __init__(self, index_path, query=None):
		self.index_path = File(index_path)
		# self.index_path = index_path
		self.directory = None
		self.reader = None
		self.query = query
		self.porter_analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
		self.load_index()

	def load_index(self):
		a = {"code": self.porter_analyzer, "description": self.porter_analyzer, "typed_method_call": KeywordAnalyzer(),
			 "extends": KeywordAnalyzer(), "used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(),
			 "class_instance_creation": KeywordAnalyzer(), "id": KeywordAnalyzer(), "literals": self.porter_analyzer}
		self.analyzer = PerFieldAnalyzerWrapper(KeywordAnalyzer(), a)
		self.directory = SimpleFSDirectory(self.index_path)

		self.searchermgr = SearcherManager(self.directory, SearcherFactory())
		self.searchermgr.maybeRefresh()
		self.searcher = self.searchermgr.acquire()
		# self.searchermgr.tryIncRef(self.searcher)
		# self.reader = DirectoryReader.open(self.directory)
		# self.searcher = IndexSearcher(self.reader)

	def search(self, a_ids):
		docs = []

		# #For exceptional UQ
		# if a_ids[0] == '0':
		# 	query = TermQuery(Term("answer_id", str(a_ids)))
		# 	print query
		# 	topdoc = self.searcher.search(query, 1).scoreDocs
        #
		# 	doc = self.searcher.doc(topdoc[0][0].doc)
		# 	docs.append(doc, 0, 'No Title', 'No Question id', 'No Answer id', 'No Description')
		# 	return docs
		# else:

		i = 0
		for i, a_id in enumerate(a_ids):
			query = TermQuery(Term("answer_id", str(a_id)))
			topdocs = self.searcher.search(query, 1).scoreDocs

			for hit in topdocs:
				doc = self.searcher.doc(hit.doc)
				docs.append(
					ResultItem(doc, len(a_ids) - i, doc.get("title"), doc.get("question_id"), doc.get("answer_id"),
							   doc.get("description")))

			if len(topdocs) > 0:
				i += 1
				if i > a_ids.__len__():
					break

		# self.searchermgr.decRef(self.searcher)
		self.searchermgr.release(self.searcher)
		self.searcher = None
		self.directory.close()
		self.directory = None
		return docs


BenchResultItem = namedtuple("BenchResultItem", "file file_content matched_terms score so_item line_numbers doc_id")
BenchResultItem.__eq__ = lambda x, y: x.file == y.file

BenchResultItem_UQ = namedtuple("BenchResultItem", "file file_content matched_terms score line_numbers doc_id")
BenchResultItem_UQ.__eq__ = lambda x, y: x.file == y.file

from GitSearch.MyUtils import remove_unified_stop_lists


class BenchSearcher:
	def __init__(self, index_path, query=None):
		self.index_path = File(index_path)
		# self.index_path = index_path
		self.directory = None
		self.reader = None
		self.query = query
		self.porter_analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
		self.load_index()

	def load_index(self):
		a = {"code": self.porter_analyzer, "description": self.porter_analyzer, "typed_method_call": KeywordAnalyzer(),
			 "extends": KeywordAnalyzer(), "used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(),
			 "class_instance_creation": KeywordAnalyzer(), "id": KeywordAnalyzer(), "literals": self.porter_analyzer}
		self.analyzer = PerFieldAnalyzerWrapper(KeywordAnalyzer(), a)
		self.directory = SimpleFSDirectory(self.index_path)

		self.searchermgr = SearcherManager(self.directory, SearcherFactory())
		self.searchermgr.maybeRefresh()
		self.searcher = self.searchermgr.acquire()
		# self.searchermgr.tryIncRef(self.searcher)
		# self.reader = DirectoryReader.open(self.directory)
		# self.searcher = IndexSearcher(self.reader)

	def tokenize_string(self, analyzer, string):
		result = []
		stream = analyzer.tokenStream(None, StringReader(string))
		cattr = stream.addAttribute(CharTermAttribute)
		stream.reset()
		while stream.incrementToken():
			result.append(cattr.toString())
		stream.close()
		return result

	def camel_case_split(self, s):
		import re
		s = s.replace("_", " ")
		s1 = re.sub('(.)([A-Z][a-z]+)', r'\1 \2', s)
		s = re.sub('([a-z0-9])([A-Z])', r'\1 \2', s1).lower().replace("  ", " ").split()
		return s

	def document_to_query(self, doc):
		""" Given a document it transforms the source code related fields to a lucene query string """
		query = ""
		for field in ["description"]:
			for val in doc.getFields(field):
				if val.stringValue().strip():
					term = QueryParser.escape(val.stringValue())
					# tokenize
					term = self.tokenize_string(StandardAnalyzer(), term)
					# CamelCase
					temp = []
					for t in term:
						temp += self.camel_case_split(t)
					# stopwords
					temp_2 = []

					for t in temp:
						if t not in english_stop_words:
							temp_2.append(t)
					# stemming
					temp_3 = []
					for t in temp_2:
						temp_3.append(stem(t))
					# stopwords
					temp_4 = []

					for t in temp_3:
						if t not in english_stop_words:
							temp_4.append(t)
					# query generation
					for term in temp_4:
						query += "%s:%s " % (field, term)

		for field in ["typed_method_call", "methods", "used_classes", "class_instance_creation", "methods_called",
					  "annotations", "literals"]:  # "used_classes", , "literals" , "extends"
			for val in doc.getFields(field):
				if val.stringValue().strip():
					term = QueryParser.escape(val.stringValue())
					java_stoplist = ["java.lang.Object", 'void', 'Global', 'boolean', 'String', 'int', 'char', 'float',
									 'double', 'write', 'close', 'from', 'println', 'StringBuilder', 'write',
									 'toString',
									 'close', 'mkdir', 'exists']

					if term not in java_stoplist:
						query += "%s:%s " % (field, term)

		if len(doc.getFields("code_hints")) > 0:
			hints = [hint.stringValue() for hint in doc.getFields("code_hints")]
			hints_str = " ".join(hints)
			for term in hints:
				if term:
					term = QueryParser.escape(term)
					if term not in english_stop_words:
						# print "Including 'code_hints' from Doc_To_Query TERMs... //", term
						query += "code_hints:%s " % term
		return query

	def get_matched_keywords2(self, query, doc):
		matched_terms = []
		weight_expl = self.searcher.explain(query, doc).toString().split("weight(")
		for expl in weight_expl:
			if " in " in expl:
				field_val = expl.split(" in ")[0]
				# field, val = field_val.split(":")
				val = field_val.split(":")[-1]
				matched_terms.append(val)
		return matched_terms

	def more_like_this2(self, limit, item_doc, score_logs_for_each, user_query, flag): #flag = UQ(1) or not(0)
		bench_result = []
		query = ""
		if flag == 1:
			query += user_query
			# item_doc = ResultItem(None, 0.0, "No Title", 'None','None', None)

		if flag == 0 and item_doc.doc:
			query += self.document_to_query(item_doc.doc)

		query = remove_unified_stop_lists(query)

		queryparser = QueryParser(Version.LUCENE_CURRENT, "typed_method_call", self.analyzer)
		if query:
			try:
				parsed_query = queryparser.parse(query)
				hits = self.searcher.search(parsed_query, limit).scoreDocs
				temp = 1
				for i, hit in enumerate(hits):
					doc = self.searcher.doc(hit.doc)
					matched = doc.get('file').split('/')[9].split('.')[0]
					score_logs_for_each += str(matched) + '\t' + str(round(hit.score, 2)) + '\n'
					matched_terms = self.get_matched_keywords2(parsed_query, hit.doc)
					# print "Matched Terms : ", matched_terms

					# print("File %s" % temp, doc.get("file"), "//", doc.get("file_content"))
					temp += 1

					file_path = doc.get("file")
					content = None
					try:
						with open(file_path) as f:
							content = f.read()
					except:
						pass

					if content:
						item = BenchResultItem(doc.get("file"), content, matched_terms, hit.score, item_doc,
											   doc.get("line_numbers"), hit.doc)
						bench_result.append(item)

			except Exception as e:
				print "BenchSearcher Error: %s" % e
				print(traceback.format_exc())

		# self.searchermgr.release()
		# self.searcher = None
		# self.directory.close()
		# self.directory = None
		return bench_result, score_logs_for_each

	def more_like_this3(self, limit, score_logs_for_each, user_query):
		query = ""
		bench_result = []
		# if not item_doc:
		# 	item_doc.append(ResultItem(None, 1.0, "No Title", 0, 0))
		# if item_doc.doc:
		# 	query += self.document_to_query(item_doc.doc)

		query += user_query
		query = remove_unified_stop_lists(query)

		queryparser = QueryParser(Version.LUCENE_CURRENT, "typed_method_call", self.analyzer)
		if query:
			try:
				parsed_query = queryparser.parse(query)
				hits = self.searcher.search(parsed_query, limit).scoreDocs
				temp = 1
				for i, hit in enumerate(hits):
					score_logs_for_each += str(round(hit.score, 2)) + '\n'
					doc = self.searcher.doc(hit.doc)
					matched_terms = self.get_matched_keywords2(parsed_query, hit.doc)
					# print "Matched Terms : ", matched_terms

					# print("File %s" % temp, doc.get("file"), "//", doc.get("file_content"))
					temp += 1

					file_path = doc.get("file")
					content = None
					try:
						with open(file_path) as f:
							content = f.read()
					except:
						pass

					if content:
						item = BenchResultItem_UQ(doc.get("file"), content, matched_terms, hit.score,
											   doc.get("line_numbers"), hit.doc)
						bench_result.append(item)

			except Exception as e:
				print "BenchSearcher Error: %s" % e
				print(traceback.format_exc())

		# self.searchermgr.decRef(self.searcher)
		self.searchermgr.release(self.searcher)
		self.searcher = None
		self.directory.close()
		self.directory = None
		return bench_result, score_logs_for_each

def recommend(results):
	recommend_list = list()

	for i in results:
		if i not in recommend_list:
			recommend_list.append(i)

	return recommend_list

# if __name__ == '__main__':
# 	question_ids = [u'19464224', u'6539514', u'11775479', u'11324006', u'20463239', u'14529301', u'15708147', u'6032511', u'6539514']
# 	answer_ids = find_answer_ids(question_ids)
# 	print answer_ids
#
# 	getDoc = GettingAnswerDocs("%sstackoverflow" % (INDICES_PATH))
# 	answer_docs = getDoc.search(answer_ids, 9)
#
# 	final_result = []
# 	benchsearcher = BenchSearcher("%sbigclonebench" % (INDICES_PATH))
#
# 	user_query = """
#     typed_method_call:FTPClient.setControlEncoding typed_method_call:FTPClient.login typed_method_call:FTPClient.disconnect typed_method_call:FTPClient.enterLocalPassiveMode typed_method_call:FTPClient.isConnected typed_method_call:FTPClient.setFileType typed_method_call:FTPClient.connect typed_method_call:FTPClient.storeFile typed_method_call:FTPClient.logout typed_method_call:FTPClient.changeWorkingDirectory typed_method_call:Log.e typed_method_call:File.getName typed_method_call:FTPClient.makeDirectory typed_method_call:FileInputStream.close used_classes:FTP used_classes:Log used_classes:FTPClient used_classes:FileInputStream used_classes:boolean class_instance_creation:FTPClient class_instance_creation:FileInputStream methods:uploadFile methods:login methods:FTPConnector methods_called:disconnect methods_called:makeDirectory methods_called:setFileType methods_called:getName methods_called:e methods_called:isConnected methods_called:login methods_called:storeFile methods_called:enterLocalPassiveMode methods_called:logout methods_called:changeWorkingDirectory methods_called:close methods_called:setControlEncoding methods_called:connect literals:LOGIN ERROR literals:UTF-8 literals:Artbit3 literals:FTP_UPLOAD literals:artbit123 literals:FTP_CONNECT literals:music_upload
#     """
#
# 	for answer_doc in answer_docs:
# 		final_result += benchsearcher.more_like_this2(answer_doc, user_query=user_query)
#
# 	print '========================================================================================================================\n\n\n\n\n\n'
#
# 	results = sorted(final_result, key=attrgetter('score'), reverse=True)
#
# 	print '========================================================================================================================\n\n\n\n\n\n'
#
#
# 	recommended = recommend(results)[:10]
# 	for result in recommended:
# 		print result.score, result.file, result.matched_terms
#
#