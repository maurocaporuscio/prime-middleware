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

import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.core.comm.addressing.URI;


public abstract class PrimeGroupMessage extends PrimeMessage{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3945764888047385154L;
	
	
	protected URI group;  
	
	public PrimeGroupMessage(CURI sourceCURI, URI aURI, PrimeProtocol type, Serializable payload){
		super(sourceCURI, type, payload);
		this.group = aURI;
	}
	  
	/**
	 * 
	 * @return the content of the message.
	 */
	public URI getGroup() {
		return group;
	}
	
}
