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

package org.prime.description.rdl;

import java.io.Serializable;

import org.w3c.dom.Node;

public class Ontology implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String URI;
	private String ns;
	private String prefix;
	
	public Ontology(Node ont){
		this.URI =    ont.getAttributes().getNamedItem("URI").getTextContent();
		this.ns =  	  ont.getAttributes().getNamedItem("ns").getTextContent();	
		this.prefix = ont.getAttributes().getNamedItem("prefix").getTextContent();
	}

	public String getURI() {
		return URI;
	}

	public String getNs() {
		return ns;
	}

	public String getPrefix() {
		return prefix;
	}
	
	
}
