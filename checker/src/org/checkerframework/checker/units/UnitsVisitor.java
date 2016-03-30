package org.checkerframework.checker.units;

import org.checkerframework.checker.units.qual.Scalar;
import org.checkerframework.checker.units.qual.UnitsBottom;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.javacutil.Pair;
import org.checkerframework.javacutil.TreeUtils;

import java.util.List;

import javax.lang.model.element.ElementKind;

import com.sun.source.tree.*;

/**
 * Units visitor.
 */
public class UnitsVisitor extends BaseTypeVisitor<UnitsAnnotatedTypeFactory> {
    public UnitsVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
        AnnotatedTypeMirror varType = atypeFactory.getAnnotatedType(node.getVariable());
        AnnotatedTypeMirror exprType = atypeFactory.getAnnotatedType(node.getExpression());
        Tree.Kind kind = node.getKind();

        atypeFactory.getUnitsMathOperatorsRelations()
            .processCompoundAssignmentOperation(node, kind, null, varType, exprType);

        return null;
    }

    // Allow references to be declared using any units annotation except
    // UnitsBottom. Classes are by default Scalar, but these reference
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

    // Allow the creation of objects using any units annotation except
    // UnitsBottom. Classes are by default Scalar, but these objects may use
    // some unit that isn't a subtype of Scalar.
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
        return declaredType.getEffectiveAnnotation(Scalar.class) != null
                && useType.getEffectiveAnnotation(UnitsBottom.class) == null;
    }

    // allow the passing of scalar number literals into method parameters that
    // require a unit. all parameters are scalar by default.
    // Developer Notes: keep in sync with super implementation.
    @Override
    protected void checkArguments(List<? extends AnnotatedTypeMirror> requiredArgs, List<? extends ExpressionTree> passedArgs) {
        assert requiredArgs.size() == passedArgs.size() : "mismatch between required args ("
                + requiredArgs + ") and passed args (" + passedArgs + ")";

        Pair<Tree, AnnotatedTypeMirror> preAssCtxt = visitorState.getAssignmentContext();
        try {
            for (int i = 0; i < requiredArgs.size(); ++i) {
                visitorState.setAssignmentContext(Pair.<Tree, AnnotatedTypeMirror> of((Tree) null, (AnnotatedTypeMirror) requiredArgs.get(i)));

                // Units Checker Code =======================
                AnnotatedTypeMirror requiredArg = requiredArgs.get(i);
                ExpressionTree passedExpression = passedArgs.get(i);
                AnnotatedTypeMirror passedArg = atypeFactory.getAnnotatedType(passedExpression);

                if (UnitsRelationsTools.hasSpecificUnit(passedArg, atypeFactory.scalar)
                        && UnitsRelationsTools.isPrimitiveNumberLiteralExpression(passedExpression)
                        && !UnitsRelationsTools.hasSpecificUnit(requiredArg, atypeFactory.BOTTOM)) {
                    // if the method argument is a scalar number literal, or a
                    // numerical expression consisting only of scalar literals,
                    // and the method parameter is has any unit other than
                    // UnitsBottom, pass, as those literals are tied to
                    // those method calls and can be safely assumed to take on
                    // the unit of the parameter. Scalar variables passed to
                    // such methods still result in errors.
                } else {
                    // Developer note: keep in sync with super implementation
                    commonAssignmentCheck(requiredArg, passedExpression, "argument.type.incompatible", false);
                }
                // End Units Checker Code ===================

                // Also descend into the argument within the correct assignment
                // context.
                scan(passedArgs.get(i), null);
            }
        } finally {
            visitorState.setAssignmentContext(preAssCtxt);
        }
    }

    // allow the invocation of a method defined in a Scalar class on an
    // UnknownUnits object (all classes are scalar by default)
    // Developer Notes: keep in sync with super implementation.
    @Override
    protected void checkMethodInvocability(AnnotatedExecutableType method, MethodInvocationTree node) {
        if (method.getReceiverType() == null) {
            // Static methods don't have a receiver.
            return;
        }
        if (method.getElement().getKind() == ElementKind.CONSTRUCTOR) {
            // TODO: Explicit "this()" calls of constructors have an implicit
            // passed
            // from the enclosing constructor. We must not use the self type,
            // but
            // instead should find a way to determine the receiver of the
            // enclosing constructor.
            // rcv =
            // ((AnnotatedExecutableType)atypeFactory.getAnnotatedType(atypeFactory.getEnclosingMethod(node))).getReceiverType();
            return;
        }

        AnnotatedTypeMirror methodReceiver = method.getReceiverType().getErased();
        AnnotatedTypeMirror treeReceiver = methodReceiver.shallowCopy(false);
        AnnotatedTypeMirror rcv = atypeFactory.getReceiverType(node);

        treeReceiver.addAnnotations(rcv.getEffectiveAnnotations());

        if (skipReceiverSubtypeCheck(node, methodReceiver, rcv)) {
            return;
        }

        // Units Checker Code =======================
        // if the method receiver is Scalar and the receiving object is
        // UnknownUnits, pass
        if (UnitsRelationsTools.hasSpecificUnit(methodReceiver, atypeFactory.scalar)
                && UnitsRelationsTools.hasSpecificUnit(treeReceiver, atypeFactory.TOP)) {
            return;
        }
        // End Units Checker Code ===================

        if (!atypeFactory.getTypeHierarchy().isSubtype(treeReceiver, methodReceiver)) {
            checker.report(Result.failure("method.invocation.invalid", TreeUtils.elementFromUse(node), treeReceiver.toString(), methodReceiver.toString()), node);
        }
    }
}