package com.gurnoors.cmpe202.uml.classdia;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.model.declarations.Declaration;

import gen.lib.cgraph.id__c;
import h.bend;
import javaslang.collection.List.Cons;
import javassist.compiler.ast.NewExpr;
import net.sourceforge.plantuml.graph.CostComputer;
import net.sourceforge.plantuml.sequencediagram.graphic.ParticipantBoxSimple;

public class UMLParser {

	// Map of className to the Set of dependencies (used in class body)
	private Map<String, Set<Association>> associations = new HashMap<>();
	private Map<String, Set<String>> dependencies = new HashMap<>();
	private Map<String, String> variables = null;
	private Set<String> interfaces = new HashSet<String>();

	public StringBuffer parse(File inpDir, Set<String> srcClasses) throws FileNotFoundException {
		StringBuffer source = new StringBuffer("@startuml\nskinparam classAttributeIconSize 0\n");

		// for all .java files
		for (File file : inpDir.listFiles()) {
			if (!file.getName().endsWith(".java")) {
				continue;
			}
			System.out.println("traversing file: " + file.getName());
			CompilationUnit cu = JavaParser.parse(file);
			populateInterfaces(cu);
			// visit classOrInterface
			new VoidVisitorAdapter<Void>() {
				public void visit(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration n, Void arg) {
					variables = new HashMap<String, String>();
					// extends, implements (same of class Or Interface
					for (Node parent : n.getExtendedTypes()) {
						source.append(parent.toString() + " <|-- " + n.getNameAsString() + "\n");
					}
					for (Node implemented : n.getImplementedTypes()) {
						source.append(implemented.toString() + " <|.. " + n.getNameAsString() + "\n");
					}

					if (!n.isInterface()) {
						// n is a class
						// handle fields
						source.append("class " + n.getNameAsString() + "{\n");
					} else {
						// n is an interface
						source.append("interface " + n.getNameAsString() + "{\n");
					}
					handleFields(n, source, srcClasses);
					handleMethods(n, source, srcClasses);
					handleConstructor(n, source, srcClasses);
					printVariables(n.getNameAsString(), source);
					source.append("}\n");
				}

			}.visit(cu, null);
		}
		printDependenciesAndAssociations(srcClasses, source);
		source.append("@enduml\n");
		System.out.println(source.toString());
		return source;
	}

	/**
	 * Append variables to StringBuffer source
	 * 
	 * @param className
	 * @param source
	 */
	private void printVariables(String className, StringBuffer source) {
		for (String key : variables.keySet()) {
			source.append(variables.get(key));
		}
	}

	private void handleConstructor(ClassOrInterfaceDeclaration n, StringBuffer source, Set<String> srcClasses) {
		Set<Association> associationsSet = associations.get(n.getNameAsString());
		if (associationsSet == null)
			associationsSet = new HashSet<Association>();

		for (BodyDeclaration<?> bodyDeclaration : n.getMembers()) {
			if (bodyDeclaration instanceof ConstructorDeclaration) {
				ConstructorDeclaration consDec = (ConstructorDeclaration) bodyDeclaration;
				String sign = getModifierSign(consDec.getModifiers());
				String signature = consDec.getDeclarationAsString(false, false, true);

				String toAppend = sign + " " + consDec.getNameAsString() + "(";
				for (Parameter parameter : consDec.getParameters()) {
					toAppend += parameter.getNameAsString() + " : " + parameter.getType().toString() + ", ";

					// associations
					Type type = parameter.getType();
					String depName;
					if (type.toString().contains("<")) {
						String typeName = type.toString();
						depName = typeName.substring(typeName.indexOf('<') + 1, typeName.indexOf('>'));
						associationsSet.add(new Association(depName, true));
					} else if (type instanceof ArrayType) {
						depName = ((ArrayType) type).getComponentType().toString();
						associationsSet.add(new Association(depName, true));
					} else {
						depName = type.toString();
						associationsSet.add(new Association(depName, false));
					}
				}
//				associations.put(n.getNameAsString(), associationsSet);
				if (consDec.getParameters().size() > 0)
					toAppend = toAppend.substring(0, toAppend.length() - 2);
				toAppend += ")\n";

				source.append(toAppend);

				List<String> paramNames = new ArrayList<>();
				for (Parameter param : consDec.getParameters()) {
					paramNames.add(param.getType().toString());
				}
				updateDependencies(n.getNameAsString(), paramNames, srcClasses);
			}
		}
	}

