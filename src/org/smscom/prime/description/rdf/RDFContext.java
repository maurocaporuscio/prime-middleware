package org.smscom.prime.description.rdf;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.smscom.prime.description.rdl.Context;

public class RDFContext extends Context {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5501267837201080945L;
	
	
	Model model;
	String ns;
	
	public RDFContext(Model model, String ns){
		super();
		this.model = model;
		this.ns = ns;
	}
	
	
	public void init(String key, String value){
		this.properties.put(key, value);
	}
	
	
	@Override
	public synchronized void add(String key, String value){

		//model.filter(arg0, arg1, arg2, arg3)

		ValueFactory f = ValueFactoryImpl.getInstance();

		// create some resources and literals to make statements out of
		URI contextprop = f.createURI(ns + "hasContext");
		URI keyprop = f.createURI(ns + key);

		for(Value v: model.filter(null, contextprop, null).objects())
			for(Resource r: model.filter(f.createURI(v.stringValue()), null, null).subjects()){
				Literal cl = f.createLiteral(value); 
				model.add(r, keyprop, cl);
			}	

		this.properties.put(key, value);
		
	}
	
	
	@Override
	public synchronized void set(String key, String value){
		
		ValueFactory f = ValueFactoryImpl.getInstance();

		URI prop = f.createURI(ns + key);

		for(Resource r: model.filter(null, prop, null).subjects()){
			Literal c = model.filter(r, prop, null).objectLiteral();
			model.remove(r, prop, c);
			
			Literal cl = f.createLiteral(value); 
			model.add(r, prop, cl);
		}	
		
		this.properties.put(key, value);
	}
	
}
