package org.hobbit.spatialbenchmark.data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.hobbit.spatialbenchmark.Trace;
import org.hobbit.spatialbenchmark.TracePoint;
import org.hobbit.spatialbenchmark.util.RandomUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class CreateInstances extends Generator {
private static final Logger LOGGER = LoggerFactory.getLogger(CreateInstances.class);

    private static final Value Trace = ValueFactoryImpl.getInstance().createURI("http://www.tomtom.com/ontologies/traces#Trace");

    private static Map<Resource, Resource> URIMap = new HashMap<Resource, Resource>(); //sourceURI, targetURI, this contains also th bnodes
    private RandomUtil ru = new RandomUtil();
    private Model sourceTrace;

    public CreateInstances() {
    }

    public void sourceInstance(Model givenModel) {
        Resource id = null;
        Coordinate p = null;
        ArrayList<Coordinate> points = new ArrayList<Coordinate>();

        Iterator<Statement> it = givenModel.iterator();
        while (it.hasNext()) {
            Statement statement = it.next();
            if (statement.getObject().stringValue().endsWith("Trace")) {
                id = statement.getSubject();
//                System.out.println("ID " + id);
            }
            if (statement.getPredicate().getLocalName().equals("lat")) {
                double latitude = Double.parseDouble(statement.getObject().stringValue());
                statement = it.next();
                double longitude = Double.parseDouble(statement.getObject().stringValue());
                p = new TracePoint(longitude, latitude).getTracePoint();
            }

            if (statement.getPredicate().getLocalName().equals("hasPoint") || !it.hasNext()) {
                call.keepPointCases();
//                if (!it.hasNext()) {
//                    point.add(statement);
//                }
                if (call.getKeepPoint() && p != null) { //check alloccation of config file for the percentage of points to keep
//                    System.out.println("p "+p);
                    points.add(p);
                }
            }
        }
//        System.out.println("id " + id);
//        System.out.println("points " + points);
//        System.out.println("points size " + points.size());
        this.sourceTrace = new Trace(id, points).getTraceModel();
//        System.out.println("this.sourceTrace " + this.sourceTrace);
    }

    public Model targetInstance(Model sourceTrace) {
        //egrapse 7 apo ta 9 traces. ta pire lathos apo ton worker logika! tsekare to 
        //oxi eixe na kanei me ton arithmo ton triples
        Resource targetURI = null;
        Geometry targetGeometry = null;
        Model targetModel;
        Iterator<Statement> it = sourceTrace.iterator();
        while (it.hasNext()) {
            Statement statement = it.next();
            if (statement.getObject().stringValue().endsWith("Trace")) {
                if (!URIMap.containsKey(statement.getSubject())) {
                    targetURI = targetSubject(statement);

                } else if (URIMap.containsKey(statement.getSubject())) {
                    targetURI = URIMap.get(statement.getSubject());
                }
//                System.out.println("targetURI " + targetURI);
            } else {

                LOGGER.info("transform " + transform);
                targetGeometry = (Geometry) transform.execute(statement.getObject().stringValue());
//                System.out.println("targetGeometry " + targetGeometry);
            }

        }
        targetModel = new Trace(targetURI, targetGeometry.getCoordinates()).getTraceModel();
//        System.out.println("targetModel " + targetModel);
        return targetModel;

    }

    public Map<Resource, Resource> getURIMap() {
        return URIMap;
    }

    public Resource targetSubject(Statement statement) {
        Resource subject;
        if (!(statement.getSubject() instanceof BNode)) {
            subject = ru.randomUniqueURI();
            URIMap.put(statement.getSubject(), subject);
        } else {
            subject = ru.randomUniqueBNode();
            URIMap.put(statement.getSubject(), subject);
        }
        return subject;
    }

    public Model getSourceTrace() {
        return this.sourceTrace;
    }

}
