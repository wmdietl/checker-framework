package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.time.duration.TimeDuration;
import org.checkerframework.checker.units.qual.time.point.TimePoint;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
 */

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import com.sun.source.tree.*;

/**
 * This class encodes the units checker's type rules for all math arithmetic
 * operators, compound assignment operators, and comparison operators.
 *
 * There are 2 main methods in this class:
 * {@link #processMathOperation(ExpressionTree, Tree.Kind, AnnotatedTypeMirror, AnnotatedTypeMirror, AnnotatedTypeMirror)}
 * and
 * {@link #processCompoundAssignmentOperation(CompoundAssignmentTree, Tree.Kind, AnnotatedTypeMirror, AnnotatedTypeMirror, AnnotatedTypeMirror)}
 *
 * Both methods are designed with {@link Tree.Kind} parameter to allow for
 * flexibility in applying the rules in methods that perform these calculations
 * and comparisons, but whereas the AST node's Kind is not necessarily the
 * specific operators. See each method's javadoc for examples.
 *
 * @author jeff luo
 */
public class UnitsMathOperatorsRelations {
    private final UnitsAnnotatedTypeFactory factory;
    private final UnitsChecker checker;
    private final ProcessingEnvironment processingEnv;
    private final Elements elements;
    private final UnitsRelationsManager unitsRelations;

    protected UnitsMathOperatorsRelations(UnitsChecker uChecker, UnitsAnnotatedTypeFactory uFactory) {
        this.checker = uChecker;
        this.factory = uFactory;
        this.processingEnv = checker.getProcessingEnvironment();
        this.elements = factory.getElementUtils();
        this.unitsRelations = factory.getUnitsRelationsManager();
    }

    // fall-through of comparison cases are intended
    /**
     * This method will assign a result type for a given arithmetic or
     * comparison operation based on the given kind, the left hand type, and
     * right hand type. Errors may also be issued on the given AST node if there
     * are any rule violations.
     *
     * It is called at {@code UnitsPropagationTreeAnnotator.visitBinary()} for
     * checking operators.
     *
     * It is also designed to support arithmetic or comparison behavior in
     * various methods such as {@link Integer#sum(int, int)},
     * {@link Math#multiplyExact(int, int)}, and
     * {@link Double#compareTo(Double)}.
     *
     * @param node the AST node of the arithmetic or comparison operation
     * @param kind the kind of the operation
     * @param resultType the result unit
     * @param lht left hand type of the operation
     * @param rht right hand type of the operation
     */
    @SuppressWarnings("fallthrough")
    public void processMathOperation(ExpressionTree node, Tree.Kind kind, AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
        // Remove Prefix.one
        if (UnitsRelationsTools.getPrefix(lht) == Prefix.one) {
            lht = UnitsRelationsTools.removePrefix(elements, lht);
        }
        if (UnitsRelationsTools.getPrefix(rht) == Prefix.one) {
            rht = UnitsRelationsTools.removePrefix(elements, rht);
        }

        // First use units relations to resolve the operation
        AnnotationMirror bestResult = null;
        for (UnitsRelations ur : unitsRelations.getUnitsRelationsMap().values()) {
            AnnotationMirror res = useUnitsRelation(kind, ur, lht, rht);

            if (bestResult != null && res != null && !bestResult.equals(res)) {
                checker.message(Kind.WARNING, "UnitsRelation mismatch, taking neither! Previous: "
                        + bestResult + " and current: " + res);
                return;
            }

            if (res != null) {
                bestResult = res;
            }
        }
        // If there's a result from units relations, then set the result
        // type to that and return
        if (bestResult != null) {
            resultType.replaceAnnotation(bestResult);
            return;
        }

        // If none of the units relations classes could resolve the
        // units, then apply default rules as follows:
        switch (kind) {
        case PLUS:
            processPlus(resultType, lht, rht, node);
            break;
        case MINUS:
            processMinus(resultType, lht, rht);
            break;
        case MULTIPLY:
            processMultiply(resultType, lht, rht, node);
            break;
        case DIVIDE:
            processDivide(resultType, lht, rht, node);
            break;
        case REMAINDER:
            processRemainder(resultType, lht);
            break;
        case EQUAL_TO:
        case NOT_EQUAL_TO:
        case GREATER_THAN:
        case GREATER_THAN_EQUAL:
        case LESS_THAN:
        case LESS_THAN_EQUAL:
            processComparison(resultType, lht, rht, node);
            break;
        default:
            // Placeholders for unhandled binary operations
            // Do nothing
            break;
        }
    }

