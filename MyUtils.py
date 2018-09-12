#-*- coding: utf-8 -*-

import re, cgi, os, shutil, hashlib
from collections import defaultdict

english_stop_words = ['',"don't", 'year', 'your', 'without', 'via', 'these', 'would', 'because', 'near', 'ten', ' ', 'unlikely', "he'll", 'thus', 'meanwhile', 'younger', 'viz', 'yourselves', 'contains', 'downed', 'eleven', 'detail', 'much', 'appropriate', 'anybody', 'least', "why's", 'turn', 'example', 'same', 'after', "shouldn't", "you've", "we'd", 'ordered', 'a', "wouldn't", 'b', 'c', 'thanx', 'd', 'e', 'f', 'namely', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'the', 'newer', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'fifth', 'thank', 'y', 'z', 'faces', 'yours', 'novel', 'nine', 'got', 'good', 'empty', 'wish', "how's", 'besides', 'serious', 'others', 'elevenelse', 'area', "t's", 'making', 'need', 'its', 'often', 'onto', 'gone', 'aside', 'therefore', 'hardly', 'useful', 'ours\tourselves', 'furthers', 'young', 'downwards', 'smallest', 'myse', "c's", 'nowhere', 'sorry', 'provides', "you're", 'end', 'forty', "we'll", 'room', 'better', 'with', 'there', 'well', 'happens', 'tries', 'smaller', 'wanting', 'years', 'turns', 'number', 'tried', 'per', "when's", 'order', 'bottombut', 'went', 'considering', 'nothing', 'anyhow', 'specify', 'forth', 'ever', "we've", 'even', 'orders', 'presenting', 'thats', 'other', 'hundred', 'indicated', 'against', 'respectively', "a's", "isn't", "hadn't", 'howbeit', 'asked', 'top', 'too', 'indicates', 'have', 'ownpart', 'accordingly', 'furthering', 'particularly', 'thoroughly', 'awfully', "ain't", 'oursourselves', 'com', 'con', 'almost', 'lets', 'amoungst', 'upon', 'points', 'latterly', 'amongst', 'etc', 'whether', 'members', 'quite', 'all', 'always', 'new', 'took', 'below', 'already', 'everyone', "didn't", 'lest', 'shall', 'less', 'were', "we're", 'try', 'became', 'cause', 'around', 'and', 'today', 'working', 'saying', 'says', 'fifteen', 'whence', 'cry', 'followed', 'despite', 'any', 'opening', 'until', 'interested', 'formerly', 'gotten', 'thought', 'anywhere', 'wherein', "wasn't", "where's", 'worked', 'differently', 'let', 'state', 'thinks', 'welcome', 'fully', 'using', 'began', 'containing', 'want', 'each', 'specifying', 'himself', 'wanted', 'must', 'maybe', 'probably', 'another', 'furthered', 'two', 'facts', 'anyway', 'found', 'are', 'does', 'taken', 'came', 'where', 'gives', 'latest', 'think', 'entirely', '...', 'call', 'such', 'ask', 'describe', 'thing', 'through', "won't", 'anyways', 'becoming', 'goods', 'needing', 'had', 'cant', 'either', 'ours', 'things', 'yourself', 'has', 'newest', 'those', "they'd", 'seeming', 'given', 'last', "let's", 'might', 'whatever', 'longer', 'everywhere', 'name', 'overall', 'Class', 'full', 'next', 'away', 'asking', 'nearly', 'show', 'becausebecomebecomes', 'non', 'anything', 'nor', 'not', 'now', 'ends', 'hence', 'early', 'thoughts', 'unto', 'yes', 'was', 'yet', "i'll", 'way', 'inasmuch', 'what', 'furthermore', 'when', 'three', 'put', 'her', 'whoever', 'largely', 'far', 'truly', 'greater', 'case', "it'll", 'okay', 'evenly', 'give', 'having', 'grouping', 'hereupon', 'himse', 'noone', "she'd", 'youngest', "mustn't", 'couldnt', 'computer', 'ways', "she's", 'older', 'merely', 'more', 'unfortunately', 'lately', 'great', 'beings', 'parted', 'sides', 'interests', 'certain', 'small', 'before', 'tell', 'used', 'him', 'looks', 'his', 'shows', 'presented', 'few', 'pointing', 'consider', 'keeps', 'described', 'group', 'otherwise', "you'd", 'whither', 'thickv', "it's", 'kind', 'particular', 'opened', 'inner', 'done', 'both', 'most', 'important', 'twice', 'parting', 'outside', 'keep', "it'd", 'who', 'part', 'why', 'their', 'elsewhere', 'point', 'general', 'alone', 'along', 'ltd', 'amount', 'move', 'hereafter', 'saw', 'clear', 'also', 'say', 'enough', 'gets', 'differ', 'someone', 'third', 'mean', 'various', 'neither', 'latter', 'uses', 'further', 'front', 'sometime', 'been', 'mostly', 'hasnt', "couldn't", 'areas', 'appreciate', 'finds', "doesn't", 'you', 'afterwards', 'sure', 'going', 'bill', 'am', 'an', 'whose', 'former', 'mill', 'as', 'at', 'trying', 'turning', 'looking', "i've", 'be', 'ordering', 'comes', 'consequently', 'how', 'see', 'inward', 'by', 'whom', 'indicate', 'mine', 'sixty', 'contain', 'right', 'possible', 'co', 'somewhat', 'under', 'did', 'de', 'rooms', 'sometimes', 'backbebecame', 'do', 'itse', 'down', 'later', 'needs', 'which', 'ignored', 'eg', 'thereafter', 'regarding', 'et', 'she', 'never', 'take', 'ex', 'immediate', 'parts', 'relatively', "aren't", 'little', 'however', 'some', 'rather', 'for', 'back', 'greetings', 'states', 'getting', 'perhaps', 'just', 'over', 'six', 'thence', 'go', 'obviously', 'kept', 'although', 'selves', 'fify', 'he', 'showing', 'presents', 'hi', 'very', 'big', 'placed', 'therein', 'soon', 'thick', 'thanks', 'else', 'four', 'beside', 'usually', 'whereas', 'ie', 'if', "there's", 'likely', 'large', 'in', 'made', 'is', 'it', 'being', 'somebody', "weren't", 'asks', 'gave', 'opens', 'hello', 'whereby', 'secondly', 'become', 'longest', 'works', 'turned', 'whereupon', 'eight', 'theres', 'known', 'member', 'hopefully', 'man', 'everything', "can't", 'together', 'knows', 'twenty', 'side', 'may', 'seemed', 'within', 'could', 'knew', 'off', 'generally', 'places', 'alsoalthoughalwaysamamong', 'able', 'theirs', 'presumably', 'use', 'several', 'while', 'liked', 'second', 'that', 'high', 'find', 'than', 'me', 'different', 'insofar', 'regardless', 'downs', 'mr', 'follows', 'seriously', 'my', 'fill', 'plus', 'becomes', 'nd', 'present', 'since', 'problems', '..', 'no', 'behind', 'best', 'herse', 'hither', 'of', 'men', 'oh', 'somehow', 'ok', 'make', 'on', 'allows', 'brief', 'certainly', 'or', 'interesting', 'exactly', "c'mon", 'concerning', 'due', 'backs', 'about', "what's", 'somewhere', "haven't", 'above', 'downing', 'fire', 'they', "here's", 'grouped', 'qv', 'old', 'myself', 'herein', 'them', 'then', 'something', 'anyanyhowanyoneanythinganyway', 'pointed', 'rd', 're', 'thereby', 'highest', 'twelve', 'except', 'sincere', 'sub', 'nevertheless', 'fact', "hasn't", 'believe', 'seen', 'long', 'seem', 'sup', 'into', 'unless', 'so', 'apart', 'ought', 'necessary', 'though', 'one', 'thorough', 'many', 'actually', 'appear', 'face', 'definitely', 'th', 'oldest', 'associated', 'showed', 'to', 'open', "they've", 'but', 'willing', 'available', 'numbers', 'seven', 'mainly', 'zero', 'whenever', 'un', 'up', 'five', 'us', 'beforehand', 'this', 'felt', 'please', 'reasonably', 'look', 'thin', 'especially', 'once', 'sees', 'know', 'vs', 'higher', 'allow', 'que', 'doing', 'needed', 'changes', "that's", 'we', 'backing', 'interest', 'themselves', 'throughout', "he's", 'wants', 'wonder', 'every', "they're", 'cases', 'again', "he'd", 'indeed', 'ones', 'backed', 'whole', 'during', 'none', 'beyond', "she'll", 'seconds', 'problem', 'nobody', 'between', 'still', 'work', 'come', "they'll", 'itself', 'toward', 'among', 'anyone', 'following', "i'd", 'our', 'ourselves', "i'm", 'specified', 'out', 'across', 'seeing', 'moreover', 'causes', 'get', 'course', 'place', 'sensible', 'wherever', 'mrs', 'puts', 'help', 'ended', 'self', 'cannot', 'hereby', 'whereafter', 'first', 'thru', 'own', 'clearly', 'only', 'should', 'from', "you'll", 'like', 'goes', 'bottom', 'towards', 'regards', 'sent', 'ending', 'edu', 'herself', 'seems', 'thereupon', 'here', 'everybody', 'according', "shan't", 'hers', 'can', "who's", 'wells', 'said', 'value', 'inc', 'greatest', 'will', 'groups', 'instead', 'really', 'currently', 'corresponding', 'tends', 'normally']

