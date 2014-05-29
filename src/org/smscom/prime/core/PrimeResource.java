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

package org.smscom.prime.core;

import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Options;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.description.Description;


/**
 * This is the base class for PRIME Resources. Every PRIME Resource must extend this class to be correctly deployed within the framework.
 * @author Mauro Caporuscio
 *
 */
public class PrimeResource extends ServerResource {

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	protected CURI curi;
	protected AURI auri;
	
	private Description d;
	/**
	 * Returns the <code>PrimeApplication<code> owning the current PrimeResource instance
	 * @return the PrimeApplication to which this resource belongs to.
	 */
	protected PrimeApplication getBase() {
        return (PrimeApplication) getApplication();
    }
	
    @Override
    protected void doInit() throws ResourceException {
    	String id = (String) getRequest().getResourceRef().getIdentifier();
        this.curi = new CURI(id.substring(7));
        this.d = getBase().registry.getDescription(this.curi);
        
        if (d != null) 
        	this.auri = d.getAURI();
        else this.auri = new AURI("ANY");
    }
	
	public PrimeResource(){
		super();
	}
	
//	private void loadDump(){
////		String cname = this.getClass().getCanonicalName();
////		int i = cname.lastIndexOf(".");
////		cname = cname.substring(i+1);
//		String cname = curi.toString().replace("/", ":");
//		
//		try {
//			FileInputStream fin = new FileInputStream(getBase().getTmpPath() + getBase().getApplicationID().toString() + "." +cname+".dat");
//			ObjectInputStream ois = new ObjectInputStream(fin);
//			d = (Description) ois.readObject();
//			ois.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	@Options()
	public StringRepresentation getDescription(){
		return new StringRepresentation(d.toString());
	}
	
	/**
	 * Returns the AURI of this PrimeResource
	 * @return the auri
	 */
	public AURI getAURI(){
		return auri;
	}

	/**
	 * Returns the CURI of this PrimeResource
	 * @return the curi
	 */
	public CURI getCURI(){
		return curi;
	}
	
	
}
