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

import org.prime.core.comm.addressing.CURI;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.prime.core.comm.IPrimeConnection;

/**
 * Manages HTTP connection towards remote resources.
 * @author Mauro Caporuscio
 *
 */
public class PrimeHTTPConnection extends ClientResource implements IPrimeConnection {
	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	

	/**
	 * Creates an HTTPConnetion towards the destination 
	 * @param curi the CURI identifying the destination
	 */
	public PrimeHTTPConnection(CURI curi){
		super("http://" + curi.toString());
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#get()
	 */
	@Override
	public Representation get() throws ResourceException{
		return super.get();
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#get(org.restlet.data.MediaType)
	 */

	@Override
	public Representation get(MediaType contenttype){
		try{
			return super.get(contenttype);
		}catch(Exception e){
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#post(org.restlet.representation.Representation)
	 */

	@Override
	public Representation post(Representation r) throws ResourceException{
		return super.post(r);
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#post(org.restlet.representation.Representation, org.restlet.data.MediaType)
	 */
	@Override
	public Representation post(Representation r, MediaType contenttype) throws ResourceException{
		return super.post(r, contenttype);
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#put(org.restlet.representation.Representation)
	 */
	@Override
	public Representation put(Representation r) throws ResourceException{
		return super.put(r);
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#put(org.restlet.representation.Representation, org.restlet.data.MediaType)
	 */
	@Override
	public Representation put(Representation r, MediaType contenttype) throws ResourceException{
		return super.put(r, contenttype);
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#options()
	 */

	@Override
	public Representation options() throws ResourceException{
		return super.options();
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#options(org.restlet.data.MediaType)
	 */

	@Override
	public Representation options(MediaType contenttype) throws ResourceException{
		return super.options(contenttype);
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#delete()
	 */
	@Override
	public Representation delete() throws ResourceException{
		return super.delete();
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#delete(org.restlet.data.MediaType)
	 */
	@Override
	public Representation delete(MediaType contenttype) throws ResourceException{
		return super.delete(contenttype);
	}
}
