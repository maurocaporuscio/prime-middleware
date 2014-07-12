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

package org.prime.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.prime.core.comm.ICommGateway;
import org.prime.core.comm.IMessageHandler;
import org.prime.core.comm.IReceiver;
import org.prime.core.comm.MessageDispatcher;
import org.prime.core.comm.NotificationHandler;
import org.prime.core.comm.PrimeHTTPConnection;
import org.prime.core.comm.PrimeP2MConnection;
import org.prime.core.comm.PrimeP2PConnection;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.core.comm.addressing.URI;
import org.prime.core.comm.protocol.PrimeGroupMessage;
import org.prime.core.comm.protocol.PrimeLookupMessage;
import org.prime.core.comm.protocol.PrimeMessage;
import org.prime.core.comm.protocol.PrimeProtocol;
import org.prime.core.comm.protocol.PrimeQueryMessage;
import org.prime.core.comm.protocol.PrimeUnicastMessage;
import org.prime.description.Description;
import org.prime.dns.IResourceRegistry;
import org.prime.dns.LookupResult;
import org.prime.dns.QueryResult;
import org.prime.dns.sesame_impl.SesameResourceRegistry;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applications should extend this class in order to benefit from the PRIME framework.
 * @author Mauro Caporuscio
 *
 */
public class PrimeApplication extends Application implements IPrimeApplication, IMessageHandler{


	static{
		System.getProperties().put("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade");
	}
	
	
	protected  static Logger log = (Logger)LoggerFactory.getLogger("PrimeApplication");
	
	/** The buffer. */
	private Collection<LookupResult>	lookupReplyBuffer = null;
	private Collection<QueryResult>	    queryReplyBuffer = null;
	
	/** Check if the queue is empty. */
	Object emptyBufferSemaphore = new Object();
	
	
	//The local set of Notification handlers
	HashMap<String, List<NotificationHandler>> notifiables;
	
	
	protected ICommGateway gateway;
	protected CURI applicationId;			 
	
	private int		httpPort;
	
	
	protected IReceiver iReceiver;
	protected MessageDispatcher dispatcher;
	
	
	protected Router router;
	protected Server HTTPServer;
	protected Component component;
	protected IResourceRegistry registry;
	protected String repo_path = null;
	protected String tmp_path = null;

