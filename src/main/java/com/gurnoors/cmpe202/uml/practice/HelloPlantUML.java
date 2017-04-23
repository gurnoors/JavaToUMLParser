package com.gurnoors.cmpe202.uml.practice;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HelloPlantUML {

	public static void main(String[] args) throws IOException {
		hi();
	}

	private static void hi() throws IOException {
		FileReader reader = new FileReader("foo.java");
		reader.read();
		reader.close();
	}

}
