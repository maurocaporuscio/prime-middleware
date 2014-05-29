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


import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.dns.IMatchMaker;
import org.smscom.prime.dns.QueryResult;
import org.smscom.prime.dns.LookupResult;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class JenaMatchMaker implements IMatchMaker{
	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());

	
	// the Jena model we are using
	private OntModel			model;
	
	
	public JenaMatchMaker(String[] ontologyURI){
		this.loadOntologyFromFiles(ontologyURI);
	}
	
	
	public double matchInstance(AURI instance, AURI request){
		OntClass instanceReg = model.getOntClass(instance.toString());
		OntClass instanceReq = model.getOntClass(request.toString());
		
		if (instanceReg == null || instanceReq == null){
			log.debug("NO INSTANCES FOUND");
			return FAIL;
		}
		
		double result = matchClasses(instanceReg, instanceReq);
		log.debug(instance + ", " + request + ": " + result);
		
		return result;
	}
	
	
	private double matchClasses(OntClass instanceReg, OntClass instanceReq){
		
		if (instanceReg.hasEquivalentClass(instanceReq) || 
			instanceReg.getLocalName().equals(instanceReq.getLocalName())	)
			return EXACT;
		
		if (instanceReg.hasSubClass(instanceReq))	
			return PLUGIN;
		
		if (instanceReg.hasSuperClass(instanceReq))
			return SUBSUME;
		
		return FAIL;
	}
	
	public double matchOutput(String instance, String request){
		OntClass instanceReg = model.getOntClass(instance);
		OntClass instanceReq = model.getOntClass(request);
		
		if (instanceReg == null || instanceReq == null)
			return FAIL;
		
		double result = matchClasses(instanceReg, instanceReq);
		log.debug(instance + ", " + request + ": " + result);
		
		return result; 
	}

	
	
	public void loadOntologyFromFiles(String[] ontologyURIs) {
		// TODO Auto-generated method stub
		// create the Jena model
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null );
		
		for(String s: ontologyURIs){
			log.debug( "Parsing the ontology: " + s + "...");
			model.read("file:" + s);
		}
		
		log.debug( "Consistency Check... " );
		model.prepare();
		model.validate();
		
		log.debug( "DONE" );
	}


	@Override
	public Collection<LookupResult> getMatchingInstances(AURI request) throws Exception {
		return null;
	}


	@Override
	public Collection<QueryResult> matchQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}


	
}
