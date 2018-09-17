from java.io import File, StringReader

from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.index import IndexReader, Term
from org.apache.lucene.search import IndexSearcher, FuzzyQuery, TermQuery
from org.apache.lucene.store import SimpleFSDirectory
from org.apache.lucene.analysis.core import WhitespaceAnalyzer
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.util import Version
from org.apache.lucene.queryparser.classic import MultiFieldQueryParser, QueryParser
from org.apache.lucene.search.BooleanClause import Occur
from org.apache.lucene.analysis.core import KeywordAnalyzer

from NewJavaParser import parse


class Searcher:
	def __init__(self, source, index_path):
		self.index_path = index_path
		self.source = source
		ast, source = parse(self.source, resolve=True, source=True)
		self.source = source
		self.ast = ast
		self.queryparser = QueryParser(Version.LUCENE_CURRENT, "typed_method_call", KeywordAnalyzer())
		self.load_index()

	def load_index(self):
		indexDir = File(self.index_path)
		index = SimpleFSDirectory(indexDir)
		self.reader = IndexReader.open(index)
		n_docs = self.reader.numDocs()
		self.searcher = IndexSearcher(self.reader)
		print("Index contains %d documents." % n_docs)

	def document_to_query(self):
		""" Given a document it transforms the source code related fields to a lucene query string"""
		query = ""
		for field in ["typed_method_call", "methods", "extends", "used_classes", "class_instance_creation", "methods_called", "annotations", "literals"]: #"used_classes", , "literals"
			for val in self.ast[field]:
				term = QueryParser.escape(val)
				query += "%s:%s " % (field, term)
		return query

	def get_matched_keywords(self, query, docid):
		matched_terms = []


		if isinstance(query, TermQuery):
			#print self.searcher.explain(query, docid)
			if self.searcher.explain(query, docid).isMatch():
				matched_terms.append( query.getTerm().text() )
		else:
			for query_term in query.getClauses():
				if self.searcher.explain(query_term.getQuery(), docid).isMatch():
					#print self.searcher.explain(query_term.getQuery(), docid)
					matched_terms.append( query_term.getQuery().getTerm().text() )

		#print "Matched Terms: %s" % matched_terms
		return matched_terms

	def get_AST_from_Doc(self, doc):
		tree = {}
		tree["typed_method_call"] = [f.stringValue() for f in doc.getFields("typed_method_call")]
		tree["methods_called"] = [f.stringValue() for f in doc.getFields("methods_called")]
		tree["imports"] = [f.stringValue() for f in doc.getFields("imports")]
		tree["used_classes"] = [f.stringValue() for f in doc.getFields("used_classes")]
		# tree["var_type_map"] = eval(doc.getField("var_type_map").stringValue())
		# tree["unresolved_method_calls"] = [f.stringValue() for f in doc.getFields("unresolved_method_calls")]

		return tree


	def more_like_this(self):

		trees = []

		file_hash_process = set()
		query = self.document_to_query()

		if query:
			print "-"*30
			print "Query: %s" % query
			print "-"*30
			try:
				like_query = self.queryparser.parse(query)

				hits = self.searcher.search(like_query, 10).scoreDocs

				for i, hit in enumerate(hits):
					doc = self.searcher.doc(hit.doc)
					matched_terms = self.get_matched_keywords(like_query, hit.doc)
					file_path = doc.getField("file").stringValue()
					#print "Matched Terms", matched_terms
					print "Path: ", file_path
					# apis = [d.stringValue() for d in doc.getFields("typed_method_call")]
					with open(file_path, "r") as f:
						file_content = f.read()

					file_hash = doc.getField("hash").stringValue()

					#print "FILE", file_content
					#print "PARSE", parse(file_content, resolve=False)
					if file_hash not in file_hash_process:
						trees.append(parse(file_content, resolve=False))
						file_hash_process.add(file_hash)
					else:
						print "Duplicate: ", file_path

					#trees.append( self.get_AST_from_Doc(doc) )


			except Exception as e:
				print "Error: %s" % e

		return trees

if __name__ == '__main__':

	source = """
		byte[] data = Base64.decodeBase64(crntImage);
try (OutputStream stream = new FileOutputStream("c:/decode/abc.bmp")) {
    stream.write(data);
}
	"""

	index = Searcher(source, "/Users/Raphael/Downloads/linkgithub")

	index.more_like_this()




