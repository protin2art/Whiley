// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import wyautl.core.Automaton;
import wyautl.io.BinaryAutomataWriter;
import wyautl.io.BinaryOutputStream;
import static wyone.core.Types.*;

public abstract class Type {

	// =============================================================
	// Public Interface
	// =============================================================
	
	private static Any T_ANY = new Any();
	private static Void T_VOID = new Void();
	private static Bool T_BOOL = new Bool();
	private static Int T_INT = new Int();
	private static Real T_REAL = new Real();
	private static Strung T_STRING = new Strung();
	private static Ref<Any> T_REFANY = new Ref(T_ANY);
	private static Meta T_METAANY = new Meta(T_ANY);
	private static List T_LISTANY = new List(true,T_ANY);
	private static Set T_SETANY = new Set(true,T_ANY);
	
	public static Any T_ANY() {
		if(T_ANY == null) {
			T_ANY = new Any();
		}
		return T_ANY;
	}
	
	public static Void T_VOID() {
		if(T_VOID == null) {
			T_VOID = new Void();
		}
		return T_VOID;
	}
	
	public static Bool T_BOOL() {
		if(T_BOOL == null) {
			T_BOOL = new Bool();
		}
		return T_BOOL;
	}
	
	public static Int T_INT() {
		if(T_INT == null) {
			T_INT = new Int();
		}
		return T_INT;
	}
	
	public static Real T_REAL() {
		if(T_REAL == null) {
			T_REAL = new Real();
		}
		return T_REAL;
	}
	
	public static Strung T_STRING() {
		if(T_STRING == null) {
			T_STRING = new Strung();
		}
		return T_STRING;
	}
	
	public static Ref<Any> T_REFANY() {
		if(T_REFANY == null) {
			T_REFANY = new Ref(T_ANY());
		}
		return T_REFANY;
	}
	
	public static Meta T_METAANY() {
		if(T_METAANY == null) {
			T_METAANY = new Meta(T_ANY());
		}
		return T_METAANY;
	}
	
	public static List T_LISTANY() {
		if(T_LISTANY == null) {
			T_LISTANY = new List(true,T_ANY());
		}
		return T_LISTANY;
	}
	
	public static Set T_SETANY() {
		if(T_SETANY == null) {
			T_SETANY = new Set(true,T_ANY());
		}
		return T_SETANY;
	}
	
	public static Collection T_COMPOUND(Type.Collection template,
			boolean unbounded, Type... elements) {
		if (template instanceof List) {
			return new List(unbounded, elements);
		} else if (template instanceof Bag) {
			return new Bag(unbounded, elements);
		} else {
			return new Set(unbounded, elements);
		}
	}
	
	public static Collection T_COMPOUND(Type.Collection template,
			boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i = 0;
		for (Type t : elements) {
			es[i++] = t;
		}
		if (template instanceof List) {
			return new List(unbounded, es);
		} else if (template instanceof Bag) {
			return new Bag(unbounded, es);
		} else {
			return new Set(unbounded, es);
		}
	}
	
	public static List T_LIST(boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new List(unbounded,es);
	}
	
	public static List T_LIST(boolean unbounded, Type... elements) {
		return new List(unbounded,elements);
	}
	
	public static Set T_SET(boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Set(unbounded,es);
	}
	
	public static Set T_SET(boolean unbounded, Type... elements) {
		return new Set(unbounded,elements);
	}
	
	public static Bag T_BAG(boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Bag(unbounded,es);
	}
	
	public static Bag T_BAG(boolean unbounded, Type... elements) {
		return new Bag(unbounded,elements);
	}
	
	public static Term T_TERM(String name, Type.Ref data) {
		if(data != null) {
			return new Term(name,data);
		} else {
			return new Term(name);
		}
	}
	
	public static Ref T_REF(Type element) {
		return new Ref(element);
	}
	
	public static Meta T_META(Type element) {
		return new Meta(element);
	}
	
	public static Not T_NOT(Type element) {
		return new Not(element);
	}
	
