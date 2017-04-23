package com.gurnoors.cmpe202.uml.practice;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class HelloJavaParser {

	private static final String INPUT_PATH = "resources/Hello1.java";

	public static void main(String[] args) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(INPUT_PATH);
		CompilationUnit cUnit = JavaParser.parse(inputStream);
		MyVisitor visitor = new MyVisitor();
		visitor.visit(cUnit, null);
		
	}

}
