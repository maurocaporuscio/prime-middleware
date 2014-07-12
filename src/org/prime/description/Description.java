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

package org.prime.description;

import java.io.Serializable;
import java.util.Map;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.rdf.RDFDescription;
import org.prime.description.rdl.Context;
import org.prime.description.rdl.Dependences;
import org.prime.description.rdl.Functional;
import org.prime.description.rdl.Ontology;
import org.prime.description.rdl.QoS;
import org.prime.description.xml.XMLDescription;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.prime.description.Description;

public abstract class Description implements Serializable{

	
	public static final int XML = 0;
	public static final int RDF = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7250419744973246423L;
	
	protected String filename;

	protected CURI cURI;
	protected AURI aURI;
	
	protected Map<String, Ontology> importsOntology;
	protected Functional functional;
	protected QoS qos;
	protected Dependences dependences;
	protected Context context;
	
	public static Description descriptionFactory(String filename, int descriptiontype) throws Exception{
		
		Description d = null;
		switch (descriptiontype){
			case XML: d = new XMLDescription(filename);
					  break;
			case RDF: d = new RDFDescription(filename);
					  break;
		}
		
		d.setFilename(filename);
		
		return d;
	}
	
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	public String getFilename(){
		return this.filename;
	}
	public abstract void setCURI(CURI curi);

	public abstract void setAURI(AURI auri);

	public abstract CURI getCURI();

	public abstract AURI getAURI();
	
	public abstract String toString();

	public abstract QoS getQoS();

	public abstract Map<AURI, AssemblyUtilityMonitor> getDependences();


	public abstract Functional getFunctional();
	
	public abstract Context getContext();
	
}