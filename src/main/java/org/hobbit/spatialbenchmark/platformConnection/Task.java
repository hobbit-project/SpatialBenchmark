package org.hobbit.spatialbenchmark.platformConnection;


import java.io.Serializable;


public class Task implements Serializable {
	
	private String taskId;
        private String relation;
        private String targetGeom;
        private String dataGen;
	private byte[] target;
	private byte[] expectedAnswers;
	
	public Task(String id, String relation, String targetGeom, String dataGen ,byte[] target, byte[] expectedAnswers) {
		this.taskId = id;
                this.relation = relation;
                this.dataGen = dataGen;
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
        
        public void setDataGen(String dataGen) {
		this.dataGen = dataGen;
	}
	
	public String getDataGen() {
		return this.dataGen;
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
