# -*- coding: utf-8 -*-
import sys

sys.path.append("/Libs/jython-standalone-2.7.0.jar")
sys.path.append("/Libs/mysql-connector-java-5.1.22-bin.jar")

import codecs
import os
# import pathlib
from GitSearch.DBManager_IJA import DBManager

# source_path = '/Users/Falcon/Desktop/IJA/Original_data/'
# dest_path = '/Users/Falcon/Desktop/IJA/Function_data/'
source_path = '/Users/Falcon/Desktop/test/test/'
dest_path = '/Users/Falcon/Desktop/test/test_result/'

DBManager.init()
DBManager.autoconnection()

def querying(javafile):
    splits = javafile.split("/")[6:]
    name = splits[1]
    type = splits[0]

    id_start_end = dict()
    id_list = list()
    query = u"""select ID, STARTLINE, ENDLINE, PROJECT from FUNCTIONS where name = '%s' and type = '%s'""" % (name, type)
    resultSet = DBManager.requestQuery(query)

    while resultSet.next():
        # print 'querying.. 1'
        id = resultSet.getInt('ID')
        start = resultSet.getInt('STARTLINE')
        end = resultSet.getInt('ENDLINE')
        project = resultSet.getString('PROJECT')
        id_start_end[id] = start, end
        # print 'id, start, end', id_start_end
        id_list.append(id)

        project_path = dest_path + str(project)
        if not os.path.exists(project_path):
            os.makedirs(project_path)

        file_path = project_path + "/" + str(name.split('.')[0])
        if not os.path.exists(file_path):
            os.makedirs(file_path)

        id_path = file_path + "/" + str(id) + '.java'
        if not os.path.isfile(id_path):
            contents = read_file(javafile)
            file_contents_with_specific_lines(id_path, contents, id_start_end[id][0], id_start_end[id][1])




    # for id in id_list:
    #     print 'Next id...'
    #     query = u"""select FUNCTIONALITY_ID from CLONES where FUNCTION_ID_ONE = %d""" % id
    #     resultSet = DBManager.requestQuery(query)
    #
    #     while resultSet.next():
    #         print 'querying.. 2'
    #         functionality = resultSet.getInt('FUNCTIONALITY_ID')
    #         func_path = basic_path + str(functionality)
    #         if not os.path.exists(func_path):
    #             os.makedirs(func_path)
    #
    #         file_path = func_path + '/' + str(name.split('.')[0])
    #         if not os.path.exists(file_path):
    #             os.makedirs(file_path)
    #         id_path = file_path + '/' + str(id) + '.java'
    #
    #         if os.path.isfile(id_path): #
    #             continue
    #         contents = read_file(javafile)
    #         file_contents_with_specific_lines(id_path, contents, id_start_end[id][0], id_start_end[id][1])
    #         print 'going to the next result set..'


def write_file(file_path, content):
    # print "File path : ", file_path, #"*** Contents *** \n", content
    # print 'Trying to write...'
    try:
        # file = open(file_path, mode='w')
        with codecs.open(file_path, mode='w', encoding='utf-8', errors='ignore') as file:
            file.write(content.encode('utf-8')) #.encode('utf-8', errors='ignore')

        # sys.stdout.flush()
        # file.flush()
        # os.fsync(file.fileno())
        # file.close()
        # print '!!! File writing done.. !!!'
        # time.sleep(3)

    except Exception as e:
        print("*************************************File write error : %s" % e)
        pass


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

def file_contents_with_specific_lines(path, file_content, line1, line2):
    i = 1
    function_contents = ""
    for line in file_content.splitlines():
        if i < line1: i+=1; continue
        if line1 <= i and i <= line2: i+=1; function_contents += (line+'\n')
        elif i > line2: i+=1; break
    # print 'writing the file, %s...' % path
    if function_contents:
        write_file(path, function_contents)
    # print 'Writing Done..'

if __name__ == '__main__':
    javafiles = java_files_from_dir(source_path)
    javafile_count = len(javafiles)
    print 'Initial java file count : ', javafile_count
    count = 0
    for javafile in javafiles:
        # print 'We are working on the %s now...' % javafile
        count += 1
        print "Count : ", float(count), "/", float(javafile_count), "/", round(float(count)/float(javafile_count)*100, 2), "%", javafile
        resultSet = querying(javafile)
        DBManager.lock.release()
    print 'All jobs are done...'