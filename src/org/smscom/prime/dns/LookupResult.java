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

package org.smscom.prime.dns;

import java.io.Serializable;

import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;

public class LookupResult implements Comparable<LookupResult>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7977277186980499702L;
	private double rank;
	//private Description description;
	private CURI cURI;
	private AURI aURI;

	public AURI getaURI() {
		return aURI;
	}

	public void setaURI(AURI aURI) {
		this.aURI = aURI;
	}

	
	public LookupResult(AURI aURI, CURI cURI, double rank){
		this.cURI = cURI;
		this.aURI = aURI;
		//this.description = d;
		this.rank = rank;
		
	}
	
//	public Result(AURI aURI, CURI cURI, Description d, double rank){
//		this.cURI = cURI;
//		this.aURI = aURI;
//	    this.description = d;
//		this.rank = rank;	
//	}
	
	public double getRank() {
		return rank;
	}
	public void setRank(double rank) {
		this.rank = rank;
	}
//	public Description getDescription() {
//		return description;
//	}
//	public void setDescription(Description description) {
//		this.description = description;
//	}
	public CURI getCURI() {
		return this.cURI;
	}
	public void setCURI(CURI curi) {
		this.cURI = curi;
	}
	
	
	
	@Override
	public int compareTo(LookupResult arg0) {
		if (this.rank < arg0.rank)
			return -1;
		if (this.rank == arg0.rank)
			return 0;
			
		return 1;
	}
	
}
