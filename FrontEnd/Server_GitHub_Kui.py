#!/usr/local/bin/jython
#-*- coding: utf-8 -*-
from flask import Flask, render_template, send_from_directory, request
from GitSearch_Item import GitSearchItem

#Git 꺼 사용,,
import traceback
import time, codecs, cgi, re, os
from pygments import highlight
from pygments.lexers.jvm import JavaLexer
from pygments.formatters.html import HtmlFormatter
from operator import attrgetter
from GitSearch.MyUtils import read_file
from GitSearch_Result_Git import GitSearchResult
from GitSearch.DBManager import DBManager
from GitSearch.FrontEnd.Generator_Code_Query import Generator
from GitSearch.Searcher.Snippet_Searcher_test import SnippetSearcher
from GitSearch.Searcher.Question_Searcher_test import GettingQuestionDocs, SimilarQsSearcher
from GitSearch.Searcher.Project_Searcher_test import GitSearcher, find_answer_ids, GettingAnswerDocs, recommend
from org.apache.lucene.search import IndexSearcher, SearcherManager, SearcherFactory, ReferenceManager
from org.apache.lucene.store import SimpleFSDirectory, FSDirectory
from java.io import File

DBManager.init()
DBManager.autoconnection()


def write_file(file_path, content):
    try:
        with codecs.open(file_path, mode='a', encoding='utf-8') as file:
            file.write(content + "\n")
        file.close()
    except:
        return None
    pass

def write_file_over(file_path, content):
    with codecs.open(file_path, mode='w', encoding='utf-8') as file:
        file.write(content + "\n")
    file.close()

def highlight_file(path):
    file_content = read_file(path)
    return highlight(file_content, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True, lineanchors="foo") )

# INDICES_PATH = "/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Indices/"
INDICES_PATH = "/Indices/"

application = Flask(__name__, static_folder='static')

search_count = 0   #전역 변수 사용시 미리 선언 + 정의해놓고, 함수나 클래스에서 global count 선언한다음 씀.

@application.route("/")
def index(name=None):
    global search_count

    print 'Searching starts..'
    query = request.args.get('q')

    ############################## 추가부분 (파일 첫줄이 파일의 경로라는 가정하에)
    file_path = query.split('\n')[0].strip()
    query = "\n".join(query.split('\n')[1:])
    ##############################

    try:
        if query:
            query = cgi.escape(re.sub(r'[^\x00-\x80]+', '', query))

            final_items = query_index(query)

            search_log = None
            search_count += 1
            git_search_result = GitSearchResult(final_items, search_count, file_path)

            # for git_search in git_search_result:
            #     file_path = git_search.file
            #     file_name = file_path.split("/")[-1]

            return render_template("search.html", query=query, git_search_result=git_search_result, search_log=search_log)
    except:
        print(traceback.format_exc())
    return render_template("index.html", name=name)

def load_logdata():
    import codecs
    f = codecs.open('static/search_log', 'r', 'utf-8')
    logs = f.read()
    f.close()
    return logs

