#!/usr/local/bin/jython
#-*- coding: utf-8 -*-

from pygments import highlight
from pygments.lexers.jvm import JavaLexer
from pygments.formatters.html import HtmlFormatter
from pygments.formatters.latex import LatexFormatter
from collections import namedtuple
import re

def generate_github_link(s):
	try:
		c = s.split("/")
		user, repo = c[6].split("_")
		rest = "/".join(c[8:])
		link = "https://github.com/%s/%s/blob/master/%s" % (user, repo, rest)
		return link
	except Exception, e:
		return "#GitHub link generating failed..."

class MyHtmlFormatter(HtmlFormatter):
	def __init__(self, **options):
		HtmlFormatter.__init__(self, **options)
		self.matched_terms = options["matched_terms"] if "matched_terms" in options else []
		self.matched_terms = [token.lower() for term in self.matched_terms for token in term.split(".")]
		h_terms = []
		for term in self.matched_terms:
			if "." in term:
				for token in term.split("."):
					h_terms.append(token)
			else:
				h_terms.append(term)
		self.matched_terms = h_terms

	def _my_format(self, tokensource, outfile):
		for ttype, value in tokensource:
			if value.lower() in self.matched_terms:
				yield ttype, '<span class="hll">%s</span>' % value
			else:
				yield ttype, value

	def format(self, tokensource, outfile):
		new_tokensource = self._my_format(tokensource, outfile)
		HtmlFormatter.format(self, new_tokensource, outfile)

GitSearchItemSnippet = namedtuple("GitSearchItemSnippet", "html startline")

class GitSearchItem:
	def __init__(self, github_item):
		self.github_item = github_item
		self.file_path = github_item.file
		self.file_name = self.file_path.split("/")[-1]
		self.file_content = github_item.file_content
		self.code_snippets = []
		lines = self.file_content.split("\n\r")
		lines2 = []
		for line in lines:  lines2 += line.split("\r\n")
		lines3 = []
		for line in lines2:  lines3 += line.split("\n")
		lines4 = []
		for line in lines3:  lines4 += line.split("\r")
		self.file_content_lines = lines4
		self.matched_terms = [matched_term for matched_term in github_item.matched_terms if matched_term not in ["int", "byte", "long", "short", "float", "double", "boolean", "char", "void", "String", "Integer"] and len(matched_term) > 1]
		self.github_link = generate_github_link(self.file_path)
		self.score = 0

		# self.so_item = github_item.so_item ###
		# self.line_numbers = self._eval(github_item.line_numbers) # allows us to map code characteristics to line numbers
		# self.matching_line_numbers = [] # Lines to highlight
		# self.html = self.highlight_code()
		

	def _eval(self, ln):
		try:
			return eval(ln)
		except:
			print "Before eval Exception..."
			return {}
		print "After eval"

	def hl_snippet(self, source, start):
		return highlight(source, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True, linenostart=start))

	def highlight_code(self):
		html_snippets = []
		if self.matched_line_number():
			snippet_cluster_lns = self.compute_lines_to_highlight(self.adjacent_line_numbers())
			snippets = []
			for snippet_cluster_ln in snippet_cluster_lns:
				snippet = []
	
				for n in snippet_cluster_ln:	
					snippet.append(self.file_content_lines[n])
				start_line = min(snippet_cluster_ln)
				
				highlight_lines = map(lambda x: x - start_line + 1, self.matching_line_numbers)
				snippets.append(("\n".join(snippet), start_line, highlight_lines))

			html_snippets = [highlight(snippet[0], JavaLexer(), LatexFormatter(linenos=True, linenostart=snippet[1])) for snippet in snippets]
			self.code_snippets = [ GitSearchItemSnippet( self.hl_snippet( snippet[0], snippet[1]), snippet[1]) for snippet in snippets] 

		
		if not html_snippets:
			html_snippets.append(highlight(self.file_content, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True)))
			self.code_snippets.append( GitSearchItemSnippet( self.hl_snippet( self.file_content, 0), 0) )
		return "".join(html_snippets)

	def compute_lines_to_highlight(self, ln):
		""" Returns the line number clusters of matched keywords in source file"""
		from itertools import groupby
		from operator import itemgetter
		ln = [e for e in ln if e >= 0]
		ln.sort()
		res = [map(itemgetter(1), g) for k, g in groupby(enumerate(ln), lambda (i,x):i-x)]
		return res

	# Note: is not very accurate, because we do not consider the tokens produced by lucene analyzers
	def matched_line_number(self):
		""" Returns the line number of matched keywords in source file"""
		current_line_number = 1
		matching_line_numbers = set()
		for line in self.file_content_lines: #  file_content.split("\n"):
			for term in self.matched_terms:
				if term in line:
					#print "Term: %s Line: %s" % (term, line)
					matching_line_numbers.add(current_line_number)
			current_line_number += 1
		return matching_line_numbers

	def adjacent_line_numbers(self, gap=15):
		""" Generates a snippet window to present the matched term(s) """
		lns = []
		# print self.line_numbers
		for term in self.matched_terms:
			# print term
			if term in self.line_numbers:
				self.matching_line_numbers.extend(self.line_numbers[term])
				for ln in self.line_numbers[term]:
					ln = self.skip_java_doc(ln)
					#print "After Java DOC line number: %s" % ln
					lbound = max(ln-gap, 0)
					rbound = min(ln+gap, len(self.file_content_lines))
					lns.extend(range(lbound, rbound))
					break # Test if we can avoid duplicate matches
		return list(set(lns))

	def skip_java_doc(self, ln):
		""" JDT returns start position of method declaration including its JavaDoc comment """
		if ln in self.file_content_lines:
			currentline = self.file_content_lines[ln].strip()
			max_file_lines = len(self.file_content_lines)
			while currentline.startswith(("/*", "*", "*/", "@")):
				if max_file_lines > ln:
					ln += 1
					currentline = self.file_content_lines[ln].strip()
		return ln

	def highlight_matched_terms(self, gitsearch_item_html):
		html_template = '<span class="hll">%s</span>'
		html = gitsearch_item_html
		for term in self.matched_terms:
			pattern = re.compile(r'\b%s\b' % term, re.IGNORECASE)
			html = pattern.sub(html_template % term, html)
			if "." in term:
				for token in term.split("."):
					html = pattern.sub(html_template % token, html)
		return html






