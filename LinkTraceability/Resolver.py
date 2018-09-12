#!/usr/bin/env python
# -*- coding: utf-8 -*-

from GitSearch.Indexer.NewJavaParser import parse
from Searcher import Searcher
from collections import defaultdict, Counter


# Plan:
# 1. Get Stackoverflow code snippet by id
# 2. Parse Snippet and match against Github Index
# 3. Take the top K unique results and use the github source to deduce API of Stackoverflow code snippets
# 4. Present resolved links to the user

# Gracefully type deduction:
# 1. FQN, 2. PQN, 3. Non-QN

class Resolver:
	"""Snippet: Incomplete Code Snipet AST, Sources: Set of complete source code ASTs """
	def __init__(self, snippet, sources):
		self.snippet = parse(snippet, resolve=True)
		self.sources = sources

		self.class_PQN_to_FQN = defaultdict(list)
		self.method_PQN_to_FQN = defaultdict(lambda: { "fqn": [], "class": []})

	def _most_common(self, lst):
		l = Counter(lst).most_common(1)
		if l:
			return l[0][0]
		

	def class_PQN_to_FQN_map(self):
		# Create Map from Simple Type -> FQN Type 
		# For Example: Activity -> android.view.Activity
		for source in self.sources:
			for im in source["imports"]:
				simple_type = im.split(".")[-1]
				if simple_type != "*": # Discard wildcard imports 
					self.class_PQN_to_FQN[simple_type].append(im)

		for simple_type in self.class_PQN_to_FQN:
			
			self.class_PQN_to_FQN[simple_type] = self._most_common( self.class_PQN_to_FQN[simple_type] )
			#print "MOST COMMON",self.class_PQN_to_FQN[simple_type]

	def method_PQN_to_FQN_map(self):
		# Create Map from Typed Method Name -> FQN Method
		# Example: Menu.findItem ->  android.view.Menu.findItem
		for source in self.sources:
			for tmc in source["typed_method_call"]:

		
				class_name, method_name = tmc.split(".")[-2:]
				
				if class_name in self.class_PQN_to_FQN:

					# if method_name in "setRequestProperty":
					# 	print tmc, tmc.replace(class_name, self.class_PQN_to_FQN[class_name], 1)

					fqnm = tmc.replace(class_name, self.class_PQN_to_FQN[class_name], 1) # Replace only first occurrence
					
					#print "METHODPQN", tmc, method_name, fqnm, self.class_PQN_to_FQN[class_name]
					
					self.method_PQN_to_FQN[method_name]["fqn"].append(fqnm)
					self.method_PQN_to_FQN[method_name]["class"].append(self.class_PQN_to_FQN[class_name])
					

		for method_name in self.method_PQN_to_FQN:
			self.method_PQN_to_FQN[method_name]["fqn"] = self._most_common( self.method_PQN_to_FQN[method_name]["fqn"] )
			self.method_PQN_to_FQN[method_name]["class"] = self._most_common( self.method_PQN_to_FQN[method_name]["class"] )
			print "MOST COMMON Method",self.method_PQN_to_FQN[method_name]["fqn"]

	def resolve_snippet_variable_class_PQN_to_FQN(self):
		# Convert partial qualified named variables to fully qualified names
		# Example: {u'connection': u'java.net.URLConnection'}
		for var, var_type in self.snippet["var_type_map"].iteritems():
			# In case PQN is prefixed by some identifier
			if "." in var_type:
				parts = var_type.split(".")
				# Get Suffix
				suffixed_var_type = parts[-1]
				prefixed_var_type = parts[0]
				if suffixed_var_type in self.class_PQN_to_FQN:
					self.snippet["var_type_map"][var] = self.class_PQN_to_FQN[suffixed_var_type]
				elif prefixed_var_type in self.class_PQN_to_FQN:
					self.snippet["var_type_map"][var] = self.class_PQN_to_FQN[prefixed_var_type] + "." + ".".join(parts[1:])
			else:
				if var_type in self.class_PQN_to_FQN:
					self.snippet["var_type_map"][var] = self.class_PQN_to_FQN[var_type]
				
			if var not in self.snippet["var_type_map"]:
				print "Could not resolve: %s, %s" %  (var_type ,var)

	def resolve_snippet_unkown_variable_class(self):
		# Resolved untyped variables
		for umc in self.snippet["unresolved_method_calls"]:
			unresolved_var, method_name = umc.split(".")

			if method_name in self.method_PQN_to_FQN:
				print unresolved_var, method_name,  self.method_PQN_to_FQN[method_name]["class"]

				self.snippet["var_type_map"][unresolved_var] = self.method_PQN_to_FQN[method_name]["class"]
			else:
				"Could not resolve: %s" % unresolved_var

	def resolve_snippet_method_class(self):
		# Resolve method calls that are not attached to a variable
		# Example: Calling a super-method: startActivity()
		for method_name in self.snippet["methods_called"]:
			if method_name in self.method_PQN_to_FQN:

				print  "Methods Called", method_name, self.method_PQN_to_FQN[method_name]["fqn"]
			else:
				"Could not resolve: %s" % method_name

	def resolve_snippet_PQN_to_FQN(self):
		# Example: SystemClock -> android.os.SystemClock
		for uc in self.snippet["used_classes"]:
			if uc in self.class_PQN_to_FQN:
				print "SimpleType", uc, self.class_PQN_to_FQN[uc]




