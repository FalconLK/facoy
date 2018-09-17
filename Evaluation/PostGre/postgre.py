#!/usr/local/bin/jython
#-*- coding: utf-8 -*-

import psycopg2
import codecs
import os
from shutil import copyfile


class Database:
    host = "localhost"
    user = "postgres"
    passwd = "djaak123"
    db = "postgres"
    port = "5433"

    def __init__(self):
        self.connection = psycopg2.connect(host=self.host,
                                           user=self.user,
                                           password=self.passwd,
                                           dbname=self.db,
                                           port=self.port)
        self.cursor = self.connection.cursor

    def query(self, q):
        cursor = self.cursor()
        cursor.execute(q)
        return cursor.fetchall()


def write_content_in_file(file_path, content):
    with codecs.open(file_path, mode='a', encoding='utf-8') as file:
        file.write(content)
    file.close()


def read_content_in_file(file_path):
    with codecs.open(file_path, mode='r', encoding='utf-8') as file:
        text = file.read()
    file.close()
    return text


def java_files_from_dir(directory):
    print "java files from the directory"
    javafiles = (os.path.join(dirpath, f)
                 for dirpath, dirnames, files in os.walk(directory)
                 for f in files if f.endswith('.java'))
    return javafiles


if __name__ == "__main__":
    target_file_path = "/Users/Falcon/Desktop/reduced_IJA/targets__111.txt"
    results = []
    db = Database()
    q = "select distinct name from functions"
    results = db.query(q)

    for i in results:
    	write_content_in_file(target_file_path, str(i))

    # HOME = "/Users/Falcon/Desktop/IJA/dataset/"
    # javafiles = java_files_from_dir(HOME)  # 자바 파일들만 뽑아내는 함수

    # for file in javafiles:
    # 	print file.split("/")[7:]

    # 파일 리스트에 있는 애들 전부 비교하면서 리스트에 있는 애들만 복사해서 특정 경로로 복사
    # file_list = read_content_in_file(target_file_path)
    # print file_list



    # copyfile(src, '/Users/Falcon/Desktop/reduced_IJA/dataset/')

