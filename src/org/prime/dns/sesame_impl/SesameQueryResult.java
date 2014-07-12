/**
 * This file is part of the PRIME middleware.
 * See http://www.erc-smscom.org
 * 
 * Copyright (C) 2008-2013 ERC-SMSCOM Project
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
 * USA, or send email
 * 
 * @author Mauro Caporuscio 
 */

package org.prime.dns.sesame_impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.prime.dns.QueryResult;

public class SesameQueryResult  implements QueryResult, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3759044252596745831L;
	
	HashMap<String, String> tuples;
	
	
	
	public SesameQueryResult(BindingSet bs){
		this.tuples = new HashMap<String, String>();
		
		Set<String> names = bs.getBindingNames();
		for(String name: names){
			String value = bs.getValue(name).stringValue();	
			tuples.put(name, value);
		}

		
		
		
	}


	@Override
	public Collection<String> getNames() {
		// TODO Auto-generated method stub
		return tuples.keySet();
	}

	@Override
	public String getValue(String name) {
		// TODO Auto-generated method stub
		return tuples.get(name);
	}


	



	

}
