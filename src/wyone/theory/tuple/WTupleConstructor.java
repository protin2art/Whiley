// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.theory.tuple;

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.congruence.*;
import wyone.util.*;

public final class WTupleConstructor extends WConstructor<WExpr> implements WExpr {
	private final ArrayList<String> fields;

	public WTupleConstructor(Collection<String> fields, Collection<WExpr> params) {
		super("(..)", params);
		this.fields = new ArrayList<String>(fields);
	}
	
	public List<String> fields() {
		return fields;
	}
	
	public WExpr field(String name) {
		for(int i=0;i!=fields.size();++i) {
			if(fields.get(i).equals(name)) {
				return subterms.get(i);
			}
		}
		throw new IllegalArgumentException("field does not exist: " + name);
	}
	
	public WLiteral rearrange(WExpr rhs) {		
		// always best to do rhs on left side, since this might a variable and
		// hence we might be creating an assignment
		return new WEquality(true,rhs,this);
	}
	
	public WTupleType type(SolverState state) {
		ArrayList<Pair<String,WType>> types = new ArrayList();
		for(int i=0;i!=fields.size();++i) {
			WType t = subterms.get(i).type(state);
			types.add(new Pair(fields.get(i),t));
		}
		return new WTupleType(types);
	}
	
	public WExpr substitute(Map<WExpr,WExpr> binding) {
		ArrayList nparams = new ArrayList();
		boolean pchanged = false;
		boolean composite = true;
		for(WExpr p : subterms) {
			WExpr np = p.substitute(binding);
			composite &= np instanceof WValue;			
			if(np != p) {				
				nparams.add(np);
				pchanged=true;				
			} else {
				nparams.add(p);
			}
		}
		if(composite) {
			return new WTupleVal(fields,nparams);
		} else if(pchanged) {
			return new WTupleConstructor(fields,nparams);
		} else {
			return this;
		}
	}
	
	public String toString() {
		String r = "(";
		boolean firstTime=true;
		for(int i=0;i!=subterms.size();++i) {		
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += fields.get(i) + "=";
			r += subterms.get(i).toString();
		}
		return r + ")";
	}
}
