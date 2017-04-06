package org.hobbit.spatialbenchmark.platformConnection;

/**
 * 
 */

import java.io.Serializable;


public class Task implements Serializable {
	
	private String taskId;
	private byte[] target;
	private byte[] expectedAnswers;
	
	public Task(String id, byte[] target, byte[] expectedAnswers) {
		this.taskId = id;
		this.target = target;
		this.expectedAnswers = expectedAnswers;
	}
	
	public void setId(String id) {
		this.taskId = id;
	}
	
	public String getTaskId() {
		return this.taskId;
	}
	
//	public void setQuery(String query) {
//		this.target = query;
//	}
//	
//	public String getQuery() {
//		return this.target;
//	}
	
        public void setTarget(byte[] res) {
		this.target =  res;
	}
	
	public byte[] getTarget() {
		return this.target;
	}
        
	public void setExpectedAnswers(byte[] res) {
		this.expectedAnswers =  res;
	}
	
	public byte[] getExpectedAnswers() {
		return this.expectedAnswers;
	}
}
