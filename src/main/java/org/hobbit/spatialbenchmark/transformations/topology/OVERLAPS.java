/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.transformations.topology;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.create.CreateOverlapsGeometryObject;
import com.vividsolutions.jts.geom.create.GeometryType;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.transformations.SpatialTransformation;

/**
 *
 * @author jsaveta
 */
public class OVERLAPS implements SpatialTransformation {

    GeometryFactory geometryFactory = new GeometryFactory();
    WKTReader reader = new WKTReader(geometryFactory);

    @Override
    public Object execute(Object arg) {
        Geometry result = null;
        try {
            Geometry geo = reader.read(arg.toString());
            if (geo instanceof LineString) {
                LineString line = (LineString) geo;
                CreateOverlapsGeometryObject instance = new CreateOverlapsGeometryObject(line, GeometryType.GeometryTypes.LineString);
                result = instance.generateGeometry();
            }

        } catch (ParseException ex) {
            Logger.getLogger(EQUALS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public String print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