	private String getModifierSign(EnumSet<Modifier> modifiers) {

		for (Modifier modifier : modifiers) {
			switch (modifier) {
			case PUBLIC:
				return "+";
			case PRIVATE:
				return "-";
			case PROTECTED:
				return "#";
			default:
				return "~";
			}
		}
		return null;
	}

	private void handleMethods(ClassOrInterfaceDeclaration n, StringBuffer source, Set<String> srcClasses) {
		for (MethodDeclaration methodDec : n.getMethods()) {

			if (isGetterOrSetter(n.getNameAsString(), methodDec)) {
				continue;
			}
			List<String> paramNames = new ArrayList<>();
			for (Parameter param : methodDec.getParameters()) {
				paramNames.add(param.getType().toString());
			}
			updateDependencies(n.getNameAsString(), paramNames, srcClasses);

			checkFieldsInBody(n.getNameAsString(), methodDec, srcClasses);

			if (methodDec.getModifiers().contains(Modifier.PUBLIC)) {
				String signature = methodDec.getDeclarationAsString(false, false, true);
				int firstSpace = signature.indexOf(' ');
				String returnType = signature.substring(0, firstSpace);
				String remaining = signature.substring(firstSpace + 1);

				String toAppend = methodDec.getNameAsString() + "(";
				for (Parameter parameter : methodDec.getParameters()) {
					toAppend += parameter.getNameAsString() + " : " + parameter.getType().toString() + ", ";
				}
				if (methodDec.getParameters().size() > 0)
					toAppend = toAppend.substring(0, toAppend.length() - 2);
				toAppend += ")";

				source.append("+ " + toAppend + " : " + returnType + "\n");
			}
		}
	}