    /**
     * This helper method encodes the rules that only apply to addition.
     *
     * @param resultType the result unit
     * @param lht left hand type of addition
     * @param rht right hand type of addition
     * @param node the AST node of the addition
     */
    private void processPlus(AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht, ExpressionTree node) {
        // if only left or right is a string, replace with the types of the
        // string
        if (factory.isSameUnderlyingType(lht, factory.stringType)) {
            resultType.replaceAnnotations(lht.getAnnotations());
            return;
        } else if (factory.isSameUnderlyingType(rht, factory.stringType)) {
            resultType.replaceAnnotations(rht.getAnnotations());
            return;
        } else if (factory.isSameUnderlyingType(lht, factory.stringType)
                && factory.isSameUnderlyingType(rht, factory.stringType)) {
            // replace with the LUB of the two strings, but it should always be
            // scalar
            resultType.replaceAnnotations(getLUBs(lht, rht));
            return;
        } else if (isTimePoint(lht) && isTimePoint(rht)) {
            // Time point + time point ==> error
            // One cannot add the 4th month of a year to the 7th month of some
            // other year
            checker.report(Result.failure("time.point.addition.disallowed", lht.toString(), rht.toString()), node);
        } else {
            // addition of other units is the same as subtraction in terms of
            // rules
            processMinus(resultType, lht, rht);
        }
    }

    /**
     * This helper method encodes the rules for subtraction, which is also
     * shared by addition.
     *
     * @param resultType the result unit
     * @param lht left hand type of addition or subtraction
     * @param rht right hand type of addition or subtraction
     */
    private void processMinus(AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
        // Process time units first
        if (UnitsRelationsTools.hasSpecificUnit(lht, factory.TOP)
                || UnitsRelationsTools.hasSpecificUnit(rht, factory.TOP)) {
            // unknown + or - unknown = unknown
            // unknown + or - anything = unknown
            // anything + or - unknown = unknown
            resultType.replaceAnnotation(factory.TOP);
        } else if (isTimePoint(lht) && isTimePoint(rht)) {
            if (UnitsRelationsTools.areSameUnits(lht, rht)) {
                // Time point - time point ==> timeDuration if they are the
                // same units
                resultType.replaceAnnotation(UnitsRelationsTools.getTimeDurationUnit(processingEnv, lht));
            } else {
                // subtraction of two different time point units results in
                // the LUB of the two respective time duration units
                AnnotationMirror lhtDurationUnit = UnitsRelationsTools.getTimeDurationUnit(processingEnv, lht);
                AnnotationMirror rhtDurationUnit = UnitsRelationsTools.getTimeDurationUnit(processingEnv, rht);
                resultType.replaceAnnotation(getLUB(lhtDurationUnit, rhtDurationUnit));
            }
        } else if (isTimePoint(lht) && isTimeDuration(rht)) {
            // point +/- duration => point
            processInstantAndDurationMathOperation(resultType, lht, rht);
        } else if (isTimeDuration(lht) && isTimePoint(rht)) {
            // duration +/- point => point
            processInstantAndDurationMathOperation(resultType, rht, lht);
        } else if (UnitsRelationsTools.areSameUnits(lht, rht)) {
            // If both operands for sum or difference have the same
            // units, we return the unit
            // this includes duration +/- duration => duration
            resultType.replaceAnnotations(lht.getAnnotations());
        } else {
            // otherwise we return in the LUB of the left and right
            // types
            // this is more flexible: e.g. km +/- meter ==> length
            resultType.replaceAnnotations(getLUBs(lht, rht));
        }
    }