def md5(fname):
    hash_md5 = hashlib.md5()
    with open(fname, 'rb') as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()


def rm_dup(path):
    """relies on the md5 function above to remove duplicate files"""
    if not os.path.isdir(path):  # make sure the given directory exists
        print('specified directory does not exist!')
        return

    md5_dict = defaultdict(list)
    for root, dirs, files in os.walk(path):  # the os.walk function allows checking subdirectories too...
        for filename in files:
            filepath = os.path.join(root, filename)
            file_md5 = md5(filepath)
            md5_dict[file_md5].append(filepath)
    for key in md5_dict:
        file_list = md5_dict[key]
        while len(file_list) > 1:
            item = file_list.pop()
            os.remove(item)
            print 'Removing item: ', item
    print('Done!')

def md5(fname):
    hash_md5 = hashlib.md5()
    with open(fname, 'rb') as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()

def copytree(src, dst, symlinks=True, ignore=None):
    for item in os.listdir(src):
        s = os.path.join(src, item)
        d = os.path.join(dst, item)
        if os.path.isdir(s):
            shutil.copytree(s, d, symlinks, ignore)
        else:
            shutil.copy2(s, d)

def java_files_from_dir(directory):
    javafiles = (os.path.join(dirpath, f)
        for dirpath, dirnames, files in os.walk(directory)
        for f in files if f.endswith('.java'))
    return javafiles

