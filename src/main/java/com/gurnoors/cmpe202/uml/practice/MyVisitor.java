package com.gurnoors.cmpe202.uml.practice;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MyVisitor extends VoidVisitorAdapter<Void> {

	@Override
	public void visit(ObjectCreationExpr n, Void arg) {
		System.out.println("Type: " + n.getType().getName());
		System.out.println(n.getAnonymousClassBody());
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ImportDeclaration n, Void arg) {
		super.visit(n, arg);
		
	}
	
	@Override
	public void visit(JavadocComment n, Void arg) {
		System.out.println(n);
		System.out.println(n.getContent());
		super.visit(n, arg);
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		NodeList<BodyDeclaration<?>> members = n.getMembers();
		for(BodyDeclaration<?> member : members){
			if( member instanceof FieldDeclaration){
				System.out.println("Its a Field!!!");
			}else if (member instanceof MethodDeclaration) {
				System.out.println("A Method!!!");
			}else if (member instanceof ConstructorDeclaration){
				System.out.println("A Constructor!!!");
			}else{
				System.out.println("Oh I forgot to consider: "+member.getClass().getName());
			}
			System.out.println(member.getClass().getName());
			System.out.println(member.toString());
			System.out.println();
			
		}
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ArrayAccessExpr n, Void arg) {
		System.out.println(n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(AssertStmt n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(BinaryExpr n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(TryStmt n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(CatchClause n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(CharLiteralExpr n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ForeachStmt n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(AnnotationDeclaration n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(WhileStmt n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
	
	@Override
	public void visit(EnumDeclaration n, Void arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}
}
