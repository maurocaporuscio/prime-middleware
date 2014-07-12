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

import java.util.HashMap;

import org.prime.core.comm.protocol.PrimeMessage;
import org.prime.core.comm.protocol.PrimeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.prime.core.comm.IMessageHandler;
import org.prime.core.comm.IReceiver;


/**
 * This class is in charge of dispatching incoming messages to local Notifiables. 
 * @author Mauro Caporuscio
 *
 */
public class MessageDispatcher implements Runnable{

protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	
	private HashMap<String, IMessageHandler> handlers;


	private IReceiver iReceiver;
	//private PrimeApplication mainApp;
	
	public int threadCount = 2;

	/** The blinker. */
	private volatile boolean blinker = true;

	public MessageDispatcher(IReceiver iReceiver){
		this.iReceiver = iReceiver;
		this.handlers = new HashMap<String, IMessageHandler>();
	}

	
	public void addHandler(PrimeProtocol messageType, IMessageHandler handler){
		this.handlers.put(messageType.getName(), handler);
	}
	
	
	public IMessageHandler getHandler(PrimeProtocol messageType){
		return this.handlers.get(messageType.getName().toString());
	}
	
	/**
	 * Stops the LocalPacketDispatcher thread
	 */
	public void stopDispatcher() {
		blinker = false;
	}

    public void setThreadNumber(int t){
    	this.threadCount = t;
    }
	
	public void start(){
		
		while (this.threadCount > 0){
			Thread t = new Thread(this);
			t.start();
			this.threadCount--;
		}
	}
	

	public void run() {
		//Thread thisThread = Thread.currentThread();

		this.blinker = true;
		while (blinker) {

			if (iReceiver != null){
				PrimeMessage pck = iReceiver.accept();
				IMessageHandler h = this.getHandler(pck.getType());
				if (h != null) 
					h.handleMessage(pck);
				else log.debug("No handler for " + pck.getType() + " -> Message dropped");
			}

		}

	}
}