    /**
     * This helper method encodes the rules for multiplication.
     *
     * Note that multiplication rules encoded in a {@link UnitsRelations}
     * subclass precedes these rules.
     *
     * @param resultType the result unit
     * @param lht left hand type of the multiplication, or the multiplier
     * @param rht right hand type of the multiplication, or the multiplicand
     * @param node the AST node of the multiplication
     */
    private void processMultiply(AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht, ExpressionTree node) {
        if (UnitsRelationsTools.hasSpecificUnit(lht, factory.TOP)
                || UnitsRelationsTools.hasSpecificUnit(rht, factory.TOP)) {
            // unknown * unknown = unknown
            // unknown * anything = unknown
            // anything * unknown = unknown
            resultType.replaceAnnotation(factory.TOP);
        } else if ((isTimeDuration(lht) && isTimePoint(rht)) ||
                (isTimePoint(lht) && isTimeDuration(rht)) ||
                (isTimePoint(lht) && isTimePoint(rht))) {
            // duration * time point = invalid
            // time point * duration = invalid
            // time point * time point = invalid
            checker.report(Result.failure("time.point.multiplication.disallowed", lht.toString(), rht.toString()), node);
        } else if (UnitsRelationsTools.hasNoUnits(lht)) {
            // any unit multiplied by a scalar keeps the unit
            // also unknown * scalar = unknown
            resultType.replaceAnnotations(rht.getAnnotations());
        } else if (UnitsRelationsTools.hasNoUnits(rht)) {
            // any scalar multiplied by a unit becomes the unit
            // also scalar * unknown = unknown
            resultType.replaceAnnotations(lht.getAnnotations());
        } else {
            // else it is a multiplication of two units that have no
            // defined relations from a relations class, so we
            // return unknown
            // Future TODO: track partial units eg (unit ^ 2), so that
            // equations like unit * unit / unit == unit work
            resultType.replaceAnnotation(factory.TOP);
        }
    }

    /**
     * This helper method encodes the rules for division.
     *
     * Note that division rules encoded in a {@link UnitsRelations} subclass
     * precedes these rules.
     *
     * @param resultType the result unit
     * @param lht left hand type of the division, or the dividend
     * @param rht right hand type of the division, or the divisor
     * @param node the AST node of the division
     */
    private void processDivide(AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht, ExpressionTree node) {
        if (UnitsRelationsTools.hasSpecificUnit(lht, factory.TOP)
                || UnitsRelationsTools.hasSpecificUnit(rht, factory.TOP)) {
            // unknown / unknown = unknown
            // unknown / anything = unknown
            // anything / unknown = unknown
            resultType.replaceAnnotation(factory.TOP);
        } else if ((isTimeDuration(lht) && isTimePoint(rht)) ||
                (isTimePoint(lht) && isTimeDuration(rht)) ||
                (isTimePoint(lht) && isTimePoint(rht))) {
            // duration / time point = invalid
            // time point / duration = invalid
            // time point / time point = invalid
            checker.report(Result.failure("time.point.division.disallowed", lht.toString(), rht.toString()), node);
        } else if (UnitsRelationsTools.areSameUnits(lht, rht)) {
            // if the units of the division match, return scalar
            resultType.replaceAnnotation(factory.scalar);
        } else if (UnitsRelationsTools.hasNoUnits(rht)) {
            // any unit divided by a scalar keeps that unit
            resultType.replaceAnnotations(lht.getAnnotations());
        } else if (UnitsRelationsTools.hasNoUnits(lht)) {
            // scalar divided by any unit returns unknown
            // Future TODO: track partial units eg (scalar / unit), so
            // that equations like scalar / unit * unit == scalar
            // work
            resultType.replaceAnnotation(factory.TOP);
        } else {
            // else it is a division of two units that have no
            // defined relations from a units relations class
            // return unknown
            // Future TODO: track partial units eg (unit1 / unit2), so
            // that equations like unit1 / unit2 * unit2 == unit1
            // work
            resultType.replaceAnnotation(factory.TOP);
        }
    }

    /**
     * This helper method encodes the rules for modulo.
     *
     * @param resultType the result unit
     * @param lht the left hand type of the modulo, or the dividend
     */
    private void processRemainder(AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht) {
        // in modulo operation, it always returns the left unit
        // regardless of what it is (scalar, unknown, or some unit)
        resultType.replaceAnnotations(lht.getAnnotations());
    }

