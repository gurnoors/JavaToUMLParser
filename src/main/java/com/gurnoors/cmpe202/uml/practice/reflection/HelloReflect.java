package com.gurnoors.cmpe202.uml.practice.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.InterfaceDeclaration;
import com.gurnoors.cmpe202.uml.practice.HelloJavaParser;

public class HelloReflect {

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method[] methods = HelloJavaParser.class.getMethods();

		for(Method method : methods){
			System.out.println(method.getModifiers());
		    System.out.println("---------method = " + method.getName());
		    System.out.println(method.getParameterCount());
		    for(Parameter parameter : method.getParameters()){
		    	System.out.println("Param-->");
		    	System.out.println(parameter.toString());
		    }
		    
		    
		    System.out.println("------end method "+method.getName());
		    System.out.println(method.getModifiers());
		}
		
		for(Annotation an : HelloJavaParser.class.getAnnotations()){
			System.out.println(an);
		}
		
		for(Constructor<?> c : HelloJavaParser.class.getConstructors()){
			System.out.println(c);
		}
		
		for(Class<?> i : HelloJavaParser.class.getInterfaces()){
			System.out.println(i);
		}
	}
	

}
