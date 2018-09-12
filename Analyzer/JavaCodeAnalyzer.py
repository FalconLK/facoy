from org.apache.lucene.analysis import AnalyzerWrapper
from org.apache.lucene.analysis.Analyzer import TokenStreamComponents
from org.apache.lucene.analysis.core import TypeTokenFilter, KeywordAnalyzer
from org.apache.lucene.analysis.en import PorterStemFilter
from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.util import Version
from org.apache.lucene.analysis.util import CharArraySet

class JavaCodeAnalyzer(AnalyzerWrapper):
	def __init__(self):
		self.baseAnalyzer = self.internal_analyzer()

	def internal_analyzer(self):
		java_stopwords = ["public","private","protected","interface",
							 "abstract","implements","extends","null","new",
							 "switch","case", "default" ,"synchronized" ,
							 "do", "if", "else", "break","continue","this",
							 "assert" ,"for","instanceof", "transient",
							 "final", "static" ,"void","catch","try",
							 "throws","throw","class", "finally","return",
							 "const" , "native", "super","while", "import",
							 "package" ,"true", "false", "enum"]

		all_stopwords = list(StandardAnalyzer(Version.LUCENE_CURRENT).getStopwordSet())
		all_stopwords.extend(java_stopwords)

		stopwords = CharArraySet(Version.LUCENE_CURRENT, all_stopwords, True)
		analyzer = StandardAnalyzer(Version.LUCENE_CURRENT, stopwords)
		return analyzer

	def close(self):
		# type: () -> object
		self.baseAnalyzer.close()
		super(JavaCodeAnalyzer, self).close()

	def getWrappedAnalyzer(self, fieldName):
		return self.baseAnalyzer

	def wrapComponents(self, fieldName, components):
		ts = components.getTokenStream()
		filteredTypes = set(["<NUM>"])
		numberFilter = TypeTokenFilter(Version.LUCENE_CURRENT, ts, filteredTypes)
		porterStem = PorterStemFilter(numberFilter)
		return TokenStreamComponents(components.getTokenizer(), porterStem)