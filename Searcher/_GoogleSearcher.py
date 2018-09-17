#!/usr/bin/env python
# -*- coding: utf-8 -*-

#Description :

import requests, requests.utils, pickle
from os.path import join, isfile
import re

class GoogleSearcher:
	def __init__(self):
		self.cookie_path = join("/tmp", ".google-cookie")
		self.session = None
		self.load_session()

	def save_session(self):
		with open(self.cookie_path, 'w') as f:
			pickle.dump(requests.utils.dict_from_cookiejar(self.session.cookies), f)

	def load_session(self):
		try:
			print "Load cookie from %s" % self.cookie_path
			with open(self.cookie_path) as f:
				cookies = requests.utils.cookiejar_from_dict(pickle.load(f))
				self.session = requests.Session()
				self.session.cookies = cookies
		except:
			print "Created a cookie. Issue the request again"
			self.session = requests.Session()

	def google_request(self, query):
		query = {"query": query.replace(" ", "+")}
		url = "http://www.google.com/search?hl=en&q=%(query)s&btnG=Google+Search&inurl=https" % (query)
		print "Current Query : ", query
		print "Current Target URL : ", url

		headers = {'User-Agent':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36', 'accept-encoding': 'gzip;q=0,deflate,sdch'}
		r = self.session.get(url, headers=headers)

		self.save_session()
		return r.text.encode("utf-8")

	def get_stackoverflow_questions(self, query):
		html = self.google_request(query)	#google_request에 query 넘겨서 google에서 검색 한 후 페이지들 가져옴.. google 엔진이 더 낫기 때문..
		return [url for url in re.findall(r'(https?://[^\s]+)', html) if url.startswith("http://stackoverflow.com/questions/")]

	def search(self, query):	#유저 query를 통해 구글에서 Stackoverflow 사이트 타겟으로 검색
		so_ids = []
		for post_id in self.get_stackoverflow_questions(query) :
			so_id = post_id.split("/")[4]	#4번째가 질문 번호들..
			if so_id not in []:		# ["299495", "41107", "151777", "160970", "240546", "320542", "304268", "333363", "26305", "14617"]:
				so_ids.append(so_id)	#설마 이거 동적으로 안되니???
			else:
				print "Exclude", so_id
		return so_ids
