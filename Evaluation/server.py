from flask import Flask, Markup, render_template, send_from_directory, request, url_for, redirect, session, jsonify
#from SEParser import BlackDuckParser, CodotaParser, CodotaParser2, GitSearchParser, GitSearchParser2, GoogleFormParser
import pickle
import os.path
from uuid import uuid4
from time import time
import requests

from werkzeug.contrib.cache import SimpleCache, FileSystemCache
#cache = SimpleCache()
cache = FileSystemCache("./cache")

# from OpenSSL import SSL
# context = SSL.Context(SSL.SSLv23_METHOD)
# context.use_privatekey_file('yourserver.key')
# context.use_certificate_file('yourserver.crt')

# # Generate SSL Keys
# # from werkzeug.serving import make_ssl_devcert
# # make_ssl_devcert('/tmp', host='localhost')

def question_to_query(question):
	return question.lower().replace("how to", "").replace("how do i", "").replace("how can i", "").replace("what is the best way to", "").replace("in java", "").replace("?", "").replace(" a ", " ").replace(" from ", " ").replace(" as ", " ").replace("'s'", "").replace(" the ", " ").replace("how ", " ").replace("  ", " ").strip()

def to_q(path):
	from urllib import quote_plus

	path = question_to_query(path)
	return quote_plus(path)

def timeit(method):

    def timed(*args, **kw):
        ts = time()
        result = method(*args, **kw)
        te = time()

        print '%r (%r, %r) %2.2f sec' % \
              (method.__name__, args, kw, te-ts)
        return result

    return timed



import hashlib
def md5(s):
	return hashlib.md5(s).hexdigest()

from functools import wraps
def cached(timeout=3600 * 24 * 14):
	def decorator(f):
		@wraps(f)
		def decorated_function(*args, **kwargs):
			cache_key = "%s" % request.url
			#print "cache_key", cache_key
			
			rv = cache.get(cache_key)
			
			if rv is not None:
				print "HIT cache", cache_key, md5(cache_key)
				return rv
			rv = f(*args, **kwargs)
			
			if ( isinstance(rv, str) or isinstance(rv, unicode) ) and "No Results!" not in rv:
				print "CACHE Key:", cache_key, timeout
				cache.set(cache_key, rv, timeout=timeout)
			else:
				print "Cache: Got no results"
			
			return rv
		return decorated_function
	return decorator


app = Flask(__name__, static_folder='static')
app.secret_key = 'A0Zr98j/3yX Rdsfsdasd!!mN]LWX/,?RT'
app.jinja_env.globals.update(to_q=to_q)

def generate_session(num=15):
	def generate_questions(num):
		return {"q%s" % i:[] for i in range(0,num)}

	d = {"study11": generate_questions(num), "study12": generate_questions(5), "study2": generate_questions(num), "v": [], "type": "n", "version":"2"}
	
	session["user"] = uuid4()
	session.permanent = True

	print "Generate new user"
	with open("Evaluation/participants/%s.pickle" % session["user"], "w") as f:
		pickle.dump(d, f)
	
	return "No User"

def load_session():
	user = get_user()
	with open("Evaluation/participants/%s.pickle" % user, "r") as f:
		return pickle.load(f)

def store_session(d):
	user = get_user()
	print "Store Data", d
	with open("Evaluation/participants/%s.pickle" % user, "w") as f:
		pickle.dump(d, f)

def generate_id(req):
	user_id = "%s-%s" % (req.remote_addr, uuid4())
	return user_id

def get_user():
	if "user" in session:
		path = "Evaluation/participants/%s.pickle" % session["user"]

		if os.path.isfile(path):
			return session["user"]
		print "User session does not exists anymore!"

	
	generate_session()
	return session["user"]



#Read/convert an InputStream to a String, validate xml against xsd, 
study11_queries = [ "How to add an image to a JPanel?",
"How to generate a random alpha-numeric string?",
"How to save the activity state in Android?" ,
"How do I invoke a Java method when given the method name as a string?",
#"How do I split a string with any whitespace chars as delimiters?",
# "What is the best way to validate an XML file against an XSD file?",
#"How do I pretty print a XML file in Java?",
#"How do I time a method's execution in Java?",
#"How do I remove repeated elements from ArrayList?",
"How to remove HTML tags from a String?",
"How to get the path of a running JAR file?",
#"How do I get a platform-independent new line character?",
"How do I get a file's MD5 Checksum in Java",
"How do I load a properties file from Java package",
"How can I play sound in Java?",
"What is the best way to download a file from a server using SFTP?"]


