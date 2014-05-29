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
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.description.Description;
import org.smscom.prime.description.rdf.RDFDescription;
import org.smscom.prime.dns.IMatchMaker;
import org.smscom.prime.dns.QueryResult;
import org.smscom.prime.dns.LookupResult;




public class  SesameMatchMaker implements IMatchMaker{
	
	/** The Constant log. */
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	private String		defaultNamespace = "";
	
	Repository repo;
	
	
	private String prefix(){
		
		String prefix = "PREFIX ex: <" + defaultNamespace + "> ";
		prefix += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
		prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
		prefix += "PREFIX rdl: <http://www.smscom.org/ontologies/2013/4/ResourceDescription#> ";
		prefix += "PREFIX owl: <http://www.w3.org/2002/07/owl#> ";
		
		return prefix;
	}
	
	
	
	private Collection<QueryResult> lookupExactMatch(String request) throws Exception{
		log.debug("RUNNING EXACT MATCH");
		return queryKB(prefix() + " select ?curi where{ "
				+ "?res rdl:cURI ?curi.  "
				+ "?res rdl:aURI ?concept. "
				+ "?concept a ex:"+request+"."
				+ "} ");  
	}
	
    //Request is a subtype of advertisement
	private Collection<QueryResult> lookupPluginMatch(String request) throws Exception{
		log.debug("RUNNING PLUGIN MATCH");
		return queryKB(prefix() + " select DISTINCT ?curi ?auri where{  "
				+ "?res rdl:cURI ?curi.  "
				+ "?res rdl:aURI ?concept. "
				+ "?concept a ?auri ."
				+ "ex:"+request+" rdfs:subClassOf ?instance ."
				+ "} ");
		
	}
	
	//Request is a supertype of advertisement
	private Collection<QueryResult> lookupSubsumeMatch(String request) throws Exception{
		log.debug("RUNNING SUBSUME MATCH");
		return queryKB(prefix() + " select DISTINCT ?curi ?auri where{ "
				+ "?res rdl:cURI ?curi.  "
				+ "?res rdl:aURI ?concept. "
				+ "?concept a ?auri ."
				+ "?instance rdfs:subClassOf ex:"+request+"."				
				+ "} ");

	}
	
	public SesameMatchMaker() throws Exception{
		
		repo = new SailRepository(new MemoryStore());
		repo.initialize();
	}
	
	public SesameMatchMaker(String[] ontologyURIs, String namespace) throws Exception{
		
		//repo = new SailRepository(new MemoryStore());
		
		repo = new SailRepository(
                new ForwardChainingRDFSInferencer(
                new MemoryStore()));
		repo.initialize();
		
		this.defaultNamespace = namespace;
		this.loadOntologyFromFiles(ontologyURIs);
	}
	
	
	