	protected Properties properties;
	
	
	public static final Integer 	HTTP_SERVER_DEFAULT_PORT = 8010;
	public static final Integer     HTTP_SERVER_DEFAULT_THREADS = 10;
	public static final Integer     HTTP_SERVER_DEFAULT_QUEUE = 0;
	
	
	/**
	 * 
	 * @param id PrimeApplication Identifier (serves as base URI for hosted resources)
	 * @param httpPort HTTP port for incoming requests
	 * @throws Exception
	 */
	public PrimeApplication(String id, int httpPort) throws Exception{
		super();
				
		this.notifiables = new HashMap<String, List<NotificationHandler>>();
		this.applicationId = new CURI(id);
		this.loadDefaultProperties();

	    
	    //RESTLET INITIALIZATION
	    this.httpPort = httpPort;
		router = new Router(getContext());
		component = new Component();
		
		HTTPServer = new Server(Protocol.HTTP, httpPort);
		component.getServers().add(HTTPServer);

		//INTERNAL HTTP PARAMETERS
//		HTTPServer.getContext().getParameters().add("maxThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
//		HTTPServer.getContext().getParameters().add("minThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
//		HTTPServer.getContext().getParameters().add("lowThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
//		HTTPServer.getContext().getParameters().add("maxQueued", HTTP_SERVER_DEFAULT_QUEUE.toString());
//		HTTPServer.getContext().getParameters().add("directBuffers", "true");
        //component.getServers().add(Protocol.HTTP, httpPort);
        //component.setContext(new Context()); 
        
		//SIMPLEFRAMEWORK PARAMETERS
		HTTPServer.getContext().getParameters().add("defaultThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
		
		//JETTY PARAMETERS
		//HTTPServer.getContext().getParameters().add("minThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
		//HTTPServer.getContext().getParameters().add("maxThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
		//HTTPServer.getContext().getParameters().add("acceptorThreads", HTTP_SERVER_DEFAULT_THREADS.toString());
		//HTTPServer.getContext().getParameters().add("acceptQueueSize", HTTP_SERVER_DEFAULT_QUEUE.toString());
		
        component.getClients().add(Protocol.HTTP); 
		component.getDefaultHost().attach("/"+id, this);	
	}
	

	/**
	 * 
	 * @param id PrimeApplication Identifier (serves as base URI for hosted resources)
	 * @throws Exception
	 */
	public PrimeApplication(String id)  throws Exception {
		this(id, HTTP_SERVER_DEFAULT_PORT);
	}
	

	
	@Override
	public void setGateway(ICommGateway gateway) {
		//PASTRY INIZIALIZATION
//		InetSocketAddress bootaddress = new InetSocketAddress(remoteAddress, remotePort);
//		InetAddress bindaddress = InetAddress.getByName(localAddress);
//		new PastryGateway(bindaddress, localPort, bootaddress);
		this.gateway = gateway;
		this.iReceiver = this.gateway.getReceiver();
	}
	
	/**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        return this.router;
    }
	
    /* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#setHTTPServerParameters(java.lang.Integer, java.lang.Integer)
	 */
	@Override
    public void setHTTPServerParameters(Integer maxThreads, Integer maxQueued){
//		HTTPServer.getContext().getParameters().set("maxThreads", maxThreads.toString());
//		HTTPServer.getContext().getParameters().set("lowThreads", maxThreads.toString());
//		HTTPServer.getContext().getParameters().set("minThreads", maxThreads.toString());
//    	HTTPServer.getContext().getParameters().set("maxQueued", maxQueued.toString());
    	
    	//SIMPLEFRAMEWORK PARAMETERS
    	HTTPServer.getContext().getParameters().set("defaultThreads", maxThreads.toString());
        
    	
		//JETTY PARAMETERS
		//HTTPServer.getContext().getParameters().set("minThreads", maxThreads.toString());
		//HTTPServer.getContext().getParameters().set("maxThreads", maxThreads.toString());
		//HTTPServer.getContext().getParameters().set("acceptorThreads", maxThreads.toString());
		//HTTPServer.getContext().getParameters().set("acceptQueueSize", maxQueued.toString());
		
		
		
    	for(Parameter p: HTTPServer.getContext().getParameters() )
    		log.debug(p.toString());

    	
    }
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#start()
	 */
	@Override
	public void startPrimeApplication() throws Exception{
		super.start();
		
		dispatcher = new MessageDispatcher(iReceiver);
		dispatcher.addHandler(PrimeProtocol.LOOKUP, this);
		dispatcher.addHandler(PrimeProtocol.LOOKUP_REPLY, this);
		dispatcher.addHandler(PrimeProtocol.NOTIFY, this);
		dispatcher.addHandler(PrimeProtocol.QUERY, this);
		dispatcher.addHandler(PrimeProtocol.QUERY_REPLY, this);
		dispatcher.start();
		
		for (Parameter p: HTTPServer.getContext().getParameters())
			System.out.println(p);
		component.start();
		
	}
	
	
	@Override
	public void stopPrimeApplication() throws Exception{
		super.stop();
		dispatcher.stopDispatcher();
	}
	
//	private void dumpTmp(String fname, Description d){
//		try {
//			FileOutputStream fout = new FileOutputStream(this.tmp_path + this.applicationId.toString() + "." + fname+".dat");
//			ObjectOutputStream oos = new ObjectOutputStream(fout);
//			oos.writeObject(d);
//			oos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			log.error(e.getMessage());
//		}
//	}
	
	protected void loadDefaultProperties(){
		
		// create and load default properties
		this.properties = new Properties();
		try {
			FileInputStream in = new FileInputStream("default.prop");
			this.properties.load(in);
			in.close();
			
			this.repo_path = this.properties.getProperty("repository");
			this.tmp_path = this.properties.getProperty("tmp");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#setResourceRepository(java.lang.String)
	 */
	@Override
	public void setRepositoryPath(String path){
		this.repo_path = path;
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#setResourceRepository(java.lang.String)
	 */
	@Override
	public void setTmpPath(String path){
		this.tmp_path = path;
	}
	
	
	public String getTmpPath(){
		return this.tmp_path;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#registerResource(org.smscom.prime.rdl.Description, java.lang.Class)
	 */
	@Override
	public CURI registerResource(Description desc, Class<? extends PrimeResource> resource){
		
		String cURI = desc.getCURI().toString();
		router.attach("/"+ cURI, resource);
		
		try {
			CURI adv = new CURI(InetAddress.getLocalHost().getHostAddress() + ":" + httpPort + "/" + this.applicationId + "/" + cURI);	
			desc.setCURI(adv);
			gateway.register(adv);
			registry.advertise(adv, desc);
			
//			String cname = resource.getCanonicalName();
//			int i = cname.lastIndexOf(".");
//			cname = cname.substring(i+1);
			//this.dumpTmp(adv.toString().replace("/", ":"), desc);
			
			
			log.info("New resource registered at: " + adv);
			
			return adv;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	
	public CURI registerFilteredResource(Description desc, Filter filter, Class<? extends PrimeResource> resource){
		
		String cURI = desc.getCURI().toString();
		
		filter.setContext(getContext());
        filter.setNext(resource);
       
		router.attach("/"+ cURI, filter);
		
		
		try {
			CURI adv = new CURI(InetAddress.getLocalHost().getHostAddress() + ":" + httpPort + "/" + this.applicationId + "/" + cURI);	
			desc.setCURI(adv);
			gateway.register(adv);
			registry.advertise(adv, desc);
			
			log.info("New resource registered at: " + adv);
			
			return adv;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#registerResource(java.lang.Class)
	 */
	@Override
	public CURI registerResource(Class<? extends PrimeResource> resource) throws Exception{
		
		String cname = resource.getCanonicalName();
		int i = cname.lastIndexOf(".");
		cname = cname.substring(i+1);
		String fname = this.repo_path + cname + ".rdf";
		Description desc = Description.descriptionFactory(fname, Description.RDF);
	
		return this.registerResource(desc, resource);
	}
		

	public CURI registerFilteredResource(Filter filter, Class<? extends PrimeResource> resource) throws Exception{
		
		String cname = resource.getCanonicalName();
		int i = cname.lastIndexOf(".");
		cname = cname.substring(i+1);
		String fname = this.repo_path + cname + ".rdf";
		Description desc = Description.descriptionFactory(fname, Description.RDF);
	
		return this.registerFilteredResource(desc, filter, resource);
	}
	
	
	/**
	 * 
	 * @param msg
	 */
	public  void processLookupRequest(PrimeLookupMessage msg) {
		
		if (registry == null)
			return;
		
		Collection<LookupResult> r;
		r = registry.search(msg.getAURI());
		if (!r.isEmpty())
			gateway.replyTo(msg, this.applicationId, PrimeProtocol.LOOKUP_REPLY, (Serializable) r);
	}
	
	public void processQueryRequest(PrimeQueryMessage pck) {
		Collection<QueryResult> r = registry.query(pck.getQuery());
		
		gateway.replyTo(pck, this.applicationId, PrimeProtocol.QUERY_REPLY, (Serializable) r);
		//gateway.replyTo(pck, this.applicationId, PrimeProtocol.QUERY_REPLY, "ACK");
	}
	
	

	@Override
    public Collection<QueryResult> query(String query, long timeout) {
		queryReplyBuffer = new Vector<QueryResult>();
		
		try {
			gateway.query(this.applicationId, query);
			
			log.debug("QUERY: waiting for results");
			Thread.sleep(timeout);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.queryReplyBuffer.isEmpty())
			return null;
		return this.queryReplyBuffer;
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#lookup(org.smscom.prime.runtime.comm.addressing.AURI, long)
	 */
	@Override
	public Collection<LookupResult> lookup(AURI aURI, long timeout){
		lookupReplyBuffer = new Vector<LookupResult>();
		
		try {
			gateway.lookup(this.applicationId, aURI);
			
			log.debug("LOOKUP: waiting for results");
			Thread.sleep(timeout);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (this.lookupReplyBuffer.isEmpty())
			return null;
		return this.lookupReplyBuffer;
	}
	
	
	
	
	
	
	
	
//	/**
//	 * 
//	 * @param pck
//	 */
//	 protected synchronized void processUnicastPacket(PrimeUnicastPacket pck) {
//		log.debug("Processing unicast packet:" + pck);
//		
//		if (pck.getType() == PrimeProtocol.LOOKUP_REPLY)    
//			this.processLookupReply(pck);
//		
//		if (pck.getType() == PrimeProtocol.QUERY_REPLY)    
//			this.processQueryReply(pck);
//	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#observe(org.smscom.prime.runtime.comm.addressing.URI, org.smscom.prime.runtime.Notifiable)
	 */
	@Override
	public void observe(URI uri, NotificationHandler notificationHandler){
		gateway.subscribe(uri);
		this.addNotifiable(notificationHandler, uri);
	}
	
	@Override
	public void unobserve(URI uri, NotificationHandler notificationHandler){
		//gateway.subscribe(uri);
		this.removeNotifiable(notificationHandler, uri);
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#notify(org.smscom.prime.runtime.comm.addressing.CURI, org.smscom.prime.runtime.comm.addressing.AURI, java.io.Serializable)
	 */
	@Override
	public void notify(CURI from, URI subject, Serializable msg){
		gateway.publish(from, subject, msg);
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#initResourceRegistry(java.lang.String)
	 */
	@Override
	public void initResourceRegistry(String[] ontology, String namespace){
		try {
			
			this.registry = new SesameResourceRegistry(ontology, namespace);
			
			//log.info("ResourceDiscovery started on: " + incomingAddress.toString());
		} catch (Exception e) {
			log.error("", e);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#getHTTPConnection(org.smscom.prime.runtime.comm.addressing.CURI)
	 */
	@Override
	public PrimeHTTPConnection getHTTPConnection(CURI target) {
		// TODO Auto-generated method stub
		return new PrimeHTTPConnection(target);
	}

	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#getP2PConnection(org.smscom.prime.runtime.comm.addressing.CURI)
	 */
	@Override
	public PrimeP2PConnection getP2PConnection(CURI target) {
		// TODO Auto-generated method stub
		return new PrimeP2PConnection(target);
	}


	/* (non-Javadoc)
	 * @see org.smscom.prime.runtime.PrimeApplicationInterface#getP2MConnection(org.smscom.prime.runtime.comm.addressing.AURI)
	 */
	@Override
	public PrimeP2MConnection getP2MConnection(AURI group) {
		// TODO Auto-generated method stub
		return new PrimeP2MConnection(group);
	}
	
	
	
	@Override
	public CURI getApplicationID() {
		// TODO Auto-generated method stub
		return this.applicationId;
	}


	@Override
	public void handleMessage(PrimeMessage pck) {
		// TODO Auto-generated method stub
			
		PrimeProtocol p = (PrimeProtocol) pck.getType();
		switch(p.getCode()){
			case 0: 	    log.debug("NOTIFY: "+ ((PrimeGroupMessage) pck).getGroup());
							this.procesNotification((PrimeGroupMessage) pck);	
							break;
			case 1:			log.debug("LOOKUPPACKET: "+ ((PrimeLookupMessage) pck).getAURI());	
							processLookupRequest((PrimeLookupMessage) pck); 
							break;
							
			case 2:    		log.debug("LOOKUPREPLY: ");
							this.processLookupReply((PrimeUnicastMessage) pck);	
							break;
		
			case 3: 		log.debug("QUERYPACKET: "+ ((PrimeQueryMessage) pck).getQuery());
							processQueryRequest((PrimeQueryMessage) pck);
							break;
		
		
			case 4:			log.debug("QUERYREPLYPACKET: "+ (PrimeUnicastMessage) pck);
							this.processQueryReply((PrimeUnicastMessage) pck);	
							break;
		} 
		
//		if (pck.getType() == PrimeProtocol.LOOKUP){
//			//PrimeLookupPacket look = (PrimeLookupPacket) obj;
//			log.debug("LOOKUPPACKET: "+ ((PrimeLookupMessage) pck).getAURI());	
//			processLookupRequest((PrimeLookupMessage) pck); 	
//		}   

//		if (pck.getType() == PrimeProtocol.QUERY){
//			//PrimeLookupPacket look = (PrimeLookupPacket) obj;
//			log.debug("QUERYPACKET: "+ ((PrimeQueryMessage) pck).getQuery());
//
//			processQueryRequest((PrimeQueryMessage) pck); 
//
//		}  


//		if (pck.getType() == PrimeProtocol.LOOKUP_REPLY){
//			PrimeUnicastMessage m = (PrimeUnicastMessage) pck;
//			log.debug("LOOKUPREPLY: "+ m.getPayload());
//			this.processLookupReply(m);
//		}  


//		if (pck.getType() == PrimeProtocol.QUERY_REPLY){  
//			PrimeUnicastMessage m = (PrimeUnicastMessage) pck;
//			log.debug("QUERYREPLYPACKET: "+ m.getPayload());
//			this.processQueryReply(m);
//		}
		
//		if (pck.getType() == PrimeProtocol.NOTIFY){
//			PrimeGroupMessage g = (PrimeGroupMessage) pck;
//			log.debug("GROUPPACKET: "+ g.getGroup());
//			this.procesNotification(g);
//		}
		
		
		
	}
	
	
	//**********************************************************************
	// PRIVATE METHODS
	//**********************************************************************
	
	/**
	 * Add a new notifiable to the Local Dispatcher
	 * @param notificationHandler The notifiable to be added
	 * @param subject the URI of interest for this notifiable
	 */
	private void addNotifiable(NotificationHandler notificationHandler, URI subject){
		if ((notifiables.containsKey(subject.toString()))){
			notifiables.get(subject.toString()).add(notificationHandler);
		}else{
			List<NotificationHandler> list = new LinkedList<NotificationHandler>();
			list.add(notificationHandler);
			notifiables.put(subject.toString(), list);
		}
	}

	/**
	 * Removes the given Notifiable from the Local Dispatcher
	 * @param notificationHandler The notifiable to be removed
	 * @param subject A Notifiable can be associated to many different URI at the same time. Subject specifies the topic of interest to be removed.
	 * if subject is <code>null</code> Notifiable is removed from all topics.   
	 */
	private void removeNotifiable(NotificationHandler notificationHandler, URI subject){
		
		if(subject == null){
			Collection<List<NotificationHandler>> allnotif = notifiables.values();
			for (List<NotificationHandler> list: allnotif){
				list.remove(notificationHandler);
			}
		}else{
			if ((notifiables.containsKey(subject.toString()))){
				notifiables.get(subject).remove(notificationHandler);
				if (notifiables.get(subject.toString()) == null)
					notifiables.remove(subject.toString());
			}
		}
	}
	
	private void procesNotification(PrimeGroupMessage msg){
		if (notifiables.containsKey(msg.getGroup().toString())){

			for (Iterator<NotificationHandler> i = notifiables.get(msg.getGroup().toString()).iterator(); i.hasNext();){
				((NotificationHandler) i.next()).handleNotification(msg.getPayload());
			}
		}
	}
	
	private void  processLookupReply(PrimeUnicastMessage pck) {
		log.debug("Processing unicast packet from " + pck.getSourceURI());
		if(pck.getPayload() instanceof Collection){
			
			@SuppressWarnings("unchecked")
			Collection<LookupResult> t = (Collection<LookupResult>) pck.getPayload();
			for(Iterator<LookupResult> i = t.iterator(); i.hasNext();){
				this.lookupReplyBuffer.add((LookupResult) i.next());	
			}
			
			
//		    synchronized(this.emptyBufferSemaphore){
//		    	this.emptyBufferSemaphore.notify();
//		    }
		}
	}
	
	private  void processQueryReply(PrimeUnicastMessage pck) {
		log.debug("Processing unicast packet from " + pck.getSourceURI());
		
		if(pck.getPayload() instanceof String)
			System.out.println(pck.getPayload());
		
		if(pck.getPayload() instanceof Collection<?>){
			
			@SuppressWarnings("unchecked")
			Collection<QueryResult> t = (Collection<QueryResult>) pck.getPayload();
			for(Iterator<QueryResult> i = t.iterator(); i.hasNext();){
				this.queryReplyBuffer.add((QueryResult) i.next());	
			}
		
			
//		    synchronized(this.emptyBufferSemaphore){
//		    	this.emptyBufferSemaphore.notify();
//		    }
		}
	}
	
	
}
