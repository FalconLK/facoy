#!/usr/bin/env python
# -*- coding: utf-8 -*-

from GitSearch.Indexer.NewJavaParser import parse
from GitSearch.MyUtils import remove_unified_stop_lists, write_search_log

def Generator(code):
    file_content = code

    # print '1. Origianl Query : ', file_content
    ast = parse(file_content, resolve=False)  # newJavaParser를 사용하여 자바 코드 파싱
    query = add_code_keyword_into_document(file_content, ast)
    # print "Query before the removing stop words : ", query
    # write_search_log("\nQuery before the removing stop words : " + str(query))

    # print '2. Right after the code query generator : ', query
    query = remove_unified_stop_lists(query)
    # print '3. Right after the stop words removing : ', query
    # print "Transformed user code query : ", query
    # write_search_log("\nTransformed user code query : " + str(query))

    return query

def add_code_keyword_into_document(file_content, node):
    query = ""

    for m in node["typed_method_call"]:
        if m:
            query += "word:" + m + " "

    for e in node["extends"]:
        if e:
            query += "word:" + e + " "

    for c in node["used_classes"]:
        if c:
            query += "word:" + c + " "

    for i in node["class_instance_creation"]:
        if i:
            query += "word:" + i + " "

    for m in node["methods"]:
        if m:
            query += "word:" + m + " "

    for m in node["methods_called"]:
        if m:
            query += "word:" + m + " "

    for m in node["unresolved_method_calls"]:
        if m:
            query += "word:" + m + " "

    for l in node["literals"]:
        if l:
            query += "word:" + l + " "
    return query

if __name__ == '__main__':

    code = """
            package com.han.streaming;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Han on 2016-07-18.
 */
public class FTPConnector {
    private FTPClient ftpClient;

    public FTPConnector() {
        ftpClient = new FTPClient();
    }

    public boolean login() {
        try {
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.connect("http://192.168.0.119");
            ftpClient.login("Artbit3", "artbit123");
            ftpClient.enterLocalPassiveMode();

            ftpClient.makeDirectory("music_upload");
            ftpClient.changeWorkingDirectory("music_upload");

            return true;
        } catch (IOException e) {
            Log.e("FTP_CONNECT", "LOGIN ERROR");
            return false;
        }
    }

    public boolean uploadFile(File file) {
        boolean uploadResult = true;

        if (ftpClient.isConnected()) {
            Log.e("FTP_UPLOAD", "CONNECTION IS NOT OPEN");
            return false;
        }

        FileInputStream inputStream = null;
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            inputStream = new FileInputStream(file);

            if (!ftpClient.storeFile(file.getName(), inputStream)) {
                uploadResult = false;
                Log.e("FTP_UPLOAD", "FILE SEND ERROR");
            }

        } catch (IOException e) {
            uploadResult = false;
            Log.e("FTP_UPLOAD", "FILE SEND ERROR IN CATCH");

        } finally {

            try {
                if (inputStream != null) inputStream.close();
                ftpClient.logout();
            } catch (IOException e) {
                uploadResult = false;
                Log.e("FTP_UPLOAD", "STREAM CLOSE ERROR");
            }
        }

        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                Log.e("FTP_UPLOAD", "DISCONNECT FAIL ERROR");
                uploadResult = false;
            }
        }

        return uploadResult;
    }
}
    """

    code = """Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
int width = (int) screenSize.getWidth();
int height = (int) screenSize.getHeight();
this.setLayout(new BorderLayout(50, 50));
this.setBounds(0, 0, width, height);
this.setExtendedState(JFrame.MAXIMIZED_BOTH);"""
    user_query = Generator(code)
    print user_query