    /**
     * This helper method encodes the rules for comparison operations.
     *
     * Comparisons are allowed between a unit and scalar at any time, and
     * between a unit and UnitsBottom (type of null reference). Otherwise,
     * comparisons must be between exactly the same units.
     *
     * @param resultType the result unit, which is set to scalar if the
     *            comparison check passes
     * @param lht left hand type of the comparison
     * @param rht right hand type of the comparison
     * @param node the AST node of the comparison
     */
    private void processComparison(AnnotatedTypeMirror resultType, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht, ExpressionTree node) {
        if (UnitsRelationsTools.areSameUnits(lht, rht)
                || UnitsRelationsTools.hasSpecificUnit(lht, factory.scalar)
                || UnitsRelationsTools.hasSpecificUnit(rht, factory.scalar)
                || UnitsRelationsTools.hasSpecificUnit(lht, factory.BOTTOM)
                || UnitsRelationsTools.hasSpecificUnit(rht, factory.BOTTOM)) {
            // if the units are the same, or either are scalar, or either are
            // bottom (for null reference comparison) then set the resulting
            // boolean or integer to scalar
            // Note: a boolean result is most common, eg x >= y, or x.equals(y)
            // an int result is also possible for Integer.compare(x, y)
            resultType.replaceAnnotation(factory.scalar);
        } else {
            // otherwise if the operands have different units, then alert error
            checker.report(Result.failure("operands.unit.mismatch", lht.toString(), rht.toString()), node);
        }
    }

    // fall-through of PLUS_ASSIGNMENT and MULTIPLY_ASSIGNMENT cases are
    // intended
    /**
     * This method encodes the rules for checking a given compound assignment
     * operation based on the given kind, the left hand variable type, and right
     * hand expression type. Errors may also be issued on the given AST node if
     * there are any rule violations.
     *
     * It is called at two locations for checking operators:
     * {@code UnitsPropagationTreeAnnotator.visitCompoundAssignment()} and
     * {@link UnitsVisitor#visitCompoundAssignment(CompoundAssignmentTree, Void)}
     * . In the former, a resultType is also passed in where the logic here
     * updates the result type accordingly. In the latter, resultType passed in
     * is null.
     *
     * It is also designed to support compound assignment behavior in boxed
     * types (or more generally any method) such as
     * {@link AtomicLong#getAndAdd(long)}.
     *
     * @param node the AST node of the compound assignment operation
     * @param kind the kind of the operation
     * @param resultType the result unit
     * @param varType left hand type of the operation
     * @param exprType right hand type of the operation
     */
    @SuppressWarnings("fallthrough")
    public void processCompoundAssignmentOperation(CompoundAssignmentTree node, Tree.Kind kind, /*@Nullable*/ AnnotatedTypeMirror resultType, AnnotatedTypeMirror varType, AnnotatedTypeMirror exprType) {
        switch (kind) {
        case PLUS_ASSIGNMENT:
            // skip checking addition on Strings
            if (factory.isSameUnderlyingType(varType, factory.stringType)) {
                break;
            }
            // otherwise the check is the same as for minus assign
            // fallthrough intended!
        case MINUS_ASSIGNMENT:
            // expr has to be a subtype of var, if so, the result has the same
            // type as var
            if (!factory.getTypeHierarchy().isSubtype(exprType, varType)) {
                checker.report(Result.failure("compound.assignment.type.incompatible", exprType, varType), node);
            }
            break;
        case MULTIPLY_ASSIGNMENT:
            // fallthrough intended!
        case DIVIDE_ASSIGNMENT:
            // if the var is @UnknownUnits, the result is @UnknownUnits
            // if the expr is @Scalar, the result keeps the unit of var
            // otherwise raise error
            if (!(UnitsRelationsTools.hasSpecificUnit(varType, factory.TOP)
                    || UnitsRelationsTools.hasSpecificUnit(exprType, factory.scalar))) {
                // if the var is any unit other than UnknownUnits, then the expr
                // can only have the type of scalar
                // generate the required type by copying the expr type and
                // replace the unit with scalar
                AnnotatedTypeMirror requiredType = exprType.deepCopy();
                requiredType.replaceAnnotation(factory.scalar);
                checker.report(Result.failure("compound.assignment.type.incompatible", exprType, requiredType), node);
            }
            break;
        case REMAINDER_ASSIGNMENT:
            // var keeps its type regardless of what expr is
            break;
        default:
            break;
        }

        if (resultType != null) {
            // if it passes the above checks, then the result type has the same
            // type as the variable
            resultType.replaceAnnotations(varType.getAnnotations());
        }
    }

