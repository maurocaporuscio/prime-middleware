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

package org.smscom.prime.dns.jena_impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.description.Description;
import org.smscom.prime.description.xml.XMLDescription;
import org.smscom.prime.dns.IMatchMaker;
import org.smscom.prime.dns.QueryResult;
import org.smscom.prime.dns.IResourceRegistry;
import org.smscom.prime.dns.LookupResult;



class RDLFileFilter implements FilenameFilter{

	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		
		return name.toLowerCase().endsWith(".rdl");
	}
	
}



public class JenaResourceRegistry implements IResourceRegistry{
	
	
	/** The Constant log. */
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	private Map<CURI, Description> repository = null;
	private IMatchMaker reasoner = null;
	
	/**
	 * 
	 * @param ontologies - the set of ontologies needed to initialize the ResourceRegistry
	 * @param ApplicationNamespace the namespace declared by the current Prime Application
	 * @throws Exception 
	 */
	public JenaResourceRegistry(String[] ontologies, String ApplicationNamespace) throws Exception{
		this.repository = new HashMap<CURI, Description>();
		this.reasoner = new JenaMatchMaker(ontologies);
	}
	
	@Override
	public IMatchMaker getMatchMaker(){
		return reasoner;
	}
	
	
	@Override
	public void advertise(CURI key, Description dom){
		this.repository.put(key, (XMLDescription) dom);
		log.debug(key + " registered into Resource Registry");
	}
	
	
	@Override
	public Description remove(CURI curi){
		return this.repository.remove(curi);
	}
	
	

	@Override
	public SortedSet<LookupResult> search(AURI request){
		
		TreeSet<LookupResult> matchList = new TreeSet<LookupResult>();
		
		Collection<Description> regInstances = this.getRegisteredResources();
		for (Description regDesc: regInstances){
			double instanceRes = reasoner.matchInstance(regDesc.getAURI(), request);
			if (instanceRes != JenaMatchMaker.FAIL){
					//matchList.add(new Result(regDesc.getAURI(), regDesc.getCURI(), regDesc, instanceRes));		
				matchList.add(new LookupResult(regDesc.getAURI(), regDesc.getCURI(), instanceRes));
			}		
		}
		return matchList;
	}
	
	
	
	@Override
	public void loadFromFolder(String foldername) throws Exception{
		File folder = new File(foldername);
		File[] flist = folder.listFiles(new RDLFileFilter());
		for (File f: flist){
			
			Description desc = Description.descriptionFactory(f.toString(), Description.XML);
			
			log.debug("Registering resource cURI: " + desc.getCURI() + "...");
			this.advertise(desc.getCURI(), desc);
		}
		
	}
	
	@Override
	public Collection<Description> getRegisteredResources(){
		return (Collection<Description>) this.repository.values();
	}
	
    
	@Override
    public AURI getAURI(CURI curi){
    	return ((XMLDescription) repository.get(curi)).getAURI();
    }
    
	@Override
    public Description getDescription(CURI curi){
    	return repository.get(curi);
    }

	@Override
	public Collection<QueryResult> query(String query) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
