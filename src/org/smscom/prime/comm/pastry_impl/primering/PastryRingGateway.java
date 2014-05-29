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

package org.smscom.prime.comm.pastry_impl.primering;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.comm.pastry_impl.PastryProxy;
import org.smscom.prime.comm.pastry_impl.PastryReceiver;

import rice.environment.Environment;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;


public class PastryRingGateway{

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
	 * @param bindAddress the local address to bind to
	 * @param bindport the local port to bind to 
	 * @param bootaddress the IP:port of the node to boot from
	 */
	public PastryRingGateway(String localAddress, int localPort, String remoteAddress, int remotePort) throws Exception {

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

		this.receiver = new PastryRingReceiver();

		
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

		log.info("Pastry ring " +node.getId() +" has been created the ring at: " + bootaddress);
 
	}

	
	public PastryRingGateway(int localPort, String remoteAddress, int remotePort) throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, localPort, remoteAddress, remotePort);
	}
	
	public PastryRingGateway(String remoteAddress, int remotePort)throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, GATEWAY_LOCAL_DEFAULT_PORT, remoteAddress, remotePort);
	}
	
	public PastryRingGateway(int localPort) throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, localPort, GATEWAY_REMOTE_DEFAULT_ADDRESS, GATEWAY_REMOTE_DEFAULT_PORT);
	}
	
	public PastryRingGateway()throws Exception {
		this(GATEWAY_LOCAL_DEFAULT_ADDRESS, GATEWAY_LOCAL_DEFAULT_PORT, GATEWAY_REMOTE_DEFAULT_ADDRESS, GATEWAY_REMOTE_DEFAULT_PORT);
	}
}
