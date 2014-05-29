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

package org.smscom.prime.comm.pastry_impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.slf4j.LoggerFactory;
import org.smscom.prime.comm.pastry_impl.protocol.PastryChannel;
import org.smscom.prime.comm.pastry_impl.protocol.PastryGossipingPacket;
import org.smscom.prime.comm.pastry_impl.protocol.PastryGroupPacket;
import org.smscom.prime.comm.pastry_impl.protocol.PastryLookupPacket;
import org.smscom.prime.comm.pastry_impl.protocol.PastryQueryPacket;
import org.smscom.prime.comm.pastry_impl.protocol.PastryUnicastPacket;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.core.comm.addressing.URI;
import org.smscom.prime.core.comm.protocol.PrimeProtocol;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.NodeSet;
import rice.pastry.commonapi.PastryIdFactory;

import org.slf4j.Logger;


@SuppressWarnings("deprecation")
public class PastryProxy implements Application, ScribeClient {

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * 
	 * The Endpoint represents the underlining node.  By making calls on the 
	 * Endpoint, it assures that the message will be delivered to a MyApp on whichever
	 * node the message is intended for.
	 */
	protected Endpoint endpoint;

	private Scribe myScribe;
	private Topic broadcastTopic;
	private Node node;
	private Collection<Topic> topicsOfInterest = new Vector<Topic>();
	private HashMap<CURI, Id> DNSTable = null;
	
	private PastryReceiver rec;

	public PastryProxy(Node node, PastryReceiver rec) {
		
		this.DNSTable = new HashMap<CURI, Id>();
		this.node = node;
		this.rec = rec;
		
		
		// We are only going to use one instance of this application on each PastryNode
		this.endpoint = node.buildEndpoint(this, "PrimeP2PCommunicationLayer");

		// the rest of the initialization code could go here
		this.myScribe = new ScribeImpl(node, "PrimeGroupCommunication");
		broadcastTopic = new Topic(new PastryIdFactory(node.getEnvironment()), PastryChannel.BROADCAST.toString()); //"PRIME-broadcast"
		this.myScribe.subscribe(this.broadcastTopic, this);
		//log.info("node LocalHandle: "+ this.node.getLocalNodeHandle().toString());

		// now we can receive messages
		this.endpoint.register();
	}

	/**
	 * Called to route a message to the id
	 */
	public void sendToId(Id to, Message pck) {
		log.debug(this+" sending to "+to);    
		endpoint.route(to, pck, null);
	}

	/**
	 * Send a message to a given cURI
	 * @param to destination cURI 
	 * @param m Message to
	 * @return True if message has been sent, False otherwise
	 */
	public boolean send(CURI to, Message m) {
		
		Id id = this.DNSTable.get(to);
		if (id == null){
			log.debug("Destination unknown: "+to);
			return false;
		}
		
		endpoint.route(id, m, null);
		log.debug("Message ["+m+"] sent to" +to);
		return true;
		
	}

	/**
	 * Send a message to a given pastry node
	 * @param to destination Id 
	 * @param m Message to
	 * @return True if message has been sent, False otherwise
	 */
	public boolean send(Id to, Message m) {

		endpoint.route(to, m, null);
		log.debug("Message ["+m+"] sent to" +to);
		return true;
		
	}
	
	
	/**
	 * Send a message to the Neighbors
	 * @param m Message to send
	 * @return True if message has been sent, False otherwise
	 */
	public boolean sendToNeighbors(Message m) {
		
		NodeSet neighbors = (NodeSet) endpoint.neighborSet(100);
		Iterator<rice.pastry.NodeHandle> i = neighbors.iterator();
	
		log.debug("Sending Message ["+m+"] to neighbor set");
		while(i.hasNext()){
			NodeHandle node = i.next();
			endpoint.route(node.getId(), m, null);
			log.debug("Message ["+m+"] sent to" + node.getId());
		}
		return true;
		
	}
	
	
	/**
	 * Called when we receive a message.
	 */
	public void deliver(Id id, Message message){
		
		//Processing MessagePacket
		if (message instanceof PastryUnicastPacket){
			PastryUnicastPacket m = (PastryUnicastPacket) message;
			if (!m.getSourceId().equals(this.endpoint.getId())){
				log.debug("I've received [UNICAST]"+ m.toString()+" from "+ m.getSourceId());
				rec.enqueue(m);
				
				
				this.DNSTable.put(m.getSourceURI(), m.getSourceId());
				
				
			}
		}

	}

