package org.checkerframework.checker.oigj;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.SuppressWarningsKeys;

/**
 * <!-- TODO: reinstate once manual chapter exists: @checker_framework.manual #oigj-checker OIGJ Checker -->
 */
@SuppressWarningsKeys({ "immutability", "oigj" })
public class ImmutabilitySubchecker extends BaseTypeChecker {
    /*
    @Override
    public void initChecker() {
        super.initChecker();
    }
    */
}
