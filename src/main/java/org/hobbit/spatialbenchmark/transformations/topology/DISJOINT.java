/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.transformations.topology;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.create.CreateDisjointGeometryObject;
import com.vividsolutions.jts.geom.create.GeometryType.GeometryTypes;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.transformations.SpatialTransformation;

/**
 *
 * @author jsaveta
 */
public class DISJOINT implements SpatialTransformation {

    GeometryFactory geometryFactory = new GeometryFactory();
    WKTReader reader = new WKTReader(geometryFactory);

    public Object execute(Object arg, GeometryTypes type) {
        Geometry result = null;
        try {
            Geometry geo = reader.read(arg.toString());
            System.out.println("geo " + geo.getCoordinates().length);
            result = (LineString) geo;
            if (geo instanceof LineString) {
                LineString line = (LineString) geo;
                if (line.getCoordinates().length >= 2) {
                    CreateDisjointGeometryObject instance = new CreateDisjointGeometryObject(line, type);
                    result = instance.generateGeometry();
                }
            }
//            System.out.println("VALID !!!! " + geo.isValid());
        } catch (ParseException ex) {
            Logger.getLogger(DISJOINT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
