#!/usr/bin/env python
# -*- coding: utf-8 -*-
############GitHub Indexing Code############

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

from java.io import File
from java.io import IOException
from org.apache.lucene.analysis.core import KeywordAnalyzer
from org.apache.lucene.analysis.en import EnglishAnalyzer
from org.apache.lucene.analysis.miscellaneous import PerFieldAnalyzerWrapper
from org.apache.lucene.document import Document, Field, StringField
from org.apache.lucene.index import IndexWriter, IndexWriterConfig, CorruptIndexException
from org.apache.lucene.store import SimpleFSDirectory, LockObtainFailedException
from org.apache.lucene.util import Version
from GitSearch.MyUtils import md5, so_tokenizer, write_file
import os, time, codecs
from GitSearch.Analyzer.JavaCodeAnalyzer import JavaCodeAnalyzer
from GitSearch.Indexer.NewJavaParser import parse
from GitSearch.Indexer.Indexer_Counter import Counter
from GitSearch.MyUtils import copytree, java_files_from_dir, write_file

hashes = set()

def filtering_test_code_only(src, dst):
    progress = 0
    test_code_count = 0
    if not os.path.isdir(dst):
        os.mkdir(dst)

    project_only_test = set()
    for javafile in java_files_from_dir(src):
        progress += 1
        if progress % 1000 == 0: print 'Parsing: ', progress
        if ('test' or 'tests') in javafile.split('/'): # and 'test' in javafile.split('/')[-1]:
            test_code_count += 1
            project_path = '/'.join(javafile.split('/')[:7])
            project_only_test.add(project_path)

    write_file('/Users/Falcon/Desktop/projects_with_test_code_only.txt', project_path)

    progress = 0
    for i in project_only_test:
        progress = progress + 1
        if progress % 1000 == 0: print 'Copy: ', progress
        target_destination_detail = dst + i.split('/')[6]
        if not os.path.isdir(target_destination_detail):
            os.mkdir(target_destination_detail)
            copytree(i, target_destination_detail)
        else:
            print 'The directory is already existing..'

    for javafile in java_files_from_dir(dst):
        if not ('test' or 'Test') in javafile.split('/'):
            os.remove(javafile)


def filtering_only_with_test_code(src, dst):
    progress = 0
    if not os.path.isdir(dst):
        os.mkdir(dst)

    project_with_test = set()
    for dirpath, dirnames, files in os.walk(src):
        progress = progress + 1 #1429000
        if progress % 1000 == 0: print 'Parsing: ', progress
        if ('test' or 'tests') in dirpath.split('/'):
            project_with_test.add('/'.join(dirpath.split('/')[:7]))

    progress = 0
    for i in project_with_test:
        progress = progress + 1
        if progress % 1000 == 0: print 'Copy: ', progress
        target_destination_detail = dst + i.split('/')[6]

        if not os.path.isdir(target_destination_detail):
            os.mkdir(target_destination_detail)
            copytree(i, target_destination_detail)
        else:
            print 'The directory is already existing..'


def generate_indices_from_projects(src, writer, counter):
    javafiles = java_files_from_dir(src)	#자바 파일들만 뽑아내는 함수
    i = 0
    j = 0
    for javafile in javafiles:
        i += 1
        if i % 1000 == 0:	#1000개 될때마다 프린트 한번씩
            print("Counter: %s" % i)
            print "typed_method_call" + str(counter.typed_method_call_count)
        document = Document()	#루씬 Document 객체

        ################################################################################################################
        splits = javafile.split("/")[6:]		###여기는 플랫폼이나 PC바뀌면 무조건 바꿔주고 인덱싱 해야 하는 부분이다..
        project_path = ""
        for names in splits:
            project_path += "/" + names
        changed_path = src + project_path

        document.add(Field("file", changed_path, Field.Store.YES, Field.Index.NO))
        ################################################################################################################

        try:
            with codecs.open(javafile, "r", encoding='utf-8', errors='ignore') as f:
                file_content = f.read().encode("utf-8", errors='ignore')

            ast = parse(file_content, resolve=False)	#newJavaParser를 사용하여 자바 코드 파싱
            if add_code_keyword_into_document(document, file_content, ast, counter):
                writer.addDocument(document)
                j += 1
                if j % 1000 == 0:
                    print "Wrote:: %s files" % j

        except Exception as e:
            print("Error: %s" % e)
            continue
    print "Number of files: %s" % i
    print "Number of duplicates: %s" % len(hashes)
    print "%s files has been indexed" % j

