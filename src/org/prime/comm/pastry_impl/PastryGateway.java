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

package org.prime.comm.pastry_impl;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.prime.comm.pastry_impl.protocol.PastryLookupPacket;
import org.prime.comm.pastry_impl.protocol.PastryQueryPacket;
import org.prime.comm.pastry_impl.protocol.PastryUnicastPacket;
import org.prime.core.comm.ICommGateway;
import org.prime.core.comm.IReceiver;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.core.comm.addressing.URI;
import org.prime.core.comm.protocol.PrimeMessage;
import org.prime.core.comm.protocol.PrimeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rice.environment.Environment;
import rice.p2p.commonapi.Message;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;


public class PastryGateway implements ICommGateway{

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());

	
	public static final String  GATEWAY_LOCAL_DEFAULT_ADDRESS = getHost();
	public static final int 	GATEWAY_LOCAL_DEFAULT_PORT = 1308;
	public static final String 	GATEWAY_REMOTE_DEFAULT_ADDRESS = getHost();
	public static final int 	GATEWAY_REMOTE_DEFAULT_PORT = 1308;
	
	private static String getHost(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	Environment env;
	PastryNode node;
	
	PastryProxy app;
	PastryReceiver receiver;


	NodeIdFactory nidFactory;

	/**
	 * This constructor sets up a PastryNode.  It will bootstrap to an 
	 * existing ring if it can find one at the specified location, otherwise
	 * it will start a new ring.
	 * 
	 * @param localAddress the local address to bind to
	 * @param localPort the local port to bind to 
	 * @param remoteAddress the IP address of the node to boot from
	 * @param remotePort IP port of the node to boot from
	 */
	public PastryGateway(String localAddress, int localPort, String remoteAddress, int remotePort) throws Exception {

		// Loads pastry settings
		env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted LAN)
		env.getParameters().setString("nat_search_policy","never");

		// Generate the NodeIds Randomly
		nidFactory = new RandomNodeIdFactory(env);  


		// construct the PastryNodeFactory, this is how we use rice.pastry.socket
		//PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
		
		InetSocketAddress bootaddress = new InetSocketAddress(remoteAddress, remotePort);
		InetAddress bindAddress = InetAddress.getByName(localAddress);
		
		
		
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindAddress, localPort, env);

		// construct a node
		this.node = factory.newNode();

		this.receiver = new PastryReceiver();

		
		// construct a new MyApp
		app = new PastryProxy(this.node, this.receiver);    


		node.boot(bootaddress);

		// the node may require sending several messages to fully boot into the ring
		synchronized(node) {
			while(!node.isReady() && !node.joinFailed()) {
				// delay so we don't busy-wait
				node.wait(500);

				// abort if can't join
				if (node.joinFailed()) {
					throw new IOException("Could not join the FreePastry ring. Reason: "+node.joinFailedReason()); 
				}
			}       
		}

		log.info("Pastry node " +node.getId() +" has joined the ring at: " + bootaddress);
 
	}

	
	public PastryGateway(int localPort, String remoteAddress, int remotePort) throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, localPort, remoteAddress, remotePort);
	}
	
	public PastryGateway(String remoteAddress, int remotePort)throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, GATEWAY_LOCAL_DEFAULT_PORT, remoteAddress, remotePort);
	}
	
	public PastryGateway(int localPort) throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, localPort, GATEWAY_REMOTE_DEFAULT_ADDRESS, GATEWAY_REMOTE_DEFAULT_PORT);
	}
	
	public PastryGateway()throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, GATEWAY_LOCAL_DEFAULT_PORT, GATEWAY_REMOTE_DEFAULT_ADDRESS, GATEWAY_REMOTE_DEFAULT_PORT);
	}
	
	
	@Override
	public IReceiver getReceiver(){
		return this.receiver;
	}

	
	
	
	
	
	@Override
	public void publish(CURI source, URI subject, Serializable msg) {
		// TODO Auto-generated method stub
		this.app.publish(source, subject, msg);
	}


	@Override
	public void subscribe(URI subject) {
		// TODO Auto-generated method stub
		this.app.subscribe(subject.toString());
	}


	


	@Override
	public void register(CURI URI) {
		this.app.gossip(URI);
	}


	@Override
	public void send(CURI source, CURI dest, PrimeProtocol type, Serializable m) {
		// TODO Auto-generated method stub
		
		Message pck = new PastryUnicastPacket(node.getId(), source, dest, type, m);
		app.send(dest, pck);
	}


	@Override
	public void lookup(CURI source, AURI aURI) {
		// TODO Auto-generated method stub
		this.app.lookup(source, aURI);
	}

	@Override
	public void query(CURI source, String query) {
		// TODO Auto-generated method stub
		this.app.query(source, query);
	}
	

	@Override
	public void replyTo(PrimeMessage from, CURI source, PrimeProtocol type, Serializable m) {
		// TODO Auto-generated method stub
		Message pck = new PastryUnicastPacket(node.getId(), source, from.getSourceURI(), type, m);
		
		if (from instanceof PastryLookupPacket)
			app.send(((PastryLookupPacket) from).getSourceId(), pck);
		if (from instanceof PastryQueryPacket)
			app.send(((PastryQueryPacket) from).getSourceId(), pck);
	}


	@Override
	public void sendToNeighbors(CURI source, PrimeProtocol type, Serializable m) {
		// TODO Auto-generated method stub
		Message pck = new PastryUnicastPacket(node.getId(), source, null, type, m);
		app.sendToNeighbors(pck);
		
	}

	
	

	/*public void send(Serializable m, String dest, String type) {
		// TODO Auto-generated method stub
		
		// route 10 messages
		for (int i = 0; i < 10; i++) {
			// pick a key at random
			Id rId = nidFactory.generateNodeId();

			// send to that key
			Message pck = new MessagePacket(node.getLocalNodeHandle(), dest, m, type);
			app.sendToId(rId, pck);

			// wait a sec
			try {
				env.getTimeSource().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}
