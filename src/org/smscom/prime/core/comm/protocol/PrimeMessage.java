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

package org.smscom.prime.core.comm.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.smscom.prime.core.comm.addressing.CURI;

public abstract class PrimeMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4932818895193598107L;
	protected CURI sourceURI;
	protected PrimeProtocol type;
	protected byte[] payload;
	
	
	public PrimeMessage(CURI sourceCURI, PrimeProtocol type, Serializable payload){
		this.sourceURI = sourceCURI;
		this.type = type;
		
		try {
			this.payload = this.serialize(payload);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	/**
	 * 
	 * @return The message content
	 */
	public Serializable getPayload() {
		try {
			return this.deserialize(payload);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @return The cURI of the resource that sent the message
	 */
	public CURI getSourceURI() {
		return sourceURI;
	}
	
	/**
	 * 
	 * @return The message type
	 */
	public PrimeProtocol getType() {
		return type;
	}
	
	
	/**
	 * Return the Serialized object contained in the packet
	 * @return the body of the packet 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Serializable deserialize(byte[] body) throws IOException, ClassNotFoundException {
	
		ByteArrayInputStream bais = new ByteArrayInputStream(body);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Serializable) ois.readObject();
		
	}
	
	/**
	 * Set the body of the packet 
	 * @param body;the body of the packet 
	 * @throws IOException
	 */
	private byte[] serialize(Serializable payload) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(payload);
		return baos.toByteArray();
	}
	

}