	/**
	 * Initialize the actual Knowledge with a set of ontologies 
	 * 
	 * @param ontologies the set on ontologies filenames to load
	 * @param ApplicationNamespace The default namespace declared by the Prime Application
	 * @throws Exception 
	 */
	public void initKnowledgeWithRDFOntology(String[] ontologies, String ApplicationNamespace) throws Exception{
		
		repo = new SailRepository(new MemoryStore());
		repo.initialize();
		
		this.defaultNamespace = ApplicationNamespace;
		
		this.loadOntologyFromFiles(ontologies);
	}
	
	
	private void saveAs(String filename) throws Exception{
    	FileOutputStream out = new FileOutputStream(filename);
    	RepositoryConnection con = repo.getConnection();
    	try {
    	   RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);

    		con.prepareGraphQuery(QueryLanguage.SPARQL, 
    		     "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ").evaluate(writer);
    	}
    	finally {
    	   con.close();
    	}
    	
    }
	
	public void addInstance(String subject, String object){
		
		ValueFactory f = repo.getValueFactory();

		// create some resources and literals to make statements out of
		URI sub = f.createURI(subject);
		URI obj = f.createURI(object);
		

		try {
		   RepositoryConnection con = repo.getConnection();

		   try {
		      con.add(sub, RDF.TYPE, obj);
		   }
		   finally {
		      con.close();
		   }
		}
		catch (OpenRDFException e) {
		   // handle exception
		}	
		
		log.debug(sub.toString() +"--"+ RDF.TYPE +"--" + obj.toString() + " instance added to model");
		
	}
	
	
	

	@Override
	public Collection<LookupResult> getMatchingInstances(AURI request) throws Exception {
		// TODO Auto-generated method stub
		//OntClass instanceReg = model.getOntClass(prefix+ "#"  + instance);
		
		Vector<LookupResult> res = new Vector<LookupResult>();
		
		//Check for direct individuals
		Collection<QueryResult> results = this.lookupExactMatch(request.toString());
		Iterator<QueryResult> r = results.iterator();
		while (r.hasNext()) {
			QueryResult qr = r.next();
			CURI curi = new CURI(qr.getValue("curi"));

			//res.add(new Result(request, curi, null, MatchMaker.EXACT));
			res.add(new LookupResult(request, curi, IMatchMaker.EXACT));
			log.debug("EXACT: " + curi);
		}
		
		
		//Check for PLUGIN individuals
		results = this.lookupPluginMatch(request.toString());
		r = results.iterator();
		while (r.hasNext()) {
			QueryResult qr = r.next();
			CURI curi = new CURI(qr.getValue("curi").toString());
			AURI auri = new AURI(qr.getValue("auri").toString());

			//res.add(new Result(auri, curi, null, MatchMaker.PLUGIN));
			res.add(new LookupResult(auri, curi, IMatchMaker.PLUGIN));
			log.debug("PLUGIN: " + curi);
		}
		
		//Check for SUBSUMES individuals
		results = this.lookupSubsumeMatch(request.toString());
		while (r.hasNext()) {
			QueryResult qr = r.next();
			CURI curi = new CURI(qr.getValue("curi").toString());
			AURI auri = new AURI(qr.getValue("auri").toString());

			//res.add(new Result(auri, curi, null, MatchMaker.SUBSUME));
			res.add(new LookupResult(auri, curi, IMatchMaker.SUBSUME));
			log.debug("SUBSUME: " + curi);
		}
				
		if (res.size() == 0)
			return null;
		
		return res;
	}


	

	private Collection<QueryResult> queryKB(String queryRequest) throws Exception {
		// TODO Auto-generated method stub
		
		Vector<QueryResult> res = new Vector<QueryResult>();
		
		//DEBUG
		this.saveAs("tmp/registryDump.rfd");
		
		
		RepositoryConnection con = repo.getConnection();

		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryRequest);

		TupleQueryResult result = tupleQuery.evaluate();

		while (result.hasNext()) {
			BindingSet bs= result.next();
			res.add(new SesameQueryResult(bs));	
		}
		
		return res;
		
	}
	
	/**
	 * Load the RDF Description into the KnowledBase
	 * @param description the RDF description to load
	 */
	public void loadDescription(Description description){
		try {
			//this.loadDescriptionFromFile(description.getFilename());
			this.loadDescriptionFromModel(((RDFDescription) description).getRDFModel());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Load the RDF File into the KnowledBase
	 * @param description the RDF description to load
	 */
//	private void loadDescriptionFromFile(String filename) throws Exception{
//		
//		RepositoryConnection con = repo.getConnection();
//		
//		File file = new File(filename);
//		con.add(file, defaultNamespace, RDFFormat.RDFXML);
//		con.close();
//	}
	
	public void loadDescriptionFromModel(Model model) throws Exception{
		
		RepositoryConnection con = repo.getConnection();
		con.add(model);
		
	}
	
	
	public void loadOntologyFromURI(URL url) throws Exception{	
		
		RepositoryConnection con = repo.getConnection();
		try {
			con.add(url, url.toString(), RDFFormat.RDFXML);
		}
		finally {
			con.close();
		}
	}
	
	
	public void loadOntologyFromFiles(String[] ontologyURIs) throws Exception {
		
		RepositoryConnection con = null;
		try{
		 con = repo.getConnection();
		}catch(Exception e ){
			e.printStackTrace();
		}
		
		for(String s: ontologyURIs){
		
			File file = new File(s);
			con.add(file, this.defaultNamespace, RDFFormat.RDFXML);
		}
		con.close();
		
		log.debug( "DONE" );
		
	}



	@Override
	public double matchInstance(AURI instance, AURI request) {
		// TODO Auto-generated method stub
		
		boolean result = false;
		RepositoryConnection con = null;
		BooleanQuery boolQuery;
		String queryRequest;
		
		
		
		int sep = instance.toString().lastIndexOf("#");
		String prefix_instance  = "PREFIX in: <" + instance.toString().substring(0, sep+1) + "> "; 
		String resorce_instance = "in:"+instance.toString().substring(sep+1);
		
		sep = request.toString().lastIndexOf("#");
		String prefix_request = "PREFIX re: <" + request.toString().substring(0, sep+1) + "> ";
		String resource_request = "re:"+request.toString().substring(sep+1);
		
		try {
			con = repo.getConnection();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		//CHECK EXACT MATCH
		try {
			queryRequest = prefix() + prefix_instance + prefix_request + 
					"ASK {"
						+  resorce_instance + " owl:sameAs " + resource_request + "."				
						+ "}";
			
			boolQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, queryRequest);
			result = boolQuery.evaluate();
			
			if (result)
				return EXACT;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//CHECK PLUGIN MATCH
		try {
		
			queryRequest = prefix() + prefix_instance + prefix_request + 
					"ASK {"
					+  resource_request + " rdfs:subClassOf " + resorce_instance + "."				
					+ "}";
			
			boolQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, queryRequest);
			result = boolQuery.evaluate();
			
			if (result)
				return PLUGIN;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		//CHECK SUBSUME MATCH
		try {

			queryRequest = prefix() + prefix_instance + prefix_request + 
					"ASK {"
					+  resorce_instance + " rdfs:subClassOf " + resource_request + "."				
					+ "}";

			boolQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, queryRequest);
			result = boolQuery.evaluate();

			if (result)
				return SUBSUME;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Otherwise Match FAIL
		return FAIL;
		
	}


	@Override
	public Collection<QueryResult> matchQuery(String query) {
		try {
			return queryKB(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		return null;
	}

	
}
