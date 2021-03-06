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


package org.prime.core.comm.addressing;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.URI;

public class AURI extends URI {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5465795824000909353L;

	public AURI(String auri){
		this.uri = auri;
	}
	
	
	public void setAURI(String curi){
		this.uri = curi;
	}

	@Override
	public String toString() {
		return this.uri;
	}
	
	@Override
	public boolean equals(Object obj){
		String s = (String) ((AURI) obj).uri;
		return this.uri.equals(s); 
	}
	
}
