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

package org.smscom.prime.comm.pastry_impl.protocol;

import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.core.comm.protocol.PrimeQueryMessage;

import rice.p2p.commonapi.Id;
import rice.p2p.scribe.ScribeContent;

public class PastryQueryPacket extends PrimeQueryMessage implements ScribeContent {
	
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 313902194076634420L;
	/**
	 * 
	 */
	
	private Id sourceId;
	
    
    
   
   /**
    *       
    * @param sourceNode the Id of the node who sent the message.
    * @param sourceCURI the CURI of the node who sent the message.
    * @param query the query
    */
    public PastryQueryPacket(Id sourceNode, CURI sourceCURI, String query) {
    	super(sourceCURI, query);
    	this.sourceId = sourceNode;
    }

  
   
    
    /**
     * 
     * @return the Pastry Id of the sourcePastryNode
     */
    public Id getSourceId() {
    	return sourceId;
    }
   
    

    @Override
    public String toString(){
    	return this.sourceId.toString()+"/"+ this.query;
    }  
}