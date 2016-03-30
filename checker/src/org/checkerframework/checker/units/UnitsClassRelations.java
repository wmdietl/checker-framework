package org.checkerframework.checker.units;

import org.checkerframework.framework.type.AnnotatedTypeMirror;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;

import com.sun.source.tree.*;

public abstract class UnitsClassRelations {
    protected final UnitsAnnotatedTypeFactory factory;
    protected final UnitsChecker checker;
    protected final ProcessingEnvironment processingEnv;
    protected final Elements elements;
    protected final UnitsMathOperatorsRelations mathOpRelations;

    protected UnitsClassRelations(UnitsChecker uChecker, UnitsAnnotatedTypeFactory uFactory) {
        this.checker = uChecker;
        this.factory = uFactory;
        this.processingEnv = checker.getProcessingEnvironment();
        this.elements = factory.getElementUtils();
        this.mathOpRelations = factory.getUnitsMathOperatorsRelations();
    }

    /**
     * processMethodInvocation is called by the units checker whenever it
     * examines a method call to an appropriately matching class the name of the
     * method that is called will be passed in as methodName, with the method's
     * arguments passed as list of {@link ExpressionTree}s.
     *
     * Implementations should update the resultType to an appropriate unit. It
     * may be useful to call {@link #mathOpRelations} and its methods to assign
     * units based on arithmetic, compound assignment, and comparison rules It
     * may also be useful to call methods in {@link UnitsRelationsTools} to
     * assist with analysis of annotated type mirrors and annotation mirrors.
     *
     * @param methodName an interned string of the name of the method that is
     *            invoked
     * @param methodArguments a list of {@link ExpressionTree}s of each method
     *            argument, from left to right order
     * @param resultType an annotated type mirror representing the return type
     *            of the method invocation, implementations should set the
     *            return type accordingly
     * @param node the AST node of the method invocation, used for issuing
     *            errors and warnings
     */
    public abstract void processMethodInvocation(String methodName, List<? extends ExpressionTree> methodArguments, AnnotatedTypeMirror resultType, MethodInvocationTree node);

    /**
     * Helper method which extracts the two arguments of a Math library method
     * invocation, then calls processMathOperation to check and assign the
     * resulting type according to math operator rules
     *
     * @param node the AST node of the math library method invocation
     * @param kind kind of operation: PLUS, MINUS, MULTIPLY, DIVIDE, REMAINDER,
     *            EQUAL_TO, etc
     * @param type the resulting type
     * @param methodArguments the arguments to the Math library method call
     */
    protected void processMathLibraryArithmeticOperation(ExpressionTree node, Tree.Kind kind, AnnotatedTypeMirror type, List<? extends ExpressionTree> methodArguments) {
        AnnotatedTypeMirror lht = factory.getAnnotatedType(methodArguments.get(0));
        AnnotatedTypeMirror rht = factory.getAnnotatedType(methodArguments.get(1));
        mathOpRelations.processMathOperation(node, kind, type, lht, rht);
    }
}
