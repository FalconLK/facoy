#!/usr/bin/env python
# -*- coding: utf-8 -*-

from java.util import Date
from java.util.concurrent import Executors, TimeUnit
from GitSearch.Indexer.NewJavaParser import parse
import GitSearch.MyUtils as MyUtils
from java.lang import Runnable
from java.util.concurrent import Callable

pool = Executors.newFixedThreadPool(4)

def convert_timestamp_to_date(timestamp):
	if timestamp:
		return Date(timestamp.getTime())
	return None

def transform_body(body):
	code_snippets = []
	code_hints = []
	for item in body.split("</code>"):
		if "<code>" in item:
			code_tag = item [item.find("<code>")+len("<code>"):]
			code_tag = MyUtils.unescape_html(code_tag)
			if "." in code_tag and "(" in code_tag:
				code_snippets.append(code_tag)

				if "<pre" not in item and len(code_tag) < 25: # Heuristic to determine if code_tag is enclosed in inline code block
					code_hints.append(code_tag)
			elif len(code_tag) < 25:
				code_hints.append(code_tag)

	l = []
	for code_hint in code_hints:
		l.extend(MyUtils.tokenize(code_hint))

	code_hints = set(l)
	asts = []
	for code_snippet in code_snippets:
		ast = parse(code_snippet, resolve=True)
		if ast:
			asts.append(ast)

	return asts, code_hints

def parallize(doc_codes):
	parsers = [JDTParser(code, doc, parse) for doc, code in doc_codes]
	futures = pool.invokeAll(parsers)
	ast_file_docs = [(future.get(3, TimeUnit.SECONDS).result, future.get(3, TimeUnit.SECONDS).source, future.get(3, TimeUnit.SECONDS).doc) for future in futures]
	return ast_file_docs

def createDoc(question_id, answer_id, last_edit_date, creation_date, body, score, is_accepted):
	code_snippets, code_hints = transform_body(body)
	d = {
		"question_id": question_id,
		"answer_id": answer_id,
		"last_edit_date": convert_timestamp_to_date(last_edit_date),
		"creation_date": convert_timestamp_to_date(creation_date),
		"is_accepted": is_accepted == 1,
		"score": score,
		"code_snippets": code_snippets,
		"code_hints": code_hints
	}
	return d

class JDTParser(Callable, Runnable):
	def __init__(self, source, doc, callback):
		self.source = source
		self.callback = callback
		self.doc = doc
		self.result = None

	def call(self):
		self.result = self.callback(self.source)
		return self

	def run(self):
		self.result = self.callback(self.source)
		return self