	/**
	 * Called when you hear about a new neighbor.
	 * Don't worry about this method for now.
	 */
	public void update(NodeHandle handle, boolean joined) {
	}

	/**
	 * Called a message travels along your path.
	 * 
	 */
	public boolean forward(RouteMessage message) {
		return true;
	}

	public String toString() {
		return "MyApp "+endpoint.getId();
	}


	//PUBLISH/SUBSCRIBE STUFF
	
	/**
	 * 
	 * @param subject
	 * @param msg
	 */
	public void publish(CURI from, URI subject, Serializable msg) {
		// TODO Auto-generated method stub
		Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject.toString());
		PastryGroupPacket pck = new PastryGroupPacket(this.endpoint.getId(), from, subject, PrimeProtocol.NOTIFY, msg);

		myScribe.publish(topic, pck); 
		log.debug("publishing " + msg + " to " + topic);
	}


	/**
	 * 
	 * @param subject
	 */
	public void subscribe(String subject) {
		// TODO Auto-generated method stub
		
		Topic topic = new Topic(new PastryIdFactory(node.getEnvironment()), subject);
		
		if (!this.topicsOfInterest.contains(topic)){
			this.topicsOfInterest.add(topic);
			myScribe.subscribe(topic, this);
		
			log.debug(this.node.getId().toString()+ "is subscribed for "+ subject);
		}
	}

	
	public void gossip(CURI curi){
		PastryGossipingPacket gossip = new PastryGossipingPacket(this.endpoint.getId(), curi);
		myScribe.publish(broadcastTopic, gossip); 
		log.debug("Gossiping " + curi);
	}


	public void lookup(CURI source, AURI aURI){
		PastryLookupPacket look = new PastryLookupPacket(this.endpoint.getId(), source, aURI);
		myScribe.publish(broadcastTopic, look); 
		log.debug("Lookup: " + aURI + ".");
	}
	
	public void query(CURI source, String query) {
		PastryQueryPacket q = new PastryQueryPacket(this.endpoint.getId(), source, query);
		myScribe.publish(broadcastTopic, q); 
		log.debug("Query: " + query);
		
	}
	
	
	public void bindTo(Map<AURI, CURI> dependences) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public boolean anycast(Topic arg0, ScribeContent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void childAdded(Topic arg0, NodeHandle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void childRemoved(Topic arg0, NodeHandle arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliver(Topic topic, ScribeContent content) {
		// TODO Auto-generated method stub

		//Processing GossipingPacket
		if (content instanceof PastryGossipingPacket){
			PastryGossipingPacket pck = (PastryGossipingPacket) content;
			
			//Skip the packet if I've sent it
			if (this.endpoint.getId().equals(pck.getSourceId()))
				return;
			
			//Else process the packet
				
			//If cURI was already there, it is replaced
			this.DNSTable.put(pck.getcURI(), pck.getSourceId());
			
			log.debug("[" + pck.getcURI()+","+pck.getSourceId()+"] entry added to DNS Table");
			
			//Should I gossip Myself here????
			//Message rpl = new MessagePacket(this.endpoint.getLocalNodeHandle(), pck.getcURI() , "Nice to see you", "ACK" );
			//endpoint.route(null, rpl, pck.getSourceNode());
		}
		
		
		//Processing LookupPacket
		if (content instanceof PastryLookupPacket){
			PastryLookupPacket pck = (PastryLookupPacket) content;

			//Skip the packet if I've sent it
			if (this.endpoint.getId().equals(pck.getSourceId()))
				return;

			//Else process the packet
			log.debug("I've received [LOOKUP] from "+ pck.getSourceURI());
			rec.enqueue(pck);
			
		}
		
		//Processing QueryPacket
		if (content instanceof PastryQueryPacket){
			PastryQueryPacket pck = (PastryQueryPacket) content;

			//Skip the packet if I've sent it
			if (this.endpoint.getId().equals(pck.getSourceId()))
				return;

			//Else process the packet
			log.debug("I've received [QUERY] from "+ pck.getSourceURI());
			rec.enqueue(pck);

		}
		
		//Processing GroupPacket
		if (content instanceof PastryGroupPacket){
			PastryGroupPacket pck = (PastryGroupPacket) content;
			log.debug("I've received ["+ pck.getPayload()+"] from topic "+ pck.getSourceId());
			rec.enqueue(pck);
		}
		
		
		
	}

	@Override
	public void subscribeFailed(Topic arg0) {
		// TODO Auto-generated method stub

	}

	

	

}

