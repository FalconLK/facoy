#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
from GitSearch_Item import GitSearchItem
from GitSearch.MyUtils import md5, write_file
from GitSearch.MyUtils import write_search_log

hitlog_path = '/Users/Falcon/Desktop/Tracing/hit_logs_for_each.txt'
scorelog_path = '/Users/Falcon/Desktop/Tracing/Score_logs/'

class GitSearchResult:
	def __init__(self, github_items, hit_logs_for_each, score_logs_for_each, count):
		self.hashes = set()
		self.items = []
		self.global_matched_terms = []
		self.load_gitsearch_items(github_items)
		print "Search Count : ", len(self.items)

		hit_logs_for_each += str(len(self.items))
		write_file(hitlog_path, hit_logs_for_each + '\t')

		score_final_path = ''
		score_final_path += scorelog_path + str(count) + '.txt'
		if not os.path.exists(scorelog_path):
			os.makedirs(scorelog_path)
		write_file(score_final_path, score_logs_for_each + '\n')

		self.rank_by_multiplication_score()

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
