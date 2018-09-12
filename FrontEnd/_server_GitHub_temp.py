#!/usr/local/bin/jython
#-*- coding: utf-8 -*-

import cgi
import re
import traceback
from operator import attrgetter

from Searcher.DBManager import DBManager
from Searcher._2_Searcher import GitHubSearcher, GoogleStackoverflowSearcher
from flask import Flask, render_template, send_from_directory, request
from pygments import highlight
from pygments.formatters.html import HtmlFormatter
from pygments.lexers.jvm import JavaLexer

from GitSearch_Item import GitSearchItem
from GitSearch_Result import GitSearchResult
from test_pack.MyUtils import read_file

DBManager.init()
DBManager.autoconnection()

def highlight_file(path):
	file_content = read_file(path)
	return highlight(file_content, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True, lineanchors="foo") )

# TODO: Query Index
# TODO: Ranking
# open files and highlight
# TODO: Return results back to user

INDICES_PATH = "/Indices/" 

app = Flask(__name__, static_folder='static')

@app.route("/")
def index(name=None):
	query = request.args.get('q')
	#evaluation = request.args.get("evaluation")
	try:
		if query:
			#if evaluation:
				#github_items = query_index2(query)	#For evaluation
			#else:

			# query = cgi.escape(re.sub(r'[^\x00-\x80]+', '', query))

			final_items = query_index(query)	#For actual service

			git_search_result = GitSearchResult(final_items)

			search_log = None#load_logdata()

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

from FrontEnd.__Code_Query_Generator import Generator
from Searcher.__Snippet_Searcher import SnippetSearcher
from Searcher.__Question_Searcher import GettingQuestionDocs, SimilarQsSearcher
from Searcher.__BigCloneBench_Searcher import find_answer_ids, GettingAnswerDocs, BenchSearcher, recommend
from test_pack.MyUtils import truncate_search_log, write_search_log, read_search_log

def query_index(query):
	#truncate_search_log()
	################################################################################
	print "*************** Searching Starts ***************\n"
	print "Original User Query : ", query
	write_search_log(u"*************** Searching Starts ***************\n\nOriginal User Query : " + query)

	#print '\n------------------------------------------------------------------------------------------'
	#print "\n*************** Generating User Code Query ***************"
	#write_search_log(u"\n------------------------------------------------------------------------------------------\n"
	#				+ u"*************** Generating User Code Query ***************\n\n")
	# User Query -> Code Query
	user_code_query = Generator(query)

	answers = SnippetSearcher("%sstackoverflow" % (INDICES_PATH), user_code_query)
	answer_ids = answers.more_like_this(20, query=user_code_query)	

	question_ids = answers.find_question_ids(answer_ids)	

	getDoc = GettingQuestionDocs("%squestionIndex" % (INDICES_PATH))
	item_docs = getDoc.search(question_ids, 20)
	item_docs = item_docs[0:3]

	similar_questions = []
	question = SimilarQsSearcher("%squestionIndex" % (INDICES_PATH))
	for item_doc in item_docs:
		similar_questions += question.more_like_this2(item_doc, 3)	
		print ""

	question.reader.close()


	if similar_questions.__len__() == 15: similar_questions = similar_questions[:-6]
	elif similar_questions.__len__() == 12:	similar_questions = similar_questions[:-3]

	answer_ids = find_answer_ids(similar_questions)
	if answer_ids == 0:
		print("Can't find anything..")
		return 0

	getDoc = GettingAnswerDocs("%sstackoverflow" % (INDICES_PATH))
	answer_docs = getDoc.search(answer_ids)

	benchsearcher = BenchSearcher("%sdyclink" % (INDICES_PATH))

	for answer_doc in answer_docs:
		final_result += benchsearcher.more_like_this2(100, answer_doc, user_query=user_code_query)

	benchsearcher.reader.close()
	results = sorted(final_result, key=attrgetter('so_item.answer_id'), reverse=True)

	id = 0
	i = 0
	temp_result = []
	for item in results:
		if id != item.so_item.answer_id:
			id = item.so_item.answer_id
			i = 1
			temp_result.append(item)
		elif id == item.so_item.answer_id and i < 500:
			i += 1
			temp_result.append(item)
		elif id == item.so_item.answer_id and i > 500:
			continue

	# Sorting by scores
	results = sorted(temp_result, key=attrgetter('score'), reverse=True)

	# Removing duplicates by using a list()
	recommended = recommend(results)

	return recommended

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
	# send_static_file will guess the correct MIME type
	return send_from_directory(app.static_folder, file_path)

def render_code_results(query):
	github_items = query_index(query)
	git_search_items = [GitSearchItem(github_item) for github_item in github_items ]
	return git_search_items

def query_index2(query):
	""" Returns a set of file paths relevant to given query """
	print "Evaluation Index"
	from time import time
	t = time()
	google = GoogleStackoverflowSearcher("%sevaluation201306" % (INDICES_PATH)) #
	so_items = google.search(query, 10)
	print "Google Request Time: %s" % (time()-t)
	t = time()
	github = GitHubSearcher("%sgithub" % (INDICES_PATH), query)
	github_items = github.more_like_this(so_items)
	print "GitSearch Request Time: %s" % (time()-t)
	return github_items

if __name__ == "__main__":
	# app.run()

	# app.run(host="0.0.0.0", port="5000")	#socket에러 떳었음..
	while True:
		try:
			app.run(host="0.0.0.0")
		except Exception as e:
			print e
