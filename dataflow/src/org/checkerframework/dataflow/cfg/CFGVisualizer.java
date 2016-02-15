package org.checkerframework.dataflow.cfg;

/*>>>
import org.checkerframework.checker.nullness.qual.Nullable;
*/

import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.FlowExpressions;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.dataflow.cfg.node.Node;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.Element;

/**
 * Perform some visualization on a control flow graph.
 * The particular operations depend on the particular implementation.
 */
public interface CFGVisualizer<A extends AbstractValue<A>,
        S extends Store<S>, T extends TransferFunction<A, S>> {
    /**
     * Initialization method guaranteed to be called once before the
     * first invocation of {@link visualize}.
     *
     * @param args Implementation-dependent options.
     */
    void init(Map<String, Object> args);

    /**
     * Output a visualization representing the control flow graph starting
     * at <code>entry</code>.
     * The concrete actions are implementation dependent.
     *
     * An invocation <code>visualize(cfg, entry, null);</code> does not
     * output stores at the beginning of basic blocks.
     *
     * @param cfg
     *            The CFG to visualize.
     * @param entry
     *            The entry node of the control flow graph to be represented.
     * @param analysis
     *            An analysis containing information about the program
     *            represented by the CFG. The information includes {@link Store}
     *            s that are valid at the beginning of basic blocks reachable
     *            from <code>entry</code> and per-node information for value
     *            producing {@link Node}s. Can also be <code>null</code> to
     *            indicate that this information should not be output.
     * @return Possible analysis results, e.g. generated files.
     */
    /*@Nullable*/ Map<String, Object> visualize(ControlFlowGraph cfg, Block entry,
            /*@Nullable*/ Analysis<A, S, T> analysis);

    /**
     * This is a double-direction delegate pattern interface.
     * this method would delegate the visualize responsibility
     * to the passed store instance.
     * Then the store would do something and delegate the 
     * visualize responsibility back to the CFGVisualizer.
     * @param store
     */
    void visualizeStore(S store);

    void visualizeStoreHeader(String classCanonicalName);

    void visualizeStoreFooter();

    void visualizeLocalVariable(FlowExpressions.LocalVariable localVar, A value);

    void visualizeThisValue(A value);

    void visualizeFieldValues(FlowExpressions.FieldAccess fieldAccess, A value);

    void visualizeArrayValue(FlowExpressions.ArrayAccess arrayValue, A value);

    void visualizeMethodValues(FlowExpressions.PureMethodCall methodCall, A value);

    void visualizeClassValues(FlowExpressions.ClassName className, A value);

    void visualizeKeyValue(String keyName, Object value);

    /** Shutdown method called once from the shutdown hook of the BaseTypeChecker.
     */
    void shutdown();
}
