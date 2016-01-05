package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.MixedUnits;
import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.checker.units.qual.UnitsBottom;
import org.checkerframework.checker.units.qual.UnknownUnits;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationUtils;

import javax.lang.model.element.AnnotationMirror;

import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree.Kind;

/**
 * Units visitor.
 *
 * Ensure consistent use of compound assignments.
 */
public class UnitsVisitor extends BaseTypeVisitor<UnitsAnnotatedTypeFactory> {
    protected final AnnotationMirror mixedUnits = AnnotationUtils.fromClass(elements, MixedUnits.class);
    protected final AnnotationMirror scalar = AnnotationUtils.fromClass(elements, Scalar.class);
    protected final AnnotationMirror TOP = AnnotationUtils.fromClass(elements, UnknownUnits.class);
    protected final AnnotationMirror BOTTOM = AnnotationUtils.fromClass(elements, UnitsBottom.class);

    public UnitsVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
        ExpressionTree var = node.getVariable();
        ExpressionTree expr = node.getExpression();
        AnnotatedTypeMirror varType = atypeFactory.getAnnotatedType(var);
        AnnotatedTypeMirror exprType = atypeFactory.getAnnotatedType(expr);

        Kind kind = node.getKind();

        // plus and minus assignment
        if ((kind == Kind.PLUS_ASSIGNMENT || kind == Kind.MINUS_ASSIGNMENT)) {
            // if the right hand side (expr) is not a subtype of the left hand side (var) then throw error
            if(!atypeFactory.getTypeHierarchy().isSubtype(exprType, varType)) {
                checker.report(Result.failure("compound.assignment.type.incompatible",
                        varType, exprType), node);
            }
        }
        // multiply, divide, modulus assignment
        else if ((kind == Kind.MULTIPLY_ASSIGNMENT || kind == Kind.DIVIDE_ASSIGNMENT || kind == Kind.REMAINDER_ASSIGNMENT)) {
            if (UnitsRelationsTools.hasSpecificUnit(varType, TOP)) {
                // if the left hand side is unknown, turn it into whatever is on right hand side
                varType.replaceAnnotations(exprType.getAnnotations());
            } else if (!UnitsRelationsTools.hasSpecificUnit(exprType, scalar)) {
                // if the right hand side is not a scalar then throw error
                // Only allow mul/div with unqualified units
                checker.report(Result.failure("compound.assignment.type.incompatible",
                        varType, exprType), node);
            }
        }

        return null; // super.visitCompoundAssignment(node, p);
    }
}