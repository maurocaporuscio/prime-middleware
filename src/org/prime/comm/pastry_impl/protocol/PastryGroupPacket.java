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
import org.prime.core.comm.addressing.URI;
import org.prime.core.comm.protocol.PrimeGroupMessage;
import org.prime.core.comm.protocol.PrimeProtocol;

import rice.p2p.commonapi.Id;
import rice.p2p.scribe.ScribeContent;

public class PastryGroupPacket extends PrimeGroupMessage implements ScribeContent{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8839236373525690402L;
	/**
	  * The source of this content.
	*/
	protected Id sourceNode;
	
	
	/**
	 * 
	 * @param sourceNode the Id of the node who sent the message.
	 * @param sourceURI the CURI of the source Resource
	 * @param group the Group URI
	 * @param type type of message
	 * @param payload the message payload
	 */
	public PastryGroupPacket(Id sourceNode, CURI sourceURI, URI group, PrimeProtocol type, Serializable payload){
		super(sourceURI, group, type, payload);
		this.sourceNode = sourceNode;
    }

	/**
	 * 
	 * @return the Id of the node who sent the message.
	 */
	public Id getSourceId() {
		return sourceNode;
	}
	
    @Override
	public String toString(){
    	if (this.type != PrimeProtocol.LOOKUP)
    		return "["+group.toString()+"]"+"["+type+"]"+this.sourceNode.toString()+":"+this.payload.toString();
    	else 
    		return "[LOOKUP]"+"["+type+"]"+this.sourceNode.toString();
	}
	  
}
