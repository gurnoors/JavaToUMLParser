package com.gurnoors.cmpe202.uml.practice;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class HelloPlantUML {

	public static void main(String[] args) throws FileNotFoundException {
		hi();
	}

	private static void hi() throws FileNotFoundException {
		FileReader reader = new FileReader("foo.java");
	}

}
