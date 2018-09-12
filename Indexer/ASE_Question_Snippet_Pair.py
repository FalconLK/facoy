#!/usr/bin/env python
# -*- coding: utf-8 -*-

############Stackoverflow Indexing Code############

import sys, codecs
from bs4 import BeautifulSoup

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

# Description: Goes trough mysql database. extracts code snippets and buid an AST of that code. Then, the AST code information are stored in lucene/mongo

from java.sql import ResultSet
from java.io import File
from java.io import IOException
from java.lang import Integer
from java.lang import String

from org.apache.lucene.analysis.standard import StandardAnalyzer
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.document import Document, Field, StringField
from org.apache.lucene.index import IndexWriter, IndexWriterConfig, CorruptIndexException
from org.apache.lucene.store import SimpleFSDirectory, LockObtainFailedException
from org.apache.lucene.util import Version
from java.util.concurrent import Executors

from GitSearch.MyUtils import unescape_html, tokenize
from GitSearch.Analyzer.PorterAnalyzer import PorterAnalyzer
from GitSearch.Analyzer.JavaCodeAnalyzer import JavaCodeAnalyzer
from GitSearch.Indexer.SOParser import PostParser
from GitSearch.Import.ImportAST import transform_body
from GitSearch.Indexer.Indexer_Counter import Counter
#from GitSearch.Indexer.JavaCodeParser import transform_body /// 이런식으로 옮겨서 하면 엿먹음
from java.sql import SQLException

pool = Executors.newFixedThreadPool(4)

def load_so_fail_ids():
    """ Contains a list of stackoverflow discussions which are not indexed by the JavaCodeSnippetIndexer because of failures or non-java snippets """
    with open(path_notIndexed, "r") as f:
        for line in f:
            yield int(line)

def get_db_connection():
    from java.lang import Class
    from java.sql import DriverManager
    Class.forName("com.mysql.jdbc.Driver").newInstance()
    #########################################  경   로  ####################################
    #newConn = DriverManager.getConnection("jdbc:mysql://203.255.81.47:3306/stackoverflow?useUnicode=yes&characterEncoding=utf8&user=test")
    newConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/stackoverflow?autoReconnect=true", "root", "")
    newConn.setAutoCommit(True)
    return newConn

def parseHTML(text):
    doc = BeautifulSoup(text, u"html.parser")
    codes = doc.select(u'code')
    result = ''
    for c, code in enumerate(codes):
        result += code.get_text().strip() + ' '
    return result

def dataGeneration():
    mysql_conn = get_db_connection()

    stmt = mysql_conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    querySO = """SELECT 
                  Q.Id as QId, 
                  Q.Title as QTitle,
                  Q.Body as QBody, 
                  A.Body as ABody, 
                  A.Id as AId 
	                  FROM posts as Q JOIN posts as A ON Q.Id = A.ParentId 
    	                  WHERE A.ParentId IS NOT NULL 
        	                  AND Q.AcceptedAnswerId = A.Id 
                              AND (Q.Tags LIKE "%<java>%" OR Q.Tags LIKE "%<android>%") 
                              AND A.Body LIKE "%<code>%"
                """
                    # Returns nearly 400,000 posts
                    #AND Q.CreationDate < "2014-01-01 00:00:00"
                    #AND A.CreationDate < "2013-06-01 00:00:00"

    stmt.setFetchSize(Integer.MIN_VALUE)
    resultSet = stmt.executeQuery(querySO)

    # i = 0
    count = 0
    while resultSet.next():
        question_title = resultSet.getString("QTitle")
        pure_q = getPureText(question_title)
        one_line_q = removeLines(pure_q)
        write_file('/Users/Falcon/Desktop/Question.txt', one_line_q)
        # print "Question body: ", question_body

        q_id = resultSet.getInt("QId")
        print q_id

        abody = resultSet.getString("ABody")
        answer_code = parseHTML(abody)
        # pure_a = getPureText(answer_code)
        one_line_a = removeLines(answer_code)
        write_file('/Users/Falcon/Desktop/Answer.txt', one_line_a)
        # print "Answer body:", abody

        count += 1
        print 'count: %d, progress %f %%' % (count, (float(float(count)/934326.0)*100))

    print "Total Count : %d" % count

def write_file(file_path, content):
    with codecs.open(file_path, mode='a', encoding='utf-8') as file:
        file.write(content + "\n")
    file.close()

def getPureText(original):
    pure_string = BeautifulSoup(original, "html.parser").text
    return pure_string

def removeLines(string):
    one_line = ''
    for c, line in enumerate(string.splitlines()):
        # print c, ":", line
        one_line += line + ' '
    return one_line

