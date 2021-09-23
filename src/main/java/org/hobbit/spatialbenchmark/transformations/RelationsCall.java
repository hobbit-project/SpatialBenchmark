/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.transformations;

import com.vividsolutions.jts.geom.create.GeometryType;
import com.vividsolutions.jts.geom.create.GeometryType.GeometryTypes;
import org.hobbit.spatialbenchmark.properties.Definitions;

/**
 *
 * @author jsaveta
 */
public class RelationsCall {

    private spatialRelation spatialRelationsPerc = spatialRelation.CONTAINS;
    private SpatialTransformation spatialRelationTransformation;
    private GeometryTypes geomType;
    private keepPoints keepPointsPerc = keepPoints.KEEP;
    private targetGeometry targetGeometryPerc = targetGeometry.LINESTRING;
    private boolean keepPoint;

    public RelationsCall() {
    }

    public static enum spatialRelation {
        EQUALS, DISJOINT, TOUCHES, CONTAINS, COVERS, INTERSECTS, WITHIN, COVERED_BY, CROSSES, OVERLAPS
    }

    private static enum keepPoints {
        KEEP, REMOVE
    }

    public static enum targetGeometry {
        LINESTRING, POLYGON
    }

    private void initializeSpatialRelationsEntity() {
        try {
            switch (Definitions.spatialRelationsAllocation.getAllocation()) {
                case 0:
                    this.spatialRelationsPerc = spatialRelation.EQUALS;
                    break;
                case 1:
                    this.spatialRelationsPerc = spatialRelation.DISJOINT;
                    break;
                case 2:
                    this.spatialRelationsPerc = spatialRelation.TOUCHES;
                    break;
                case 3:
                    this.spatialRelationsPerc = spatialRelation.CONTAINS;
                    break;
                case 4:
                    this.spatialRelationsPerc = spatialRelation.COVERS;
                    break;
                case 5:
                    this.spatialRelationsPerc = spatialRelation.INTERSECTS;
                    break;
                case 6:
                    this.spatialRelationsPerc = spatialRelation.WITHIN;
                    break;
                case 7:
                    this.spatialRelationsPerc = spatialRelation.COVERED_BY;
                    break;
                case 8:
                    this.spatialRelationsPerc = spatialRelation.CROSSES;
                    break;
                case 9:
                    this.spatialRelationsPerc = spatialRelation.OVERLAPS;
                    break;
            }
        } catch (IllegalArgumentException iae) {
            System.err.println("Check spatial transformation percentages");
        }
    }

    private void initializeKeepPointsEntity() {
        try {
            switch (Definitions.keepPointsAllocation.getAllocation()) {
                case 0:
                    this.keepPointsPerc = keepPoints.KEEP;
                    break;
                case 1:
                    this.keepPointsPerc = keepPoints.REMOVE;
                    break;

            }
        } catch (IllegalArgumentException iae) {
            System.err.println("Check transformation percentages");
        }
    }

    private void initializeTargetGeometryEntity() {
        try {
            switch (Definitions.targetGeometryAllocation.getAllocation()) {
                case 0:
                    this.targetGeometryPerc = targetGeometry.LINESTRING;
                    break;
                case 1:
                    this.targetGeometryPerc = targetGeometry.POLYGON;
                    break;

            }
        } catch (IllegalArgumentException iae) {
            System.err.println("Check transformation percentages");
        }
    }

//EQUALS, DISJOINT, TOUCHES, CONTAINS, COVERS, INTERSECTS, WITHIN, COVERED_BY, CROSSES, OVERLAPS
    public void spatialRelationsCases() {
        initializeSpatialRelationsEntity();
        spatialRelationTransformation = null;
        switch (spatialRelationsPerc) {
            case EQUALS:
                spatialRelationTransformation = RelationsConfiguration.equals();
                break;
            case DISJOINT:
                spatialRelationTransformation = RelationsConfiguration.disjoint();
                break;
            case TOUCHES:
                spatialRelationTransformation = RelationsConfiguration.touches();
                break;
            case CONTAINS:
                spatialRelationTransformation = RelationsConfiguration.contains();
                break;
            case COVERS:
                spatialRelationTransformation = RelationsConfiguration.covers();
                break;
            case INTERSECTS:
                spatialRelationTransformation = RelationsConfiguration.intersects();
                break;
            case WITHIN:
                spatialRelationTransformation = RelationsConfiguration.within();
                break;
            case COVERED_BY:
                spatialRelationTransformation = RelationsConfiguration.coveredBy();
                break;
            case CROSSES:
                spatialRelationTransformation = RelationsConfiguration.crosses();
                break;
            case OVERLAPS:
                spatialRelationTransformation = RelationsConfiguration.overlaps();
                break;

        }
    }

    public void keepPointCases() {
        initializeKeepPointsEntity();
        switch (keepPointsPerc) {
            case KEEP:
                keepPoint = true;
                break;
            case REMOVE:
                keepPoint = false;
                break;
        }
    }

    public void targetGeometryCases() {
        initializeTargetGeometryEntity();
        switch (targetGeometryPerc) {
            case LINESTRING:
                geomType = GeometryType.GeometryTypes.LineString;
                break;
            case POLYGON:
                geomType = GeometryType.GeometryTypes.Polygon;
                break;
        }
    }

    public SpatialTransformation getSpatialRelationsConfiguration() {
        return spatialRelationTransformation;
    }

    public boolean getKeepPoint() {
        return keepPoint;
    }
    
    public GeometryTypes getTargetGeometryType() {
        return geomType;
    }
    
}
