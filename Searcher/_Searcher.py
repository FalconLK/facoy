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

from java.io import File, StringReader
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.index import IndexReader, Term
from org.apache.lucene.search import IndexSearcher, FuzzyQuery, TermQuery, BooleanQuery
from org.apache.lucene.search.spans import SpanNearQuery, SpanQuery, SpanTermQuery, SpanMultiTermQueryWrapper
from org.apache.lucene.search.BooleanClause import Occur
from org.apache.lucene.store import SimpleFSDirectory
from org.apache.lucene.analysis.core import WhitespaceAnalyzer
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.analysis.tokenattributes import CharTermAttribute
from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
from org.apache.lucene.queries.mlt import MoreLikeThis
from org.apache.lucene.queries.function.valuesource import LongFieldSource
from org.apache.lucene.queries.function import FunctionQuery
from org.apache.lucene.queries import CustomScoreQuery
from org.apache.lucene.util import Version

from Analyzer.JavaCodeAnalyzer import JavaCodeAnalyzer
from Analyzer.PorterAnalyzer import PorterAnalyzer
from zlib import decompress
from collections import Counter, namedtuple
from _GoogleSearcher import GoogleSearcher
import traceback


class GoogleStackoverflowSearcher:
	def __init__(self, index_path):
		# 요 부분은 다 lucene을 통해 인덱스들을 불러오고, reader와 searcher 초기화
		indexDir = File(index_path)
		index = SimpleFSDirectory(indexDir)
		self.reader = IndexReader.open(index)		#IndexReader 열고 닫지 않았었음...........................
		self.searcher = IndexSearcher(self.reader)
	# n_docs = self.reader.numDocs()

	def search(self, query, limit=1):  # default 파라미터로 5를 넣었음..
		# 이 함수를 통해 결과불러줄 것들을 정리하는 거인듯..
		docs = []
		query += " java site:stackoverflow.com"
		g = GoogleSearcher()
		q_ids = g.search(query)	#q_ids : Stackoverflow ids

		i = 0
		for i, q_id in enumerate(q_ids):
			query = TermQuery(Term("id", q_id));
			topdocs = self.searcher.search(query,10).scoreDocs
			# index searcher에 TermQuery의 객체가 들어가고.. 질문 id에 달려있는 답변 중 상위 n개 가져옴/ scoreDocs는 점수 (소수형임..)
			for hit in topdocs:
				doc = self.searcher.doc(hit.doc)
				docs.append(SOResultItem(doc, len(q_ids) - i, doc.get("title"), doc.get("id"), doc.get("description")))

				print("%s, Question Id: %s, Answer Id: %s" % (doc.get("title"), doc.get("id"), doc.get("answer_id")))

			if len(topdocs) > 0:
				i += 1
				if i >= limit:
					break
			else:
				print "Stackoverflow id %s is not in our index" % q_id
		return docs

def tokenize_string(analyzer, string):
	result = []
	stream = analyzer.tokenStream(None, StringReader(string))
	cattr = stream.addAttribute(CharTermAttribute)
	stream.reset()
	while stream.incrementToken():
		result.append(cattr.toString())
	stream.close()
	return result

def getSpanNearQuery(analyzer, s, field="title", slop=100, inOrder=True):
	keywords = tokenize_string(analyzer, s)
	spanTermQueries = [ SpanMultiTermQueryWrapper( FuzzyQuery( Term(field, keyword) ) ) for keyword in keywords]
	return SpanNearQuery( spanTermQueries, slop, inOrder)

def retrieve_ranked_apis(question_id):
	docs = coll.find(BasicDBObject({"question_id": question_id}))
	apis = []
	for doc in docs:
		answer = doc.toMap()
		apis.append(answer["typed_method_call"])
	print apis

SOResultItem = namedtuple("SOResultItem", "doc score title id description")