def arranging_query_regex(query):
    arranged_query = re.sub(r"(word):([A-Za-z0-9]+:)\s", '', query)
    arranged_query = re.sub(r"(word):([A-Za-z0-9]+:([A-Za-z0-9])+\s)", '', arranged_query)
    arranged_query = re.sub(r"(word):([A-Za-z0-9]+:.[^\s]+\s)", '', arranged_query)
    arranged_query = re.sub(r"(word):([^A-Za-z0-9]).+?(?=word|$|\n|\s|\t)", '', arranged_query)
    arranged_query = re.sub(r"(word):([A-Za-z0-9])+:([^A-Za-z0-9]).+?(?=word|$|\n|\s|\t)", '', arranged_query)	#2 번째 콜론 뒤 특수문자
    arranged_query = re.sub(r"(word):([A-Z-a-z0-9\s]).+?:(?=word|$|\n|\s|\t)\s", '', arranged_query)
    arranged_query = re.sub(r"(word):([-,!@#$%^&*();:'`/<>[\]]).+;\s", '', arranged_query)						#1 번째 콜론 뒤 특수문자 +
    arranged_query = re.sub(r"(word):([-,!@#$%^&*();:'`/<>[\]])(?=word|$|\n|\s|\t)", '', arranged_query)			#word:#
    arranged_query = re.sub(r"(word):([-,!@#$%^&*();:'`/<>[\]]):(?=word|$|\n|\s|\t)", '', arranged_query)		#word:/:
    arranged_query = re.sub(r"(word):([-,!@#$%^&*();:'`/<>[\]]|\s).+?(?=word|$|\n|\s|\t)", '', arranged_query)

    re.sub(r"  ", ' ', arranged_query)

    return arranged_query