	public static And T_AND(Type... elements) {
		return new And(elements);
	}
	
	public static And T_AND(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i = 0;
		for (Type t : elements) {
			es[i++] = t;
		}
		return new And(es);
	}
	
	public static Or T_OR(Type... elements) {
		return new Or(elements);
	}
	
	public static Or T_OR(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Or(es);
	}
	
	public static Fun T_FUN(Type ret, Type param) {
		return new Fun(ret,param);
	}
	
	/**
	 * Coerce the result of the given expression into a value. In other words,
	 * if the result of the expression is a reference then dereference it!
	 * 
	 * @param type
	 *            --- type to unbox.
	 */
	public static Type unbox(Type type) {		
		if(type instanceof Type.Ref) {
			Type.Ref ref = (Type.Ref) type;
			return ref.element();
		} else {
			return type;
		}
	}
	
	/**
	 * Coerce the result of the given expression into a reference. In other
	 * words, if the result of the expression is a value then generate a
	 * reference to that value!
	 * 
	 * @param type
	 *            --- type to box.
	 */
	public static Type box(Type type) {
		if (type instanceof Type.Ref) {
			return type;
		} else {
			return Type.T_REF(type);
		}
	}
	
	// ==================================================================
	// Atoms
	// ==================================================================
	
	public static abstract class Atom extends Type {
		public Atom(int kind) {
			if (kind != K_Any && kind != K_Void && kind != K_Bool
					&& kind != K_Int && kind != K_Real && kind != K_String) {
				throw new IllegalArgumentException("Invalid atom kind");
			}
			int root = automaton.add(new Automaton.Term(kind));
			automaton.setMarker(0,root);
		}
	}
	
	public static final class Any extends Atom {
		private Any() {
			super(K_Any);
		}
	}

	public static final class Void extends Atom {
		private Void() {
			super(K_Void);
		}
	}
	
	public static final class Bool extends Atom {
		private Bool() {
			super(K_Bool);
		}
	}

	public static final class Int extends Atom {
		private Int() {
			super(K_Int);
		}
	}

	public static final class Real extends Atom {
		private Real() {
			super(K_Real);
		}
	}
	
	public static final class Strung extends Atom {
		private Strung() {
			super(K_String);
		}
	}
	
	// ==================================================================
	// Unary Terms
	// ==================================================================
	
