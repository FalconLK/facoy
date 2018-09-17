#!/usr/local/bin/jython
#-*- coding: utf-8 -*-
import time, codecs, sys, traceback, cgi, re, os
sys.setrecursionlimit(50000)

from flask import Flask, render_template, send_from_directory, request
from operator import attrgetter
from pygments import highlight
from pygments.lexers.jvm import JavaLexer
from pygments.formatters.html import HtmlFormatter

from GitSearch_Item import GitSearchItem
from GitSearch_Result import GitSearchResult
from GitSearch.MyUtils import read_file, write_file
from GitSearch.DBManager import DBManager
from GitSearch.FrontEnd.Generator_Code_Query import Generator
from GitSearch.Searcher.Snippet_Searcher import SnippetSearcher
from GitSearch.Searcher.Question_Searcher import GettingQuestionDocs, SimilarQsSearcher
from GitSearch.Searcher.BigCloneBench_Searcher import find_answer_ids, GettingAnswerDocs, BenchSearcher, recommend
from collections import namedtuple
DBManager.init()
DBManager.autoconnection()
INDICES_PATH = '/Indices/'
hitlog_path = '/Users/Falcon/Desktop/Tracing/hit_logs_for_each.txt'
scorelog_path = '/Users/Falcon/Desktop/Tracing/Score_logs/'
app = Flask(__name__, static_folder='static')

if not os.path.exists(scorelog_path):
    os.makedirs(scorelog_path)

def static_vars(**kwargs):
    def decorate(func):
        for k in kwargs:
            setattr(func, k, kwargs[k])
        return func
    return decorate

@static_vars(counter=0)

@app.route("/")
def index(name=None):
    index.counter += 1
    print index.counter

    query = request.args.get('q')
    hit_logs_for_each = ''
    score_logs_for_each = ''
    try:
        if query:

            start_time = time.time()

            query = cgi.escape(re.sub(r'[^\x00-\x80]+', '', query))

            final_items, hit_logs_for_each, score_logs_for_each = query_index(query, hit_logs_for_each, score_logs_for_each)

            git_search_result = GitSearchResult(final_items, hit_logs_for_each, score_logs_for_each, index.counter)
            for i in git_search_result.items:
                print i.file_name

            search_log = None

            print("$$$%s\tseconds" % (time.time() - start_time))

            return render_template("search.html", query=query, git_search_result=git_search_result, search_log=search_log)
    except:
        hit_logs_for_each += '0'    # java lexer recursion limit exceeded
        write_file(hitlog_path, hit_logs_for_each)

        score_final_path = ''
        score_final_path = scorelog_path + str(index.counter) + '.txt'
        if not os.path.exists(scorelog_path):
            os.makedirs(scorelog_path)
        write_file(score_final_path, score_logs_for_each + '\n')
        print(traceback.format_exc())

    return render_template("index.html", name=name)

def query_index(query, hit_logs_for_each, score_logs_for_each):
    print "*************** Searching Starts ***************"
    ### 1_Query Alternation
    user_code_query = Generator(query)

    ### 2_Finding 3 Answer Snippets using the User Query (refined)
    answers = SnippetSearcher("%sstackoverflow" % (INDICES_PATH), user_code_query)
    answer_ids = answers.more_like_this(20, query=user_code_query) #여기서 3개를 자르면, 3개의 answer 중 question 아이디가 존재하지 않을 경우, 그 수가 현저히 적어짐..