def truncate_search_log():
    file_path = "/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/FrontEnd/static/search_log"
    file = open(file_path, 'w')
    file.truncate()
    file.close()

def write_search_log(content):
    import codecs
    file_path = "/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/FrontEnd/static/search_log"
    with codecs.open(file_path, mode='a', encoding='utf-8') as file:
        #encodedContent = unicode(content, encoding=u'euc-kr').encode(encoding='utf-8')
        file.write(content)  #encodedContent)
    file.close()

def read_search_log():
    file_path = "/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/FrontEnd/static/search_log"
    file = open(file_path, 'r')
    content = file.readline()
    file.close()
    return content

def get_mongo_connection():
    from com.mongodb import MongoClient
    from com.mongodb import DB
    from com.mongodb import DBCollection
    from com.mongodb import BasicDBObject
    from com.mongodb import DBObject
    mongoClient = MongoClient()
    db = mongoClient.getDB("Answers")
    return db

def camel_case_split(s):
    import re
    s = s.replace("_", " ")
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1 \2', s)
    s = re.sub('([a-z0-9])([A-Z])', r'\1 \2', s1).lower().replace("  ", " ").split()
    return s

def tokenize(s):
    import re
    return re.findall(r"[\w']+", s)

def unescape_html(s):
    from HTMLParser import HTMLParser
    p = HTMLParser()
    return p.unescape(s)

def get_code(s):
    code_snippets = []
    for item in s.split("</code>"):
        if "<code>" in item:
            code_tag = item [item.find("<code>")+len("<code>"):]
            if "." in code_tag and "(" in code_tag:
                code_tag = unescape_html(code_tag)
                code_snippets.append(code_tag)
    return code_snippets

def remove_code_block(s):
    from org.jsoup import Jsoup
    doc = Jsoup.parse(s)
    for element in doc.select("code"):
        element.remove()

    return doc.text()

def remove_html_tags(s):
    from org.jsoup import Jsoup
    return Jsoup.parse(s).text()

def so_text(s):
    """ Removes code tag and its content from SO body as well as all html tags"""
    from org.jsoup import Jsoup
    s = unescape_html(s)
    doc = Jsoup.parse(s)
    for element in doc.select("code"):
        element.remove()

    return doc.text()


java_stopwords = ["public","private","protected","interface",
                             "abstract","implements","extends","null","new",
                             "switch","case", "default" ,"synchronized" ,
                             "do", "if", "else", "break","continue","this",
                             "assert" ,"for","instanceof", "transient",
                             "final", "static" ,"void","catch","try",
                             "throws","throw","class", "finally","return",
                             "const" , "native", "super","while", "import",
                             "package" ,"true", "false", "enum"]
def so_tokenizer(s, remove_html=True, as_str=True):

    if remove_html:
        from org.jsoup import Jsoup
        s = unescape_html(s)
        doc = Jsoup.parse(s)
        s = doc.text()
    tokens = tokenize(s)
    tokens = set(tokens)

    res = []
    for token in tokens:
        res.extend( camel_case_split(token) )

        res.append(token.lower())

    res = [item for item in res if item not in java_stopwords]
    res = set(res)
    if as_str:
        return " ".join(res)
    else:
        return res


