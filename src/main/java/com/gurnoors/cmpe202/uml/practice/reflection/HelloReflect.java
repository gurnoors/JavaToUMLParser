package com.gurnoors.cmpe202.uml.practice.reflection;

import java.lang.reflect.Method;

import com.gurnoors.cmpe202.uml.practice.HelloJavaParser;

public class HelloReflect {

	public static void main(String[] args) {
		Method[] methods = HelloJavaParser.class.getMethods();

		for(Method method : methods){
		    System.out.println("method = " + method.getName());
		    System.out.println(method.getParameterCount());
		}
	}

}
