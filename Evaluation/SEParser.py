#!/usr/local/bin/jython
#-*- coding: utf-8 -*-
from urllib import quote, quote_plus
import urllib2
import sys
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/jsoup-1.8.2.jar")
from org.jsoup import Jsoup
from flask import Flask, request


app = Flask(__name__)

@app.route("/blackduck")
def blackduck():
	query = request.args.get('q')

	if query:
		print "----"*10
		print "Blackduck: Retrieve HTML from BLACKDUCK"
		p = BlackDuckParser(query)
		html = p.get_html(5)
		#cache.set("blackduck", html, timeout=3600 * 24 * 7)
		return html

@app.route("/codota")
def codota():
	query = request.args.get('q')
	if query:
		p = CodotaParser(query)
		return p.get_html(5)

# Codota Snippet Proxy
@app.route("/doSearchScenarios")
def codotaDetail():
	q = request.args.get('searchQuery')
	if q:
		p = CodotaParser2(q)
		return p.get_html(1)

	

@app.route("/gitsearch")
def gitsearch():
	query = request.args.get('q')

	if query:
		p = GitSearchParser(query)
		return p.get_html(5)

@app.route("/gitsearcheval")
def gitsearcheval():
	query = request.args.get('q')

	if query:
		p = GitSearchParser2(query)
		return p.get_html(5)


@app.route("/stackoverflow")
def stackoverflow():
	query = request.args.get('q')
	lang = request.args.get('lang')

	if query:
		p = StackoverflowParser(query, lang)
		return p.get_html(5)




class AbstractParser:
	def __init__(self):
		#self.url = url
		self.doc = None
		self.snippets = None
		self.connect()

	def connect(self):
		try:
			# r = requests.get(self.url, timeout=20)
			self.doc = Jsoup.connect(self.url).userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0").referrer("https://www.google.com").timeout(0).get()
		except Exception as e:
			print "Download - Error: ", self.url, e
		

	def _relative_to_absolute(self):
		select = self.doc.select("a");
		for e in select:
			absUrl = e.absUrl("href")
			e.attr("href", "#")
			e.attr("target", "")

		select = self.doc.select("link");
		for e in select:
			absUrl = e.absUrl("href")
			e.attr("href", absUrl)
		
		select = self.doc.select("img");
		for e in select:
			e.attr("src", e.absUrl("src"))

	def _add_style(self):
		head = self.doc.head();
		head.append('<style type="text/css">input[type=checkbox] {width:20px; height:20px;}</style>')


	def get_html(self):	
		self._relative_to_absolute()	
		self._add_style()
		return self.doc.outerHtml()

class BlackDuckParser(AbstractParser):
	def __init__(self, query):
		self.query = query
		self.url = self.generate_url()
		self.stylesheets = ["https://code.openhub.net/style/csOhlohDesign.css"]
		AbstractParser.__init__(self)

	def generate_url(self):
		s = "http://code.openhub.net/search?s=%s&pp=0&fl=Java&ff=1&mp=1&ml=1&me=1&md=1&filterChecked=true"
		query = quote(self.query)

		return s % query

	def get_html(self, n=10):
		if self.doc:
			element = self.doc.select("#snippet_results").first()
			#print element, type(element)
			elements = self.doc.select(".snippetResult")
			
			table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="blackduck-%s"/></td><td>%s</td></tr></table>'
			self.snippets = [table_checkbox % (i, element.toString()) for i, element in enumerate(elements[:n])]
			# self.doc.body().html(element.toString())
			if self.snippets:
				self.doc.body().html("".join(self.snippets))
			else:
				self.doc.body().html("No Results!")

			
			return AbstractParser.get_html(self)

		return "No Results!"