@app.route("/", methods=["GET"])
def index():

	participant_type = request.form['participant'] if 'participant' in request.form else "None"
	print "Participant Type: ", participant_type
	d = load_session()
	d["type"] = participant_type
	store_session(d)
	
	return redirect(url_for("study11"))

	# if request.method == 'POST':

	# 	participant_type = request.form['participant'] if 'participant' in request.form else "None"
	# 	print "Participant Type: ", participant_type
	# 	d = load_session()
	# 	d["type"] = participant_type
	# 	store_session(d)

	# 	return redirect(url_for("study11"))
		
	# return render_template("index.html")


#@app.route("/", methods=['GET'])
@app.route("/study11", methods=['GET'])
def study11(name=None):

	query = request.args.get('q', "0") #if request.args.get('q') else "0"

	if query.isdigit():
		query = int(query)

		if 0 <= query < len(study11_queries):
			print "We render the page again!"
			return render_template("study11.html", i=query, queries=study11_queries)
		elif query == len(study11_queries):
			return redirect(url_for('study2'))

	return render_template("index.html")

@app.route("/study11", methods=['POST'])
def study11post():
	print "Pre-POST"
	r = request.get_json(force=True)
	print "GOT JSON", r
	d = load_session()
	print "Session", d

	# Store Answers
	d["study11"][r["q"]] = r["a"]
	# Store traversed questions
	d["v"].append("study11-%s" % r["v"])

	store_session(d)

	return jsonify(ok="True")


study12_queries = [
{	
	"title": "Download and save a file from internet",  
	"description": "There is an online file (such as http://www.example.com/information.asp) I need to grab and save to a directory. I know there are several methods for grabbing and reading online files (URLs) line-by-line, but is there a way to just download and save the file using Java?"
},
{
	"title": "Send an email by Java application using Gmail/ Yahoo/ Hotmail", 
	"description": "Is it possible to send an email from my Java application using a GMail account? I have configured my company mail server with Java app to send email, but that's not going to cut it when I distribute the application. Answers with any of using Hotmail, Yahoo or GMail are acceptable."
},
{
	"title": "Get OS-level system information",
	"description": "I'm currently building a Java app that could end up being run on many different platforms, but primarily variants of Solaris, Linux and Windows.\nHas anyone been able to successfully extract information such as the current disk space used, CPU utilisation and memory used in the underlying OS? What about just what the Java app itself is consuming?\nPreferrably I'd like to get this information without using JNI."
},
{
	"title": "Resize an image in java",
	"description": "I need to resize PNG, JPEG and GIF files. How can I do this using Java?"
},
{
	"title": "Generating an AST using the JDT framework",
	"description": 	"I am currently working with eclipse AST to generate source code. Other than in most examples, I am generating the source code from scratch and in a stand-alone application, as opposed to an eclipse plug-in. Any experiences in generating AST from scratch ?"
}
]

@app.route("/study12", methods=['GET', 'POST'])
def study12(name=None):

	if request.method == 'POST':
		r = request.get_json(force=True)
		print "GOT JSON", r
		d = load_session()

		d["study12"][r["q"]] = r["a"]
		d["v"].append("study12-%s" % r["v"])

		store_session(d)

		return jsonify(ok="True")
	else:
	
		n = request.args.get('n', "0")
		query = request.args.get('q')

		if n.isdigit():
			n = int(n)

		if query:
			
			return render_template("study12.html", queries=study12_queries, query = query, i=n)

		if n == len(study12_queries):
			return "<h1>Thank you for your participation!</h1>"
			

		return render_template("search.html", queries=study12_queries, i = n )


