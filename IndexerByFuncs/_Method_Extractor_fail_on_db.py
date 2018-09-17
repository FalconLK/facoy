# -*- coding: utf-8 -*-

# from GitSearch.MyUtils import write_file, read_file
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

import codecs
import os
from GitSearch.DBManager_IJA import DBManager

source_path = '/Users/Falcon/Desktop/IJA/Original_data/'
dest_path = '/Users/Falcon/Desktop/IJA/Function_data/'
# source_path = '/Users/Falcon/Desktop/test/test/'
# dest_path = '/Users/Falcon/Desktop/test/test_result/'

DBManager.init()
DBManager.autoconnection()

def querying(name, type):
    query = u"""select ID, STARTLINE, ENDLINE from FUNCTIONS where name = '%s' and type = '%s'""" % (name, type)
    resultSet = DBManager.requestQuery(query)
    return resultSet

def write_file(file_path, content, count):
    # print "File path : ", file_path #, "*** Contents *** \n", content

    try:
        with codecs.open(file_path, mode='w', encoding='utf-8', errors='ignore') as file:
            file.write(content) #.encode('utf-8', errors='ignore')
        file.close()
        count += 1
    except Exception as e:
        print("*************************************File write error : %s" % e)

    return count

def read_file(file_path):
    try:
        with codecs.open(file_path, "r", encoding='utf-8', errors='ignore') as file:
            file_content = file.read().encode("utf-8", errors='ignore')
        file.close()
        return file_content
    except Exception as e:
        print("*************************************File open error : %s" % e)

def java_files_from_dir(source_path):
    javafiles = []
    for path, subdirs, files in os.walk(source_path):
        for name in files:
            if name.endswith('.java'):
                javafiles.append(os.path.join(path, name))
    return javafiles

def file_contents_with_specific_lines(file_content, id, line1, line2, func_count):
    i = 1
    function_contents = ''
    for line in file_content.splitlines():
        if i < line1: i+=1; continue
        if line1 <= i and i <= line2: i+=1; function_contents += (line+'\n')
        elif i > line2: i+=1; break

    final_path = dest_path + str(id) + '.java'
    if function_contents:
        func_count = write_file(final_path, function_contents, func_count)
    return func_count
    # print 'Writing Done..'

if __name__ == '__main__':
    javafiles = java_files_from_dir(source_path)  # 자바 파일들만 뽑아내는 함수
    javafile_count = len(javafiles)

    print 'Initial java file count : ', javafile_count

    java_count = 0
    func_count = 0
    #모든 자바파일 가져와서 전부 반복
    for javafile in javafiles:
        # print javafile
        java_count += 1
        print "Count : ", float(java_count), "/", float(javafile_count), "/", round(float(java_count)/float(javafile_count)*100, 2), "%", javafile

        splits = javafile.split('/')[6:]
        #BigCloneBench에 NAME, TYPE 가지고 쿼리해서 ID, StartLine, EndLine 가져옴
        resultSet = querying(splits[1], splits[0])

        while resultSet.next():
            id = resultSet.getInt('ID')
            start = resultSet.getInt('STARTLINE')
            end = resultSet.getInt('ENDLINE')

            contents = read_file(javafile)
            func_count = file_contents_with_specific_lines(contents, id, start, end, func_count)

        resultSet.close()
        DBManager.lock.release()

    print func_count
    print 'Jobs are done...'
    pass