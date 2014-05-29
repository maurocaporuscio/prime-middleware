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

import java.io.IOException;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLDescriptionParser {
	
	public static Document parseFile(String filename){
		
		Document dom = null;
		try {
			
			//Using factory get an instance of document builder
			DOMParser parser = new DOMParser();
			parser.setFeature("http://xml.org/sax/features/validation",true);
			parser.setFeature("http://apache.org/xml/features/validation/schema",true);
			
			//parse using builder to get DOM representation of the XML file
			parser.parse(filename);
			dom = parser.getDocument();
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return dom;
	}
	
	public static void walkDocument(Node start){
		
		
		if (start.getNodeType() == Node.ELEMENT_NODE)    
        {       
            System.out.print("<"+start.getNodeName());       
            NamedNodeMap startAttr = start.getAttributes();   
            for (int i = 0; i < startAttr.getLength(); i++) {   
                Node attr = startAttr.item(i);   
                System.out.print(" "+attr.getNodeName()+   
                                "=\""+attr.getNodeValue()+"\"");   
            }       
            System.out.print(">");   
         } else if (start.getNodeType() == Node.TEXT_NODE)  {
            System.out.print(start.getNodeValue());       
         }   
            
         for (Node child = start.getFirstChild();    
             child != null;   
             child = child.getNextSibling())   
         {   
             walkDocument(child);   
         }   
            
         if (start.getNodeType() == Node.ELEMENT_NODE)    
         {       
             System.out.print("</"+start.getNodeName()+">");   
         }
		
	}

	public static String serializeDocument(Node start){
		
		String out = "";
	
		if (start.getNodeType() == Node.ELEMENT_NODE){       
			out += "<"+start.getNodeName();       
			NamedNodeMap startAttr = start.getAttributes();   
			for (int i = 0; i < startAttr.getLength(); i++) {   
				Node attr = startAttr.item(i);   
				out+= " "+attr.getNodeName()+   
                      "=\""+attr.getNodeValue()+"\"";   
			}       
			out += ">";   
		} else if (start.getNodeType() == Node.TEXT_NODE)  {
			out += start.getNodeValue();       
		}   
        
		for (Node child = start.getFirstChild();    
         	child != null;   
         	child = child.getNextSibling()){   
				out += serializeDocument(child);   
		}   
        
		if (start.getNodeType() == Node.ELEMENT_NODE)
			out += "</"+start.getNodeName()+">";   
			
		return out;
	}

	public static String serializeRDL(XMLDescription d) {
		// TODO Auto-generated method stub
		return serializeDocument(d.getXMLDocument().getFirstChild());
	}
	
}
