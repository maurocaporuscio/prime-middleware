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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.IReceiver;
import org.smscom.prime.core.comm.protocol.PrimeMessage;

public class PastryReceiver implements IReceiver {

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
//	private Queue<PrimePacket> queue = new ArrayDeque<PrimePacket>();

	private BlockingQueue<PrimeMessage> queue = new LinkedBlockingQueue<PrimeMessage>();
	
	/**
	 * 
	 * @param obj
	 */
//	public void enqueue(PrimePacket obj){
//		synchronized (queue){
//			if(queue.isEmpty()){
//				queue.add(obj);
//				queue.notify();
//			}else
//				queue.add(obj);
//		}
//		log.debug("Message received and enqueued");
//	}

	public void enqueue(PrimeMessage obj){
		try {
			queue.put(obj);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Message received and enqueued");
	}
	
	@Override
	public PrimeMessage accept(){
		log.debug("is waiting for messages");
		try {
			return queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
