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

package org.smscom.prime.description.rdl;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Context implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6850859236381986618L;
	
	
	protected HashMap<String, String> properties;
	
	public Context(){
		properties = new HashMap<String, String>();
	}
	
	public abstract void add(String key, String value);
	
	public String get(String key){
		return this.properties.get(key);
	}
	
	public abstract void set(String key, String value);
	
}
