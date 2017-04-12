/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.util.ArrayList;
import java.util.Arrays;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author jsaveta
 */
public class Trace {

    private Resource id;
    private ArrayList<Coordinate> pointsOfTrace;
    private static final URI rdfType = ValueFactoryImpl.getInstance().createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static final Value Trace = ValueFactoryImpl.getInstance().createURI("http://www.tomtom.com/ontologies/traces#Trace");
    private static final String stRDF = "http://strdf.di.uoa.gr/ontology#";
    private static final URI WKT = ValueFactoryImpl.getInstance().createURI(stRDF + "WKT");

    public Trace(Resource id, ArrayList<Coordinate> pointsOfTrace) {
        this.id = id;
        this.pointsOfTrace = pointsOfTrace;
    }

    public Trace(Resource id, Coordinate[] pointsOfTrace) {
        this.id = id;
        this.pointsOfTrace = new ArrayList<Coordinate>(Arrays.asList(pointsOfTrace));
    }

    public Model getTraceModel() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Model trace = new LinkedHashModel();

        trace.add(id, rdfType, Trace);

        LineString line = geometryFactory.createLineString(pointsOfTrace.toArray(new Coordinate[]{}));
        Value geometryValue = ValueFactoryImpl.getInstance().createLiteral(line.toText(), WKT);

        trace.add(id, ValueFactoryImpl.getInstance().createURI(stRDF + "hasGeometry"), geometryValue);

        return trace;
    }

    //getter
    public ArrayList<Coordinate> getPointsOfTrace() {
        return this.pointsOfTrace;
    }

    public Resource getTraceId() {
        return this.id;
    }

    public void addPointsOfTrace(Coordinate p) {
        this.pointsOfTrace.add(p);
    }

}
