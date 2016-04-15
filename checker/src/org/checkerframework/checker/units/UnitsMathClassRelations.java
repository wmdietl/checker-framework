package org.checkerframework.checker.units;

import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.Pair;

import java.util.*;

import com.sun.source.tree.*;

public class UnitsMathClassRelations extends UnitsClassRelations {

    // cache expressions evaluated in evalMathExpression
    private final Map<String, Pair<Double, Boolean>> mathExpressions;

    protected UnitsMathClassRelations(UnitsChecker uChecker, UnitsAnnotatedTypeFactory uFactory) {
        super(uChecker, uFactory);

        mathExpressions = new HashMap<String, Pair<Double, Boolean>>();
    }

    @Override
    public void processMethodInvocation(String methodName, List<? extends ExpressionTree> methodArguments, AnnotatedTypeMirror resultType, MethodInvocationTree node) {
        // java.lang.StrictMath implements the same methods as
        // java.lang.Math except for incrementExact, decrementExact, and
        // negateExact
        // The common methods have the same signature and same expected
        // units

        // process each math library method call
        switch (methodName) {
        case "addExact":
            processMathLibraryArithmeticOperation(node, Tree.Kind.PLUS, resultType, methodArguments);
            break;
        case "subtractExact":
            processMathLibraryArithmeticOperation(node, Tree.Kind.MINUS, resultType, methodArguments);
            break;
        case "multiplyExact":
            processMathLibraryArithmeticOperation(node, Tree.Kind.MULTIPLY, resultType, methodArguments);
            break;
        case "floorDiv":
            processMathLibraryArithmeticOperation(node, Tree.Kind.DIVIDE, resultType, methodArguments);
            break;
        case "IEEEremainder":
            processMathLibraryArithmeticOperation(node, Tree.Kind.REMAINDER, resultType, methodArguments);
            break;
        case "atan2":
            // atan2 is always called with 2 arguments
            AnnotatedTypeMirror lht = factory.getAnnotatedType(methodArguments.get(0));
            AnnotatedTypeMirror rht = factory.getAnnotatedType(methodArguments.get(1));
            // atan2 must be called with the same units for both of its
            // arguments
            if (!UnitsRelationsTools.areSameUnits(lht, rht)) {
                checker.report(Result.failure("two.parameter.method.arguments.unit.mismatch", lht.toString(), rht.toString()), node);
            }
            break;
        case "sqrt":
            // sqrt is always called with 1 argument
            AnnotatedTypeMirror sqrtArg = factory.getAnnotatedType(methodArguments.get(0));
            processSquareRoot(sqrtArg, resultType);
            break;
        case "cbrt":
            // cbrt is always called with 1 argument
            AnnotatedTypeMirror cbrtArg = factory.getAnnotatedType(methodArguments.get(0));
            processCubeRoot(cbrtArg, resultType);
            break;
        case "pow":
            // pow is always called with 2 arguments
            AnnotatedTypeMirror powBase = factory.getAnnotatedType(methodArguments.get(0));
            ExpressionTree powExponent = methodArguments.get(1);
            processPower(powBase, powExponent, resultType);
            break;
        default:
            break;
        }
    }

    /**
     * If arg is one of the supported area units, then it will return the
     * corresponding length unit
     *
     * @param arg a unit which may or may not be one of the supported area units
     * @param type the result unit
     * @return type will be replaced with the corresponding length unit if arg
     *         is a supported area unit
     */
    private void processSquareRoot(AnnotatedTypeMirror arg, AnnotatedTypeMirror type) {
        if (UnitsRelationsTools.hasSpecificUnit(arg, factory.unitsMirrors.m2)) {
            // if sqrt was called with a meter-squared unit, return
            // meter
            type.replaceAnnotation(factory.unitsMirrors.m);
        } else if (UnitsRelationsTools.hasSpecificUnit(arg, factory.unitsMirrors.mm2)) {
            // if sqrt was called with a millimeter-squared unit, return
            // millimeter
            type.replaceAnnotation(factory.unitsMirrors.mm);
        } else if (UnitsRelationsTools.hasSpecificUnit(arg, factory.unitsMirrors.km2)) {
            // if sqrt was called with a kilometer-squared unit, return
            // kilometer
            type.replaceAnnotation(factory.unitsMirrors.km);
        }
    }