def query_index(query):
    ### 1_Query Alternation
    user_code_query = Generator(query)
    print 'query: ', query
    print 'user_code_query: ', user_code_query

    #open
    directory = SimpleFSDirectory(File(INDICES_PATH+'stackoverflow'))
    searchermgr = SearcherManager(directory, SearcherFactory())
    searchermgr.maybeRefresh()
    searcher = searchermgr.acquire()

    ### 2_Finding 3 Answer Snippets using the User Query (refined)
    answers = SnippetSearcher(searcher, user_code_query)
    answer_ids = answers.more_like_this(10, query=user_code_query)
    print 'answer_ids: ', answer_ids

    #close
    searchermgr.release(searcher)
    searchermgr.close()
    searcher = None
    directory.close()
    directory = None

    ### 3_Finding the Associated Questions
    question_ids = answers.find_question_ids(answer_ids)
    print 'question ids: ', question_ids

    #open
    directory = SimpleFSDirectory(File(INDICES_PATH + 'questionIndex'))
    searchermgr = SearcherManager(directory, SearcherFactory())
    searchermgr.maybeRefresh()
    searcher = searchermgr.acquire()

    ### 4_Cutting items
    getDoc = GettingQuestionDocs(searcher)
    item_docs = getDoc.search(question_ids, 10)[0:3]  # 순위대로 최소 7개의 question을 얻기 위해서 여기서 7개를 자름.
    # print 'item docs: ', item_docs

    ### 5_Finding 3 Similar Questions per a Question (3 X 3)
    similar_questions = []
    question = SimilarQsSearcher(searcher)

    if item_docs:
        for item_doc in item_docs:
            similar_question = question.more_like_this2(item_doc, 3)  # 각 question 들에 대해 7개씩 비슷한 것들 찾음.
            similar_questions += similar_question

    print 'similar_questions: ', similar_questions

    searchermgr.release(searcher)
    searchermgr.close()
    searcher = None
    directory.close()
    directory = None

    ### 6_Finding Associated Answers for each Question (9 - 9)
    answer_ids = find_answer_ids(similar_questions)
    print 'answer ids: ', answer_ids

    if not answer_ids:
        recommended = ''
        return recommended
        # dest_path = u'/Users/Falcon/Desktop/***Ongoing***/***[4]_FaCoY_Defect4J_Data_Share_Kui/Defect4J_Results/'
        # project_name = u'Chart/'###################################################
        # write_file()

    directory = SimpleFSDirectory(File(INDICES_PATH + 'stackoverflow'))
    searchermgr = SearcherManager(directory, SearcherFactory())
    searchermgr.maybeRefresh()
    searcher = searchermgr.acquire()

    ### 7_Getting Answer Docs for the Final Query
    getDoc = GettingAnswerDocs(searcher)
    answer_docs = getDoc.search(answer_ids)

    # print 'answer docs: ', answer_docs

    searchermgr.release(searcher)
    searchermgr.close()
    searcher = None
    directory.close()
    directory = None

    directory = SimpleFSDirectory(File(INDICES_PATH + 'github_testcode'))
    searchermgr = SearcherManager(directory, SearcherFactory())
    searchermgr.maybeRefresh()
    searcher = searchermgr.acquire()

    git_results = []
    gitsearcher = GitSearcher(searcher)

    ### 7_Appending for the user query results
    git_result = gitsearcher.more_like_this2(200, answer_docs[0], user_code_query, 1)
    git_results += git_result

    # print 'answer docs: ', answer_docs

    ### 8_Querying for the Final Results
    for answer_doc in answer_docs:
        git_result = gitsearcher.more_like_this2(200, answer_doc, user_code_query, 0)
        git_results += git_result

    searchermgr.release(searcher)
    searchermgr.close()
    searcher = None
    directory.close()
    directory = None

    git_results = sorted(git_results, key=attrgetter('so_item.answer_id'), reverse=True)
    id = 0
    i = 0
    temp_result = []
    for item in git_results:
        if id != item.so_item.answer_id:
            id = item.so_item.answer_id
            i = 1
            temp_result.append(item)

        elif id == item.so_item.answer_id and i < 10:    # 3
            i += 1
            temp_result.append(item)
        elif id == item.so_item.answer_id and i > 10:    # 3
            continue

    sorted_git_results = sorted(temp_result, key=attrgetter('score'), reverse=True)

    print 'Search Count : ', len(sorted_git_results)
    recommended = recommend(sorted_git_results)
    print 'Final Count : ', len(recommended)

    # Defect4J 쿼리 결과저장
    # cot = 0
    # for c, item in enumerate(recommended):
    #     cot += 1
    #     if cot > 10:
    #         break
    #     result_file = "/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Defect4J_FaCoY/" + str(c+1) + "_" + str('_'.join(str(item[0]).split("/")[6:]))
    #     write_file_over(result_file, str(item.file_content))

    # result_file = '/Users/Falcon/Desktop/test.txt'
    # if os.path.exists(result_file):
    #     os.remove(result_file)
    #
    # write_file(result_file, 'User Code Query \n' + str(query) + '\n' + '---------------------------' + '\n')
    # for c, i in enumerate(recommended):
    #     contents = ''
    #     contents = 'Rank: %d' % (int(c)+int(1))
    #     contents += '\nFile path: %s' % str(i.file[6:]) + '\n' + '---------------------------' + '\n'
    #     contents += str(i.file_content) +'\n' + '=================================================================' + '\n\n\n'
    #     write_file(result_file, contents)

    return recommended

def to_q(path):
    from urllib import quote_plus
    return quote_plus(path)

application.jinja_env.globals.update(to_q=to_q)

@application.route('/source')
def show_source():
    file_path = request.args.get('q')
    if file_path:
        html = highlight_file(file_path)
        return render_template("file.html", file_path=file_path, source=html)

@application.route('/<path:file_path>')
def static_proxy(file_path):
    return send_from_directory(application.static_folder, file_path)

def render_code_results(query):
    github_items = query_index(query)
    git_search_items = [GitSearchItem(github_item) for github_item in github_items ]
    return git_search_items

if __name__ == "__main__":
    while True:
        try:
            # application.run(host="0.0.0.0", port="5001")
            application.run(host="0.0.0.0", port=5000)
        except Exception as e:
            print e
