# from java.io import File, StringReader
#
# from org.apache.lucene.analysis.standard import StandardAnalyzer
# from org.apache.lucene.index import IndexReader, Term
# from org.apache.lucene.search import IndexSearcher, FuzzyQuery
# from org.apache.lucene.store import SimpleFSDirectory
# from org.apache.lucene.analysis.core import WhitespaceAnalyzer
# from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
# from org.apache.lucene.util import Version
# from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
# from org.apache.lucene.search.BooleanClause import Occur
# from org.apache.lucene.analysis.core import KeywordAnalyzer
#
# from org.apache.lucene.search.spans import SpanNearQuery, SpanQuery, SpanTermQuery, SpanMultiTermQueryWrapper
#
# from org.apache.lucene.analysis.tokenattributes import CharTermAttribute
#
# from org.apache.lucene.queries.function.valuesource import LongFieldSource
# from org.apache.lucene.queries.function import FunctionQuery
# from org.apache.lucene.queries import CustomScoreQuery
#
# from PorterAnalyzer import PorterAnalyzer
#
# from com.mongodb import BasicDBObject
#
# from collections import Counter
#
# import utils
#
#
# def tokenize_string(analyzer, string):
# 	result = []
# 	stream = analyzer.tokenStream(None, StringReader(string))
# 	cattr = stream.addAttribute(CharTermAttribute)
# 	stream.reset()
# 	while stream.incrementToken():
# 		result.append(cattr.toString())
#
# 	return result
#
# def find_similar_document():
# 	pass
#
# def getSpanNearQuery(analyzer, s, field="title", slop=10, inOrder=True):
# 	keywords = tokenize_string(analyzer, s)
# 	print keywords
# 	spanTermQueries = [ SpanMultiTermQueryWrapper( FuzzyQuery( Term(field, keyword) ) ) for keyword in keywords]
# 	return SpanNearQuery( spanTermQueries, slop, inOrder)
#
#
# #coll = utils.get_mongo_connection().getCollection("AnswerAST")
#
# def retrieve_ranked_apis(question_id):
#
# 	docs = coll.find(BasicDBObject({"question_id": question_id}))
#
# 	apis = []
# 	for doc in docs:
# 		answer = doc.toMap()
# 		apis.append(answer["typed_method_call"])
#
# 	print apis
#
#
# indexDir = File("/tmp/stackoverflow")
#
# # 1. open the index
# analyzer = PorterAnalyzer( StandardAnalyzer(Version.LUCENE_CURRENT))
# index = SimpleFSDirectory(indexDir)
# reader = IndexReader.open(index)
# n_docs = reader.numDocs()
# print("Index contains %d documents." % n_docs)
#
# # 2. parse the query from the command line
# a = {"typed_method_call": KeywordAnalyzer(), "extends": KeywordAnalyzer()}
# wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a)
#
# query_string = "lucene get similar documents to the current one"
# query_parser = MultiFieldQueryParser(Version.LUCENE_CURRENT, ["title"], wrapper_analyzer)
#
#
# #base_query = getSpanNearQuery(analyzer, query_string)
#
# base_query = query_parser.parse(query_string)
#
# #http://shaierera.blogspot.com/2013/09/boosting-documents-in-lucene.html
# boost_query = FunctionQuery( LongFieldSource("view_count"))
# query = CustomScoreQuery(base_query, boost_query)
#
# # queryparser = QueryParser(Version.LUCENE_CURRENT, "title", analyzer)
# # query = queryparser.parse(query_string)
#
# # 3. search the index for the query
# # We retrieve and sort all documents that match the query.
# # In a real application, use a TopScoreDocCollector to sort the hits.
# searcher = IndexSearcher(reader)
# hits = searcher.search(query, 10).scoreDocs
#
# # 4. display results
# print(query_string)
# print("Found %d hits:" % len(hits))
#
# api_acc = []
# for i, hit in enumerate(hits):
#     doc = searcher.doc(hit.doc)
#     apis = [d.stringValue() for d in doc.getFields("typed_method_call")]
#     api_acc.extend(apis)
#     #retrieve_ranked_apis(doc.get("answer_id"))
#     print("%d.  %s, Answer Id: %s, Method: %s, Score: %s" % (i + 1, doc.get("title"), doc.get("answer_id"), apis, hit.score))
#
# print Counter(api_acc).most_common(5)
# # 5. close resources
