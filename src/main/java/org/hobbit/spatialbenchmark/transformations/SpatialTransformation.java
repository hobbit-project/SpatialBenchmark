/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.transformations;

import com.vividsolutions.jts.geom.create.GeometryType.GeometryTypes;


/**
 *
 * @author jsaveta
 */
public interface SpatialTransformation {

    public abstract Object execute(Object arg, GeometryTypes type);

}
