#!/usr/local/bin/jython
#-*- coding: utf-8 -*-

from pygments import highlight
from pygments.lexers.jvm import JavaLexer
from pygments.formatters.html import HtmlFormatter
from pygments.formatters.latex import LatexFormatter
from collections import namedtuple
import re, codecs, os
from GitSearch.MyUtils import read_file

def java_files_from_dir(directory):
	javafiles = (os.path.join(dirpath, f)
		for dirpath, dirnames, files in os.walk(directory)
		for f in files if f.endswith('.java'))
	return javafiles

def write_file(file_path, content):
	try:
		with codecs.open(file_path, mode='a', encoding='utf-8') as file:
			file.write(content + "\n")
		file.close()
	except:
		return None
	pass

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
	def __init__(self, github_item, search_count, rank, targetpath):
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

		self.so_item = github_item.so_item ###
		self.line_numbers = self._eval(github_item.line_numbers) # allows us to map code characteristics to line numbers
		self.matching_line_numbers = [] # Lines to highlight

		self.targetpath = targetpath  # Kui 추가 부분
		self.html = self.highlight_code(search_count, rank, targetpath)



	def _eval(self, ln):
		try:
			return eval(ln)
		except:
			print "Before eval Exception..."
			return {}
		print "After eval"

	def hl_snippet(self, source, start):
		return highlight(source, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True, linenostart=start))

	def highlight_code(self, search_count, rank, targetpath):
		html_snippets = []
		matched_line_numbers = self.matched_line_number()

		if matched_line_numbers:
			snippet_cluster_lns = self.compute_lines_to_highlight(self.adjacent_line_numbers())
			snippets = []

			##################### Data provision ######################
			pure_snippets_for_data_requirement = []								##
			#snippet_cluster_lns		##lines_to_be_highlighted
			#self.file_content_lines	##entire code

			for snippet_cluster_ln in snippet_cluster_lns:
				snippet = []
				for n in snippet_cluster_ln:
					snippet.append(self.file_content_lines[n])
					pure_snippets_for_data_requirement.append(self.file_content_lines[n])

				start_line = min(snippet_cluster_ln)
				# end_line = max(snippet_cluster_ln)
				highlight_lines = map(lambda x: x - start_line + 1, self.matching_line_numbers)
				snippets.append(("\n".join(snippet), start_line, highlight_lines))

			# #Data provision_ Defect4J (모든) 각 쿼리별 랭킹순으로 하이라이트 라인번호 + 전체코드 파일
			final_str = "\n".join(self.file_content_lines)
			original_project_list = ['knutwalker_google-closure-compiler', 'google_closure-compiler',
									 'weitzj_closure-compiler', 'jfree_jfreechart-fse', 'jfree_jfreechart',
									 'apache_commons-math', 'apache_commons-lang', 'mockito_mockito',
									 'bryceguo_mockito']

			if not self.file_path.split('/')[6] in original_project_list:	#Check the duplicate projects.
				purepath = targetpath[:-4]
				if not os.path.exists(purepath):
					os.makedirs(purepath)

				testcode_path = purepath + '_result_testcode'
				if not os.path.exists(testcode_path):
					os.makedirs(testcode_path)


				if self.file_path.split('/')[-1] == 'Test.java':	# 결과 파일이 Test.java 인 것들 걸러내기
					pass
				else:
					final_path = purepath + "/" + str(rank) + '_' + str("||".join(self.file_path.split('/')[6:]))
					write_file(final_path, str(final_str))
					write_file(final_path + "_", str(snippet_cluster_lns))
					print "*****************************", final_path, "is Done.."

					# 여기서 test code 또한 찾아서 셋트로 돌려줘보자.
					# >> 각각 결과코드에 해당하는 프로젝트 경로를 새로 다 뒤져서 현재 결과파일 앞뒤로 test 키워드 들어있는 파일들을 찾아본다.
					# >> 테스트 파일 찾았으면 복붙

					result_file_name = self.file_path.split('/')[-1]
					result_pure_file_name = ((self.file_path.split('/')[-1]).split('.'))[0]	#pure name of the java file (e.g., ABC.java -> ABC)

					stopwords = ['A', 'a', 'test']
					javafiles = java_files_from_dir('/'.join(self.file_path.split('/')[:7]))
					for javafile in javafiles:
						if result_pure_file_name in stopwords:
							continue
						name_of_javafile = javafile.split('/')[-1].split('.')[0]
						if 'test' in name_of_javafile and result_pure_file_name in name_of_javafile:	#해당 결과파일의 이름과 'test' 라는 키워드가 들어가있는 파일이면 (e.g., xxxtest.java or testxxx.java)
							content = read_file(javafile)
							testcode_path = testcode_path + '/' + str(rank) + '_' + str('||'.join(javafile.split('/')[6:]))
							write_file(testcode_path, content)
							write_file('/Users/Falcon/Desktop/count.txt', testcode_path)
							write_file('/Users/Falcon/Desktop/count.txt', javafile)
							write_file('/Users/Falcon/Desktop/count.txt', '**********************')

			html_snippets = [highlight(snippet[0], JavaLexer(), LatexFormatter(linenos=True, linenostart=snippet[1])) for snippet in snippets]
			self.code_snippets = [GitSearchItemSnippet(self.hl_snippet( snippet[0], snippet[1]), snippet[1]) for snippet in snippets]

		if not html_snippets:
			html_snippets.append(highlight(self.file_content, JavaLexer(), HtmlFormatter(linenos=True, anchorlinenos=True)))
			self.code_snippets.append(GitSearchItemSnippet(self.hl_snippet( self.file_content, 0), 0) )
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


# 결과 출력 라인 결정
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
					lbound = max(ln - gap, 0)												#결과 출력부, Low bound
					rbound = min(ln + gap, len(self.file_content_lines))					#결과 출력부, Upper bound
					lns.extend(range(lbound, rbound))
					# print "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", lns, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
					break # Test if we can avoid duplicate matches
		return list(set(lns))

	def skip_java_doc(self, line):
		""" JDT returns start position of method declaration including its JavaDoc comment """
		if line in self.file_content_lines:
			currentline = self.file_content_lines[line].strip()
			max_file_lines = len(self.file_content_lines)
			while currentline.startswith(("/*", "*", "*/", "@")):
				if max_file_lines > line:
					line += 1
					currentline = self.file_content_lines[line].strip()
		return line

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