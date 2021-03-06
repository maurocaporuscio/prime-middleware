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

package org.prime.dns;

import java.io.Serializable;
import java.util.Collection;

/**
 * A representation of a variable-binding query result as a sequence of <Name, Value> objects. 
 * Each query result consists of zero or more solutions, each of which represents a single query solution as a set of bindings. 
 * 
 *
 */
public interface QueryResult extends Serializable{

	/**
	 * Gets the names of the result, in order of projection.
	 * @return The binding names, in order of projection.
	 *
	 */
	public Collection<String> getNames();
	

	/**
	 * Gets the value of a given binding
	 * @param name of the binding
	 * @return the value associated to the name
	 */
	public String getValue(String name);
	
}
