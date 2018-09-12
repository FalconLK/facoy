#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/jsoup-1.8.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-analyzers-common-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-core-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-queries-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/lucene-queryparser-4.10.4.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/jython-standalone-2.7.0.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mysql-connector-java-5.1.22-bin.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/py4j-0.9.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.apache.commons.lang_2.6.0.v201205030909.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.cdt.core_5.6.0.201402142303.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.jobs_3.5.200.v20120521-2346.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources.win32.x86_3.5.100.v20110423-0524.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.resources_3.8.0.v20120522-2034.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.core_3.8.1.v20120531-0637.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jdt.ui_3.8.2.v20130107-165834.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.jface.text_3.8.0.v20120531-0600.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.ltk.core.refactoring_3.6.100.v20130605-1748.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.osgi_3.8.0.v20120529-1548.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/org.eclipse.text_3.5.0.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/bson-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-3.0.2.jar")
sys.path.append("/Users/Falcon/Desktop/Pycharm_Project/FaCoY_Project/GitSearch/Libs/mongodb-driver-core-3.0.2.jar")

from JavaASTVisitor_noUS import JavaASTVisitor
from org.eclipse.jdt.core.dom import ASTParser, AST, ASTNode, ASTVisitor, MethodDeclaration, CompilationUnit
from java.lang import String

class TestVisitor(ASTVisitor):
    def __init__(self):
        self.result = None

    def visit(self, node):
        if isinstance(node, MethodDeclaration):
            self.result = 2
            return False
        return True

class JavaParser:
    def __init__(self, resolve):
        self.resolve = resolve
        self.flag = 0
        self.cutype = 0

        # cutype =  0 => already has class body and method body
        # 			1 => has a method wrapper but no class
        # 			2 => missing both method and class wrapper (just a bunch of statements)

    def getASTParser(self, sourceCode, parserType):
        parser = ASTParser.newParser(AST.JLS3);
        # print "RESOLVE", self.resolve

        parser.setSource(String(sourceCode).toCharArray())
        parser.setResolveBindings(self.resolve);
        parser.setStatementsRecovery(self.resolve);
        parser.setBindingsRecovery(self.resolve);
        parser.setKind(parserType)
        return parser


    def getCompilationUnitFromString(self, code):
        parser = self.getASTParser(code, ASTParser.K_COMPILATION_UNIT)
        cu = parser.createAST(None)
        self.cutype = 0

        if cu.types().isEmpty():
            self.flag = 1
            self.cutype = 1
            s1 = "public class MyJDTWrapperClass{\n %s \n}" % code
            parser = self.getASTParser(s1, ASTParser.K_COMPILATION_UNIT)

            cu = parser.createAST(None) #Null pointer exception occured here.
            v = TestVisitor()
            cu.accept(v)
            if v.result:
                self.flag = v.result
            else:
                print "Test visitor failed.."

            if self.flag == 1:
                s1 = "public class MyJDTWrapperClass{\n public void MyJDTWrapperMethod(){\n %s \n}\n}" % code
                self.cutype = 2
                parser = self.getASTParser(s1, ASTParser.K_COMPILATION_UNIT)

                cu = parser.createAST(None)
            if self.flag == 2:
                s1 = "public class MyJDTWrapperClass{\n %s \n}" % code
                self.cutype = 1
                parser = self.getASTParser(s1, ASTParser.K_COMPILATION_UNIT)

                cu = parser.createAST(None)
        else:
            self.cutype = 0
            parser = self.getASTParser(code, ASTParser.K_COMPILATION_UNIT)
            cu = parser.createAST(None)

        s1 = s1 if self.cutype != 0 else code #0 이면 코드를 그대로 쓰게됨.
        return cu, s1

def revise_AST(AST):	#Lucene Query Parser 에서 대괄호들이 문제가 됨..
    newset = set([])
    for a in AST['usedclasses']:
        idx = a.find('[')
        if idx > 0:
            idx2 = a.rfind(']')
            newset.add(a[:idx] + a[idx2 + 1:])
        else:
            newset.add(a)
    AST['usedclasses'] = newset
    return AST

def parse(code, resolve=False, source=False):
    try:
        parser = JavaParser(resolve)
        cu, s1 = parser.getCompilationUnitFromString(code)
        v = JavaASTVisitor(cu, s1)
        cu.accept(v)

    except Exception as e:
        print "Parser error! // ", e
        return None

    if source:
        return revise_AST(v.get_AST()), s1	#
    return revise_AST(v.get_AST())			#

