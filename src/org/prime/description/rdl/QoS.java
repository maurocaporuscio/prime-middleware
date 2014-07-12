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


public class QoS implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8541747456617999615L;
	
	
	public static final int FLAT_RATE_COST = 0;
	public static final int INVOCATION_RATE_COST = 1;

	private double reliability;
	private double cost;
	private int cost_rate;
	private double responseTime;
	private double structural;
	private double availability;
	

	public double getReliability() {
		return reliability;
	}

	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

	public double getCost() {
		return cost;
	}

	public int getCostRate() {
		return this.cost_rate;
	}
	
	public void setCostRate(String rate){
		if (rate.equalsIgnoreCase("FlatCost"))
			this.cost_rate = FLAT_RATE_COST;
		this.cost_rate = INVOCATION_RATE_COST;
	}
	
	public void setCostRate(Double rate){
		if (rate == 0.0)
			this.cost_rate = FLAT_RATE_COST;
		this.cost_rate = INVOCATION_RATE_COST;
	}
	
	
	
	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(double responseTime) {
		this.responseTime = responseTime;
	}

	public double getStructural() {
		return structural;
	}

	public void setStructural(double structural) {
		this.structural = structural;
	}

	public void setAvailability(double availability) {
		// TODO Auto-generated method stub
		this.availability = availability;
	}
	
	public double getAvailability() {
		// TODO Auto-generated method stub
		return this.availability;
	}
	

}
