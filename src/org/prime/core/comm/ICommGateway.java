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


package org.prime.core.comm;

import java.io.Serializable;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.core.comm.addressing.URI;
import org.prime.core.comm.protocol.PrimeMessage;
import org.prime.core.comm.protocol.PrimeProtocol;
import org.prime.core.comm.IReceiver;



/**
 * This interface defines the set of communication capabilities provided by the PRIME framework
 * @author Mauro Caporuscio
 *
 */
public interface ICommGateway {
	
	/**
	 * PRIME implements the Publish/Subscribe communication paradigm. Specifically, this method publishes 
	 * a given message to a specific topic
	 * @param source the CURI of the source node
	 * @param topic the topic of the message. Note that a topic is identified by a URI. 
	 * @param msg the message to be published 
	 */
	public void publish(CURI source, URI topic, Serializable msg);
	
	/**
	 * PRIME implements the Publish/Subscribe communication paradigm.
	 * Specifically, this method subscribes the current node for a topic of interest
	 * @param topic the subject of interest
	 */
	public void subscribe(URI topic);
	
	
	
	/**
	 * Registers a given <code>CURI</code> within the underlying layer.
	 * @param curi the of the new resource
	 */
	public void register(CURI curi);

	
	/**
	 * return a Receiver object needed for receiving messages from the underlying layer
	 * @return The @Receiver
	 */
	public IReceiver getReceiver();
	
		
	
	/**
	 * Sends a given message m to a specific destination
	 * @param source The source node
	 * @param dest   The destination node
	 * @param type   The message type
	 * @param m      The message
	 */
	void send(CURI source, CURI dest, PrimeProtocol type, Serializable m);
	
	/**
	 * Send a given message m to the set of Neighbors (the definition of "Neighborhoods" depends on the specific underlying Peer-to-peer system).
	 * @param source The source node
	 * @param type   The message type
	 * @param m      The message
	 */
	void sendToNeighbors(CURI source, PrimeProtocol type, Serializable m);
	
	
	/**
	 * Replies to a received message
	 * @param from   The PrimePacket to reply
	 * @param source The source node
	 * @param type   The message type
	 * @param m      The message
	 */
	void replyTo(PrimeMessage from, CURI source, PrimeProtocol type, Serializable m);
	
	/**
	 * Looks up for a resource of interest
	 * @param source The source node
	 * @param aURI  The AURI of interest
	 */
	void lookup(CURI source, AURI aURI);

	/**
	 * Looks up for resources of interest matching the given SPARQL query
	 * @param curi the CURI identifier of the resource issuing the query 
	 * @param query SPARQL query
	 */
	public void query(CURI curi, String query);
	

	
	
}