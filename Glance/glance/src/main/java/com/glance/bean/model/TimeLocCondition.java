package com.glance.bean.model;

public class TimeLocCondition {
	
	
	 private String condition;
	 private boolean conditionVal;
	 //String artifactId;

	 public TimeLocCondition()
	 {
		 
	 }
	 
	 public void setCondition(String condition)
	 {
		 this.condition=condition;
	 }
	 public String getCondition()
	 {
		 return condition;
	 }
	 public void setConditionVal(boolean conditionVal)
	 {
		 this.conditionVal=conditionVal;
	 }
	 public boolean getConditionVal()
	 {
		 return conditionVal;
	 }
}
