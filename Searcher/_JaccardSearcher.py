# from java.io import File, StringReader
# from org.apache.lucene.index import IndexReader, Term
# from org.apache.lucene.search import IndexSearcher, FuzzyQuery
# from org.apache.lucene.store import SimpleFSDirectory
# from org.apache.lucene.analysis.core import KeywordAnalyzer
# from org.apache.lucene.util import Version
# from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
# from collections import Counter
#
# indexDir = File("/tmp/github")
#
# # 1. open the index
# analyzer = KeywordAnalyzer()
# index = SimpleFSDirectory(indexDir)
# reader = IndexReader.open(index)
# n_docs = reader.numDocs()
# print("Index contains %d documents." % n_docs)
#
# # 2. parse the query from the command line
# # a = {"typed_method_call": WhitespaceAnalyzer()}
# # wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a)
#
# query_string = "HttpURLConnection.disconnect Exception.printStackTrace BufferedReader.close HttpURLConnection.setRequestProperty HttpURLConnection.setRequestMethod DataOutputStream.writeBytes HttpURLConnection.getInputStream DataOutputStream.close HttpURLConnection.setUseCaches StringBuffer.append URL.openConnection HttpURLConnection.getOutputStream Integer.toString String.getBytes StringBuffer.toString HttpURLConnection.setDoOutput BufferedReader.readLine DataOutputStream.flush HttpURLConnection.setDoInput"
# query_parser = MultiFieldQueryParser(Version.LUCENE_CURRENT, ["typed_method_call"], analyzer)
#
#
# #base_query = getSpanNearQuery(analyzer, query_string)
#
# base_query = query_parser.parse(query_string)
#
# #http://shaierera.blogspot.com/2013/09/boosting-documents-in-lucene.html
# # boost_query = FunctionQuery( LongFieldSource("view_count"))
# #query = CustomScoreQuery(base_query, boost_query)
#
# # queryparser = QueryParser(Version.LUCENE_CURRENT, "title", analyzer)
# # query = queryparser.parse(query_string)
#
# # 3. search the index for the query
# # We retrieve and sort all documents that match the query.
# # In a real application, use a TopScoreDocCollector to sort the hits.
# searcher = IndexSearcher(reader)
# hits = searcher.search(base_query, 10).scoreDocs
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
#     print("%d.  %s Method: %s, Score: %s" % (i + 1, doc.get("file"), apis, hit.score))
#
# print Counter(api_acc).most_common(5)
# # 5. close resources
