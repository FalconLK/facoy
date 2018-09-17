#!/usr/bin/env python
# -*- coding: utf-8 -*-

from GitSearch.Indexer.NewJavaParser import parse
from GitSearch.MyUtils import remove_unified_stop_lists
import codecs

def Generator(code):
    file_content = code
    print '1. Origianl Query : ', file_content
    ast = parse(file_content, resolve=False)
    query = add_code_keyword_into_document(file_content, ast)
    print "2. Right after alternation & before the removing stop words : ", query
    query = remove_unified_stop_lists(query)
    print '3. Right after the stop words removing : ', query
    return query

def add_code_keyword_into_document(file_content, node):
    unified_query = ""
    if node == None:
        return unified_query

    for m in node["typed_method_call"]:
        if m:
            unified_query += "typed_method_call:" + m + " "

    for e in node["extends"]:
        if e:
            unified_query += "extends:" + e + " "

    for c in node["used_classes"]:
        if c:
            unified_query += "used_classes:" + c + " "

    for i in node["class_instance_creation"]:
        if i:
            unified_query += "class_instance_creation:" + i + " "

    for m in node["methods"]:
        if m:
            unified_query += "methods:" + m + " "

    for m in node["methods_called"]:
        if m:
            unified_query += "methods_called:" + m + " "

    for m in node["unresolved_method_calls"]:
        if m:
            unified_query += "unresolved_method_calls:" + m + " "

    for l in node["literals"]:
        if l:
            unified_query += "literals:" + l + " "
    return unified_query

def get_empty_lines(refine_target):  #Answer snippet 중 파싱 실패하는 녀석들은, Questions 과 함께 데이터 셋에서 제외
    empty_list = []
    contents = load_file_by_line(refine_target)

    for num, line in enumerate(contents, 1):
        print num, line
        if line == u'':
            empty_list.append(num)
    return empty_list

def load_file_by_line(file_path):
    with codecs.open(file_path, "r", encoding='utf-8') as file:
        lst = file.read().split('\n')
        file.close
    return lst

def write_file(dest_path, mode, content):
    with codecs.open(dest_path, mode, encoding='utf-8') as dest:
        dest.write(content + '\n')
    dest.close

if __name__ == '__main__':
    # code = """object List<String> ClassCastException List<?> list = (List<?>) object;\n\n"""
    # print Generator(code)

    count = 0
    file_path = "/Users/Falcon/Desktop/clean.txt"
    code_query_path = "/Users/Falcon/Desktop/clean_cq.txt"

    ####Code Query Generation
    lst = load_file_by_line(file_path)

    for i in lst:
        count += 1
        print count
        code_query = Generator(i)
        write_file(code_query_path, "a", code_query)
    ####