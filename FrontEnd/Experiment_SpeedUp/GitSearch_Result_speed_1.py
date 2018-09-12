#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
from GitSearch_Item_speed import GitSearchItem
from GitSearch.MyUtils import md5, write_file

hitlog_path = '/Users/Falcon/Desktop/Tracing/hit_logs_for_each_1.txt'
scorelog_path = '/Users/Falcon/Desktop/Tracing/Score_logs_1/'

class GitSearchResult:
	def __init__(self, github_items):
		self.hashes = set()
		self.items = []
		self.global_matched_terms = []
		self.load_gitsearch_items(github_items)


		# self.rank_by_multiplication_score()

	def load_gitsearch_items(self, github_items):
		matched_term_acc = set()
		for github_item in github_items:
			gitsearch_item = GitSearchItem(github_item)
			gitsearch_item_hash = md5(str(gitsearch_item.file_path))

			if gitsearch_item_hash not in self.hashes:
				self.items.append(gitsearch_item)
				self.hashes.add(gitsearch_item_hash)
				matched_term_acc = matched_term_acc.union(gitsearch_item.matched_terms)
		self.global_matched_terms = list(matched_term_acc)

	def sort(self, git_search_score_tuple):
		self.ranked.sort(key=lambda tup: tup[1], reverse=True)

	def rank_by_multiplication_score(self):
		for git_search_item in self.items:
			git_search_item.score = git_search_item.github_item.score #* git_search_item.so_item.score
		self.items.sort(key=lambda item: item.score, reverse=True)
