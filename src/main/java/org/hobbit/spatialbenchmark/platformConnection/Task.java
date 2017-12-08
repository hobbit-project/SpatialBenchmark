package org.hobbit.spatialbenchmark.platformConnection;

/**
 * 
 */

import java.io.Serializable;


public class Task implements Serializable {
	
	private String taskId;
        private String relation;
        private String targetGeom;
	private byte[] target;
	private byte[] expectedAnswers;
	
	public Task(String id, String relation, String targetGeom, byte[] target, byte[] expectedAnswers) {
		this.taskId = id;
                this.relation = relation;
		this.target = target;
		this.expectedAnswers = expectedAnswers;
	}
	
	public void setId(String id) {
		this.taskId = id;
	}
	
	public String getTaskId() {
		return this.taskId;
	}
        
        public void setRelation(String relation) {
		this.relation = relation;
	}
	
	public String getRelation() {
		return this.relation;
	}
        
        public void setTargetGeom(String targetGeom) {
		this.targetGeom = targetGeom;
	}
	
	public String getTargetGeom() {
		return this.targetGeom;
	}
	
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