class CodotaParser(AbstractParser):
	"""docstring for CodotaParser"""
	def __init__(self, query):
		self.query = query
		self.url = self.generate_url()
		AbstractParser.__init__(self)
	
	def generate_url(self):
		s = "http://www.codota.com/doSearchScenarios?searchQuery=%s&target=all"
		query = quote_plus(self.query)
		
		return s % query

	def get_code_snippets(self):
		elements = self.doc.select(".top-buffer")
		self.snippets = [element.outerHtml() for element in elements]

	def _relative_to_absolute(self):
		select = self.doc.select("a");
		for e in select:
			absUrl = e.absUrl("href")
			# e.attr("href", "#")
			e.attr("target", "")
			e.attr("data-href", e.attr("href"))
			e.attr("href", "#")
			e.attr("class", "popper")

		select = self.doc.select("link");
		for e in select:
			absUrl = e.absUrl("href")
			e.attr("href", absUrl)
		
		select = self.doc.select("img");
		for e in select:
			e.attr("src", e.absUrl("src"))

	def get_html(self, n=10):

		if self.doc:
			element = self.doc.select(".span11").first()
			#print element, type(element)
			
			head = self.doc.head();
			head.append('<style type="text/css">input[type=checkbox] {width:20px; height:20px;}</style>')

			elements = self.doc.select(".top-buffer")


			
			self.doc.body().attr("style", "display:block !important;")

			table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="codota-%s"/></td><td>%s</td></tr></table>'
			self.snippets = [table_checkbox % (i, element.toString()) for i, element in enumerate(elements[:n])]
			# self.doc.body().html(element.toString())
			self.doc.body().html("".join(self.snippets))

			self._relative_to_absolute()
			return self.doc.outerHtml()

		return "No Results!"


class CodotaParser2(AbstractParser):
	def __init__(self, query):
		self.query = query
		self.url = self.generate_url()	
		AbstractParser.__init__(self)

	def generate_url(self):
		s = "http://www.codota.com/doSearchScenarios?searchQuery=%s&source=typeahead&target=all"
		query = self.query
		
		return s % (query)

	def get_code_snippets(self):
		elements = self.doc.select(".snippet-row")
		self.snippets = [element.outerHtml() for element in elements]

	def get_html(self, n=1):

		if self.doc:
			#element = self.doc.select(".span11").first()
			#print element, type(element)
			elements = self.doc.select(".snippet-row")
			
			self.doc.body().attr("style", "display:block !important; margin-top:0px; padding-top:0px;")

			#table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="codota-%s"/></td><td>%s</td></tr></table>'
			self.snippets = [ element.toString() for i, element in enumerate(elements[:n])]
			# self.doc.body().html(element.toString())
			if self.snippets:
				self.doc.body().html("".join(self.snippets))
			else:
				self.doc.body().html("No Example")

			return AbstractParser.get_html(self)
		return "No Results!"



class GitSearchParser(AbstractParser):
	"""docstring for CodotaParser"""
	def __init__(self, query):
		self.query = query
		self.url = self.generate_url()
		
		
		AbstractParser.__init__(self)
	
	def generate_url(self):
		s = "http://localhost:5001/?q=%s"
		query = quote_plus(self.query)
		
		return s % (query)

	def get_code_snippets(self):
		elements = self.doc.select(".top-buffer")
		self.snippets = [element.outerHtml() for element in elements]

	def get_html(self, n=10):
		element = self.doc.select("#code_container").first()
		#print element, type(element)
		elements = self.doc.select(".snippet_item")
		
		# self.doc.body().html(element.toString())

		table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="gitsearch-%s"/></td><td>%s</td></tr></table>'
		self.snippets = [table_checkbox % (i, element.attr("style", "margin-left: 0").toString()) for i, element in enumerate(elements[:n])]
		# self.doc.body().html(element.toString())
		self.doc.body().html("".join(self.snippets))

		return AbstractParser.get_html(self)

class GitSearchParser2(GitSearchParser):
	def __init__(self, query):
		GitSearchParser.__init__(self, query)
		print self.url

	def generate_url(self):
		s = "http://localhost:5001/?q=%s&evaluation=T"
		query = quote_plus(self.query)
		
		return s % (query)

	def get_html(self, n=10):
		element = self.doc.select("#code_container").first()
		#print element, type(element)
		elements = self.doc.select(".snippet_item")
		
		# self.doc.body().html(element.toString())

		table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="gitsearch-%s"/></td><td>%s</td></tr></table>'
		self.snippets = [table_checkbox % (i, element.attr("style", "margin-left: 0").toString()) for i, element in enumerate(elements[:n])]
		# self.doc.body().html(element.toString())
		self.doc.body().html("".join(self.snippets))

		self._add_style()
		return self.doc.outerHtml()


import requests
import json
def get_json(url, n=10):
	r = requests.get(url)
	return r.json()
	#return json.loads(html)



# def get(url, n=10, json=False):
	
	

# 	# print "URL", url
# 	response = urllib2.urlopen(url)
# 	html = response.read()
# 	# print "HTML", html

