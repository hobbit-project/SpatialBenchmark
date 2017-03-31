package org.hobbit.spatialbenchmark.platformConnection;

import java.io.Serializable;


public class Task implements Serializable {

    private String taskId;
    private byte[] task;
    private byte[] expectedAnswers;

    public Task(String id, byte[] task, byte[] expectedAnswers) {
        this.taskId = id;
        this.task = task;
        this.expectedAnswers = expectedAnswers;
    }

    public void setId(String id) {
        this.taskId = id;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskData(byte[] query) {
        this.task = task;
    }

    public byte[] getTaskData() {
        return this.task;
    }

    public void setExpectedAnswers(byte[] res) {
        this.expectedAnswers = res;
    }

    public byte[] getExpectedAnswers() {
        return this.expectedAnswers;
    }
}