def add_code_keyword_into_document(document, file_content, node, counter):
    # Flag is set when at least 1 code characteristics has been stored
    flag = False
    #document.add( Field("var_type_map", str(dict(node["var_type_map"])), Field.Store.YES, Field.Index.NO))
    document.add(Field("line_numbers", str(dict(node["line_numbers"])), Field.Store.YES, Field.Index.NO))
    document.add(Field("hash", str(md5(file_content)), Field.Store.YES, Field.Index.NO))
    document.add(Field("code", so_tokenizer(file_content, False), Field.Store.YES, Field.Index.ANALYZED))
    #루씬에서 인덱싱 할때 알아서 tokenize해줄텐데.. 왜 so_tokenizer를 사용했지 여기서??

    for m in node["typed_method_call"]:
        if m:
            document.add( Field("typed_method_call", m, Field.Store.YES, Field.Index.ANALYZED))
            counter.typed_method_call_count += 1
            flag = True

    for e in node["extends"]:
        if e:
            document.add(Field("extends", e, Field.Store.NO, Field.Index.ANALYZED))
            counter.extends_count += 1

    for c in node["used_classes"]:
        if c:
            document.add( Field("used_classes", str(c), Field.Store.YES, Field.Index.ANALYZED))
            counter.used_classes_count += 1

    for i in node["class_instance_creation"]:
        if i:
            document.add( Field("class_instance_creation", i, Field.Store.YES, Field.Index.ANALYZED) )
            counter.class_instance_creation_count += 1
            flag = True

    for m in node["methods"]:
        if m:
            document.add(Field("methods", m, Field.Store.YES, Field.Index.ANALYZED))
            counter.methods_count += 1

    for m in node["methods_called"]:
        if m:
            document.add(Field("methods_called", m, Field.Store.YES, Field.Index.ANALYZED))
            counter.methods_called_count += 1
            flag = True

    for m in node["unresolved_method_calls"]:
        if m:
            document.add(Field("unresolved_method_calls", m, Field.Store.YES, Field.Index.ANALYZED))
            counter.unresolved_method_calls_count += 1

    # for l in node["literals"]:
    # 	if l:
    # 		document.add( StringField("literals", l, Field.Store.YES))
    # 		counter.literals_count += 1

    return flag

def main(src, dst):
    try:
        start_time = time.time()

        print "Indexing starts..."
        indicesDestination = File(dst)
        #writer = IndexWriter(SimpleFSDirectory(indexDestination), StandardAnalyzer(), True, IndexWriter.MaxFieldLength.UNLIMITED)
        #Analyzer : 본문이나 제목 등의 텍스트를 색인하기 전에 반드시 분석기를 거쳐 단어로 분리해야 한다. Analyzer 클래스는 Directory와 함께 IndexWrite 클래스의 생성 메소드에 지정하며 지정된 텍슽트를 색인할 단위 단어로 분리하고 필요 없는 단어를 제거하는 등의 역할을 담당

        analyzer = KeywordAnalyzer()  #전체 텍스트를 하나의 토큰으로 다룬다. (즉, Analyze 하지 않는 것과 결과적으로 동일하다.)
        a = {"code": JavaCodeAnalyzer(), "comments": EnglishAnalyzer(Version.LUCENE_CURRENT)} #PerFieldAnalyzerWrapper를 사용하기 위한 map 생성 (Python 에서는 Dict())
        wrapper_analyzer = PerFieldAnalyzerWrapper(analyzer, a) 				#http://svn.apache.org/viewvc/lucene/pylucene/trunk/test/test_PerFieldAnalyzerWrapper.py?revision=1757704&view=co
        config = IndexWriterConfig(Version.LUCENE_CURRENT, wrapper_analyzer)

        writer = IndexWriter(SimpleFSDirectory(indicesDestination), config)
        #SimpleFSDirectory 옵션은 파일시스템에 특정 디렉토리에 인덱스 파일을 저장하겠다. DB, RAM, File system 3개가 있음
        #config 는 IndexWriter 사용에 필요한 Analyzed 된 token이다.

        counter = Counter()
        generate_indices_from_projects(src, writer, counter)
        writer.close()
        print "Done"
        print str(counter)
        print "$$$%s\tseconds" % (time.time() - start_time)

    except CorruptIndexException as e:		#when index is corrupt
            e.printStackTrace()
    except LockObtainFailedException as e:	#when other writer is using the index
            e.printStackTrace()
    except IOException as e:	#when directory can't be read/written
            e.printStackTrace()

def listing_projects_without_testcode(original, test):
    for i in os.listdir(original):
        if i in os.listdir(test):
            continue
        else:
            write_file('/Users/Falcon/Desktop/list.txt', i)

if __name__ == '__main__':
    # Indexing projects (entire) with test codes
    # filtering_only_with_test_code('/Users/Falcon/Desktop/GitArchive/Git_20161108', '/Users/Falcon/Desktop/GitArchive/Git_with_test/')

    # source = '/Users/Falcon/Desktop/GitArchive/Git_with_Test'
    # destination = '/Users/Falcon/Desktop/GitArchive/New_Indices'
    # main(source, destination)

    # listing_projects_without_testcode('/Users/Falcon/Desktop/GitArchive/Git_20161108', '/Users/Falcon/Desktop/GitArchive/Git_with_test')



    # Indexing only test codes with meta data of the projects >> 332944 files
    filtering_test_code_only('/Users/Falcon/Desktop/GitArchive/Git_20161108', '/Users/Falcon/Desktop/GitArchive/Git_testcode_only/')
    # >> 332944 files in 4813 projects

    source = '/Users/Falcon/Desktop/GitArchive/Git_testcode_only'
    destination = '/Users/Falcon/Desktop/GitArchive/New_Indices'
    main(source, destination)
    pass