study2_queries = [
{ "title": "How to create a json object using jackson?", "lang": "java"},
{ "title": "How do I a get unique device hardware id for Android?", "lang": "android"},
{ "title": "How do I read pdf file and write it to outputStream?", "lang": "java"},
#{ "title": "How to create a Path from String in Java7?", "lang": "java"},
#{ "title": "How to convert int to double Java?", "lang": "java"},
{ "title": "How to play a mp3 file?", "lang": "java"},
{ "title": "How to Customize Toast In Android", "lang": "android"},
{ "title": "How to read data from XLS Excel file in java", "lang": "java"},
#{ "title": "How to sort file names in ascending order?", "lang": "java"},
#{ "title": "How to download multiple files into a local directory using apache commons FTP library?", "lang": "java"},
{ "title": "How to read pkcs12 certificate information?", "lang": "java"},
{ "title": "How to upload local image using the Android Facebook SDK 3.0?", "lang": "android"},
#{ "title": "How play an avi video file in java swing", "lang": "java"},
{ "title": "How to convert an image into byte array and into string", "lang": "java"},
{ "title": "How to get current location in Android", "lang": "android"}, 

#{ "title": "How to remove only trailing spaces of a string in Java and keep leading spaces?", "lang": "java"}
]

@app.route("/study2", methods=['GET', 'POST'])
def study2():
	if request.method == 'POST':
		
		r = request.get_json(force=True)
		
		d = load_session()

		d["study2"][r["q"]] = r["a"]
		d["v"].append("study2-%s" % r["v"])

		store_session(d)

		return jsonify(ok="True")
	else:
		query = request.args.get('q', "0") #if request.args.get('q') else "0"

		if query.isdigit():
			query = int(query)

			if 0 <= query < len(study2_queries):
				return render_template("study2.html", i=query, queries=study2_queries )
			elif query == len(study2_queries):
				return redirect(url_for('study12'))

@app.route("/questionaire")
def questionaire():
	form_url = "https://docs.google.com/forms/d/1OpCSatjYZyzlMMS7Iv9cH32SA1JyC-rH2E3GBEyknWU/viewform?embedded=true"
	
	p = GoogleFormParser(form_url)
	return p.get_html()




def get(endpoint, q_id, param="q", param2=None):
	
	parser_url = "http://localhost:5003/%s?%s=%s" % (endpoint, param, q_id)

	r = requests.get(parser_url, timeout=60)

	return r.text

def get_stackoverflow(query, lang):
	parser_url = "http://localhost:5003/stackoverflow?q=%s&lang=%s" % (query, lang)

	r = requests.get(parser_url, timeout=25)

	return r.text

@app.route("/blackduck")
@cached()
def blackduck():
	query = request.args.get('q')


	if query:

		print "----"*10
		print "Blackduck: Retrieve HTML from BLACKDUCK"
		
		return get("blackduck", query)


@app.route("/codota")
@cached()
def codota():
	query = request.args.get('q')


	if query:

		return get("codota", query)

# Codota Snippet Proxy
@app.route("/doSearchScenarios")
@cached()
def codotaDetail():
	q = request.args.get('searchQuery')

	if q:
		return get("doSearchScenarios", q, "searchQuery")

	

@app.route("/gitsearch")
@cached()
def gitsearch():
	query = request.args.get('q')

	if query:
		return get("gitsearch", query)


@app.route("/gitsearcheval")
@cached()
def gitsearcheval():
	query = request.args.get('q')

	if query:
		return get("gitsearcheval", query)

@app.route("/stackoverflow")
@cached()
def stackoverflow():
	query = request.args.get('q')

	lang = request.args.get("lang", "java")
	print "Lang", lang
	if query:
		return get_stackoverflow(query, lang)

@app.route("/test")
def test():
	# p = BlackDuckParser("send http request")
	# p.connect()
	# p.get_code_snippets()

	# c = CodotaParser("send http request")
	# c.connect()
	# c.get_code_snippets()

	# stylesheets = []
	# stylesheets.extend(p.stylesheets)
	# stylesheets.extend(c.stylesheets)

	return render_template("study12.html")

@app.route('/<path:file_path>')
def static_proxy(file_path):

	# send_static_file will guess the correct MIME type
	return send_from_directory(app.static_folder, file_path)

if __name__ == "__main__":
	app.run(debug=True, port=80) #host="0.0.0.0", 