# 	return html
		


class StackoverflowParser:
	def __init__(self, query, lang):
		self.query = query
		self.lang = lang
		self.template = None

	def _stackoverflow_links(self, n=3):	#기본 값이 10임..
		url = "https://api.stackexchange.com/2.2/search/advanced?todate=1370044800&order=desc&sort=relevance&q=%s&accepted=True&tagged=%s&site=stackoverflow" % (quote(self.query), self.lang)
		#API를 통해서 가져오기 때문에, 쿼리에 대한 결과를 json으로 돌려줄 수 있다. 즉 크로울러는 필요없다는 얘기지...
		print "SOParser", url
		json_obj = get_json(url)
		print "SO LINKS", len(json_obj["items"])
		links = [{"title": item["title"], "link": item["link"]} for item in json_obj["items"][:n]]
		#궁금점. 각 아이템별 타이틀과 링크들을 가져와서 리턴하는데, 왜 3개임?? (설마 탑 3임??)
		return links

	def _relative_to_absolute(self):
		select = self.template.select("a");
		for e in select:
			absUrl = e.absUrl("href")
			e.attr("href", "#")
			e.attr("target", "")

		select = self.template.select("link");
		for e in select:
			absUrl = e.absUrl("href")
			e.attr("href", absUrl)
		
		select = self.template.select("img");
		for e in select:
			e.attr("src", e.absUrl("src"))

		es = self.template.select("script[src]")
		for e in es:
			print e.attr("src")
			if "stub.en.js" in e.attr("src"):
				e.attr("src", "js/stub.en.js")
			else:
				e.attr("src", e.absUrl("src"))

	def _add_style(self):
		head = self.template.head();
		head.append('<style type="text/css">input[type=checkbox] {width:20px; height:20px;}</style>')
		head.append('<link rel="stylesheet" href="css/vs.css"><script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.7/highlight.min.js"></script>')
		head.append("""<script>$(document).ready(function() {
					  $('pre code').each(function(i, block) {
					    hljs.highlightBlock(block);
					  });
					});</script>""")
		# head.append('<script src="js/anon.js"> </script>')
		# head.append('<script src="js/snippet.js"> </script>')
		# head.append('<script src="js/valid.js"> </script>')
		# head.append('<script src="js/pretty.js"> </script>')


	def get_html(self, n=10):	#Stackoverflow Parser 시작점....!!!!!!!!
		elements = []
		so_question_list = self._stackoverflow_links(n)
		for so_question in so_question_list:
			print "Treat link", so_question["title"]
			#doc = Jsoup.connect(so_question["link"]).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get()
			doc = Jsoup.connect(so_question["link"]).userAgent(
				'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36').get()

			#TODO 여기부터..
			if not self.template: 	#template을 None으로 초기화 했기 때문에 template이 None이면 초기를 doc으로 설정
				self.template = doc
			elements.append({"e": doc.select(".accepted-answer .post-text"), "title": so_question["title"]})

		if self.template:
			table_checkbox = self._table()
			self.snippets = [table_checkbox % (element["title"], i, element["e"].outerHtml()) for i, element in enumerate(elements[:n])]
			
			self._relative_to_absolute()
			self.template.body().html("".join(self.snippets))
			self._add_style()

			return self.template.outerHtml()
		return "%s Results. Your search returned no matches." % (len(so_question_list))


	def _table(self):
		return """<table style="margin-bottom: 25px; border-bottom: 1px dashed black">
					<tr style="background-color: #C1CAE2"><td></td><td style="padding: 7px; font-size: 18px;">Title: <b>%s</b></td></tr>
					<tr><td style="padding:5px;"><input type="checkbox" name="stackoverflow-%s"/></td><td>%s</td></tr>
				  </table>"""

class GoogleFormParser(AbstractParser):
	def __init__(self, url):
		self.url = url
		self.doc = None
		AbstractParser.__init__(self)

if __name__ == '__main__':
	# #---------------------------
	#bd = BlackDuckParser(query)
	#print bd.get_html()

	#궁금점. BlackDuckParser와 CodotaParser를 이용해서도 실험이 진행이 되었었나..?

	#---------------------------
	# co = CodotaParser(query)
	# co.connect()
	# co.get_code_snippets()

	query = "send http request"
	p = StackoverflowParser(query, 'java')
	print p.get_html()
	app.run(host="0.0.0.0", debug=True, port=5003)

