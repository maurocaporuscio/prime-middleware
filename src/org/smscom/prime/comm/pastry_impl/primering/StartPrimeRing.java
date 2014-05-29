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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rice.environment.Environment;

import com.beust.jcommander.JCommander;

public class StartPrimeRing {

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	
	public static void main(String[] args) throws Exception {

		PrimeRingParameters param = new PrimeRingParameters();
		JCommander jc = new JCommander(param, args);
		jc.setProgramName("StartPrimeRing");

		// Loads pastry settings
		Environment env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted LAN)
		env.getParameters().setString("nat_search_policy","never");

		try {
			new PastryRingGateway(param.bindaddr, param.bindport, param.bootaddr, param.bootport);
			
		} catch (Exception e) {
			// remind user how to use
			e.printStackTrace();
			jc.usage(); 

		} 
	}

}
