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
import com.vividsolutions.jts.geom.create.GeometryType.GeometryTypes;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.platformConnection.DataGenerator;
import org.hobbit.spatialbenchmark.transformations.SpatialTransformation;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class OVERLAPS implements SpatialTransformation {

    GeometryFactory geometryFactory = new GeometryFactory();
    WKTReader reader = new WKTReader(geometryFactory);

    public Object execute(Object arg, GeometryTypes type) {
        Geometry result = null;
        try {
            Geometry geo = reader.read(arg.toString());

            result = (LineString) geo;
            if (geo instanceof LineString) {
                LineString line = (LineString) geo;
                if (line.getCoordinates().length >= 4) {
                    CreateOverlapsGeometryObject instance = new CreateOverlapsGeometryObject(line, type);
                    result = instance.generateGeometry();
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(OVERLAPS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
