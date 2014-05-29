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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.beust.jcommander.Parameter;

public class PrimeRingParameters {
	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	private static String getHost(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Parameter
	public List<String> parameters = new ArrayList<String>();

	@Parameter(names = "-bindaddr", description = "the IP address to use locally")
	public String bindaddr = getHost();
	
	@Parameter(names = "-bindport", description = "the port to use locally")
	public Integer bindport = 1308;

	@Parameter(names = "-bootaddr", description = "the IP address of a remote node to join")
	public String bootaddr = getHost();

	@Parameter(names = "-bootport", description = "the port of a remote node to join")
	public Integer bootport = 1308;
}
