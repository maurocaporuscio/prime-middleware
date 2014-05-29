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

import java.io.Serializable;
import java.util.Collection;

import org.smscom.prime.core.comm.ICommGateway;
import org.smscom.prime.core.comm.NotificationHandler;
import org.smscom.prime.core.comm.PrimeHTTPConnection;
import org.smscom.prime.core.comm.PrimeP2MConnection;
import org.smscom.prime.core.comm.PrimeP2PConnection;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.core.comm.addressing.URI;
import org.smscom.prime.description.Description;
import org.smscom.prime.dns.QueryResult;
import org.smscom.prime.dns.LookupResult;

/**
 * This interface defines the set of operations provided by PRIME to Application under development.
 * For benefiting from them, Application must extend the <code>PrimeApplication</code> Class
 * @author Mauro Caporuscio
 *
 */
public interface IPrimeApplication {

	/**
	 * Start up the services provided by the PrimeApplication
	 * @throws Exception
	 */
	public abstract void startPrimeApplication() throws Exception;

	
	/**
	 * Stop the services provided by the PrimeApplication
	 * @throws Exception
	 */
	public abstract void stopPrimeApplication() throws Exception;
	
	
	/**
	 * Get the unique identifier of this Prime Application
	 * @return the application identifier
	 */
	public abstract CURI getApplicationID();
	
	/**
	 * Sets the path to the Resource Repository folder
	 * @param the location where resource descriptions are stored.
	 */
	public abstract void setRepositoryPath(String path);

	/**
	 * Sets the path to the Temporary folder
	 * @param the location where temporary resources will be stored.
	 */
	public abstract void setTmpPath(String path);
	
	
	/**
	 * Sets the Communication Gateway through which the PrimeApplication communicates
	 * @param ICommGateway instance.
	 */
	public abstract void setGateway(ICommGateway gateway);
	
	/**
	 * 
	 * @param maxThreads (DEFAULT = 10) Maximum number of worker threads that can service calls. If this number is reached then additional calls are queued if the "maxQueued" value hasn't been reached.
	 * @param maxQueued (DEFAULT = 0) Maximum number of calls that can be queued if there aren't any worker thread available to service them. If the value is '0', then no queue is used and calls are rejected if no worker thread is immediately available. If the value is '-1', then an unbounded queue is used and calls are never rejected.
	 */
    public abstract void setHTTPServerParameters(Integer maxThreads, Integer maxQueued);
	
	
	/**
	 * Registers a new resource, identified by the given Description, into the DNS
	 * @param desc  The resource Description.
	 * @param resource  The Java class implementing the resource to be deployed.
	 */
	public abstract CURI registerResource(Description desc, Class<? extends PrimeResource> resource);

	/**
	 * Registers a new resource into the DNS. Description is automatically retrieved from the 
	 * Resource Repository (it must have the same file name of the Class<? extends PrimeResource> 
	 * resource given as input).
	 * @param resource  The Java class implementing the resource to be deployed.
	 */
	public abstract CURI registerResource(Class<? extends PrimeResource> resource) throws Exception;

	/**
	 * Interrogates the Prime DNS asking for the set of available resources implementing the given AURI
	 * @param aURI The Semantic concept to search for.
	 * @param timeout The timeout (expressed in milliseconds).
	 * @return A Java Collection of LookupResults. null if NO results
	 */
	public abstract Collection<LookupResult> lookup(AURI aURI, long timeout);

	/**
	 * Interrogates the Prime DNS asking for the set of available resources matching the given SPARQL Query
	 * @param query The SPARQL Query.
	 * @param timeout The timeout (expressed in milliseconds).
	 * @return A Java Collection of QueryResults. null if NO results
	 */
	public abstract Collection<QueryResult> query(String query, long timeout);
	
	

	/**
	 * Declares the intent of Observing the given URI (either a cURI or aURI)
	 * @param uri The URI of interests.
	 * @param notificationHandler The Notifiable Object in charge to handle notifications.
	 */
	public abstract void observe(URI uri, NotificationHandler notificationHandler);

	
	/**
	 * Stop observing the given URI (either a cURI or aURI)
	 * @param uri A NotificationHandler can be associated to many different URI at the same time. URI specifies the topic of interest to be removed. if uri is <code>null</code> NotificationHandler is removed from all topics.
	 * @param notificationHandler The NotificationHandler in charge to handle notifications.
	 */
	void unobserve(URI uri, NotificationHandler notificationHandler);
	
	/**
	 * Notifies the observers by means of the provided message.
	 * @param from The CURI of the resource which is issuing the notification
	 * @param subject The URI of interest.
	 * @param msg The message to notify to all the observers.
	 */
	public abstract void notify(CURI from, URI subject, Serializable msg);

	/**
	 * Initializes the Knowledge
	 * @param ontologies The set of RDF ontology filenames needed for initializing the knowledge.
	 * @param applicationNamespace The NameSpace defined by this Prime Application
	 */
	public abstract void initResourceRegistry(String[] ontologies, String applicationNamespace);

	
	
	/**
	 * Returns a HTTP connection towards the resource identified by target
	 * @param target  the CURI of the target resource
	 * @return the PrimeConnection object managing the connection
	 */
	public abstract PrimeHTTPConnection getHTTPConnection(CURI target);

	/**
	 * Returns a point-to-point connection towards the resource identified by target
	 * @param target the CURI of the target resource
	 * @return the PrimeConnection object managing the connection
	 */
	public abstract PrimeP2PConnection getP2PConnection(CURI target);
	
	/**
	 * Returns a point-to-multipoint connection towards the resources belonging to group
	 * @param group the AURI identifying the set of target resources
	 * @return the PrimeConnection object managing the connection
	 */
	public abstract PrimeP2MConnection getP2MConnection(AURI group);


	

}