class StackoverflowSearcher:
	def __init__(self, index_path):
		self.index_path = index_path
		self.reader = None
		self.query = None
		self.analyzer = None
		self.load_index()

	# 1. open the index
	def load_index(self):
		indexDir = File(self.index_path)
		porter_analyzer = PorterAnalyzer( StandardAnalyzer(Version.LUCENE_CURRENT))
		a = {	"typed_method_call": KeywordAnalyzer(), 
				"extends": KeywordAnalyzer(), 
				"used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(), "class_instance_creation": KeywordAnalyzer(), "id": KeywordAnalyzer(), "code": JavaCodeAnalyzer()}

		self.analyzer = PerFieldAnalyzerWrapper(porter_analyzer, a)
		index = SimpleFSDirectory(indexDir)
		self.reader = IndexReader.open(index)		#IndexReader 열고 닫지 않았었음...........................
		n_docs = self.reader.numDocs()
		print("Index contains %d documents." % n_docs)

	# 2. parse the query from the command line
	def parse_query(self, query_string, order_matters=True):
		query_parser = MultiFieldQueryParser(Version.LUCENE_CURRENT, ["title", "qbody"], self.analyzer)
		if order_matters:
			# Take into account order of query terms
			base_query = getSpanNearQuery(self.analyzer, query_string)
		else:
			# Considers query keywords as bag of words 
			base_query = query_parser.parse(query_string)
		#http://shaierera.blogspot.com/2013/09/boosting-documents-in-lucene.html
		boost_query = FunctionQuery( LongFieldSource("view_count"))
		self.query = CustomScoreQuery(base_query, boost_query)

		# queryparser = QueryParser(Version.LUCENE_CURRENT, "title", analyzer)
		# query = queryparser.parse(query_string)

	# 3. search the index for the query
	# We retrieve and sort all documents that match the query.
	# In a real application, use a TopScoreDocCollector to sort the hits.
	def search(self, query_string=""):

		if not self.query:
			self.parse_query(query_string)

		searcher = IndexSearcher(self.reader)
		hits = searcher.search(self.query, 5).scoreDocs

		# When ordered query does not return any result issue an query where order does not matter
		if len(hits) < 5:
			print "Order does not matter"
			self.parse_query(query_string, False)

		print "Number of hits %s " % len(hits)
		# 4. display results
		print(query_string)
		print("Found %d hits:" % len(hits))

		items = []
		api_acc = []
		for i, hit in enumerate(hits):
			doc = searcher.doc(hit.doc)
			
			apis = [d.stringValue() for d in doc.getFields("typed_method_call")]
			#queries.append(self.document_to_query(doc)) # hit.doc returns lucenes internal ID for the given document 
			items.append(SOResultItem(doc, hit.score, doc.get("title"), doc.get("id"), doc.get("description")))
			api_acc.extend(apis)
			#retrieve_ranked_apis(doc.get("answer_id"))
			print("%d.  %s, Answer Id: %s, Method: %s, Score: %s" % (i + 1, doc.get("title"), doc.get("answer_id"), apis, hit.score))

		print Counter(api_acc).most_common(5)
		return items

GithubResultItem = namedtuple("GithubResultItem", "file file_content matched_terms score so_item line_numbers doc_id")

class GitHubSearcher:
	def __init__(self, index_path, query=None):
		self.index_path = index_path
		self.reader = None
		self.query = query
		self.porter_analyzer = PorterAnalyzer( StandardAnalyzer(Version.LUCENE_CURRENT))
		self.load_index()

	def load_index(self):
		indexDir = File(self.index_path)
		a = {"code": self.porter_analyzer}
		self.analyzer = PerFieldAnalyzerWrapper(KeywordAnalyzer(), a) 
		index = SimpleFSDirectory(indexDir)
		self.reader = IndexReader.open(index)		#IndexReader 열고 닫지 않았었음...........................
		n_docs = self.reader.numDocs()
		self.searcher = IndexSearcher(self.reader)
		print("Index contains %d documents." % n_docs)

	def get_DF(self, field, term):
		return self.reader.docFreq(Term(field, term))

	def get_IDF(self, field, term):
		from math import log10, sqrt
		docF = self.reader.docFreq(Term(field, term))

		return log10(self.reader.numDocs() / (docF + 1)) + 1

	def get_minimum_IDF(self, docF=2):
		from math import log10, sqrt
		return log10(self.reader.numDocs() / (docF + 1)) + 1

	def document_to_query(self, doc):
		""" Given a document it transforms the source code related fields to a lucene query string"""
		query = ""
		for field in ["typed_method_call", "methods", "used_classes", "class_instance_creation", "methods_called", "annotations", "literals"]: #"used_classes", , "literals" , "extends"
			for val in doc.getFields(field):
				if val.stringValue().strip():
					term = QueryParser.escape(val.stringValue())

					# Filter out noisy terms
					stoplist = ["java.lang.Object"]
					if term not in stoplist:
						# idf = self.get_IDF(field, term)
						
						# print self.get_DF(field, term), term, field
						#query += "%s:%s^%s " % (field, term, idf)
						query += "%s:%s " % (field, term)
				
			
					#print "term: %s idf: %s" % (term, self.get_minimum_IDF())

				#query += "%s:%s " % (field, term)
				#print "%s:%s^%s" % (field, term, self.getIDF(field, term))
		# for hint in doc.getFields("code_hints"):
		# 	tokens = utils.tokenize(hint.stringValue())
		# 	for token in tokens:
		# 		#print token
		# 		token = QueryParser.escape(token)
		# 		if token.strip():
		# 			print "HINTS", token
		# 			query += "code:%s^5.0 " % (token)


		if len(doc.getFields("code_hints")) > 0:
			hints = [hint.stringValue() for hint in doc.getFields("code_hints")]
			hints_str = " ".join(hints)
			for term in hints:
				if term:
					term = QueryParser.escape(term)
					print "TERM", term
					# if term[0].isupper():
					# 	query += "used_classes:%s^5.0 class_instance_creation:%s^5.0 " % (term, term)
					# elif "(" in term or "." in term or "#" in term: # Heuristic to boost only code identifiers
					# 	query += "methods:%s^5.0 methods_called:%s^5.0 " % (term, term)

					
					#query += "code:%s^5.0 " % (term)

		
		
		return query

	def get_matched_keywords(self, query, docid):
		matched_terms = []
		# def _get_matched_keywords(q, matched_terms):
		# 	print type(q), matched_terms
		# 	if isinstance(q, TermQuery):
		# 		if self.searcher.explain(q, docid).isMatch():
		# 			matched_terms.append( q.getTerm().text() )
		# 	elif isinstance(q, BooleanQuery):
		# 		for query_term in query.getClauses():
		# 			_get_matched_keywords(query_term, matched_terms)
		# 			# if self.searcher.explain(query_term.getQuery(), docid).isMatch():
		# 			# 	matched_terms.append( query_term.getQuery().getTerm().text() )

		# _get_matched_keywords(query, matched_terms)


		if isinstance(query, TermQuery):
			if self.searcher.explain(query, docid).isMatch():
				matched_terms.append( query.getTerm().text() )
		elif isinstance(query, BooleanQuery):
			for query_term in query.getClauses():
				if self.searcher.explain(query_term.getQuery(), docid).isMatch():
					matched_terms.append( query_term.getQuery().getTerm().text() )
		
		#print "Matched Terms: %s" % matched_terms
		return matched_terms

	def get_matched_keywords2(self, query, doc):
		matched_terms = []
		weight_expl = self.searcher.explain(query, doc).toString().split("weight(")
		for expl in weight_expl:
			if " in " in expl:
				field_val = expl.split(" in ")[0]
				#field, val = field_val.split(":")
				val = field_val.split(":")[-1]
				matched_terms.append(val)
		return matched_terms

	def code_as_text(self):
		""" Extends a query by matching query keywords in source code as text"""

		query = " "
		for term in tokenize_string(self.porter_analyzer, self.query):
			if term:
				term = QueryParser.escape(term)
				query += "code:%s " % (term)

		return query
		
	def lexical_search(self):
		""" In case no term is matching with stackoverflow we perform a simple lexical search on GitHub """
		github_result = []
		query = self.code_as_text().strip()
		query = QueryParser(Version.LUCENE_CURRENT, "code", self.analyzer).parse(query)
		hits = self.searcher.search(query, 10).scoreDocs
		for hit in hits:
			doc = self.searcher.doc(hit.doc)
			matched_terms = self.get_matched_keywords(query, hit.doc)

			# apis = [d.stringValue() for d in doc.getFields("typed_method_call")]

			item = GithubResultItem(doc.get("file"), decompress( doc.get("file_content") ), matched_terms, hit.score, so_item, doc.get("line_numbers"), hit.doc) # code

			github_result.append( item )

		return github_result

	def more_like_this(self, so_items):

		github_result = []
		if not so_items:
			so_items.append(SOResultItem(None, 1.0, "No Title", 0, "") )

		for so_item in so_items:
			queryparser = QueryParser(Version.LUCENE_CURRENT, "typed_method_call", self.analyzer)
			query = ""
			if so_item.doc:
				query = self.document_to_query(so_item.doc)

			query += self.code_as_text()
			if query:
				print "-"*30
				print "Query: %s" % query
				print "-"*30
				try:
					like_query = queryparser.parse(query)

					hits = self.searcher.search(like_query, 10).scoreDocs

					for i, hit in enumerate(hits):
						doc = self.searcher.doc(hit.doc)
						matched_terms = self.get_matched_keywords2(like_query, hit.doc)
						# apis = [d.stringValue() for d in doc.getFields("typed_method_call")]
						item = GithubResultItem(doc.get("file"), decompress( doc.get("file_content") ), matched_terms, hit.score, so_item, doc.get("line_numbers"), hit.doc) # code
						github_result.append( item )
						#print("%d. File: %s, Matched: %s, Score: %s" % (i + 1, doc.get("file"), matched_terms, hit.score))
				except Exception as e:
					print "Error: %s" % e
		# print Counter(files).most_common(5)
		return github_result

	def more_like_this2(self, so_items):
		if not so_items:
			so_items.append( SOResultItem(None, 1.0, "No Title", 0, "") )
		query = ""

		queryparser = QueryParser(Version.LUCENE_CURRENT, "typed_method_call", self.analyzer)  ####
		###아래의 반복문이 Agumented Query 생성부
		for so_item in so_items:
			if so_item.doc:
				query += self.document_to_query(so_item.doc)
			query += self.code_as_text()

		github_result = []
		if query:
			print "-"*50
			print "UNified Query: %s" % query
			print "-"*50
			try:
				###루씬에 맞는 Query로 최종 변환
				like_query = queryparser.parse(query)
				###아래 줄이 실제로 GitHub Indices들 찾아들어가서 like_query와 비교 견적 상위 5개..
				hits = self.searcher.search(like_query, 5).scoreDocs	#상위 5개 결과
				#hits에 5개의 결과가 들어감..

				for i, hit in enumerate(hits):
					doc = self.searcher.doc(hit.doc)
					matched_terms = self.get_matched_keywords2(like_query, hit.doc)
					print "Matched Terms : ", matched_terms

					# apis = [d.stringValue() for d in doc.getFields("typed_method_call")]
 					print("file", doc.get("file"), "file_content", doc.get("file_content"), "line_numbers", doc.get("line_numbers") )
					file_path = doc.get("file")
					#file_path = "/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch" + doc.get("file")[24:]
					#file_path = "/root/GitSearch" + doc.get("file")[24:]
					#print(doc.get("file")[32:])
					#print(doc.get("file")[0:])
					#print(file_path)

					content = None
					try:
						with open(file_path) as f:	#실제 프로젝트 경로 쭉 찾아들어가서 파일 열고 읽어서 content에 넣음
							content = f.read()
					except:
						pass

					#File 찾고 내용 존재 시, 형식에 맞게 item에 넣음.
					if content:
						item = GithubResultItem(doc.get("file"), content, matched_terms, hit.score, so_item, doc.get("line_numbers"), hit.doc) # code
						github_result.append(item)

			except Exception as e:
				print "GitSearcher: Error: %s" % e
				print(traceback.format_exc())

		return github_result

if __name__ == '__main__':
	# so = StackoverflowSearcher("/Users/Raphael/Downloads/stackoverflow")
	# so_items = so.search("messageformat receive a stack trace")

	# git = GitHubSearcher("/Users/Raphael/Downloads/github")
	# git.more_like_this(so_items)

	porter_analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
	print tokenize_string(porter_analyzer, "Convert and Int to String")
