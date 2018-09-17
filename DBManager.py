#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import print_function
import sys
sys.path.append("/Libs/mysql-connector-java-5.1.22-bin.jar")

from java.lang import Integer
from java.lang import Class
from java.sql import DriverManager
from java.sql import ResultSet

import threading
from threading import Lock

class DBManager(object):
    GlobalConn = None
    timeout = 200.0
    # lock = None

    @staticmethod
    def init():
        # DBManager.lock = Lock()
        DBManager.GlobalConn = None
        print (u'[DBManager] initialized DBManager!')

    @staticmethod
    def autoconnection():
        # DBManager.lock.acquire()
        if DBManager.GlobalConn is not None:
            DBManager.GlobalConn.close()
            DBManager.GlobalConn = None

        Class.forName("com.mysql.jdbc.Driver").newInstance()
        newConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/stackoverflow?", "root", "")
        newConn.setAutoCommit(True)
        DBManager.GlobalConn = newConn

        pass

    @staticmethod
    def requestOneColumnQuery(_query):
        '''
        request query with lock
        :param _query:
        :return:
        '''
        # DBManager.lock.acquire()

        stmt = DBManager.GlobalConn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

        stmt.setFetchSize(Integer.MIN_VALUE)
        items = []
        try:
            resultSet = stmt.executeQuery(_query)
        except:
            # DBManager.lock.release()
            return items

        while resultSet.next():
            items.append(resultSet.getInt(1))
        resultSet.close()

        # DBManager.lock.release()
        return items