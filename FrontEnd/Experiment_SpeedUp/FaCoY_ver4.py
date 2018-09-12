#!/usr/local/bin/jython
# -*- coding: utf-8 -*-
import time, codecs, sys, traceback, cgi, re, os
sys.setrecursionlimit(50000)

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
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.jobs_3.5.200.v20120521-2346.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources.win32.x86_3.5.100.v20110423-0524.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources_3.8.0.v20120522-2034.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.core_3.8.1.v20120531-0637.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.ui_3.8.2.v20130107-165834.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jface.text_3.8.0.v20120531-0600.jar")
sys.path.append(
	"/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.ltk.core.refactoring_3.6.100.v20130605-1748.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.osgi_3.8.0.v20120529-1548.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.text_3.5.0.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/bson-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-core-3.0.2.jar")

import GitSearch_Result_speed_1
from operator import attrgetter
from GitSearch.MyUtils import write_file
from GitSearch.DBManager import DBManager
from GitSearch.FrontEnd.Generator_Code_Query_text_base import Generator
from GitSearch.Searcher.Snippet_Searcher_test import SnippetSearcher
from GitSearch.Searcher.Question_Searcher_test import GettingQuestionDocs, SimilarQsSearcher
from GitSearch.Searcher.BigCloneBench_Searcher_test import find_answer_ids, GettingAnswerDocs, BenchSearcher, recommend

from org.apache.lucene.search import IndexSearcher, SearcherManager, SearcherFactory, ReferenceManager
from org.apache.lucene.store import SimpleFSDirectory, FSDirectory
from java.io import File

DBManager.init()
DBManager.autoconnection()
INDICES_PATH = '/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Indices/'
hitlog_path = '/Users/Falcon/Desktop/Tracing/hit_logs_for_each_4.txt'
scorelog_path = '/Users/Falcon/Desktop/Tracing/Score_logs_4/'
base_path = '/Users/Falcon/Desktop/IJA/dataset/Experiment/'

def save_experiment_result(input, string):
    write_file(base_path + "New_Recommended_Set_4/" + str(input) + "_result.txt", string)

def read_content_in_file(file_path):
    try:
        with codecs.open(file_path, mode='r', encoding='utf-8') as file:
            text = file.read()
        file.close()
    except:
        return None
    return text

def write_file(file_path, content):
    file = codecs.open(file_path, mode='a', encoding='utf-8')
    file.write(content + '\n')
    file.close()

def static_vars(**kwargs):
    def decorate(func):
        for k in kwargs:
            setattr(func, k, kwargs[k])
        return func

    return decorate

@static_vars(counter=0)
def run(name=None):
    total_time = time.time()
    if not os.path.exists(scorelog_path):
        os.makedirs(scorelog_path)

    targets = read_content_in_file(base_path + "targets_for_recommendation_4.txt")
    for query_number in targets.splitlines():
        hit_logs_for_each = ''
        score_logs_for_each = ''
        run.counter += 1
        print '[%s] : [%d] / [%d] : ' % (str(query_number), run.counter, len(targets.split('\n'))), round(
            (float(run.counter) / float(len(targets.split('\n'))) * 100), 2), '%', 'In progress..'

        target_file = base_path + "targets/" + query_number + ".txt"
        code_query = read_content_in_file(target_file)
        code_query = cgi.escape(re.sub(r'[^\x00-\x80]+', '', code_query))

        try:
            if code_query:
                search_time = time.time()
                final_items, hit_logs_for_each, score_logs_for_each = query_index(code_query, hit_logs_for_each,
                                                                                  score_logs_for_each)
                git_search_result = GitSearch_Result_speed_1.GitSearchResult(final_items)
                for i in git_search_result.items:
                    # print u'************************************************', i.file_name
                    save_experiment_result(query_number, i.file_name.split('.')[0])

                if not git_search_result.items:
                    save_experiment_result(query_number, '0')
                write_file(hitlog_path, hit_logs_for_each)
                score_final_path = scorelog_path + query_number + '.txt'
                write_file(score_final_path, score_logs_for_each + '\n')
                print("Item Searching Time : %s seconds" % (time.time() - search_time))
            print ('Total Time Taken : %s seconds' % (time.time() - total_time))
            print "***********************************************************************************"

        except:
            hit_logs_for_each += '0'  # java lexer recursion limit exceeded
            write_file(hitlog_path, hit_logs_for_each)
            score_final_path = scorelog_path + query_number + '.txt'
            write_file(score_final_path, score_logs_for_each + '\n')
            save_experiment_result(query_number, '0')
            print(traceback.format_exc())
            print ('Total Time Taken : %s seconds' % (time.time() - total_time))
            print "***********************************************************************************"



def query_index(query, hit_logs_for_each, score_logs_for_each):
    ### 1_Query Alternation
    user_code_query = Generator(query)

    directory = SimpleFSDirectory(File(INDICES_PATH + 'bigclonebench_4_text'))
    searchermgr = SearcherManager(directory, SearcherFactory())
    searchermgr.maybeRefresh()
    searcher = searchermgr.acquire()

    benchsearcher = BenchSearcher(searcher)  # BigCloneBench
    ### 8_Querying for the Final Results
    # Log : Bench_result for each query
    bench_result, score_logs_for_each = benchsearcher.more_like_this3(5000, score_logs_for_each, user_code_query)

    searchermgr.release(searcher)
    searchermgr.close()
    searcher = None
    directory.close()
    directory = None

    if bench_result:
        hit_logs_for_each += str(len(bench_result)) + '\t'
    else:
        hit_logs_for_each += ('0' + '\t')

    sorted_bench_results = sorted(bench_result, key=attrgetter('score'), reverse=True)

    print 'Search Count : ', len(sorted_bench_results)
    recommended = recommend(sorted_bench_results)
    print 'Final Count : ', len(recommended)
    if bench_result:
        hit_logs_for_each += str(len(recommended)) + '\t'
    else:
        hit_logs_for_each += ('0' + '\t')
    return recommended, hit_logs_for_each, score_logs_for_each

if __name__ == "__main__":
    run()