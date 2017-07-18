/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.transformations;

import org.hobbit.spatialbenchmark.transformations.topology.CONTAINS;
import org.hobbit.spatialbenchmark.transformations.topology.COVERED_BY;
import org.hobbit.spatialbenchmark.transformations.topology.COVERS;
import org.hobbit.spatialbenchmark.transformations.topology.CROSSES;
import org.hobbit.spatialbenchmark.transformations.topology.DISJOINT;
import org.hobbit.spatialbenchmark.transformations.topology.EQUALS;
import org.hobbit.spatialbenchmark.transformations.topology.INTERSECTS;
import org.hobbit.spatialbenchmark.transformations.topology.OVERLAPS;
import org.hobbit.spatialbenchmark.transformations.topology.TOUCHES;
import org.hobbit.spatialbenchmark.transformations.topology.WITHIN;

/**
 *
 * @author jsaveta
 */
public class RelationsConfiguration {
//SPATIAL RELATIONS
//EQUALS, DISJOINT, TOUCHES, CONTAINS, COVERS, INTERSECTS, WITHIN, COVERED_BY, CROSSES, OVERLAPS

    public static EQUALS equals() {
        return new EQUALS();
    }

    public static DISJOINT disjoint() {
        return new DISJOINT();
    }

    public static TOUCHES touches() {
        return new TOUCHES();
    }

    public static CONTAINS contains() {
        return new CONTAINS();
    }

    public static COVERS covers() {
        return new COVERS();
    }

    public static INTERSECTS intersects() {
        return new INTERSECTS();
    }

    public static WITHIN within() {
        return new WITHIN();
    }

    public static COVERED_BY coveredBy() {
        return new COVERED_BY();
    }

    public static CROSSES crosses() {
        return new CROSSES();
    }

    public static OVERLAPS overlaps() {
        return new OVERLAPS();
    }
}
