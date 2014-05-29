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

package org.smscom.prime.dns.sesame_impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.description.Description;
import org.smscom.prime.dns.IMatchMaker;
import org.smscom.prime.dns.QueryResult;
import org.smscom.prime.dns.IResourceRegistry;
import org.smscom.prime.dns.LookupResult;



class RDFFileFilter implements FilenameFilter{

	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		
		return name.toLowerCase().endsWith(".rdf");
	}
	
}



public class SesameResourceRegistry implements IResourceRegistry {
	
	
	/** The Constant log. */
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	private Map<CURI, Description> repository = null;
	private SesameMatchMaker reasoner = null;
	
	/**
	 * 
	 * @param ontologies - the set of ontologies needed to initialize the ResourceRegistry
	 * @param ApplicationNamespace the namespace declared by the current Prime Application
	 * @throws Exception 
	 */
	public SesameResourceRegistry(String[] ontologies, String ApplicationNamespace) throws Exception{
		this.repository = new HashMap<CURI, Description>();
		this.reasoner = new SesameMatchMaker(ontologies, ApplicationNamespace);
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#getMatchMaker()
	 */
	@Override
	public IMatchMaker getMatchMaker(){
		return reasoner;
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#advertise(org.smscom.prime.comm.addressing.CURI, org.smscom.prime.description.Description)
	 */
	@Override
	public void advertise(CURI key, Description d){
		reasoner.loadDescription(d);
		this.repository.put(key, d);
		log.debug(key + " registered into Resource Registry");
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#remove(java.lang.String)
	 */
	@Override
	public Description remove(CURI curi){
		return this.repository.remove(curi);
	}
	

//	public Collection<Result> search(String request, String output){
//		
//		Collection<Result> matchList = new Vector<Result>();
//		
//		Collection<RDFDescription> regInstances = this.getInstancesRelated(request);
//		for (RDLDescription regDesc: regInstances){
//			double instanceRes = reasoner.matchInstance(regDesc.getAURI().toString(), request);
//			if (instanceRes != SesameMatchMaker.FAIL){
//				for (String methodName: regDesc.getFunctional().getAvailableOperations()){
//					Operation op = regDesc.getFunctional().getOperation(methodName);
//					double outputRes = reasoner.matchOutput(output, op.getSemanticRef());
//					if (outputRes > instanceRes){
//						matchList.add(new Result(regDesc.getCURI(), regDesc, methodName, outputRes));
//					}else{
//						matchList.add(new Result(regDesc.getCURI(), regDesc, methodName, instanceRes));
//					}
//					
//				}
//			}		
//		}
//		return matchList;
//	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#search(java.lang.String)
	 */
	@Override
	public Collection<LookupResult> search(AURI request){
		
		Vector<LookupResult> matchList = new Vector<LookupResult>();
		
		Collection<Description> regInstances = this.getRegisteredResources();
		for (Description regDesc: regInstances){
			double instanceRes = reasoner.matchInstance(regDesc.getAURI(), request);
			if (instanceRes != SesameMatchMaker.FAIL){
					//matchList.add(new Result(regDesc.getAURI(), regDesc.getCURI(), regDesc, instanceRes));	
					matchList.add(new LookupResult(regDesc.getAURI(), regDesc.getCURI(), instanceRes));	
			}		
		}
		return matchList;
	}
	
	@Override
	public Collection<QueryResult> query(String query) {
		Collection<QueryResult>  res =  reasoner.matchQuery(query);

		return res;
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#loadFromFolder(java.lang.String)
	 */
	@Override
	public void loadFromFolder(String foldername) throws Exception{
		File folder = new File(foldername);
		File[] flist = folder.listFiles(new RDFFileFilter());
		for (File f: flist){
			
			Description desc = Description.descriptionFactory(f.toString(), Description.RDF);
			
			log.debug("Registering resource cURI: " + desc.getCURI() + "...");
			this.advertise(desc.getCURI(), desc);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#getRegisteredResources()
	 */
	@Override
	public Collection<Description> getRegisteredResources(){
		return this.repository.values();
	}
	
    
    /* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#getAURI(org.smscom.prime.comm.addressing.CURI)
	 */
    @Override
	public AURI getAURI(CURI curi){
    	return ((Description) repository.get(curi)).getAURI();
    }
    
    /* (non-Javadoc)
	 * @see org.smscom.prime.dns.ResourceRegistry#getDescription(org.smscom.prime.comm.addressing.CURI)
	 */
    @Override
	public Description getDescription(CURI curi){
    	return repository.get(curi);
    }

	
	
}
