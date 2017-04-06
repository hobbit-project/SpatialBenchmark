/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatiotemporalbenchmark.platformConnection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jsaveta
 */
public class EvaluationModuleTest {
    
    public EvaluationModuleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

//    /**
//     * Test of getEVALUATION_PARAMETER_KEY method, of class EvaluationModule.
//     */
//    @Test
//    public void testGetEVALUATION_PARAMETER_KEY() {
//        System.out.println("getEVALUATION_PARAMETER_KEY");
//        EvaluationModule instance = new EvaluationModule();
//        String expResult = "";
//        String result = instance.getEVALUATION_PARAMETER_KEY();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEVALUATION_PARAMETER_KEY method, of class EvaluationModule.
//     */
//    @Test
//    public void testSetEVALUATION_PARAMETER_KEY() {
//        System.out.println("setEVALUATION_PARAMETER_KEY");
//        String eVALUATION_PARAMETER_KEY = "";
//        EvaluationModule instance = new EvaluationModule();
//        instance.setEVALUATION_PARAMETER_KEY(eVALUATION_PARAMETER_KEY);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEVALUATION_AVERAGE_TASK_DELAY method, of class EvaluationModule.
//     */
//    @Test
//    public void testGetEVALUATION_AVERAGE_TASK_DELAY() {
//        System.out.println("getEVALUATION_AVERAGE_TASK_DELAY");
//        EvaluationModule instance = new EvaluationModule();
//        Property expResult = null;
//        Property result = instance.getEVALUATION_AVERAGE_TASK_DELAY();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEVALUATION_AVERAGE_TASK_DELAY method, of class EvaluationModule.
//     */
//    @Test
//    public void testSetEVALUATION_AVERAGE_TASK_DELAY() {
//        System.out.println("setEVALUATION_AVERAGE_TASK_DELAY");
//        Property eVALUATION_AVERAGE_TASK_DELAY = null;
//        EvaluationModule instance = new EvaluationModule();
//        instance.setEVALUATION_AVERAGE_TASK_DELAY(eVALUATION_AVERAGE_TASK_DELAY);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEVALUATION_RECALL method, of class EvaluationModule.
//     */
//    @Test
//    public void testGetEVALUATION_RECALL() {
//        System.out.println("getEVALUATION_RECALL");
//        EvaluationModule instance = new EvaluationModule();
//        Property expResult = null;
//        Property result = instance.getEVALUATION_RECALL();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEVALUATION_RECALL method, of class EvaluationModule.
//     */
//    @Test
//    public void testSetEVALUATION_RECALL() {
//        System.out.println("setEVALUATION_RECALL");
//        Property eVALUATION_RECALL = null;
//        EvaluationModule instance = new EvaluationModule();
//        instance.setEVALUATION_RECALL(eVALUATION_RECALL);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEVALUATION_PRECISION method, of class EvaluationModule.
//     */
//    @Test
//    public void testGetEVALUATION_PRECISION() {
//        System.out.println("getEVALUATION_PRECISION");
//        EvaluationModule instance = new EvaluationModule();
//        Property expResult = null;
//        Property result = instance.getEVALUATION_PRECISION();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEVALUATION_PRECISION method, of class EvaluationModule.
//     */
//    @Test
//    public void testSetEVALUATION_PRECISION() {
//        System.out.println("setEVALUATION_PRECISION");
//        Property eVALUATION_PRECISION = null;
//        EvaluationModule instance = new EvaluationModule();
//        instance.setEVALUATION_PRECISION(eVALUATION_PRECISION);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEVALUATION_FMEASURE method, of class EvaluationModule.
//     */
//    @Test
//    public void testGetEVALUATION_FMEASURE() {
//        System.out.println("getEVALUATION_FMEASURE");
//        EvaluationModule instance = new EvaluationModule();
//        Property expResult = null;
//        Property result = instance.getEVALUATION_FMEASURE();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEVALUATION_FMEASURE method, of class EvaluationModule.
//     */
//    @Test
//    public void testSetEVALUATION_FMEASURE() {
//        System.out.println("setEVALUATION_FMEASURE");
//        Property eVALUATION_FMEASURE = null;
//        EvaluationModule instance = new EvaluationModule();
//        instance.setEVALUATION_FMEASURE(eVALUATION_FMEASURE);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isFlag method, of class EvaluationModule.
//     */
//    @Test
//    public void testIsFlag() {
//        System.out.println("isFlag");
//        EvaluationModule instance = new EvaluationModule();
//        boolean expResult = false;
//        boolean result = instance.isFlag();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFlag method, of class EvaluationModule.
//     */
//    @Test
//    public void testSetFlag() {
//        System.out.println("setFlag");
//        boolean flag = false;
//        EvaluationModule instance = new EvaluationModule();
//        instance.setFlag(flag);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of init method, of class EvaluationModule.
//     */
//    @Test
//    public void testInit() throws Exception {
//        System.out.println("init");
//        EvaluationModule instance = new EvaluationModule();
//        instance.init();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of receiveCommand method, of class EvaluationModule.
//     */
//    @Test
//    public void testReceiveCommand() {
//        System.out.println("receiveCommand");
//        byte command = 0;
//        byte[] data = null;
//        EvaluationModule instance = new EvaluationModule();
//        instance.receiveCommand(command, data);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of evaluateResponse method, of class EvaluationModule.
//     */
//    @Test
//    public void testEvaluateResponse() throws Exception {
//        System.out.println("evaluateResponse");
//        byte[] expectedData = null;
//        byte[] receivedData = null;
//        long taskSentTimestamp = 0L;
//        long responseReceivedTimestamp = 0L;
//        EvaluationModule instance = new EvaluationModule();
//        instance.evaluateResponse(expectedData, receivedData, taskSentTimestamp, responseReceivedTimestamp);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of summarizeEvaluation method, of class EvaluationModule.
//     */
//    @Test
//    public void testSummarizeEvaluation() throws Exception {
//        System.out.println("summarizeEvaluation");
//        EvaluationModule instance = new EvaluationModule();
//        Model expResult = null;
//        Model result = instance.summarizeEvaluation();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
}
