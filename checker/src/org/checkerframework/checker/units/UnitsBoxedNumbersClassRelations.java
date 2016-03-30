package org.checkerframework.checker.units;

import org.checkerframework.framework.type.AnnotatedTypeMirror;

import java.util.List;

import com.sun.source.tree.*;

/**
 * This class handles the methods defined in {@link Number} and its subclasses
 * that behave like arithmetic and comparison operations
 *
 * @author jeff luo
 */
public class UnitsBoxedNumbersClassRelations extends UnitsClassRelations {
    protected UnitsBoxedNumbersClassRelations(UnitsChecker uChecker, UnitsAnnotatedTypeFactory uFactory) {
        super(uChecker, uFactory);
    }

    @Override
    public void processMethodInvocation(String methodName, List<? extends ExpressionTree> methodArguments, AnnotatedTypeMirror resultType, MethodInvocationTree node) {
        // process each math library method call
        switch (methodName) {
        case "equals":
        case "compareTo":
            // x.equals(y) or x.compareTo(y)
            AnnotatedTypeMirror receiverType = factory.getReceiverType(node);
            AnnotatedTypeMirror argumentType = factory.getAnnotatedType(methodArguments.get(0));
            mathOpRelations.processMathOperation(node, Tree.Kind.EQUAL_TO, resultType, receiverType, argumentType);
            break;
        case "compare":
        case "compareUnsigned":
            // for Integer and Long
            // Integer.compare(x, y) or Integer.compareUnsigned(x, y)
            processMathLibraryArithmeticOperation(node, Tree.Kind.EQUAL_TO, resultType, methodArguments);
            break;
        case "divideUnsigned":
            // for Integer and Long
            processMathLibraryArithmeticOperation(node, Tree.Kind.DIVIDE, resultType, methodArguments);
            break;
        case "remainderUnsigned":
            // for Integer and Long
            processMathLibraryArithmeticOperation(node, Tree.Kind.REMAINDER, resultType, methodArguments);
            break;
        case "sum":
            // for Integer, Long, Float, and Double
            processMathLibraryArithmeticOperation(node, Tree.Kind.PLUS, resultType, methodArguments);
            break;
        default:
            break;
        }
    }
}