def variable_type_map(source):
    import re
    from collections import defaultdict

    type_map = defaultdict("")

    # Deduce types from method declaration
    parent = re.compile(r"\(\s*([^)]+?)\s*\)")
    for argumentlist in parent.findall(source):
        args = argumentlist.split(",")
        for arg in args:
            tokens = arg.split()
            if len(tokens) >= 2:
                type_map[tokens[-1]] = tokens[-2]

    # Deduce types from variable declartion

def md5(s):
    import hashlib
    return hashlib.md5(s).hexdigest()


import codecs
def read_file(file_path):
    with codecs.open(file_path, "r", encoding='utf-8') as file:
        return file.read()


def write_file(file_path, content):
    with codecs.open(file_path, mode='a', encoding='utf-8') as file:
        file.write(content + "\n")
    file.close()

def over_write_file(file_path, content):
    with open(file_path, "w") as f:
        f.write(content)


def get_inline_and_block_code(s):
    code_snippets = set()
    for item in s.split("</code></pre>"):
        if "<pre><code>" in item:
            code_tag = item [item.find("<pre><code>")+len("<pre><code>"):]
            if "." in code_tag and "(" in code_tag:
                code_tag = unescape_html(code_tag)
                code_snippets.add(code_tag.strip())
    return code_snippets

def remove_unified_stop_lists(unified_query):
    # TODO : Stop Keyword in here!
    stop_keywords = [
        'unresolved_method_calls:ex.printStackTrace',

        'typed_method_call:Log.e',
        'typed_method_call:Log.i',
        'typed_method_call:Log.d',
        'typed_method_call:System.exit',
        'typed_method_call:AND',

        'typedmethodcall:Log.e',
        'typedmethodcall:Log.i',
        'typedmethodcall:Log.d',
        'typedmethodcall:System.exit',
        'typedmethodcall:AND',

        'used_classes:void',
        'used_classes:Global',
        'used_classes:boolean',
        'used_classes:String',
        'used_classes:int',
        'used_classes:char',
        'used_classes:float',
        'used_classes:double',
        'used_classes:byte',
        'used_classes:long',
        'used_classes:T',
        'used_classes:short',
        'used_classes:System',
        'used_classes:AND',

        'usedclasses:void',
        'usedclasses:Global',
        'usedclasses:boolean',
        'usedclasses:String',
        'usedclasses:int',
        'usedclasses:char',
        'usedclasses:float',
        'usedclasses:double',
        'usedclasses:byte',
        'usedclasses:long',
        'usedclasses:T',
        'usedclasses:short',
        'usedclasses:System',
        'usedclasses:AND',

        'methods_called:write',
        'methods_called:close',
        'methods_called:from',
        'methods_called:close',
        'methods_called:mkdir',
        'methods_called:exists',
        # 'methods_called:println',
        # 'methods_called:print',
        'methods_called:size',
        'methods_called:compareTo',
        'methods_called:AND',

        'methodscalled:write',
        'methodscalled:close',
        'methodscalled:from',
        'methodscalled:close',
        'methodscalled:mkdir',
        'methodscalled:exists',
        # 'methods_called:println',
        # 'methods_called:print',
        'methodscalled:size',
        'methodscalled:compareTo',
        'methodscalled:AND',


        # 'unresolved_method_calls:out.println',
        'methods:write',
        'methods:main',

        'methods:OR',
        'methods:AND',
        'methods:NOT',

        'used_classes:CREATE'
        'used_classes:AND',
        'used_classes:NOT',
        'used_classes:OR',
        'used_classes:FROM',
        'used_classes:WHERE',
        'used_classes:SELECT',
        'used_classes:JOIN',
        'used_classes:ON',
        'used_classes:REFERENCES',
        'used_classes:GOTO',
        'used_classes:GRANT',
        'used_classes:GROUP',
        'used_classes:HAVING',
        'used_classes:BY',
        'used_classes:RESTRICT',
        'used_classes:DISTINCT',
        'used_classes:REVOKE',
        'used_classes:RETURN',
        'used_classes:SCHEMA',
        'used_classes:DROP',
        'used_classes:ORDER',

        'usedclasses:CREATE'
        'usedclasses:AND',
        'usedclasses:NOT',
        'usedclasses:OR',
        'usedclasses:FROM',
        'usedclasses:WHERE',
        'usedclasses:SELECT',
        'usedclasses:JOIN',
        'usedclasses:ON',
        'usedclasses:REFERENCES',
        'usedclasses:GOTO',
        'usedclasses:GRANT',
        'usedclasses:GROUP',
        'usedclasses:HAVING',
        'usedclasses:BY',
        'usedclasses:RESTRICT',
        'usedclasses:DISTINCT',
        'usedclasses:REVOKE',
        'usedclasses:RETURN',
        'usedclasses:SCHEMA',
        'usedclasses:DROP',
        'usedclasses:ORDER',

        'code_hints:NULL',
        'code_hints:UNIQUE',
        'code_hints:PRIMARY',
        'code_hints:NOT',
        'code_hints:KEY',
        'code_hints:OR',
        'code_hints:FOUR',
        'code_hints:AND',

        'codehints:NULL',
        'codehints:UNIQUE',
        'codehints:PRIMARY',
        'codehints:NOT',
        'codehints:KEY',
        'codehints:OR',
        'codehints:FOUR',
        'codehints:AND',

        'typed_method_call:String[].getClass'
        
        'typedmethodcall:String[].getClass'

    ]

    splited_unified_query = unified_query.split()
    query = ""

    for keyword in splited_unified_query:
        if keyword not in stop_keywords:
            query += keyword + " "

    return query

