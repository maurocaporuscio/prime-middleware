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


/**
 * In order to receive notifications Prime Applications should extend this class 
 * by implementing the <code>handleNotification(Serializable msg)</code> method.
 *
 */
public abstract class NotificationHandler {
	
	protected String id;
	
	/**
	 * 
	 * @param id the unique identifier of this Notifiable
	 */
	public NotificationHandler(String id){
		this.id = id;
	}
	
	/**
	 * 
	 * @return the Notifiable identifier
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * Notifiables must implement this method for call back. It defines how to manage the incoming notification.
	 * @param msg the incoming notification
	 */
	public abstract void handleNotification(Serializable msg);
	
	
}
