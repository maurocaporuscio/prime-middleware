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

package org.smscom.prime.core.comm.protocol;

import java.io.Serializable;

//public enum PrimeProtocol implements PrimeProtocolDef{
//	NOTIFY, 		//Notify the observers
//	LOOKUP,			//Lookup for Resource of interest
//	LOOKUP_REPLY,
//	QUERY,
//	QUERY_REPLY
//}

public class PrimeProtocol implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int    code; 
	private String name;
	private String description;
	
	
	public static final PrimeProtocol NOTIFY = new PrimeProtocol(0,"NOTIFY","");
	public static final PrimeProtocol LOOKUP = new PrimeProtocol(1,"LOOKUP","");
	public static final PrimeProtocol LOOKUP_REPLY = new PrimeProtocol(2,"LOOKUP_REPLY","");
	public static final PrimeProtocol QUERY = new PrimeProtocol(3,"QUERY","");
	public static final PrimeProtocol QUERY_REPLY = new PrimeProtocol(4,"QUERY_REPLY","");
	
	
	public PrimeProtocol(int code, String name, String description){
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	
	public int getCode(){
		return this.code;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getName(){
		return this.name;
	}
	
	@Override
	public boolean equals(Object pp){
		return (this.code == ((PrimeProtocol) pp).code);
	}
	
}