    /**
     * Uses a units relations class to resolve and return an annotation mirror
     * representing the type of the result of a calculation, or null if this
     * units relations class doesn't specify what type to return
     *
     * @param kind Tree.Kind representing a mathematical operation
     * @param ur units relations class
     * @param lht left type
     * @param rht right type
     * @return an AnnotationMirror representing the type of the result of a
     *         calculation, or null if this units relations class doesn't
     *         specify what type to return
     */
    private AnnotationMirror useUnitsRelation(Tree.Kind kind, UnitsRelations ur, AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
        // TODO: add support for other relations?
        // compound assignment, comparison, plus, minus, remainder
        AnnotationMirror res = null;
        if (ur != null) {
            switch (kind) {
            case DIVIDE:
                res = ur.division(lht, rht);
                break;
            case MULTIPLY:
                res = ur.multiplication(lht, rht);
                break;
            default:
                // Do nothing
            }
        }
        return res;
    }

    // ========================================
    // Helper methods for processing units
    // ========================================

    /**
     * This helper method encodes the rules for adding a time duration to a time
     * point. E.g. 5 am + 5 hours = 10 am
     *
     * If the time point is based on the same unit as the duration, then it
     * will return the {@link TimePoint} unit.
     *
     * @param resultType the result unit
     * @param point the time point unit
     * @param duration the time duration unit
     */
    private void processInstantAndDurationMathOperation(AnnotatedTypeMirror resultType, AnnotatedTypeMirror point, AnnotatedTypeMirror duration) {
        if (UnitsRelationsTools.areSameUnits(UnitsRelationsTools.getTimeDurationUnit(processingEnv, point), UnitsRelationsTools.getUnit(duration))) {
            // If the point is based upon the same unit as the duration,
            // then Time point + or - time duration => time point
            resultType.replaceAnnotations(point.getAnnotations());
        } else {
            // if the point isn't based upon the same unit however, then
            // return @TimePoint
            resultType.replaceAnnotation(factory.timeInstant);
        }
    }

    /**
     * Checks to see if the units annotation in the annotated type mirror is a
     * subtype of {@link TimeDuration}
     *
     * @param atm annotated type mirror with a units annotation
     * @return true if it is a subtype of {@link TimeDuration}, false otherwise
     */
    protected boolean isTimeDuration(final AnnotatedTypeMirror atm) {
        return annotatedTypeIsSubtype(atm, factory.timeDuration);
    }

    // There is also a version of isTimePoint in UnitsRelationsTools. The
    // version here is dependent on the type factory and qualifier
    // hierarchy, and cannot be statically called by external developers who
    // wish to provide units extensions. The UnitsRelationsTools version checks
    // by examining the meta-annotations and can be statically called without
    // this dependence.
    //
    // TODO: possible to unify the two designs?
    /**
     * Checks to see if the units annotation in the annotated type mirror is a
     * subtype of {@link TimePoint}
     *
     * @param atm annotated type mirror with a units annotation
     * @return true if it is a subtype of {@link TimePoint}, false otherwise
     */
    protected boolean isTimePoint(final AnnotatedTypeMirror atm) {
        return annotatedTypeIsSubtype(atm, factory.timeInstant);
    }

    /**
     * This helper method checks to see if the units annotation in the annotated
     * type mirror is a subtype of superType. In Units checker, each annotated
     * type mirror can only have 1 units annotation at any given time
     *
     * @param atm annotated type mirror with a units annotation
     * @param superType an annotation mirror denoting a supertype
     * @return true if the units annotation in the atm is a subtype of superType
     */
    private boolean annotatedTypeIsSubtype(final AnnotatedTypeMirror atm, final AnnotationMirror superType) {
        // if one of the annotations of the atm is a subtype of superType then
        // return true
        for (AnnotationMirror anno : atm.getEffectiveAnnotations()) {
            if (factory.getQualifierHierarchy().isSubtype(anno, superType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the set of least upper bounds of the two annotated types through
     * the type factory
     *
     * @param lht left type
     * @param rht right type
     * @return the set of least upper bounds of the two annotated types
     */
    private Set<? extends AnnotationMirror> getLUBs(AnnotatedTypeMirror lht, AnnotatedTypeMirror rht) {
        return factory.getQualifierHierarchy().leastUpperBounds(lht, rht, lht.getAnnotations(), rht.getAnnotations());
    }

    /**
     * Returns the least upper bound of two annotation mirrors through the type
     * factory
     *
     * @param left left annotation
     * @param right right annotation
     * @return the least upper bound of two annotation mirrors
     */
    private AnnotationMirror getLUB(AnnotationMirror left, AnnotationMirror right) {
        return factory.getQualifierHierarchy().leastUpperBound(left, right);
    }
}