#Log : Answer count
    if answer_ids: hit_logs_for_each += str(len(answer_ids)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')

    answers.reader.close()
    answers.directory.close()

    ### 3_Finding the Associated Questions
    question_ids = answers.find_question_ids(answer_ids)
# Log : Answer - Question count
    if question_ids: hit_logs_for_each += str(len(question_ids)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')
    getDoc = GettingQuestionDocs("%squestionIndex" % (INDICES_PATH))
    item_docs = getDoc.search(question_ids, 20)[0:7]    #순위대로 최소 7개의 question을 얻기 위해서 여기서 7개를 자름.

# Log : Question ItemDoc count
    if item_docs: hit_logs_for_each += str(len(item_docs)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')
    getDoc.reader.close()
    getDoc.directory.close()

    ### 4_Finding 3 Similar Questions per a Question (3 X 3)
    similar_questions = []
    question = SimilarQsSearcher("%squestionIndex" % (INDICES_PATH))

# Log : Similar Question count for each of Question ItemDoc
    i = 1
    if item_docs:
        for item_doc in item_docs:
            similar_question = question.more_like_this2(item_doc, 7)  #각 question 들에 대해 7개씩 비슷한 것들 찾음.
            if similar_question: hit_logs_for_each += str(len(similar_question)) + '\t'
            else: hit_logs_for_each += ('0' + '\t')
            similar_questions += similar_question
            i += 1
    else:
        hit_logs_for_each += ('0' + '\t' + '0' + '\t' + '0' + '\t' + '0' + '\t' + '0' + '\t' + '0' + '\t' + '0' + '\t') #7개

# Log : Similar Question result count
    if similar_questions: hit_logs_for_each += str(len(similar_questions)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')
    question.reader.close()
    question.directory.close()

    ### 5_Finding Associated Answers for each Question (9 - 9)
    answer_ids = find_answer_ids(similar_questions)

# Log : Question - Answer count
    if answer_ids: hit_logs_for_each += str(len(answer_ids)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')

    ### 6_Getting Answer Docs for the Final Query
    getDoc = GettingAnswerDocs("%sstackoverflow" % (INDICES_PATH))
    answer_docs = getDoc.search(answer_ids)

# Log : Answer Docs count
    if answer_docs: hit_logs_for_each += str(len(answer_docs)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')

    # temp_doc = getDoc.search(['0',])

    getDoc.reader.close()
    getDoc.directory.close()

    bench_results = []
    benchsearcher = BenchSearcher("%sbigclonebench" % (INDICES_PATH))  # BigCloneBench

    # Exceptional
    ### 7_Appending for the user query results
# Log : Bench_result for UQ
#     temp_doc = ResultItem(None, 0, 'No Title', 'No Question id', 'No Answer id', 'No Description')

    bench_result, score_logs_for_each = benchsearcher.more_like_this2(1, answer_docs[0], score_logs_for_each, user_code_query, 1)
    if bench_result: hit_logs_for_each += str(len(bench_results)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')
    bench_results += bench_result


    ### 8_Querying for the Final Results
# Log : Bench_result for each query
    for answer_doc in answer_docs:
        bench_result, score_logs_for_each = benchsearcher.more_like_this2(1, answer_doc, score_logs_for_each, user_code_query, 0)#, user_query=user_code_query)
        if bench_result: hit_logs_for_each += str(len(bench_result)) + '\t'
        else: hit_logs_for_each += ('0' + '\t')
        bench_results += bench_result

    if answer_docs < 49:
        for a in range(49- len(answer_docs)):
            hit_logs_for_each += ('0' + '\t')

    # print 'Count(Sum of the bench results) : ', len(bench_results)

# Log : Results count
    if bench_results: hit_logs_for_each += str(len(bench_results)) + '\t'
    else: hit_logs_for_each += ('0' + '\t')
    benchsearcher.reader.close()
    benchsearcher.directory.close()

    # print '%%%final_results : ', final_result
    sorted_bench_results = sorted(bench_results, key=attrgetter('score'), reverse=True)
    # results = sorted(final_result, key=attrgetter('so_item.answer_id'), reverse=True)
    # print '%%%final_results_____ : ', results

    print "***********************************************************************************"
    # print sorted_bench_results

    # Answer set에서 나오는 숫자 제한하기.. 즉, 이걸 제한하면 같은 종류의 answer에 대한 snippet들이 반복되어 출력되는걸 막는다.
    # id = 0; i = 0; final_temp_result = []
    # for item in sorted_bench_results:
    #     if id != item.so_item.answer_id:
    #         id = item.so_item.answer_id
    #         i = 1
    #         final_temp_result.append(item)
    #     elif id == item.so_item.answer_id and i < 500:
    #         i += 1
    #         final_temp_result.append(item)
    #     elif id == item.so_item.answer_id and i > 500:
    #         continue
    # final_results = sorted(final_temp_result, key=attrgetter('score'), reverse=True)
    # print 'Count(Final results) : ', len(final_results)
    # recommended = recommend(final_results)

    print 'Count(Final results) : ', len(sorted_bench_results)
    recommended = recommend(sorted_bench_results)
    return recommended, hit_logs_for_each, score_logs_for_each

def highlight_file(path):
    file_content = read_file(path)
    return highlight(file_content, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True, lineanchors="foo") )

def load_logdata():
    import codecs
    f = codecs.open('static/search_log', 'r', 'utf-8')
    logs = f.read()
    f.close()
    return logs

def to_q(path):
    from urllib import quote_plus
    return quote_plus(path)

app.jinja_env.globals.update(to_q=to_q)

@app.route('/source')
def show_source():
    file_path = request.args.get('q')
    if file_path:
        html = highlight_file(file_path)
        return render_template("file.html", file_path=file_path, source = html)

@app.route('/<path:file_path>')
def static_proxy(file_path):
    return send_from_directory(app.static_folder, file_path)

def render_code_results(query):
    github_items = query_index(query)
    git_search_items = [GitSearchItem(github_item) for github_item in github_items ]
    return git_search_items

if __name__ == "__main__":
    count = 0
    while True:
        try:
            app.run(host="0.0.0.0", port="5000")
        except Exception as e:
            print e