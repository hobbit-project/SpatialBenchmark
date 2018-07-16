/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.create.GeometryType.GeometryTypes;
import java.util.ArrayList;
import java.util.Arrays;
import static org.hobbit.spatialbenchmark.data.Generator.getRelationsCall;
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
public class Area {

    private Resource id;
    private ArrayList<Coordinate> pointsOfArea;
    private Geometry geometry;
    private String generator;

    private static final URI rdfType = ValueFactoryImpl.getInstance().createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static final Value tomtomTrace = ValueFactoryImpl.getInstance().createURI("http://www.tomtom.com/ontologies/traces#Trace");
    private static final Value tomtomRegion = ValueFactoryImpl.getInstance().createURI("http://www.tomtom.com/ontologies/regions#Region");
    private static final Value spatenTrace = ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/traces#Trace");
    private static final Value spatenRegion = ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/regions#Region");
    private static final String stRDF = "http://strdf.di.uoa.gr/ontology#";
    private static final URI WKT = ValueFactoryImpl.getInstance().createURI(stRDF + "WKT");

    public Area(Resource id, ArrayList<Coordinate> pointsOfArea, String generator) {
        this.id = id; //this is commented out ONLY to let OntoIdea run experiments with different URI from TomTom
        
//        this.id = ValueFactoryImpl.getInstance().createURI(id.toString().replace("spaten", "tomtom"));
        
        this.pointsOfArea = pointsOfArea;
        this.generator = generator;
    }

    public Area(Resource id, Coordinate[] pointsOfArea, String generator) {
        this.id = id; //this is commented out ONLY to let OntoIdea run experiments with different URI from TomTom
        
//        this.id = ValueFactoryImpl.getInstance().createURI(id.toString().replace("spaten", "tomtom"));
        
        this.pointsOfArea = new ArrayList<Coordinate>(Arrays.asList(pointsOfArea));
        this.generator = generator;
    }

    public Area(Resource id, Geometry geometry, String generator) {
        this.id = id; //this is commented out ONLY to let OntoIdea run experiments with different URI from TomTom
        
//        this.id = ValueFactoryImpl.getInstance().createURI(id.toString().replace("spaten", "tomtom"));
        
        this.geometry = geometry;
        this.generator = generator;
    }

    public Model getSourceModel() {

        GeometryFactory geometryFactory = new GeometryFactory();
        Model trace = new LinkedHashModel();
        
        if (generator.equals("tomtom")) {
            trace.add(id, rdfType, tomtomTrace);
        } else {
            trace.add(id, rdfType, spatenTrace);
        }
        LineString line = geometryFactory.createLineString(pointsOfArea.toArray(new Coordinate[]{}));
        Value geometryValue = ValueFactoryImpl.getInstance().createLiteral(line.toText(), WKT);

        trace.add(id, ValueFactoryImpl.getInstance().createURI(stRDF + "hasGeometry"), geometryValue);
       
        return trace;
    }

    public Model getTargetModel() {
        Model trace = new LinkedHashModel();
        if (generator.equals("tomtom")) {
            if (getRelationsCall().getTargetGeometryType().equals(GeometryTypes.LineString)) {
                trace.add(id, rdfType, tomtomTrace);
            } else if (getRelationsCall().getTargetGeometryType().equals(GeometryTypes.Polygon)) {
                trace.add(id, rdfType, tomtomRegion);
            }
        } 
        else{
            if (getRelationsCall().getTargetGeometryType().equals(GeometryTypes.LineString)) {
                trace.add(id, rdfType, spatenTrace);
            } else if (getRelationsCall().getTargetGeometryType().equals(GeometryTypes.Polygon)) {
                trace.add(id, rdfType, spatenRegion);
            }
        } 
        Geometry target = this.geometry;
        Value geometryValue = ValueFactoryImpl.getInstance().createLiteral(target.toText(), WKT);

        trace.add(id, ValueFactoryImpl.getInstance().createURI(stRDF + "hasGeometry"), geometryValue);

        return trace;
    }

}
