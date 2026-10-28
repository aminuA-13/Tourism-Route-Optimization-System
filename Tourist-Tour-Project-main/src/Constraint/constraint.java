package Constraint;

import java.util.Date;

public class constraint {
	
	private double budget;
	private Date from;
	private Date to;
	private String site;
	
	
	
	
	public constraint() {
		this.budget=0.0;
		
	}

	
	
	
	public constraint(double budget, Date from, Date to, String site) {
		this.budget = budget;
		this.from = from;
		this.to = to;
		this.site = site;
	}



	public double getBudget() {
		return budget;
	}


	
	public void setBudget(double budget) {
		this.budget = budget;
	}


	
	public Date getFrom() {
		return from;
	}


	
	public void setFrom(Date from) {
		this.from = from;
	}


	
	public Date getTo() {
		return to;
	}


	
	public void setTo(Date to) {
		this.to = to;
	}


	public String getSite() {
		return site;
	}


	
	public void setSite(String site) {
		this.site = site;
	}
	
	
	
	

}
