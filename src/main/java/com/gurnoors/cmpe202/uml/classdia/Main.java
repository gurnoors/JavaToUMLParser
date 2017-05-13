package com.gurnoors.cmpe202.uml.classdia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import javassist.compiler.Parser;
import javassist.compiler.ast.NewExpr;
import net.sourceforge.plantuml.SourceStringReader;

public class Main {

	private static String INPUT_PATH = "resources/Hello1.java";
	private static String OUTPUT_PATH = null;

	public static void main(String[] args) throws IOException {
		if (args != null && args.length >= 1) {
			INPUT_PATH = args[0];
			switch (args.length) {
			case 1:
				OUTPUT_PATH = System.getProperty("user.dir") + "/uml.png";
				break;
			case 2:
				OUTPUT_PATH = args[1];
				break;
			default:
				System.out.println("Maximum 2 arguments!!! " + "(Most likely you did not escape a space in path name)\n"
						+ "Run this command:\n" + "java -jar umlparser src/folder/path optional/output/path.png");
				System.exit(0);
				break;
			}
			UMLParser parser = new UMLParser();
			Set<String> srcClasses = new HashSet<String>();
			
			File inpDir = new File(INPUT_PATH);
			populateSrcClasses(srcClasses, inpDir);
			
			
			System.out.println("op path: "+OUTPUT_PATH);
			StringBuffer ast = parser.parse(inpDir, srcClasses);
			OutputStream png = new FileOutputStream(new File(OUTPUT_PATH));
			
			SourceStringReader reader = new SourceStringReader(ast.toString());
			// Write the first image to "png"
			String desc = reader.generateImage(png); // Return a null string if
														// no generation
			
		} else {
			System.out.println("Incorrect no. of argumments: Run this command:\n"
					+ "java -jar umlparser src/folder/path optional/output/path");
			System.exit(0);
		}

		// FileInputStream inputStream = new FileInputStream(INPUT_PATH);
		// CompilationUnit cUnit = JavaParser.parse(inputStream);
		// MyVisitor visitor = new MyVisitor();
		// visitor.visit(cUnit, null);

	}

	
	/**
	 * srcClasses will contain all .java files in inpDir
	 * @param srcClasses
	 * @param inpDir 
	 */
	private static void populateSrcClasses(Set<String> srcClasses, File inpDir) {
		for(File srcFile : inpDir.listFiles()){
			String name = srcFile.getName();
			if(name.endsWith(".java")){
				srcClasses.add(name.substring(0, name.length()-5));
			}
		}
	}

}
