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

package org.prime.description.rdl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.prime.core.comm.addressing.AURI;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;

public class Dependences implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9032261022431206293L;
	
	
	private Map<AURI,AssemblyUtilityMonitor> dependences;
	
	public Dependences(){
		this.dependences = new HashMap<AURI,AssemblyUtilityMonitor>();
		
	}
	
	public void put(AURI auri, AssemblyUtilityMonitor info){
		this.dependences.put(auri, info);
	}

	public AssemblyUtilityMonitor get(AURI auri){
		return this.dependences.get(auri);
	}
	
	
	public Map<AURI, AssemblyUtilityMonitor> getDependencies() {
		// TODO Auto-generated method stub
		return this.dependences;
	}
	
	
}