    /**
     * If arg is one of the supported volume units, then it will return the
     * corresponding length unit
     *
     * @param arg a unit which may or may not be one of the supported volume
     *            units
     * @param type the result unit
     * @return type will be replaced with the corresponding length unit if arg
     *         is a supported volume unit
     */
    private void processCubeRoot(AnnotatedTypeMirror arg, AnnotatedTypeMirror type) {
        if (UnitsRelationsTools.hasSpecificUnit(arg, factory.unitsMirrors.m3)) {
            // if cbrt was called with a meter-cubed unit, return
            // meter
            type.replaceAnnotation(factory.unitsMirrors.m);
        } else if (UnitsRelationsTools.hasSpecificUnit(arg, factory.unitsMirrors.mm3)) {
            // if cbrt was called with a millimeter-cubed unit, return
            // millimeter
            type.replaceAnnotation(factory.unitsMirrors.mm);
        } else if (UnitsRelationsTools.hasSpecificUnit(arg, factory.unitsMirrors.km3)) {
            // if cbrt was called with a kilometer-cubed unit, return
            // kilometer
            type.replaceAnnotation(factory.unitsMirrors.km);
        }
    }

    /**
     * This method will first attempt to evaluate powExponent down to a double
     * value, then depending on the value of the exponent, it will produce a
     * unit for the resultType depending on the unit of powBase
     *
     * @param powBase
     * @param powExponent
     * @param resultType
     */
    private void processPower(AnnotatedTypeMirror powBase, ExpressionTree powExponent, AnnotatedTypeMirror resultType) {
        double exp;
        if (powExponent instanceof BinaryTree
                || powExponent instanceof ParenthesizedTree
                || powExponent instanceof LiteralTree) {
            // if the second argument is a mathematical expression
            // consisting of only parentheses and literals (eg (1.0 + 3) / 8)
            // then evaluate it to a single value.
            // if the second argument is a numerical literal, then
            // extract the value.
            Pair<Double, Boolean> evalValue = evalMathExpression(powExponent);

            // the Boolean returned is false if the expression contains
            // any operator or argument for which it cannot evaluate

            // if successfully, set exponent to the evaluated value,
            // otherwise set to 0.0
            exp = evalValue.second ? evalValue.first : 0.0;
        } else {
            // if the second argument is not a mathematical expression
            // nor a numerical literal, then replace with UnitsUnknown and
            // return

            // replacement done below
            exp = 0.0;
        }

        // see what is the exponent
        if (exp == 1.0) {
            // return the unit of the first parameter
            resultType.replaceAnnotations(powBase.getAnnotations());
        } else if (exp == 2.0) {
            // detect the unit of the first parameter
            if (UnitsRelationsTools.hasSpecificUnit(powBase, factory.unitsMirrors.m)) {
                resultType.replaceAnnotation(factory.unitsMirrors.m2);
            } else if (UnitsRelationsTools.hasSpecificUnit(powBase, factory.unitsMirrors.mm)) {
                resultType.replaceAnnotation(factory.unitsMirrors.mm2);
            } else if (UnitsRelationsTools.hasSpecificUnit(powBase, factory.unitsMirrors.km)) {
                resultType.replaceAnnotation(factory.unitsMirrors.km2);
            } else {
                // if the base unit is something other than m, mm, or km, then
                // return unknown
                resultType.replaceAnnotation(factory.unitsMirrors.TOP);
            }
        } else if (exp == 3.0) {
            // detect the unit of the first parameter
            if (UnitsRelationsTools.hasSpecificUnit(powBase, factory.unitsMirrors.m)) {
                resultType.replaceAnnotation(factory.unitsMirrors.m3);
            } else if (UnitsRelationsTools.hasSpecificUnit(powBase, factory.unitsMirrors.mm)) {
                resultType.replaceAnnotation(factory.unitsMirrors.mm3);
            } else if (UnitsRelationsTools.hasSpecificUnit(powBase, factory.unitsMirrors.km)) {
                resultType.replaceAnnotation(factory.unitsMirrors.km3);
            } else {
                // if the base unit is something other than m, mm, or km, then
                // return unknown
                resultType.replaceAnnotation(factory.unitsMirrors.TOP);
            }
        } else if (exp == 0.5) {
            // taking the power of 0.5 is the same as taking the square
            // root
            processSquareRoot(powBase, resultType);
        } else if (exp == (1.0d / 3)) {
            // taking the power of 1/3 is the same as taking the cube
            // root
            processCubeRoot(powBase, resultType);
        } else {
            // exp is either 0.0 or some other unhandled value
            // replace with UnknownUnits
            resultType.replaceAnnotation(factory.unitsMirrors.TOP);
        }
    }