	private boolean isGetterOrSetter(String className, MethodDeclaration methodDec) {
		String methodName = methodDec.getNameAsString();
		if (methodName.startsWith("get") || methodName.startsWith("set")) {
			String variableName = methodName.substring(3);
			if (variables.containsKey(variableName)) {
				String value = variables.get(variableName);
				value = "+ " + value.substring(2);
				variables.put(variableName, value);
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks Fields in method body
	 * 
	 * @param methodDec
	 * @param srcClasses
	 */
	private void checkFieldsInBody(String className, MethodDeclaration methodDec, Set<String> srcClasses) {
		Set<String> dependencySet = dependencies.get(className);

		if (dependencySet == null) {
			dependencySet = new HashSet<String>();
		}
		for (Statement statement : methodDec.getBody().orElse(new BlockStmt()).getStatements()) {
			Queue<Node> children = new LinkedList<>(statement.getChildNodes());
			while (children != null) {
				Node child = children.poll();
				if (child == null)
					break;
				if (child instanceof FieldDeclaration) {
					FieldDeclaration fieldDeclaration = (FieldDeclaration) child;
					for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
						dependencySet.add(variableDeclarator.getType().toString());
					}

				} else {
					// if( child instanceof ObjectCreationExpr){
					// ObjectCreationExpr expr = (ObjectCreationExpr) child;
					for (String part : child.toString().split("\\[\\s\\=\\(\\)\\]")) {
						if (interfaces.contains(part)) {
							dependencySet.add(part);
						}
					}
				}
				if (child.getChildNodes() != null) {
					children.addAll(child.getChildNodes());
				}
			}
		}
		dependencies.put(className, dependencySet);
	}

	private boolean isCollection(Type type) {
		if (type instanceof Collection<?>) {
			return true;
		} else if (type instanceof ArrayType) {
			return true;
		} else {
			return false;
		}
	}

	private void printDependenciesAndAssociations(Set<String> srcClasses, StringBuffer source) {
		for (String classOrInterfaceName : dependencies.keySet()) {
			for (String dep : dependencies.get(classOrInterfaceName)) {
				if (interfaces.contains(dep)) {
					source.append(dep + " <.. " + classOrInterfaceName + "\n");
				}
			}
		}

		for (String className : associations.keySet()) {
			for (Association asso : associations.get(className)) {
				if (srcClasses.contains(asso.dep) && srcClasses.contains(className)) {
					if (asso.multiple) {
						source.append(asso.dep + " -- \"*\" " + className + "\n");
					} else {
						source.append(asso.dep + " -- " + className + "\n");
					}
				}
			}

		}
	}

	private void handleFields(ClassOrInterfaceDeclaration classDec, StringBuffer source,
			Set<String> classesInSrcFolder) {

		List<String> variableTypes = new ArrayList<>();
		for (FieldDeclaration dec : classDec.getFields()) {
			for (VariableDeclarator var : dec.getVariables()) {
				variableTypes.add(var.getType().toString());
			}
		}
		updateDependencies(classDec.getNameAsString(), variableTypes, classesInSrcFolder);

		// handleFields
		if (variables == null) {
			variables = new HashMap<String, String>();
		}

		for (FieldDeclaration field : classDec.getFields()) {
			if (field.getModifiers().contains(Modifier.PUBLIC)) {
				updateAssociations(classDec.getNameAsString(), field);
				for (VariableDeclarator variable : field.getVariables()) {
					variables.put(variable.getNameAsString(),
							"+ " + variable.getNameAsString() + " : " + variable.getType().toString() + "\n");
				}
			} else if (field.getModifiers().contains(Modifier.PRIVATE)) {
				updateAssociations(classDec.getNameAsString(), field);
				for (VariableDeclarator variable : field.getVariables()) {
					variables.put(variable.getNameAsString(),
							"- " + variable.getNameAsString() + " : " + variable.getType().toString() + "\n");
				}
			}
		}
		// associations.put(classDec.getNameAsString(), associationsSet);
	}

	private void updateAssociations(String className, FieldDeclaration field) {
		Set<Association> associationsSet = associations.get(className);
		if (associationsSet == null)
			associationsSet = new HashSet<Association>();
		for (VariableDeclarator variable : field.getVariables()) {
			Type type = variable.getType();
			String depName;
			if (type.toString().contains("<")) {
				String typeName = type.toString();
				depName = typeName.substring(typeName.indexOf('<') + 1, typeName.indexOf('>'));
				associationsSet.add(new Association(depName, true));
			} else if (type instanceof ArrayType) {
				depName = ((ArrayType) type).getComponentType().toString();
				associationsSet.add(new Association(depName, true));
			} else {
				depName = type.toString();
				associationsSet.add(new Association(depName, false));
			}
		}
		associations.put(className, associationsSet);
	}

	private void updateDependencies(String className, java.util.List<String> variableTypes,
			Set<String> classesInSrcFolder) {
		Set<String> classDecDep = dependencies.get(className);
		if (classDecDep == null) {
			classDecDep = new HashSet<String>();
		}
		for (String variableType : variableTypes) {
			if (classesInSrcFolder.contains(variableType)) {
				classDecDep.add(variableType);
			}
		}
		dependencies.put(className, classDecDep);

	}

	private void populateInterfaces(CompilationUnit cu) {
		for (Node node : cu.getChildNodes()) {
			if (node instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration) node;
				if (declaration.isInterface()) {
					interfaces.add(declaration.getNameAsString());
				}

			}
		}
	}

}