if __name__ == '__main__':
    s = """word:BufferedReader.readLine word:StringBuffer.toString word:BufferedReader.close word:StringBuffer.append word:OutputStreamWriter.write word:URL.openStream word:OutputStreamWriter.close word:File.toString word:InputStreamReader word:InputStream word:ArchiveFile word:StringBuffer word:boolean word:FileOutputStream word:BufferedReader word:OutputStreamWriter word:InputStreamReader word:ArchiveFile word:StringBuffer word:FileOutputStream word:BufferedReader word:OutputStreamWriter word:publishPage word:add word:getMessage word:readLine word:openStream word:toString word:close word:write word:append word:archiveFiles.add word:e.getMessage word:errors.add word:out.println word::
     word:BufferedReader.readLine word:BufferedReader.close word:String.trim word:InputStreamReader word:URL word:BufferedReader word:InputStreamReader word:URL word:BufferedReader word:getWebpage word:printStackTrace word:toLowerCase word:readLine word:openStream word:trim word:close word:startsWith word:e.printStackTrace word:e2.printStackTrace word:http:// 
word:File.listFiles word:String.equals word:ZipOutputStream.write word:BufferedInputStream.close word:BufferedInputStream.read word:File.getName word:ZipOutputStream.putNextEntry word:File.isDirectory word:ZipEntry.getName word:FileInputStream.close word:byte word:ZipEntry word:FileInputStream word:int word:BufferedInputStream word:File word:ZipEntry word:FileInputStream word:BufferedInputStream word:zip word:zip word:getName word:putNextEntry word:read word:equals word:write word:close word:listFiles word:isDirectory word:./ 

"""
    a = arranging_query_regex(s)

    pass

    # print camel_case_split("myFucking_CamelCase.7.9/66")
    # s = """
    # 	View.OnClickListener mStartButtonListener = new OnClickListener() {
    # 	@Override
    # 	public void onClick(View arg0) {
    # 		mChronometer.setBase(SystemClock.elapsedRealtime());
    # 		mChronometer.start();
    # 	}
    # };
    # """
    # print tokenize(s)
    # print so_tokenizer(s, remove_html=False)
# 	print camel_case_split("ABCWordDEF")
# 	print camel_case_split("camel_case_split")
# 	print unescape_html("&lt;")

# 	s = """Hello dfsdf <code>Integer.toString(  )   	</code>
# 			<pre><code>String.valueOf ()</code></pre>
# 	"""
# 	print get_inline_and_block_code(s)