def read_content_in_file(file_path):
    with codecs.open(file_path, mode='r', encoding='utf-8') as file:
        text = file.read()
    file.close()
    return text

def lineCheck(file_path):
    count = 0
    text = read_content_in_file(file_path)
    count = len(text.splitlines())
    return count

def file_len(fname):
    with open(fname) as f:
        for i, l in enumerate(f):
            pass
    return i + 1

def main():
    try:
        print "Indexing..."
        #########################################  경   로  ####################################
        indexDestination = File("/Users/Falcon/Desktop/New_Indices/Stack_A_Indices")

        #writer = IndexWriter(SimpleFSDirectory(indexDestination), StandardAnalyzer(), True, IndexWriter.MaxFieldLength.UNLIMITED)
        analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
        a = {	"typed_method_call": analyzer, "extends": analyzer,
                "used_classes": analyzer, "methods": analyzer,
                "class_instance_creation": analyzer, "methods_called": analyzer, "view_count" : KeywordAnalyzer(), "code_hints": JavaCodeAnalyzer() }
        wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a)
        config = IndexWriterConfig(Version.LUCENE_CURRENT, wrapper_analyzer)
        writer = IndexWriter(SimpleFSDirectory(indexDestination), config)

        # analyzer = PorterAnalyzer(StandardAnalyzer(Version.LUCENE_CURRENT))
        # a = {"typed_method_call": KeywordAnalyzer(), "extends": KeywordAnalyzer(),
        # 	 "used_classes": KeywordAnalyzer(), "methods": KeywordAnalyzer(),
        # 	 "class_instance_creation": KeywordAnalyzer(), "methods_called": KeywordAnalyzer(),
        # 	 "view_count": KeywordAnalyzer(), "code_hints": JavaCodeAnalyzer()}
        # wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a)
        # config = IndexWriterConfig(Version.LUCENE_CURRENT, wrapper_analyzer)
        # writer = IndexWriter(SimpleFSDirectory(indexDestination), config)

        counter = Counter()
        index_code_snippet(writer, counter)
        writer.commit()
        writer.close()

        print "Done"
        print str(counter)

    except CorruptIndexException as e:		#when index is corrupt
            e.printStackTrace()
    except LockObtainFailedException as e:	#when other writer is using the index
            e.printStackTrace()
    except IOException as e:	#when directory can't be read/written
            e.printStackTrace()
    except SQLException as e: 	#when Database error occurs
            e.printStackTrace()

if __name__ == '__main__':
    # dataGeneration()

    count_q = file_len('/Users/Falcon/Desktop/Question.txt')
    count_a = file_len('/Users/Falcon/Desktop/Answer.txt')
    print count_q, count_a


    # a = """Answer body: <p>Could not get the example to work using FileChannel.read(ByteBuffer) because it isn't a blocking read. Did however get the code below to work:</p>&#xA;&#xA;<pre><code>boolean running = true;&#xA;BufferedInputStream reader = new BufferedInputStream(new FileInputStream( "out.txt" ) );&#xA;&#xA;public void run() {&#xA;    while( running ) {&#xA;        if( reader.available() &gt; 0 ) {&#xA;            System.out.print( (char)reader.read() );&#xA;        }&#xA;        else {&#xA;            try {&#xA;                sleep( 500 );&#xA;            }&#xA;            catch( InterruptedException ex ) {&#xA;                running = false;&#xA;            }&#xA;        }&#xA;    }&#xA;}&#xA;</code></pre>&#xA;&#xA;<p>Of course the same thing would work as a timer instead of a Thread, but I leave that up to the programmer. I'm still looking for a better way, but this works for me for now.</p>&#xA;&#xA;<p>Oh, and I'll caveat this with: I'm using 1.4.2. Yes I know I'm in the stone ages still.</p>&#xA;"""
    # b = """<p><strong>In C#,</strong> the 'int' type is the same as <code>System.Int32</code> and is <a href="http://msdn.microsoft.com/en-us/library/s1ax56ch.aspx" rel="noreferrer">a value type</a> (ie more like the java 'int'). An integer (just like any other value types) can be <a href="http://msdn.microsoft.com/en-us/library/yz2be5wk.aspx" rel="noreferrer">boxed</a> ("wrapped") into an object. </p>"""

    # answer_code = parseHTML(b)
    # print answer_code
    # pure_text = getPureText(answer_code)
    # one_line = removeLine(pure_text)

    # d = "<code>"
    # fileNameOnly = a[:a.find(ext) + len(ext)]

#total: 677,828