if __name__ == '__main__':
	
	def random_with_N_chars(n):
		import uuid; 
		return str(uuid.uuid4().get_hex().upper()[:n])

	snippet = """
	public class ActivityMain extends FragmentActivity implements OnRefreshListener {

	private SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
		mSwipeRefreshLayout.setOnRefreshListener(this);

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onRefresh() {
		Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}, 2000);
	}
}
	"""

	snippet = """View.OnClickListener mStartButtonListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			mChronometer.setBase(SystemClock.elapsedRealtime());
			mChronometer.start();
		}
	}; """

	snippet = """public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_item_search);
		SearchView searchView = (SearchView) searchItem.getActionView();


		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if(null!=searchManager ) {   
		 searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}
		searchView.setIconifiedByDefault(false);

		return true;
	} """

	snippet = """ 
		EditText myEditText = (EditText) findViewById(R.id.myEditText);
// Check if no view has focus:
View view = this.getCurrentFocus();
if (view != null) {  
InputMethodManager imm = (InputMethodManager)getSystemService(
      Context.INPUT_METHOD_SERVICE);
imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
}
	"""

	index = Searcher(snippet, "/Users/Raphael/Downloads/linkgithub") ##????
	sources = index.more_like_this()

	#print "Sources", sources
	r =	Resolver(snippet, sources)

	r.class_PQN_to_FQN_map()
	r.method_PQN_to_FQN_map()
	r.resolve_snippet_variable_class_PQN_to_FQN()
	r.resolve_snippet_unkown_variable_class()

	r.resolve_snippet_method_class()

	r.resolve_snippet_PQN_to_FQN()

	#print dict(r.class_PQN_to_FQN), dict(r.method_PQN_to_FQN)
	print dict(r.snippet)

	print r.class_PQN_to_FQN

	snippet = index.source
	print snippet
	r_type_map = defaultdict()
	for uc in sorted(r.snippet["positions"]["used_classes"], key=lambda x: len(x[0]), reverse=True):
		c, start, end = uc[0], uc[1][0], uc[1][1]
		if c in r.class_PQN_to_FQN:
			print "Replace", c
			#snippet = snippet[:start] + r.class_PQN_to_FQN[c] + snippet[end:]
			l = len(c)
			n = random_with_N_chars(l)

			r_type_map[n] = { "type":r.class_PQN_to_FQN[c], "start": start, "end": end }
			snippet = snippet[:start] + str(n) + snippet[end:]

	print snippet
	for uc in sorted(r.snippet["positions"]["variables"], key=lambda x: len(x[0]), reverse=True):
		c, start, end = uc[0], uc[1][0], uc[1][1]
		if c in r.snippet["var_type_map"]:
			print "Replace", c
			#snippet = snippet[:start] + r.class_PQN_to_FQN[c] + snippet[end:]
			l = len(c)
			n = random_with_N_chars(l)

			r_type_map[n] = { "type": r.snippet["var_type_map"][c], "start": start, "end": end }
			snippet = snippet[:start] + str(n) + snippet[end:]


	print snippet

	keys = r_type_map.keys()
	for key in sorted(keys, key=lambda x: len(x), reverse=True):
		print "REPLACE", key, r_type_map[key]
		snippet = snippet.replace(key, r_type_map[key]["type"], 1)

	print snippet