# 	print "-"*10
# 	print get_code(s)

# 	print so_text(s)

# 	print md5(s)
# 	# from java.sql import ResultSet
# 	# conn = get_db_connection()
# 	# stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
# 	# rs = stmt.executeQuery("SELECT * FROM posts LIMIT 1")
# 	# rs.next()
# 	# print rs.getString("Title")

# 	source = """<div class="post-text" itemprop="text">
# <p>You can use an <a href="http://developer.android.com/reference/java/net/Authenticator.html"><code>Authenticator</code></a>. For example:</p>

# <pre class="lang-java prettyprint prettyprinted"><code><span class="typ">Authenticator</span><span class="pun">.</span><span class="pln">setDefault</span><span class="pun">(</span><span class="kwd">new</span><span class="pln"> </span><span class="typ">Authenticator</span><span class="pun">()</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
#  </span><span class="lit">@Override</span><span class="pln">
#         </span><span class="kwd">protected</span><span class="pln"> </span><span class="typ">PasswordAuthentication</span><span class="pln"> getPasswordAuthentication</span><span class="pun">()</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
#          </span><span class="kwd">return</span><span class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span class="typ">PasswordAuthentication</span><span class="pun">(</span><span class="pln">
#    </span><span class="str">"user"</span><span class="pun">,</span><span class="pln"> </span><span class="str">"password"</span><span class="pun">.</span><span class="pln">toCharArray</span><span class="pun">());</span><span class="pln">
#         </span><span class="pun">}</span><span class="pln">
# </span><span class="pun">});</span></code></pre>

# <p>This sets the default <code>Authenticator</code> <code>Collections.binarySearch</code> and will be used in <em>all</em> requests. Obviously the setup is more involved when you don't need credentials for all requests or a number of different credentials, maybe on different threads.</p>

# <p>Alternatively you can use a <a href="http://developer.android.com/reference/org/apache/http/impl/client/DefaultHttpClient.html"><code>DefaultHttpClient</code></a> where a GET request with basic HTTP authentication would look similar to:</p>

# <pre class="lang-java prettyprint prettyprinted"><code><span class="typ">HttpClient</span><span class="pln"> httpClient </span><span class="pun">=</span><span class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span class="typ">DefaultHttpClient</span><span class="pun">();</span><span class="pln">
# </span><span class="typ">HttpGet</span><span class="pln"> httpGet </span><span class="pun">=</span><span class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span class="typ">HttpGet</span><span class="pun">(</span><span class="str">"http://foo.com/bar"</span><span class="pun">);</span><span class="pln">
# httpGet</span><span class="pun">.</span><span class="pln">addHeader</span><span class="pun">(</span><span class="typ">BasicScheme</span><span class="pun">.</span><span class="pln">authenticate</span><span class="pun">(</span><span class="pln">
#  </span><span class="kwd">new</span><span class="pln"> </span><span class="typ">UsernamePasswordCredentials</span><span class="pun">(</span><span class="str">"user"</span><span class="pun">,</span><span class="pln"> </span><span class="str">"password"</span><span class="pun">),</span><span class="pln">
#  </span><span class="str">"UTF-8"</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">false</span><span class="pun">));</span><span class="pln">

# </span><span class="typ">HttpResponse</span><span class="pln"> httpResponse </span><span class="pun">=</span><span class="pln"> httpClient</span><span class="pun">.</span><span class="pln">execute</span><span class="pun">(</span><span class="pln">httpGet</span><span class="pun">);</span><span class="pln">
# </span><span class="typ">HttpEntity</span><span class="pln"> responseEntity </span><span class="pun">=</span><span class="pln"> httpResponse</span><span class="pun">.</span><span class="pln">getEntity</span><span class="pun">();</span><span class="pln">

# </span><span class="com">// read the stream returned by responseEntity.getContent()</span></code></pre>

# <p>I recommend using the latter because it gives you a lot more control (e.g. method, headers, timeouts, etc.) over your request.</p>
#     </div>"""

# 	print so_tokenizer(source)
