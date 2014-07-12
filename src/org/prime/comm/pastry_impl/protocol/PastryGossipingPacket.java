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

import rice.p2p.commonapi.Id;
import rice.p2p.scribe.ScribeContent;

import org.prime.core.comm.addressing.CURI;

/**
 * 
 * @author Mauro Caporuscio
 *
 */
public class PastryGossipingPacket implements ScribeContent {
	
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 313902194076634420L;
	/**
	 * 
	 */
	
	private Id sourceNode;
    private CURI cURI;
    private PastryProtocol type;
    
    
    public PastryGossipingPacket(){
    	this.type = PastryProtocol.GOSSIP;
    }
    
  /**
   *       
   * @param sourceNode Who sent the message.
   * @param curi the resource CURI available at the <i>sourcePastryNode</i> node
   */
    public PastryGossipingPacket(Id sourceNode, CURI curi) {
    	this.type = PastryProtocol.GOSSIP;
    	this.sourceNode = sourceNode;
    	this.cURI = curi;
    }

  
    public PastryProtocol getType() {
    	return type;
    }
    
    /**
     * 
     * @return the Pastry Id of the sourcePastryNode
     */
    public Id getSourceId() {
    	return sourceNode;
    }

    /**
     * 
     * @return the cURI registered at the sourcePastryNode
     */
    public CURI getcURI() {
    	return cURI;
    }

    @Override
    public String toString(){
    	return this.sourceNode.toString()+"/"+cURI;
    }  
    
}