    /**
     * Attempts to evaluate a mathematical expression (passed in as root) into a
     * double value. The method returns the double value and true as a pair if
     * the evaluation was successful, otherwise it returns 0.0 and false as a
     * pair.
     *
     * Expressions with any non number literals will not be successfully
     * evaluated.
     *
     * @param root the root node in the expression tree of a mathematical
     *            expression
     * @return a pair of a Double and a Boolean
     */
    private Pair<Double, Boolean> evalMathExpression(ExpressionTree root) {
        String mathExpression = root.toString().intern();
        // check against the cache to see if this expression has been
        // evaluated before
        if (mathExpressions.containsKey(mathExpression)) {
            // if so return prior results
            return mathExpressions.get(mathExpression);
        } else {
            // otherwise evaluate the expression, cache the results, and
            // return the results
            Pair<Double, Boolean> evaluation = evalMathExpression(root, true);
            mathExpressions.put(mathExpression, evaluation);
            return evaluation;
        }
    }

    // evaluate the expression and sub-expressions via recursion
    private Pair<Double, Boolean> evalMathExpression(ExpressionTree node, Boolean success) {
        // if a previous step resulted in failure then terminate the
        // recursion chain by returning 0 as the double value
        if (success == false) {
            return Pair.of(0.0, false);
        }

        switch (node.getKind()) {
        // if the expression is a numerical literal, then extract and return
        // the value
        case DOUBLE_LITERAL:
            return Pair.of((double) ((LiteralTree) node).getValue(), true);
        case FLOAT_LITERAL:
            return Pair.of((double) (float) ((LiteralTree) node).getValue(), true);
        case LONG_LITERAL:
            return Pair.of((double) (long) ((LiteralTree) node).getValue(), true);
        case INT_LITERAL:
            return Pair.of((double) (int) ((LiteralTree) node).getValue(), true);
        case PARENTHESIZED:
            // if there's a parenthesis, evaluate and return the value of
            // the parenthesized expression
            return evalMathExpression(((ParenthesizedTree) node).getExpression(), success);
        case PLUS:
        case MINUS:
        case MULTIPLY:
        case DIVIDE:
        case REMAINDER:
            return evalMathOperation((BinaryTree) node, success);
        default:
            // otherwise return 0 and false
            return Pair.of(0.0, false);
        }
    }

    // evaluates only binary trees for plus, minus, multiply, divide, and
    // remainder
    private Pair<Double, Boolean> evalMathOperation(BinaryTree node, Boolean success) {
        // evaluate each operand and then return the result
        Pair<Double, Boolean> left = evalMathExpression(node.getLeftOperand(), success);
        Pair<Double, Boolean> right = evalMathExpression(node.getRightOperand(), left.second);

        // if either of the evaluations resulted in failure then
        // terminate the recursion chain
        if (left.second == false || right.second == false) {
            return Pair.of(0.0, false);
        }

        // TODO: bitshift support?
        switch (node.getKind()) {
        case PLUS:
            return Pair.of(left.first + right.first, true);
        case MINUS:
            return Pair.of(left.first - right.first, true);
        case MULTIPLY:
            return Pair.of(left.first * right.first, true);
        case DIVIDE:
            return Pair.of(left.first / right.first, true);
        case REMAINDER:
            return Pair.of(left.first % right.first, true);
        default:
            return Pair.of(0.0, false);
        }
    }

}
