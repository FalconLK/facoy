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

from java.io import File, StringReader
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.analysis.tokenattributes import CharTermAttribute
from org.apache.lucene.index import IndexReader, Term, DirectoryReader
from org.apache.lucene.search import IndexSearcher, FuzzyQuery, TermQuery, BooleanQuery, SearcherFactory, SearcherManager, SearcherFactory, ReferenceManager
from org.apache.lucene.store import SimpleFSDirectory, RAMDirectory, FSDirectory
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
from org.apache.lucene.util import Version

import traceback
from GitSearch.Analyzer.PorterAnalyzer import PorterAnalyzer
from GitSearch.MyUtils import english_stop_words, write_search_log
from GitSearch.MyUtils import remove_unified_stop_lists
from stemming.porter2 import stem
from collections import namedtuple

INDICES_PATH = "/Indices/"

ResultItem = namedtuple("ResultItem", "doc score title question_id")
GithubResultItem = namedtuple("GithubResultItem", "file file_content matched_terms score so_item line_numbers doc_id")
java_stopwords = ["public","private","protected","interface","abstract","implements","extends","null","new",
"switch","case", "default" ,"synchronized" ,"do", "if", "else", "break","continue","this",
"assert" ,"for","instanceof", "transient","final", "static" ,"void","catch","try",
"throws","throw","class", "finally","return","const" , "native", "super","while", "import",
"package" ,"true", "false", "enum"]

class GettingQuestionDocs:	#Index path를 입력으로 받은 객체를 생성하고 search를 통해 현재 Answer Serchear_1에서 받은 질문 리스트들에 대한 doc들을 얻을 것.
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

	def search(self, q_ids, limit):
		docs = []
		c = 0
		for i, q_id in enumerate(q_ids):#Index 가 안되어있는 Question은 찾지 못함.
			query = TermQuery(Term("question_id", str(q_id)))
			topdocs = self.searcher.search(query, 1).scoreDocs	#현재는 Accepted Answer 하나만 인덱싱 되기 때문에 1개로 한정
			# index searcher에 TermQuery의 객체가 들어가고.. 질문 id에 달려있는 답변 중 상위 n개 가져옴/ scoreDocs는 점수 (소수형임..)
			for hit in topdocs:
				doc = self.searcher.doc(hit.doc)
				docs.append(ResultItem(doc, len(q_ids) - i, doc.get("title"), doc.get("question_id")))

			if len(topdocs) > 0:
				c += 1
				if c >= limit:
					break

		# self.searchermgr.decRef(self.searcher)
		self.searchermgr.release(self.searcher)
		self.searcher = None
		self.directory.close()
		self.directory = None
		return docs

class SimilarQsSearcher:
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
		""" Given a document it transforms the source code related fields to a lucene query string"""
		query = ""
		for field in ["description"]:	#여기의 필드가 description 으로 설정 했고... 맨 끝에서 field, term이런식으로 넣으니.. 중복이 많음..
			for val in doc.getFields(field):
				if val.stringValue().strip():
					term = QueryParser.escape(val.stringValue())
					#tokenize
					term = self.tokenize_string(StandardAnalyzer(), term)
					#CamelCase
					temp = []
					for t in term:
						temp += self.camel_case_split(t)
					#stopwords
					temp_2 = []
					for t in temp:
						if t not in english_stop_words:
							temp_2.append(t)
					#stemming
					temp_3 = []
					for t in temp_2:
						temp_3.append(stem(t))
					#stopwords
					temp_4 = []
					for t in temp_3:
						if t not in english_stop_words:
							temp_4.append(t)
					#query generation
					for term in temp_4:
						query += "%s:%s " % (field, term)

		for field in ["typed_method_call", "methods", "used_classes", "class_instance_creation", "methods_called"]:  # "extends", "annotations", "literals"
			for val in doc.getFields(field):
				if val.stringValue().strip():
					term = QueryParser.escape(val.stringValue())	#이 자리에서 Unified Query 정제 되나 한번 보자......
					stoplist = ["java.lang.Object"]
					if term not in stoplist:
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
				#field, val = field_val.split(":")
				val = field_val.split(":")[-1]
				matched_terms.append(val)
		return matched_terms

	def code_as_text(self, query):
		new_query = " "
		for term in self.tokenize_string(self.porter_analyzer, query):
			if term:
				term = QueryParser.escape(term)
				new_query += "description:%s " % (term)
		return new_query


	def more_like_this2(self, item_doc, result_num):	#들어온 질문 docs들에 대해 순회하면서 최종 query로 생성하고 Question Index에서 비슷한거 검색할 것.
		similar_questions = []
		if not item_doc:
			item_doc.append(ResultItem(None, 1.0, "No Title", 0))
		query = ""
		if item_doc.doc:
			query += self.document_to_query(item_doc.doc)

		query = remove_unified_stop_lists(query)
		queryparser = QueryParser(Version.LUCENE_CURRENT, "term", self.analyzer)

		if query:	#########이 시점에서의 Unified Query는 Tokenization, Stemming 이 되어있음..########
			try:
				like_query = queryparser.parse(query)
				hits = self.searcher.search(like_query, result_num).scoreDocs	#Q와 비슷한 Q들 상위 3개씩의 결과 그럼 총 9개

				for i, hit in enumerate(hits):
					doc = self.searcher.doc(hit.doc)
					similar_questions.append(doc.get("question_id"))

			except Exception as e:
				print "Question Searcher: Error: %s" % e
				# write_search_log("Question Searcher: Error: %s" % e + "\n")
				print(traceback.format_exc())

		# self.searchermgr.decRef(self.searcher)
		# self.searchermgr.release(self.searcher)
		# self.searcher = None
		# self.directory.close()
		# self.directory = None
		return similar_questions

	# def release(self, searcher):


