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

package org.prime.description.rdf;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.description.rdl.Context;
import org.prime.description.rdl.Dependences;
import org.prime.description.rdl.Functional;
import org.prime.description.rdl.IOOperation;
import org.prime.description.rdl.Operation;
import org.prime.description.rdl.QoS;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RDFDescription extends Description implements Serializable{

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8207803790214544616L;
	
	static final String ns = "http://www.erc-smscom.org/ontologies/2013/4/ResourceDescription#";
	
	
	protected Model model = null;
	
	public RDFDescription(String filename) throws Exception{
		this.model = RDFDescriptionParser.parseFile(filename);
		this.parseModel();
	}
	
	private synchronized void parseModel() throws Exception{
		
		ValueFactory f = new MemValueFactory();
		
		this.qos = new QoS();
		this.dependences = new Dependences();
		this.functional = new Functional();
		this.context = new RDFContext(this.model, ns);
		
		Iterator<Statement> iterator = model.iterator();
		

		while(iterator.hasNext()){
			Statement s = iterator.next();
			
			//CURI
			if (s.getPredicate().toString().equals(ns + "cURI")){
				String tmp = s.getObject().stringValue();
				this.cURI = new CURI(tmp);
				//int i = s.getObject().toString().lastIndexOf("/");
				//this.cURI = new CURI(s.getObject().toString().substring(i+1));
				log.debug("cURI: " + this.cURI.toString());
			}
			
			//AURI
			if (s.getPredicate().toString().equals(ns + "aURI")){		
				String obj = s.getObject().stringValue();
				Value auri = GraphUtil.getOptionalObject(model, f.createBNode(obj), f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
				this.aURI = new AURI(auri.toString());
				log.debug("aURI: " + this.aURI.toString());
			}
			
			//================================================			
			//			QOS
			//================================================		
			//RELIABILITY
			if (s.getPredicate().toString().equals(ns + "reliability")){		
				this.qos.setReliability(Double.parseDouble(s.getObject().stringValue()));
				log.debug("qos.reliability: " + this.qos.getReliability());
			}
			
			//AVAILABILITY
			if (s.getPredicate().toString().equals(ns + "availability")){		
				this.qos.setAvailability(Double.parseDouble(s.getObject().stringValue()));
				log.debug("qos.availability: " + this.qos.getAvailability());
			}
			
			
			//RESPONSE TIME
			if (s.getPredicate().toString().equals(ns + "response_time")){		
				this.qos.setResponseTime(Double.parseDouble(s.getObject().stringValue()));
				log.debug("qos.response_time: " + this.qos.getResponseTime());
			}
			
			//STRUCTURAL
			if (s.getPredicate().toString().equals(ns + "structural")){		
				this.qos.setStructural(Double.parseDouble(s.getObject().stringValue()));
				log.debug("qos.structural: " + this.qos.getStructural());
			}
			
			//COST
			if (s.getPredicate().toString().equals(ns + "cost")){		
				String obj = s.getObject().stringValue();
				Value cost = GraphUtil.getOptionalObject(model, f.createBNode(obj), f.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
				int i = cost.stringValue().lastIndexOf("#");
				this.qos.setCostRate(cost.stringValue().substring(i+1));
				log.debug("qos.cost_rate: " + this.qos.getCostRate());
			}
			
			//PRICE
			if (s.getPredicate().toString().equals(ns + "price")){		
				this.qos.setCost(Double.parseDouble(s.getObject().stringValue()));
				log.debug("qos.cost: " + this.qos.getCost());
			}
			
			//================================================			
			//			DEPENDENCES
			//================================================	
			if (s.getPredicate().toString().equals(ns + "resType")){
				AURI type = new AURI(s.getObject().stringValue());
				Resource sub = s.getSubject();
				
				int times = Integer.parseInt(GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "times")).stringValue());
				Metrics metrics = Metrics.parseString(GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "metrics")).stringValue());
				
				AssemblyUtilityMonitor r = new AssemblyUtilityMonitor(type, null, times, metrics, 0.0);
				this.dependences.put(type, r); 
				
				log.debug("qos.dependence: " + type.toString() + " times: " + times + "metrics: " + metrics);
			}
			
			//================================================			
			//			CONTEXT
			//================================================	
			if (s.getPredicate().toString().equals(ns + "hasContext")){
				String obj  = s.getObject().stringValue();
				String lat = GraphUtil.getOptionalObject(model, f.createBNode(obj), f.createURI(ns + "latitude")).stringValue();
				((RDFContext) this.context).init("latitude", lat);
				
				String lon = GraphUtil.getOptionalObject(model, f.createBNode(obj), f.createURI(ns + "longitude")).stringValue();
				((RDFContext) this.context).init("longitude", lon);

				log.debug("context.properties:  lat: " + lat + ", lon: "+lon);
			}
			
			//================================================			
			//			FUNCTIONAL
			//================================================
			
			//GET
			if (s.getObject().toString().equals(ns + "GET")){	
				Resource sub = s.getSubject();
				
				Operation op = new Operation();
				Value v =  GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "semanticRef"));
				if (v != null){ 
					String ref = v.stringValue();
					op.setSemanticRef(ref);
				}
				String out = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "output")).stringValue();
				op.setOutput(out);
				this.functional.put(Operation.GET, op);
				
				log.debug("Operation: " + Operation.GET + ", out: " + op.getOutput() + ", ref: " + op.getSemanticRef());
			}

			//DELETE
			if (s.getObject().toString().equals(ns + "DELETE")){	
				Resource sub = s.getSubject();
				
				Operation op = new Operation();
				String ref = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "semanticRef")).stringValue();
				op.setSemanticRef(ref);
				String out = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "output")).stringValue();
				op.setOutput(out);
				this.functional.put(Operation.DELETE, op);
				
				log.debug("Operation: " + Operation.DELETE + ", out: " + op.getOutput() + ", ref: " + op.getSemanticRef());
			}
			
			//OPTION
			if (s.getObject().toString().equals(ns + "OPTIONS")){	
				Resource sub = s.getSubject();
				
				Operation op = new Operation();
				String ref = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "semanticRef")).stringValue();
				op.setSemanticRef(ref);
				String out = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "output")).stringValue();
				op.setOutput(out);
				this.functional.put(Operation.OPTIONS, op);
				
				log.debug("Operation: " + Operation.OPTIONS + ", out: " + op.getOutput() + ", ref: " + op.getSemanticRef());
			}
			
			//PUT
			if (s.getObject().toString().equals(ns + "PUT")){	
				Resource sub = s.getSubject();
				
				IOOperation op = new IOOperation();
				String ref = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "semanticRef")).stringValue();
				op.setSemanticRef(ref);
				String out = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "output")).stringValue();
				op.setOutput(out);
				String in = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "input")).stringValue();
				op.setInput(in);
				
				this.functional.put(Operation.PUT, op);
				
				log.debug("Operation: " + Operation.PUT + ", in: " + op.getInput() + ", out: " + op.getOutput() + ", ref: " + op.getSemanticRef());
			}
			
			//POST
			if (s.getObject().toString().equals(ns + "POST")){	
				Resource sub = s.getSubject();
				
				IOOperation op = new IOOperation();
				String ref = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "semanticRef")).stringValue();
				op.setSemanticRef(ref);
				String out = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "output")).stringValue();
				op.setOutput(out);
				String in = GraphUtil.getOptionalObject(model, sub, f.createURI(ns + "input")).stringValue();
				op.setInput(in);
				
				this.functional.put(Operation.POST, op);
				
				log.debug("Operation: " + Operation.POST + ", in: " + op.getInput() + ", out: " + op.getOutput() + ", ref: " + op.getSemanticRef());
			}
			
			//log.debug(s.getSubject().toString() + " : " + s.getPredicate().toString() + " : " + s.getObject().toString());
			
		}
		
		
		
	}
	
	
	public Model getRDFModel(){
		return this.model;
	}
	

	@Override
	public String toString() {
		

		return RDFDescriptionParser.modelToString(model);
		
		
		//return model.toString();
	}
	
	@Override
	public void setCURI(CURI curi){
		this.cURI = curi;
		
		//model.filter(arg0, arg1, arg2, arg3)
		
		ValueFactory f = ValueFactoryImpl.getInstance();

		// create some resources and literals to make statements out of
		URI prop = f.createURI(ns + "cURI");
		
        for(Resource r: model.filter(null, prop, null).subjects()){
			Literal c = model.filter(r, prop, null).objectLiteral();
			model.remove(r, prop, c);
			Literal cl = f.createLiteral(curi.toString()); 
			model.add(r, prop, cl);
		}
        
		//con.add(sub, RDF.TYPE, obj);
		
		
	}

	@Override
	public void setAURI(AURI auri){
		this.aURI = auri;
	}

	@Override
	public CURI getCURI() {
		return this.cURI;
	}

	@Override
	public AURI getAURI() {
		return this.aURI;
	}

	@Override
	public QoS getQoS() {
		// TODO Auto-generated method stub
		return this.qos;
	}

	@Override
	public Map<AURI, AssemblyUtilityMonitor> getDependences() {
		if (dependences == null)
			return null;
		
		return dependences.getDependencies();
	}

	@Override
	public Functional getFunctional() {
		// TODO Auto-generated method stub
		return this.functional;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this.context;
	}
	
	

	
	
}
