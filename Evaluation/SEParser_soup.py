# import Jsoup
from urllib import quote, quote_plus
#from org.jsoup import Jsoup
import requests
#from BeautifulSoup import BeautifulSoup
from bs4 import BeautifulSoup

from urlparse import urljoin

def html(html):
	return [BeautifulSoup(html, "html.parser")]


class AbstractParser:
	def __init__(self):
		#self.url = url
		self.soup = None
		self.doc = None
		self.snippets = None
		self.connect()

	def connect(self):
		try:
			r = requests.get(self.url, timeout=25, headers={"user-agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36"})
			print "Got response", type(r.text)
			self.soup = BeautifulSoup(r.text, "html.parser")
			self.doc = self.soup.html #Jsoup.connect(self.url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").timeout(25000).get()
		except Exception as e:
			print "Download - Error: ", self.url, e
		

	def _relative_to_absolute(self):
		select = self.doc.find_all("a");
		for e in select:
			#absUrl = e.absUrl("href")
			e["href"] = "#"
			e["target"] = ""

		select = self.doc.find_all("link");
		for e in select:
			#print "LINK", self.url, e["href"], urljoin( self.url, e["href"])
			e["href"] =  urljoin( self.url, e.get("href"))
		
		select = self.doc.find_all("img");
		for e in select:
			e["src"] =  urljoin( self.url, e.get("src"))

	def _add_style(self):
		style_tag = self.soup.new_tag("style", type="text/css")
		style_tag.string = "input[type=checkbox] {width:20px; height:20px;}"
		self.doc.head.append(style_tag)


	def get_html(self):	
		self._relative_to_absolute()	
		self._add_style()
		return unicode(self.doc)

class BlackDuckParser(AbstractParser):
	def __init__(self, query):
		self.query = query
		self.url = self.generate_url()
		AbstractParser.__init__(self)
		#self.get_html()

	def generate_url(self):
		s = "http://code.openhub.net/search?s=%s&pp=0&fl=Java&ff=1&mp=1&ml=1&me=1&md=1&filterChecked=true"
		query = quote(self.query)
		return s % query

	def get_html(self, n=10):
		if self.doc:
			#element = self.doc.select("#snippet_results").first()
			#print element, type(element)
			
			self._relative_to_absolute()
			elements = self.doc.find_all("div", class_="snippetResult")
			
			table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="blackduck-%s"/></td><td>%s</td></tr></table>'
			self.snippets = [table_checkbox % (i, unicode(element) ) for i, element in enumerate(elements[:n])]
			# self.doc.body().html(element.toString())
			if self.snippets:
				self.doc.body.contents = html("".join(self.snippets))
			else:
				self.doc.body.string = "No Results!"

			
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
		select = self.doc.find_all("a");
		for e in select:
			#absUrl = e.absUrl("href")
			# e.attr("href", "#")
			e["target"] = ""
			e["data-href"] = e.get("href")
			e["href"] = "#"
			e["class"] = "popper"

		select = self.doc.find_all("link");
		for e in select:
			#print "LINK", self.url, e["href"], urljoin( self.url, e["href"])
			e["href"] =  urljoin( self.url, e.get("href"))
		
		select = self.doc.find_all("img");
		for e in select:
			e["src"] =  urljoin( self.url, e.get("src") )


	def get_html(self, n=10):
		if self.doc:
			self._relative_to_absolute()
			style_tag = self.soup.new_tag("style", type="text/css")
			style_tag.string = "input[type=checkbox] {width:20px; height:20px;}"
			self.doc.head.append(style_tag)
			
			elements = self.doc.find_all(class_="top-buffer")

			self.doc.body["style"] =  "display:block !important;"
			table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="codota-%s"/></td><td>%s</td></tr></table>'
			self.snippets = [table_checkbox % (i, unicode(element) ) for i, element in enumerate(elements[:n])]
			# self.doc.body().html(element.toString())
			self.doc.body.contents = html("".join(self.snippets))
			return unicode(self.doc) #self.doc.outerHtml()
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
			elements = self.doc.find_all(class_="snippet-row")
			

			self.doc.body["style"] =  "display:block !important; margin-top:0px; padding-top:0px;"

			#table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="codota-%s"/></td><td>%s</td></tr></table>'
			self.snippets = [ unicode(element) for i, element in enumerate(elements[:n])]
			# self.doc.body().html(element.toString())
			if self.snippets:
				self.doc.body.contents = html("".join(self.snippets))
			else:
				self.doc.body.string = "No Example"

			return AbstractParser.get_html(self)
		return "No Results!"



class GitSearchParser(AbstractParser):
	"""docstring for CodotaParser"""
	def __init__(self, query):
		self.query = query
		self.url = self.generate_url()
		
		
		AbstractParser.__init__(self)
	
	def generate_url(self):
		s = "http://149.202.46.177:5001/?q=%s"
		query = quote_plus(self.query)
		print s % (query)
		return s % (query)

	def get_code_snippets(self):
		elements = self.doc.find_all(class_="top-buffer")
		self.snippets = [element.outerHtml() for element in elements]

	def get_html(self, n=10):
	
		#self._relative_to_absolute()
		elements = self.doc.body.find_all(class_="snippet_item")
		
		# self.doc.body().html(element.toString())

		table_checkbox = '<table><tr><td style="padding:5px;"><input type="checkbox" name="gitsearch-%s"/></td><td>%s</td></tr></table>'
		self.snippets = [] 
		for i, element in enumerate(elements[:n]):
			element["style"]="margin-left: 0"

			# print "----------"*20
			# print unicode(element)
			self.snippets.append( table_checkbox % (i, unicode(element) ) )
		# self.doc.body().html(element.toString())
		#print type(self.doc.body.contents)
		self.doc.body.contents = html("".join(self.snippets))

		
		# c = BeautifulSoup("".join(self.snippets), "html.parser")
		# print type(c.contents), len(c.contents)
		# self.doc.body.contents = BeautifulSoup("".join(self.snippets), "html.parser").contents
		

		return AbstractParser.get_html(self)

class GitSearchParser2(GitSearchParser):
	def __init__(self, query):
		GitSearchParser.__init__(self, query)
		print self.url

	def generate_url(self):
		s = "http://localhost:5001/?q=%s&evaluation=T"
		query = quote_plus(self.query)
		return s % (query)

class GoogleFormParser(AbstractParser):
	def __init__(self, url):
		self.url = url
		self.doc = None
		AbstractParser.__init__(self)


if __name__ == '__main__':
	query = "remove HTML tags from a String"
	#---------------------------
	# bd = CodotaParser(query)
	# print bd.get_html()

	git = GitSearchParser(query)
	print git.get_html(5)

	#bd = BlackDuckParser(query)
	#print bd.get_html()
	#---------------------------
	# co = CodotaParser(query)
	# co.get_code_snippets()