# if __name__ == '__main__':
# 	###처음 생각 : Question들 꺼내오면 xml 형식으로 되어있으니.. 파싱해야되고.. 쭉 question id가 3개니 반복 3번 돌면서 비슷한 question들 3개씩 꺼내야함
#
# 	#실제 해야 할 일들..
# 	#1. __Answer_Searcher_1 로 부터 넘어온 question id들 3개를 가지고 Question Index에서 해당 (구조화 되고 Tokenized 된) 정보가져옴.
# 	#2. 가져온 3개의 정보들에 대해 각각 3개씩 비슷한 놈들을 뽑기 작업 (큰 반복이 3번 돌아야 함)
#
# 	question_ids = ['19464224', '11324006', '15708147'] #['4922', '39312589', '39310083']
# 	similar_questions = []
# 	#questions = QuestionSearcher("%squestionIndex" % (INDICES_PATH))    #인스턴스 생성
#
# 	getDoc = GettingQuestionDocs("%squestionIndex" % (INDICES_PATH))
# 	item_docs = getDoc.search(question_ids, 3)
#
# 	question = SimilarQsSearcher("%squestionIndex" % (INDICES_PATH))
#
# 	for item_doc in item_docs:
# 		similar_questions += question.more_like_this2(item_doc, 1)
#
# 	print similar_questions
#
#
# 	#question_contents = questions.get_these(question_ids)
#
# 	#query = """<p>I searched here for this topic and I tried all I found, but it still does not work.</p>&#xA;&#xA;<pre><code>    import java.io.FileInputStream;&#xA;    import java.io.IOException;&#xA;&#xA;    import org.apache.commons.net.ftp.FTPClient;&#xA;&#xA;    import android.os.Bundle;&#xA;    import android.os.Environment;&#xA;    import android.app.Activity;&#xA;&#xA;&#xA;    public class MainActivity extends Activity {&#xA;&#xA;        public static final String TAG = "Contacts";&#xA;&#xA;        @Override&#xA;        protected void onCreate(Bundle savedInstanceState) {&#xA;            super.onCreate(savedInstanceState);&#xA;            setContentView(R.layout.activity_main);&#xA;</code></pre>&#xA;&#xA;<p>I made a Thread, because it wasn't allowed to run it on the main thread;</p>&#xA;&#xA;<pre><code>            Thread t = new Thread(new Runnable(){&#xA;                @Override&#xA;                public void run(){&#xA;                    Versuch();&#xA;                }&#xA;            });&#xA;            t.start();&#xA;    }&#xA;</code></pre>&#xA;&#xA;<p>Here I try to upload the data and it does not show me an error in Log Cat.</p>&#xA;&#xA;<pre><code>public void Versuch(){&#xA;        FTPClient client = new FTPClient();&#xA;        FileInputStream fis = null;&#xA;&#xA;        try {&#xA;            client.connect("ftp-web.example");&#xA;            client.login("ftpuser", "ftppassword");&#xA;&#xA;&#xA;            String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";&#xA;            fis = new FileInputStream(filename);&#xA;&#xA;            // Store file to server&#xA;            //&#xA;            client.storeFile(filename, fis);&#xA;            client.logout();&#xA;        } catch (IOException e) {&#xA;            e.printStackTrace();&#xA;        } finally {&#xA;            try {&#xA;                if (fis != null) {&#xA;                    fis.close();&#xA;                }&#xA;                client.disconnect();&#xA;            } catch (IOException e) {&#xA;                e.printStackTrace();&#xA;            }&#xA;        }&#xA;&#xA;&#xA;&#xA;    }&#xA;}&#xA;</code></pre>&#xA;"""
# 	#tt = camel_case_split(query)
#
# 	#analyzer = PerFieldAnalyzerWrapper(PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT)))
# 	#tokens = tokenize_string(analyzer=analyzer, string=query)
# 	#print tokens
#