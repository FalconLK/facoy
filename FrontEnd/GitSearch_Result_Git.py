#!/usr/bin/env python
# -*- coding: utf-8 -*-

from GitSearch_Item import GitSearchItem
from GitSearch.MyUtils import md5
from operator import attrgetter
# rank = 0

class GitSearchResult:
	def __init__(self, github_items, search_count, targetpath):
		self.hashes = set()
		self.items = []
		self.global_matched_terms = []
		self.target_path = targetpath #Kui 추가부분 file_path
		self.load_gitsearch_items(github_items, search_count, targetpath)	#Kui 추가부분 file_path
		self.rank_by_multiplication_score()

	def load_gitsearch_items(self, github_items, search_count, targetpath):
		matched_term_acc = set()
		# global rank

		#여기서 github_items 정렬 다시해보자..
		github_items = sorted(github_items, key=attrgetter('score'), reverse=True)
		rank = 0
		for github_item in github_items:
			rank += 1
			gitsearch_item = GitSearchItem(github_item, search_count, rank, targetpath)
			gitsearch_item_hash = md5(str(gitsearch_item.file_path))

			if gitsearch_item_hash not in self.hashes:
				self.items.append(gitsearch_item)
				self.hashes.add(gitsearch_item_hash)
				matched_term_acc = matched_term_acc.union(gitsearch_item.matched_terms)
		self.global_matched_terms = list(matched_term_acc)

	def sort(self, git_search_score_tuple):
		self.ranked.sort(key=lambda tup: tup[1], reverse=True)

	# 이 부분에서 랭크가 한번더 최종적으로 바뀐후에 웹에 로딩.. 상위 랭크가 크게 바뀌진 않음.
	def rank_by_multiplication_score(self):
		for git_search_item in self.items:
			git_search_item.score = git_search_item.github_item.score * git_search_item.so_item.score
		self.items.sort(key=lambda item: item.score, reverse=True)
