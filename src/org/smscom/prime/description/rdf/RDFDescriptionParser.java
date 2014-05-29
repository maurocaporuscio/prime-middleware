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

package org.smscom.prime.description.rdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

public class RDFDescriptionParser {

	

public static Model parseFile(String filename){
		
		Model model = null;
		try {
			
			File file = new File(filename);
			FileInputStream input = new FileInputStream(file);
			
			RDFFormat format = RDFFormat.forFileName(filename);
			
			RDFParser rdfParser = Rio.createParser(format);
			model = new LinkedHashModel();
			StatementCollector collector = new StatementCollector(model);
			rdfParser.setRDFHandler(collector);
			
			
			rdfParser.parse(input, filename);
			
		}catch(Exception se) {
			se.printStackTrace();
		}
		
		return model;
	}
	
public static String modelToString(Model model){
	
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	String s = null;
	
	RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
	try {
		writer.startRDF();
		for (Statement st: model) {
			writer.handleStatement(st);
		}
		writer.endRDF();
		
		s = new String(out.toByteArray(),"UTF-8");
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	
	
	
	return s;
	
}

}
