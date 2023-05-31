package org.checkerframework.framework.util.defaults;

import org.plumelib.util.StringsPlume;

import java.util.TreeSet;

/**
 * An ordered set of Defaults (see {@link org.checkerframework.framework.util.defaults.Default}).
 * This class provides a little syntactic sugar and a better toString over TreeSet. Defaults are
 * compared by their locations only, ensuring that a DefaultSet contains at most one default per
 * location.
 */
@SuppressWarnings("serial")
class DefaultSet extends TreeSet<Default> {

    /** Creates a DefaultSet. */
    public DefaultSet() {
        super(Default::compareToLocationOnly);
    }

    @Override
    public String toString() {
        return "DefaultSet( " + StringsPlume.join(", ", this) + " )";
    }

    public static final DefaultSet EMPTY = new DefaultSet();
}
