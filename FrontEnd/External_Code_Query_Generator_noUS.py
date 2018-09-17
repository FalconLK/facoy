#!/usr/bin/env python
# -*- coding: utf-8 -*-


#External Code Query Generator without Under-score in key values//
# e.g., typed_method_call -> typedmethodcall

from GitSearch.Indexer.NewJavaParser_noUS import parse
from GitSearch.MyUtils import remove_unified_stop_lists
import codecs
import io

def Generator(code):
    file_content = code
    # print '1. Origianl Query : ', file_content
    ast = parse(file_content, resolve=False)  # newJavaParser를 사용하여 자바 코드 파싱
    query = add_code_keyword_into_document(file_content, ast)
    # print "2. Right after alternation & before the removing stop words : ", query
    query = remove_unified_stop_lists(query)
    # print '3. Right after the stop words removing : ', query
    return query

def add_code_keyword_into_document(file_content, node):
    unified_query = ""
    if node == None:
        return unified_query

    for m in node["typedmethodcall"]:
        if m:
            unified_query += "typedmethodcall:" + m + " "

    for e in node["extends"]:
        if e:
            unified_query += "extends:" + e + " "

    for c in node["usedclasses"]:
        if c:
            unified_query += "usedclasses:" + c + " "

    for i in node["classinstancecreation"]:
        if i:
            unified_query += "classinstancecreation:" + i + " "

    for m in node["methods"]:
        if m:
            unified_query += "methods:" + m + " "

    for m in node["methodscalled"]:
        if m:
            unified_query += "methodscalled:" + m + " "

    for m in node["unresolvedmethodcalls"]:
        if m:
            unified_query += "unresolvedmethodcalls:" + m + " "

    for l in node["literals"]:
        if l:
            unified_query += "literals:" + l + " "
    return unified_query

def get_empty_lines(refine_target):  #Answer snippet 중 파싱 실패하는 녀석들은, Questions 과 함께 데이터 셋에서 제외
    empty_list = []
    contents = load_file(refine_target)

    for num, line in enumerate(contents, 1):
        print num, line
        if line == u'':
            empty_list.append(num)
    return empty_list

def load_file(file_path):
    with codecs.open(file_path, "r", encoding='utf-8') as file:
        lst = file.read().split('\n')
        file.close
    return lst

def write_file(dest_path, mode, content):
    with codecs.open(dest_path, mode, encoding='utf-8') as dest:
        dest.write(content + '\n')
    dest.close

if __name__ == '__main__':
    # code = """{call OMV_CREAR_INTERACCION(?,?,?,?,?,?,?,?)} setXXX() cst java.lang.NumberFormatException objSite cst.setInt(1, Integer.parseInt(objSite)); CallableStatement cst = conn.prepareCall("{call OMV_CREAR_INTERACCION(?,?,?,?,?,?,?,?)}");           cst.setInt(1, Integer.parseInt(objSite)); cst.setString(2, titulo); cst.setString(3, "CRM"); cst.setString(4, ""); cst.setString(5, razon1); cst.setString(6, razon2); cst.setString(7, razon3); cst.registerOutParameter(8, Types.VARCHAR);//out parameter for p_resultado      cst.execute();  //to get out parameter String str = cst.getString(8);"""
    # print Generator(code)

    count = 0
    encoding = 'utf-8'
    question_path = "/Users/Falcon/Desktop/NMT/Question.txt"
    revised_q_path = "/Users/Falcon/Desktop/NMT/Question_revised.txt"

    answer_path = "/Users/Falcon/Desktop/NMT/Answer.txt"
    revised_a_path = "/Users/Falcon/Desktop/NMT/Answer_revised.txt"

    codequery_path = "/Users/Falcon/Desktop/NMT/CodeQuery.txt"
    revised_cq_path = "/Users/Falcon/Desktop/NMT/CodeQuery_revised.txt"

    #####Code Query Generation
    lst = load_file(answer_path)
    for i in lst:
        count += 1
        # if count < 532379:      ##Deleted list 354864, 483983, 532379, 606377          (Null pointer exception occured.)
        #     continue
        print count
        code_query = Generator(i)
        write_file(codequery_path, "a", code_query)
    #####



    ##### Revision for empty lines
    empty = get_empty_lines(codequery_path) #Code Query 중에 Parsing 실패한 라인들 전부.

    count = 0
    question_lines = load_file(question_path)
    for num, ln in enumerate(question_lines, 1):
        if num in empty:
            continue
        write_file(revised_q_path, "a", ln)
        count += 1
        print "Count: ", count, "Line Number:", num

    count = 0
    with io.open(answer_path, "r", encoding='utf-8') as f:
        for c, line in enumerate(f, 1):
            if c in empty:
                continue
            write_file(revised_a_path, "a", line.strip())
            count += 1
            print "Answer // Count: ", count, "Line Number:", c

    count = 0
    with io.open(codequery_path, "r", encoding='utf-8') as f:
        for c, line in enumerate(f, 1):
            if c in empty:
                continue
            write_file(revised_cq_path, "a", line.strip())
            count += 1
            print "CodeQuery // Count: ", count, "Line Number:", c
