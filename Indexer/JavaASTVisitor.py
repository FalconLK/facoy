#!/usr/bin/env python
# -*- coding: utf-8 -*-

from org.eclipse.jdt.core.dom import ASTVisitor, ASTNode, SimpleName, MethodInvocation, QualifiedName, SimpleType, ParameterizedType, PrimitiveType
from collections import defaultdict

# TODO: Capture parameters of method declarations
class JavaASTVisitor(ASTVisitor):
	def __init__(self, cu=None, source=None):
		self.cu = cu
		self.source = source
		self.AST = {}
		self.AST["imports"] = set()
		self.AST["extends"] = set()
		self.AST["super_class"] = None
		self.AST["classes"] = set()
		self.AST["used_classes"] = set()
		self.AST["methods"] = set()
		self.AST["methods_called"] = set()
		self.AST["variables"] = set()
		self.AST["returns"] = set()
		self.AST["typed_method_call"] = set()
		self.AST["class_instance_creation"] = set()
		self.AST["annotations"] = set()
		self.AST["literals"] = set()
		self.AST["line_numbers"] = defaultdict(list)
		self.AST["positions"] = defaultdict(set)
		self.AST["unresolved_method_calls"] = set()
		self.AST["var_type_map"] = defaultdict(lambda: "")

	def _type_method_calls(self):
		""" Infer variable type by myself to avoid (very) expensive parsing process """
		l = set()

		non_super_methods = set()
		for mc in self.AST["typed_method_call"]:

			if mc[0].isupper():
				l.add(mc)
				non_super_methods.add(mc)
				continue

			variable_name, method_name = mc.split(".")
			non_super_methods.add(method_name)
			var_dot = "%s." % variable_name

			if variable_name in self.AST["var_type_map"]:
				var_type = self.AST["var_type_map"][variable_name]
				var_type_dot = "%s." % var_type
				new_mc = mc.replace(var_dot, var_type_dot, 1)
				l.add(new_mc)
			else:
				self.AST["unresolved_method_calls"].add(mc)

		# Add SuperClass method calls
		for m in self.AST["methods_called"]:
			if m not in self.AST["methods"] and m not in non_super_methods and self.AST["super_class"]:
				s = "%s.%s" % (self.AST["super_class"], m)
				l.add(s)
		self.AST["typed_method_call"] = l


	def get_AST(self):
		self._type_method_calls()
		if "MyJDTWrapperMethod" in self.AST["methods"]:
			self.AST["methods"].remove("MyJDTWrapperMethod")
		if "MyJDTWrapperClass" in self.AST["used_classes"]:
			self.AST["MyJDTWrapperClass"].remove("MyJDTWrapperClass")

		del self.AST["var_type_map"]
		return self.AST

	def log_ln(self, name, node):
		if self.cu:
			line_number = self.cu.getLineNumber(node.getStartPosition()) - 1
			if len(self.AST["line_numbers"]) < 2500:
				self.AST["line_numbers"][name].append(line_number)

	def log_position(self, node_type, value, node):
		self.AST["positions"][node_type].add( (value, self.node_range(node)) )

	def node_range(self, node):
		return (node.getStartPosition(), node.getStartPosition() + node.getLength())

	def postVisit(self, node):
		nodeType = node.getNodeType()
		if nodeType == ASTNode.TYPE_DECLARATION:
			class_name = node.getName().getFullyQualifiedName()
			self.AST["classes"].add(class_name)

			# Modify type method calls e.g. this.getWidth() is stored as MyCustomClass.getWidth()			
			self.AST["typed_method_call"] = set([ mc.replace(class_name, "this") if mc.startswith(class_name) else mc for mc in self.AST["typed_method_call"] ])

			if node.getSuperclassType():
				super_class = node.getSuperclassType()
				super_class_name = super_class.toString()
				self.AST["super_class"] = super_class_name
				self.AST["extends"].add(super_class_name)
				self.AST["used_classes"].add(super_class_name)
				self.log_ln(super_class_name, super_class)

			if node.superInterfaceTypes().size() > 0:
				for itername in node.superInterfaceTypes():
					self.AST["extends"].add(itername.toString())
					self.AST["used_classes"].add(itername.toString())
					self.log_ln(itername.toString(), itername)

		elif nodeType == ASTNode.METHOD_INVOCATION:
			self.AST["methods_called"].add(node.getName().getFullyQualifiedName())
			self.log_ln(node.getName().getFullyQualifiedName(), node)
			exp = node.getExpression()

			if exp:
				if isinstance(exp, SimpleName):
					if exp.getIdentifier()[0].islower():
						self.AST["variables"].add(exp.getIdentifier())
						self.log_position("variables", exp.getIdentifier(), exp)

						if exp.getIdentifier() == 'onCreateOptionsMenu':
							print "METHOD_INVOCATION"
					else:
						self.AST["used_classes"].add(exp.getIdentifier())
						self.log_position("used_classes", exp.getIdentifier(), exp)

					method_str = "%s.%s" % (exp.getIdentifier(), node.getName().getFullyQualifiedName())
					self.AST["typed_method_call"].add( method_str )
					self.log_ln(method_str, node)

				elif isinstance(exp, QualifiedName):
					method_str = "%s.%s" % (exp.getName().getFullyQualifiedName(), node.getName().getFullyQualifiedName())
					self.AST["typed_method_call"].add( method_str )
					self.log_ln(method_str, node)

		elif nodeType == ASTNode.METHOD_DECLARATION:
			self.AST["methods"].add(node.getName().getFullyQualifiedName())
			self.log_ln(node.getName().getFullyQualifiedName(), node)
			#return type as both return type and class use
			if node.getReturnType2():	#if return type is not void
				self.AST["returns"].add(node.getReturnType2().toString())
				self.AST["used_classes"].add(node.getReturnType2().toString())	
			var_type_name = None
			for par in node.parameters():
				var_name = par.getName().getIdentifier()
				var_type = par.getType()
				if isinstance(var_type, SimpleType):
					var_type_name = var_type.getName().getFullyQualifiedName()
				elif isinstance(var_type, PrimitiveType):
					var_type_name = var_type.toString()
				elif isinstance(var_type, ParameterizedType):
					if isinstance(var_type.getType(), SimpleType):
						var_type_name = var_type.getType().getName().getFullyQualifiedName()

				if var_type_name:
					self.AST["var_type_map"][var_name] = var_type_name
			
		elif nodeType == ASTNode.IMPORT_DECLARATION:
			self.AST["imports"].add(node.getName().getFullyQualifiedName())

		elif nodeType == ASTNode.FIELD_DECLARATION:
			self.AST["used_classes"].add(node.getType().toString())
			self.log_position("used_classes", node.getType().toString(), node.getType())

			var_type = node.getType()

			var_type_name = None
			if isinstance(var_type, SimpleType):
				var_type_name = var_type.getName().getFullyQualifiedName()
			elif isinstance(var_type, PrimitiveType):
				var_type_name = var_type.toString()
			elif isinstance(var_type, ParameterizedType):
				if isinstance(var_type.getType(), SimpleType):
					var_type_name = var_type.getType().getName().getFullyQualifiedName()

			# TODO: ArrayType
			#print "FIELD_DECLARATION", node.getType().toString(), var_type_name
			if var_type_name:
				for frag in node.fragments():
					var_name = frag.getName().getIdentifier()

				self.AST["var_type_map"][var_name] = var_type_name

		elif nodeType == ASTNode.SINGLE_VARIABLE_DECLARATION:
			#print "SINGLE_VARIABLE_DECLARATION", node.getName().getIdentifier(), node.getType().toString()
			if isinstance(node, SimpleName):
				var_name = node.getName().getIdentifier()
				var_type_name = node.getType().toString()

				self.AST["var_type_map"][var_name] = var_type_name


		elif nodeType == ASTNode.VARIABLE_DECLARATION_STATEMENT:
			self.AST["used_classes"].add(node.getType().toString())
			self.log_position("used_classes", node.getType().toString(), node.getType())

			#print "VARIABLE_DECLARATION_STATEMENT 2", node.getType().toString()
			for fragment in node.fragments():
				#print node.getType().toString(), fragment.getName().getFullyQualifiedName()
				#print "VARIABLE_DECLARATION_STATEMENT", node.getType().toString(), fragment.getName().getFullyQualifiedName()
				self.AST["variables"].add(fragment.getName().getFullyQualifiedName())
				self.log_position("variables", fragment.getName().getFullyQualifiedName(), fragment.getName())

				if fragment.getName().getFullyQualifiedName() == 'onCreateOptionsMenu':
					print "VARIABLE_DECLARATION_STATEMENT", node.getType().toString(), fragment.getStartPosition(), fragment.getLength(), 
				self.AST["var_type_map"][fragment.getName().getFullyQualifiedName()] = node.getType().toString()

		elif nodeType == ASTNode.CLASS_INSTANCE_CREATION:
			self.AST["used_classes"].add(node.getType().toString())
			self.log_position("used_classes", node.getType().toString(), node.getType())

			self.AST["class_instance_creation"].add( node.getType().toString() )
			self.log_ln(node.getType().toString(), node)

		elif nodeType == ASTNode.QUALIFIED_NAME:
			node_ = node.getQualifier().getFullyQualifiedName()#패키지 명을 사용된 used classess 로 가져오는 걸 방지..
			if 'a' <= node_[0] <= 'z':#
				#print "Node would be passed : ", node_
				pass
			else:
				#print "Node added to used_classes: ", node_
				self.AST["used_classes"].add(node.getQualifier().getFullyQualifiedName())
				self.log_position("used_classes", node.getQualifier().getFullyQualifiedName(), node.getQualifier())

				#node_[0] System 이면


		elif nodeType == ASTNode.MARKER_ANNOTATION:
			self.AST["annotations"].add(node.getTypeName().getFullyQualifiedName())

		elif nodeType == ASTNode.STRING_LITERAL:
			pass
			# v = node.getLiteralValue()
			# if 1 < len(v) and len(v) < 15:
			# 	self.AST["literals"].add(v)