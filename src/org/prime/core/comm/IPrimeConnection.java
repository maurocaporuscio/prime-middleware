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

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public interface IPrimeConnection {

	public abstract Representation get();

	public abstract Representation get(MediaType contenttype);

	public abstract Representation post(Representation r);
	
	public abstract Representation post(Representation r, MediaType contenttype);

	public abstract Representation put(Representation r);
	
	public abstract Representation put(Representation r, MediaType contenttype);

	public abstract Representation options();
	
	public abstract Representation options(MediaType contenttype);

	public abstract Representation delete();

	public abstract Representation delete(MediaType contenttype);
}