	public static abstract class Unary extends Type {
		public Unary(int kind, Type element) {		
			if (kind != K_Meta && kind != K_Ref && kind != K_Not) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.markers[0],
					element_automaton);
			int root = automaton.add(new Automaton.Term(kind, elementRoot));
			automaton.setMarker(0,root);
		}
		private Unary(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getMarker(0)).kind;
			if (kind != K_Meta && kind != K_Ref && kind != K_Not) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
		}
		public Type element() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			return extract(term.contents);
		}
	}

	public static final class Meta extends Unary {
		private Meta(Type element) {
			super(K_Meta, element);
		}
		private Meta(Automaton automaton) {
			super(automaton);
		}
	}

	public static final class Ref<T extends Type> extends Unary {
		private Ref(T element) {
			super(K_Ref, element);
		}

		private Ref(Automaton automaton) {
			super(automaton);
		}

		public T element() {
			return (T) super.element();
		}
	}
	
	public static final class Not extends Unary {
		private Not(Type element) {
			super(K_Not, element);
		}

		private Not(Automaton automaton) {
			super(automaton);
		}
	}
	
	// ==================================================================
	// Nary Terms
	// ==================================================================
	
	public static abstract class Nary extends Type {
		private Nary(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getMarker(0)).kind;
			if (kind != K_And && kind != K_Or && kind != K_Fun) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		private Nary(int kind, int compound, Type... elements) {
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.markers[0],
						element_automaton);
				children[i] = child;
			}
			int compoundRoot;
			switch (compound) {
			case K_Set:
				compoundRoot = automaton.add(new Automaton.Set(children));
				break;
			case K_Bag:
				compoundRoot = automaton.add(new Automaton.Bag(children));
				break;
			case K_List:
				compoundRoot = automaton.add(new Automaton.List(children));
				break;
			default:
				throw new IllegalArgumentException(
						"invalid compound type in Nary constructor");
			}

			int root = automaton.add(new Automaton.Term(kind, compoundRoot));
			automaton.setMarker(0,root);
		}

		public Type element(int index) {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			return extract(collection.get(index));
		}

		public Type[] elements() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			Type[] elements = new Type[collection.size()];
			for (int i = 0; i != elements.length; ++i) {
				elements[i] = extract(collection.get(i));
			}
			return elements;
		}
	}
	
	public static final class Term extends Type {
		private Term(String name) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			int argument = automaton.add(new Automaton.List(stringRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setMarker(0,root);
		}

		private Term(String name, Type.Ref element) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.markers[0],
					element_automaton);
			int argument = automaton.add(new Automaton.List(stringRoot,
					elementRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setMarker(0,root);
		}

		private Term(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getMarker(0)).kind;
			if (kind != K_Term) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		public String name() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
			return str.value;
		}
		
		public Ref element() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			if(list.size() < 2) {
				return null;
			} else {
				return (Ref) extract(list.get(1));
			}
		}		
	}
	
	public static final class Nominal extends Type {
		private Nominal(String name) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			int argument = automaton.add(new Automaton.List(stringRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setMarker(0,root);
		}

		private Nominal(String name, Type.Ref element) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.markers[0],
					element_automaton);
			int argument = automaton.add(new Automaton.List(stringRoot,
					elementRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setMarker(0,root);
		}

		private Nominal(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getMarker(0)).kind;
			if (kind != K_Nominal) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		public String name() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
			return str.value;
		}
		
		public Type element() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);			
			return extract(list.get(1));			
		}		
	}
	
	public static final class Fun extends Nary {
		private Fun(Type ret, Type param) {
			super(K_Fun,K_List,ret,param);
		}
		private Fun(Automaton automaton) {
			super(automaton);
		}
		public Type ret() {
			return element(0);
		}

		public Type param() {
			return element(1);
		}
	}
	
	public static final class And extends Nary {
		private And(Type... bounds) {
			super(K_And,K_Set,bounds);
		}

		private And(Automaton automaton) {
			super(automaton);
		}		
	}
	
	public static final class Or extends Nary {
		private Or(Type... bounds) {
			super(K_Or,K_Set,bounds);
		}
		
		private Or(Automaton automaton) {
			super(automaton);
		}		
	}
	
	// ==================================================================
	// Compounds
	// ==================================================================			
	
	public static abstract class Collection extends Type {
		private Collection(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getMarker(0)).kind;
			if (kind != K_Set && kind != K_Bag && kind != K_List) {
				throw new IllegalArgumentException("Invalid collection kind");
			}
		}
		private Collection(int kind, boolean unbounded,
				Type... elements) {
			if (kind != K_Set && kind != K_Bag && kind != K_List) {
				throw new IllegalArgumentException("Invalid collection kind");
			}
			
			int boolRoot = automaton.add(new Automaton.Bool(unbounded));
					
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.markers[0],
						element_automaton);
				children[i] = child;
			}
			int compoundRoot;
			switch (kind) {
			case K_Set:
				compoundRoot = automaton.add(new Automaton.Set(children));
				break;
			case K_Bag:
				compoundRoot = automaton.add(new Automaton.Bag(children));
				break;
			case K_List:
				compoundRoot = automaton.add(new Automaton.List(children));
				break;
			default:
				throw new IllegalArgumentException(
						"invalid compound type in Nary constructor");
			}
			
			int listRoot = automaton.add(new Automaton.List(boolRoot,compoundRoot));
			int root = automaton.add(new Automaton.Term(kind, listRoot));
			automaton.setMarker(0,root);
		}

		public boolean unbounded() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Bool bool = (Automaton.Bool) automaton.get(list.get(0));
			return bool.value;
		}
		
		public Type element(int index) {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(list.get(1));
			return extract(collection.get(index));
		}
		
		public Type[] elements() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(list.get(1));
			Type[] elements = new Type[collection.size()];
			for(int i=0;i!=elements.length;++i) {
				elements[i] = extract(collection.get(i));
			}
			return elements;
		}
		
		public Type element() {
			Type[] elements = elements();
			if(elements.length == 0) {
				return Type.T_VOID;
			} else if(elements.length == 1) {
				return elements[0];
			} else {
				return T_OR(elements());
			} 
		}
		
		protected String body() {
			String r = "";
			Type[] elements = elements();
			for (int i = 0; i != elements.length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += elements[i];
			}
			if (unbounded()) {
				r += "...";
			}
			return r;
		}
	}
	
	public final static class Set extends Collection {
		private Set(boolean unbounded, Type... elements) {
			super(K_Set, unbounded, elements);
		}

		private Set(Automaton automaton) {
			super(automaton);
		}
	}
	
	public final static class Bag extends Collection {
		private Bag(boolean unbounded, Type... elements) {
			super(K_Bag, unbounded, elements);
		}

		private Bag(Automaton automaton) {
			super(automaton);
		}
	}
	
	public final static class List extends Collection {
		private List(boolean unbounded, Type... elements) {
			super(K_List, unbounded, elements);
		}

		private List(Automaton automaton) {
			super(automaton);
		}
	}
			
	// =============================================================
	// Private Implementation
	// =============================================================

	protected final Automaton automaton;
	
	private Type() {
		this.automaton = new Automaton();
	}	
	
	private Type(Automaton automaton) {
		this.automaton = automaton;
	}
	
	public Automaton automaton() {
		return automaton;
	}
	
	/**
	 * <p>
	 * Return true if argument (<code>t1</code>) is a subtype of this type (
	 * <code>t2</code>). This function operates in a seemingly strange way. To
	 * perform the subtype check, it computes the type <code>t1 && !t2</code>.
	 * If this reduces to type <code>void</code>, then we can be certain that
	 * <code>t1</code> is entirely closed within <code>t2</code>.
	 * </p>
	 * 
	 * 
	 * 
	 * @param t1
	 *            --- super-type to test for.
	 * @param t2
	 *            --- sub-type to test for.
	 * @return
	 */
	public boolean isSubtype(Type t) {
//		Type result = Type.T_AND(Type.T_NOT(this),t);
//		// result.reduce();
//		System.out.println("GOT: " + result);
//		return result.equals(Type.T_VOID());
		return isSubtype(this,t,10);
	}	
	
	
	public int hashCode() {
		return automaton.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Type) {
			Type r = (Type) o;
			return automaton.equals(r.automaton);
		}
		return false;
	}
	
	public String toString() {
		int root = automaton.getMarker(0);
		int[] headers = new int[automaton.nStates()];		
		automaton.findHeaders(root,headers);
		return toString(root,headers);
	}
	
	public String toString(int root, int[] headers) {
		String body = "";	
		int header = 0;
		if(root >= 0) {
			header = headers[root];
			if(header > 1) {
				body = ("$" + (header-2) + "<");
				headers[root] = -header;
			} else if(header < 0) {
				return "$" + ((-header)-2);
			}
		}
		Automaton.Term term = (Automaton.Term) automaton.get(root);
		switch(term.kind) {
			case K_Bool:
				body += "bool";
				break;
			case K_Int:
				body += "int";
				break;
			case K_Real:
				body += "real";
				break;
			case K_String:
				body += "string";
				break;
			case K_Ref: 
				body += "^" + toString(term.contents,headers);
				break;
			case K_Meta: 
				body += "?" + toString(term.contents,headers);
				break;
			case K_Not: 
				body += "!" + toString(term.contents,headers);
				break;
			case K_Or : {
				Automaton.Set set = (Automaton.Set) automaton
						.get(term.contents);
				for (int i = 0; i != set.size(); ++i) {
					if (i != 0) {
						body += "|";
					}
					body += toString(set.get(i), headers);
				}
				break;
			}
			case K_And : {
				Automaton.Set set = (Automaton.Set) automaton
						.get(term.contents);
				for (int i = 0; i != set.size(); ++i) {
					if (i != 0) {
						body += "&";
					}
					body += toString(set.get(i), headers);
				}
				break;
			}
			case K_List:
			case K_Bag:
			case K_Set: {
				Automaton.List list = (Automaton.List) automaton.get(term.contents);
				// FIXME: following 2 lines to be updated
				Automaton.Bool unbounded = (Automaton.Bool) automaton.get(list.get(0));
				// end
				Automaton.Collection c = (Automaton.Collection) automaton.get(list.get(1));
				String tmp = "";
				for(int i=0;i!=c.size();++i) {
					if(i != 0) {
						tmp += ",";
					}
					tmp += toString(c.get(i),headers);
				}				
				if(unbounded.value) {
					tmp += "...";
				}
				if(c instanceof Automaton.Set){
					body += "{" + tmp + "}";
				} else if(c instanceof Automaton.Bag){
					body +=  "{|" + tmp + "|}";
				} else {
					body += "[" + tmp + "]";
				}		
				break;
			}
			case K_Nominal: {
				Automaton.List list = (Automaton.List) automaton.get(term.contents);
				Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));								
				body += str.value;				
				body += "<" + toString(list.get(1),headers) + ">";
				break;
			}
			case K_Term: {
				Automaton.List list = (Automaton.List) automaton.get(term.contents);
				Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
				if(list.size() > 1) {
					body += str.value + "(" + toString(list.get(1),headers) + ")";
				} else {
					body += str.value;
				}
				break;
			}
			default:
				throw new IllegalArgumentException("unknown type encountered (" + SCHEMA.get(term.kind).name + ")");
		}
		
		if(header > 1) {
			body += ">";
		}
		
		return body;
	}	
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BinaryOutputStream bos = new BinaryOutputStream(bout);
		BinaryAutomataWriter bw = new BinaryAutomataWriter(bos, SCHEMA);
		automaton.compact();
		bw.write(automaton);		
		bw.flush();
		return bout.toByteArray();		
	}

	/**
	 * Extract the type described by a given node in the automaton. This is
	 * primarily used to extract subcomponents of a type (e.g. the element of a
	 * reference type).
	 * 
	 * @param child
	 *            --- child node to be extracted.
	 * @return
	 */
	protected Type extract(int child) {
		Automaton automaton = new Automaton();
		int root = automaton.addAll(child, this.automaton);
		automaton.setMarker(0,root);
		return construct(automaton);
	}
				
	/**
	 * Construct a given type from an automaton. This is primarily used to
	 * reconstruct a type after expansion.
	 * 
	 * @param automaton
	 * @return
	 */
	public static Type construct(Automaton automaton) {
		// TODO: should additionally minimise the automaton
		automaton.compact();
		
		int root = automaton.getMarker(0);
		Automaton.State state = automaton.get(root);
		switch(state.kind) {
		// atoms
		case K_Void:
			return Type.T_VOID;
		case K_Any:
			return Type.T_ANY;
		case K_Bool:
			return Type.T_BOOL;
		case K_Int:
			return Type.T_INT;
		case K_Real:
			return Type.T_REAL;
		case K_String:
			return Type.T_STRING;
		// unaries
		case K_Ref:
			return new Type.Ref(automaton);
		case K_Meta:
			return new Type.Meta(automaton);
		case K_Not:
			return new Type.Not(automaton);
		case K_Term:
			return new Type.Term(automaton);
		case K_Nominal:
			return new Type.Nominal(automaton);
		// naries
		case K_Fun:
			return new Type.Fun(automaton);
		case K_And:
			return new Type.And(automaton);
		case K_Or:
			return new Type.Or(automaton);
		// compounds
		case K_Set:
			return new Type.Set(automaton);
		case K_Bag:
			return new Type.Bag(automaton);
		case K_List:
			return new Type.List(automaton);
		default:
			throw new IllegalArgumentException("Unknown kind encountered - " + state.kind);
		}
	}
	
	/**
	 * Following is useful for bootstrapping the whole system.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static boolean isSubtype(Type t1, Type t2, int count) {
		if(t1 == null || t2 == null) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if(count == 0) {
			return true;
		} else {
			count = count - 1;
		}
		if (t1 == t2 || (t2 instanceof Void) || t1 instanceof Any) {
			return true;
		} else if ((t1 instanceof Set && t2 instanceof Set)
				|| (t1 instanceof Bag && (t2 instanceof Set || t2 instanceof Bag))
				|| (t1 instanceof List && (t2 instanceof Set || t2 instanceof Bag || t2 instanceof List))) {
			// RULE: S-LIST
			Collection l1 = (Collection) t1;
			Collection l2 = (Collection) t2;
			Type[] l1_elements = l1.elements();
			Type[] l2_elements = l2.elements();
			if (l1_elements.length != l2_elements.length && !l1.unbounded()) {
				return false;
			} else if (l2.unbounded() && !l1.unbounded()) {
				return false;
			} else if(l2.elements().length < l1.elements().length-1) {
				return false;
			}
			int min_len = Math.min(l1_elements.length, l2_elements.length);
			for (int i = 0; i != min_len; ++i) {
				if (!isSubtype(l1_elements[i], l2_elements[i], count)) {					
					return false;
				}
			}
			Type l1_last = l1_elements[l1_elements.length-1];
			for (int i = min_len; i != l2_elements.length; ++i) {
				if (!isSubtype(l1_last,l2_elements[i], count)) {
					return false;
				}
			}			
			return true;
		} else if (t1 instanceof Term && t2 instanceof Term) {			
			Term n1 = (Term) t1;
			Term n2 = (Term) t2;
			if(n1.name().equals(n2.name())) {
				Type n1_element = n1.element();
				Type n2_element = n2.element();
				if(n1_element == null || n2_element == null) {
					return n1_element == n2_element;
				} else {
					return isSubtype(n1_element,n2_element,count);
				}
			} else {				
				//System.out.println("STAGE 6");
				return false;
			}
		} else if(t1 instanceof Nominal && t2 instanceof Nominal) {
			Nominal n1 = (Nominal) t1;
			Nominal n2 = (Nominal) t2;
			if(n1.name().equals(n2.name())) {
				return true; // early exit
			} else {
				return isSubtype(n1.element(),n2.element(),count);
			}
		} else if(t1 instanceof Nominal) {
			Nominal n1 = (Nominal) t1;
			return isSubtype(n1.element(),t2,count);
		} else if(t2 instanceof Nominal) {
			Nominal n2 = (Nominal) t2;
			return isSubtype(t1,n2.element(),count);
		} else if (t2 instanceof Or) {				
			Or o2 = (Or) t2;
			for(Type b2 : o2.elements()) {
				if(!isSubtype(t1,b2,count)) {
					return false;
				}
			}
			return true;
		} else if (t1 instanceof Or) {	
			Or o1 = (Or) t1;
			for(Type b1 : o1.elements()) {
				if(isSubtype(b1,t2,count)) {
					return true;
				}
			}
			return false;
		} else if (t1 instanceof Not && t2 instanceof Not) {
			Not r1 = (Not) t1;
			Not r2 = (Not) t2;
			return isSubtype(r1.element(),r2.element(),count);
		} else if(t1 instanceof Ref && t2 instanceof Ref) {
			Ref r1 = (Ref) t1;
			Ref r2 = (Ref) t2;
			return isSubtype(r1.element(),r2.element(),count);
		} else if(t1 instanceof Meta && t2 instanceof Meta) {
			Meta r1 = (Meta) t1;
			Meta r2 = (Meta) t2;
			return isSubtype(r1.element(),r2.element(),count);
		}
		return false;
	}
	
}

