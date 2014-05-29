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
package org.smscom.prime.description.xml;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.description.Description;
import org.smscom.prime.description.rdl.Context;
import org.smscom.prime.description.rdl.Dependences;
import org.smscom.prime.description.rdl.Functional;
import org.smscom.prime.description.rdl.IOOperation;
import org.smscom.prime.description.rdl.Ontology;
import org.smscom.prime.description.rdl.Operation;
import org.smscom.prime.description.rdl.QoS;
import org.smscom.prime.extension.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.smscom.prime.extension.goprime.management.assemblymanagement.Metrics;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDescription extends Description implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2339038549279533207L;
	/**
	 * 
	 */
	
	private Document document;
	
	
	public XMLDescription(String filename){
		super();
		this.document = XMLDescriptionParser.parseFile(filename);
		parseDocument();
	}
	
	
	private void parseDocument(){
		
		
		this.importsOntology = new HashMap<String, Ontology>();
		
		Node node = this.document.getFirstChild();
		this.cURI = new CURI(node.getAttributes().getNamedItem("cURI").getTextContent());
		this.aURI = new AURI(node.getAttributes().getNamedItem("aURI").getTextContent());
		
		
		for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()){
			if (child.getNodeType() == Node.ELEMENT_NODE){
				if (child.getNodeName().equalsIgnoreCase("importsOntology")){
					Ontology ont = new Ontology(child);
					importsOntology.put(ont.getPrefix(), ont);
				}
				
				//INSTANTIATE FUNCTIONAL
				if (child.getNodeName().equalsIgnoreCase("functional")){
					this.setFunctional(child);
					
				}
				
				
				//INSTANTIATE QOS
				if (child.getNodeName().equalsIgnoreCase("qos")){
					this.setQoS(child);
				}
				
				//INSTANTIATE DEPENDENCES
				if (child.getNodeName().equalsIgnoreCase("dependences")){
					this.setDependences(child);
				}
				
			}
		}
	}

	
	public Document getXMLDocument(){
		return this.document;
	}
	
	
	public void setCURI(CURI curi){
		this.cURI = curi;
		document.getFirstChild().getAttributes().getNamedItem("cURI").setTextContent(curi.toString());
		
	}

	public void setAURI(AURI auri){
		this.aURI = auri;
		document.getFirstChild().getAttributes().getNamedItem("aURI").setTextContent(auri.toString());
	}

	public CURI getCURI() {
		return this.cURI;
	}

	public AURI getAURI() {
		return this.aURI;
	}

	public Ontology getImportsOntology(String key) {
		return importsOntology.get(key);
	}
	
	public Collection<Ontology> getImportedOntologies(){
		return this.importsOntology.values();
	}
	

	public Functional getFunctional() {
		return functional;
	}
	
	public QoS getQoS() {
		return qos;
	}
	
	public Map<AURI, AssemblyUtilityMonitor> getDependences() {
		if (dependences == null)
			return null;
		
		return dependences.getDependencies();
	}
	
	
	public void setQoS(QoS qos) {
		this.qos = qos;
	}
	
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return this.context;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return XMLDescriptionParser.serializeDocument(document);
	}
	
	
	
	private void setFunctional(Node node){
		this.functional = new Functional();

		for(Node met = node.getFirstChild(); met != null; met = met.getNextSibling()){
			if (met.getNodeType() == Node.ELEMENT_NODE){
				if (met.getNodeName().equalsIgnoreCase("get"))
					this.functional.put("get", setOperation(met));
				if (met.getNodeName().equalsIgnoreCase("delete"))
					this.functional.put("delete", setOperation(met));
				if (met.getNodeName().equalsIgnoreCase("inspect"))
					this.functional.put("inspect", setOperation(met));
				if (met.getNodeName().equalsIgnoreCase("put"))
					this.functional.put("put", setIOOperation(met));
				if (met.getNodeName().equalsIgnoreCase("post"))
					this.functional.put("post", setIOOperation(met));
			}
		}
	}
	
	private Operation setOperation(Node operation){
		Operation op = new Operation();
		op.setSemanticRef(operation.getAttributes().getNamedItem("semanticRef").getTextContent());
		op.setOutput(operation.getAttributes().getNamedItem("output").getTextContent());
		
		return op;
	}
	
	private IOOperation setIOOperation(Node operation){
		IOOperation op = new IOOperation();
		op.setSemanticRef(operation.getAttributes().getNamedItem("semanticRef").getTextContent());
		op.setOutput(operation.getAttributes().getNamedItem("output").getTextContent());
		op.setInput(operation.getAttributes().getNamedItem("input").getTextContent());
		
		return op;
		
	}
	
	
	
	private void setQoS(Node node){
		this.qos = new QoS();

		NodeList childs = node.getChildNodes();
		for (int i = 0; i<childs.getLength(); i++){
			Node tmp = childs.item(i);
			if (tmp.getNodeType() == Node.ELEMENT_NODE){
				if (tmp.getNodeName().equalsIgnoreCase("Reliability"))
					this.qos.setReliability(Double.parseDouble(tmp.getTextContent()));//    getNodeValue());
				if (tmp.getNodeName().equalsIgnoreCase("Cost")){
					this.qos.setCostRate(tmp.getAttributes().getNamedItem("type").getTextContent());
					this.qos.setCost(- Double.parseDouble(tmp.getTextContent()));
				}
				if (tmp.getNodeName().equalsIgnoreCase("ResponseTime"))
					this.qos.setResponseTime(- Double.parseDouble(tmp.getTextContent()));
				if (tmp.getNodeName().equalsIgnoreCase("Structural"))
					this.qos.setStructural(Double.parseDouble(tmp.getTextContent()));
			}
		}	
	}
	
	private void setDependences(Node node){
		this.dependences = new Dependences();

		NodeList childs = node.getChildNodes();
		for (int i = 0; i<childs.getLength(); i++){
			Node tmp = childs.item(i);
			if (tmp.getNodeType() == Node.ELEMENT_NODE){
				if (tmp.getNodeName().equalsIgnoreCase("requires")){
					AURI type = new AURI(tmp.getAttributes().getNamedItem("type").getTextContent());
					Integer times = Integer.parseInt(tmp.getAttributes().getNamedItem("times").getTextContent());
					Metrics metrics = Metrics.parseString(tmp.getAttributes().getNamedItem("metrics").getTextContent());
					AssemblyUtilityMonitor r = new AssemblyUtilityMonitor(type, null, times, metrics, 0.0);
					dependences.put(type, r);
				}


			}
		}	
	}


	
	
	
}
