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

package org.prime.dns;

import java.util.Collection;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.dns.IMatchMaker;
import org.prime.dns.LookupResult;
import org.prime.dns.QueryResult;

public interface IResourceRegistry {

	/**
	 * Returns an instance of the Semantic MatchMaker used by the Resource Registry
	 * @return The current Semantic MatchMaker
	 */
	public abstract IMatchMaker getMatchMaker();

	/**Registers an entry into the Registry
	 * 
	 * @param curi of the new Resource
	 * @param d the Resource's Description 
	 */
	public abstract void advertise(CURI curi, Description d);

	/**Removes an entry from the Resource Registry
	 * 
	 * @param curi the resource to remove 
	 * @return the Description removed from the registry
	 */
	public abstract Description remove(CURI curi);

	/**
	 * Searches a Resource with a specified aURI and output.
	 * @param request is the aURI of interest
	 * @return a Collection of Result 
	 * @see LookupResult
	 */
	public abstract Collection<LookupResult> search(AURI request);

	/**
	 * Searches a Resource matching the given query.
	 * @param query is the SPARQL query to match
	 * @return a Collection of Result 
	 * @see LookupResult
	 */
	public abstract Collection<QueryResult> query(String query);


	/**
	 * Load resources from a given folder
	 * 
	 * @param foldername
	 * @throws Exception 
	 */
	public abstract void loadFromFolder(String foldername) throws Exception;

	/**
	 * Returns the set of descriptions of registered resources
	 * @return A Collection containing the set of Descriptions
	 */
	public abstract Collection<Description> getRegisteredResources();

	/**
	 * Given a resource CURI, returns the resource AURI 
	 * @param curi - the resource unique identifier
	 * @return the AURI of the given resource
	 */
	public abstract AURI getAURI(CURI curi);
	
	
	/**
	 * Given a resource CURI, returns its Description 
	 * @param curi - the resource unique identifier
	 * @return the Description of the given resource
	 */
	public abstract Description getDescription(CURI curi);

	

}