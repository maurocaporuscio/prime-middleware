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

package org.prime.comm.pastry_impl.protocol;

import java.io.Serializable;

import org.prime.core.comm.addressing.CURI;
import org.prime.core.comm.protocol.PrimeProtocol;
import org.prime.core.comm.protocol.PrimeUnicastMessage;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public class PastryUnicastPacket extends PrimeUnicastMessage implements Message, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2401873056964221880L;
	private Id sourceId;
	
	
	
	/**
	 * 
	 * @param sourceId Who send the packet
	 * 
	 */
	public PastryUnicastPacket(Id sourceId, CURI sourceURI, CURI destURI, PrimeProtocol type, Serializable payload){
		super(sourceURI, destURI, type, payload);
		this.sourceId = sourceId;
	}

	

	
    @Override
	public String toString(){
		return "["+type+"]"+this.sourceId.toString()+"["+this.payload.toString()+"]";
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return MAX_PRIORITY;
	}




	public Id getSourceId() {
		// TODO Auto-generated method stub
		return sourceId;
	}
	
}
