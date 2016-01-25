package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.checker.units.qual.UnitsBottom;
import org.checkerframework.checker.units.qual.UnknownUnits;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.javacutil.AnnotationUtils;

import javax.lang.model.element.AnnotationMirror;

import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;

/**
 * Units visitor.
 *
 * Ensure consistent use of compound assignments.
 */
public class UnitsVisitor extends BaseTypeVisitor<UnitsAnnotatedTypeFactory> {
    protected final AnnotationMirror scalar = AnnotationUtils.fromClass(elements, Scalar.class);
    protected final AnnotationMirror TOP = AnnotationUtils.fromClass(elements, UnknownUnits.class);
    protected final AnnotationMirror BOTTOM = AnnotationUtils.fromClass(elements, UnitsBottom.class);

    public UnitsVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    // Override to allow references to be declared using any units annotation
    // except UnitsBottom. Classes are by default Scalar, but these reference
    // declarations will use some unit that isn't a subtype of Scalar.
    @Override
    public boolean isValidUse(AnnotatedDeclaredType declarationType, AnnotatedDeclaredType useType, Tree tree) {
        // eg for the statement "@m Double x;" the declarationType is @Scalar
        // Double, and the useType is @m Double
        if (isValidDeclarationTypeUse(declarationType, useType)) {
            // if declared type of a class is Scalar, and the use of that class
            // is any of the Units annotations other than UnitsBottom, return
            // true
            return true;
        } else {
            // otherwise check the usage using super
            return super.isValidUse(declarationType, useType, tree);
        }
    }

    // Override to allow the creation of objects using any units annotation
    // except UnitsBottom. Classes are by default Scalar, but these objects may
    // use some unit that isn't a subtype of Scalar.
    @Override
    protected boolean checkConstructorInvocation(AnnotatedDeclaredType useType, AnnotatedExecutableType constructor, Tree src) {
        // The declared constructor return type is the same as the declared type
        // of the class that is being constructed, by default this will be
        // Scalar.
        // eg for the statement "new @m Double(30.0);" the return type is
        // @Scalar
        // Double while the declared use type is @m Double.
        AnnotatedTypeMirror declaredConstructorReturnType = constructor.getReturnType();

        if (isValidDeclarationTypeUse(declaredConstructorReturnType, useType)) {
            return true;
        } else {
            // otherwise check the constructor invocation using super
            return super.checkConstructorInvocation(useType, constructor, src);
        }
    }

    // If a class is declared as Scalar, and the use of the class is any units
    // annotation except UnitsBottom, then return true
    private boolean isValidDeclarationTypeUse(AnnotatedTypeMirror declaredType, AnnotatedTypeMirror useType) {
        return declaredType.getEffectiveAnnotation(Scalar.class) != null &&
                useType.getEffectiveAnnotation(UnitsBottom.class) == null;
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
            // if the right hand side (expr) is not a subtype of the left hand
            // side (var) then throw error
            if (!atypeFactory.getTypeHierarchy().isSubtype(exprType, varType)) {
                checker.report(Result.failure("compound.assignment.type.incompatible",
                        varType, exprType), node);
            }
        }
        // multiply, divide, modulus assignment
        else if ((kind == Kind.MULTIPLY_ASSIGNMENT || kind == Kind.DIVIDE_ASSIGNMENT || kind == Kind.REMAINDER_ASSIGNMENT)) {
            if (UnitsRelationsTools.hasSpecificUnit(varType, TOP)) {
                // if the left hand side is unknown, turn it into whatever is on
                // right hand side
                varType.replaceAnnotations(exprType.getAnnotations());
            } else if (!UnitsRelationsTools.hasSpecificUnit(exprType, scalar)) {
                // if the right hand side is not a scalar then throw error
                // Only allow mul/div with unqualified units
                checker.report(Result.failure("compound.assignment.type.incompatible",
                        varType, exprType), node);
            }
        }

        return null;
    }
}