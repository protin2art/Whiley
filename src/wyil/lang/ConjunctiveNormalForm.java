package wyil.lang;

import java.util.ArrayList;

import wyautl.lang.*;

/**
 * <p>
 * This simplification rule converts a type into <i>conjunctive normal form</i>.
 * This achieved by repeated application of the following rewrites:
 * </p>
 * <ul>
 * <li><code>T | any</code> => <code>any</code>.</li>
 * <li><code>T & void</code> => <code>void</code>.</li>
 * <li><code>!!T</code> => <code>T</code>.</li>
 * <li><code>!any</code> => <code>void</code>.</li>
 * <li><code>!void</code> => <code>any</code>.</li>
 * <li><code>!(T_1 | T_2)</code> => <code>!T_1 & !T_2</code>.</li>
 * <li><code>!(T_1 & T_2)</code> => <code>!T_1 | !T_2</code>.</li>
 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2 & T_3)</code>.</li>
 * <li><code>T_1 & (T_2|T_3)</code> => <code>(T_1 & T_2) | (T_1 & T_3)</code>.</li>
 * </ul>
 * 
 * @author djp
 * 
 */
public final class ConjunctiveNormalForm implements RewriteRule {
	
	public final boolean apply(int index, Automata automata) {
		Automata.State state = automata.states[index];
		switch(state.kind) {
		case Type.K_UNION:
			return applyUnion(index,state,automata);
		case Type.K_INTERSECTION:
			return applyIntersection(index,state,automata);
		case Type.K_NEGATION:
			return applyNot(index,state,automata);
		}
		return false;
	}
	
	public boolean applyNot(int index, Automata.State state,
			Automata automata) {				
		int childIndex = state.children[0];
		Automata.State child = automata.states[childIndex];
		switch(child.kind) {
			case Type.K_ANY:
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			case Type.K_VOID:
				automata.states[index] = new Automata.State(Type.K_ANY);
				return true;
			case Type.K_NEGATION:
				// bypass this node altogether
				int childChildIndex = child.children[0];
				Automata.State childChild = automata.states[childChildIndex];
				automata.states[index] = new Automata.State(childChild);
				return true;
			case Type.K_UNION:
			case Type.K_INTERSECTION: {						
				int[] child_children = child.children;
				int[] nchildren = new int[child_children.length];
				Automata.State[] nstates = new Automata.State[child_children.length];				
				for(int i=0;i!=child_children.length;++i) {					
					int[] children = new int[1];
					children[0] = child_children[i];
					nchildren[i] = i + automata.size();
					nstates[i] = new Automata.State(Type.K_NEGATION,children);
				}
				Automatas.inplaceAppendAll(automata, nstates);
				state = automata.states[index];
				int nkind = child.kind == Type.K_UNION ? Type.K_INTERSECTION : Type.K_UNION;
				state.kind = nkind;				
				state.children = nchildren;
				state.deterministic = true;
				
				return true;
			}
			default:
				return false;
		}
	}
	
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {				
		int[] children = state.children;		
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			switch(child.kind) {
			case Type.K_VOID:								
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			case Type.K_INTERSECTION:
				return flattenChildren(index,i,state,automata);
			case Type.K_UNION:
				return distributeChildren(index,i,state,automata);			
			}
		}
		return false;		
	}
	
	/**
	 * This rule distributes over children of type UNION
	 * 
	 * @param pivot
	 *            --- the distribution point
	 * @param index
	 *            --- index of automata state
	 * @param state
	 *            --- automata state in question
	 * @param automata
	 * @return
	 */
	public boolean distributeChildren(int pivot, int index, Automata.State state,
			Automata automata) {
		// x&y&(a|b)&z => x&y&z&a | x&y&z&b
		
		return false;
	}
		
	public boolean applyUnion(int index, Automata.State state,
			Automata automata) {		
		int[] children = state.children;				
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			switch(child.kind) {
			case Type.K_ANY:								
				automata.states[index] = new Automata.State(Type.K_ANY);
				return true;			
			case Type.K_UNION:
				return flattenChildren(index,i,state,automata);
			}
		}
		return false;
	}
	
	/**
	 * This rule flattens children which have the same kind as the given state.
	 * 
	 * @param index
	 * @param state
	 * @param automata
	 * @return
	 */
	public boolean flattenChildren(int index, int start,
			Automata.State state, Automata automata) {
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		int[] children = state.children;
		final int kind = state.kind;		
		
		for (int i = start; i != children.length; ++i) {
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			if(child.kind == kind) {
				for (int c : child.children) {
					nchildren.add(c);
				}
			} else {				
				nchildren.add(iChild);
			}
		}

		children = new int[nchildren.size()];
		for (int i = 0; i != children.length; ++i) {
			children[i] = nchildren.get(i);
		}
		
		automata.states[index] = new Automata.State(kind,
				children, false);

		return true;
	}	
}