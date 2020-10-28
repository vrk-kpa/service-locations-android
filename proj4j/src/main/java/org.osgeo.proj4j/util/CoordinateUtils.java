package org.osgeo.proj4j.util;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

public class CoordinateUtils {

    private static final CRSFactory CRS_FACTORY = new CRSFactory();
    private static final CoordinateReferenceSystem EPSG_3067 = CRS_FACTORY.createFromName("EPSG:3067");
    private static final CoordinateReferenceSystem EPSG_4326 = CRS_FACTORY.createFromName("EPSG:4326");
    /**
     * Transformation used to transform geo coordinates from EPSG:3067 to EPSG:4326 system.
     */
    private static final CoordinateTransform TRANSFORM_FROM_4326_TO_3067 = new CoordinateTransformFactory().createTransform(EPSG_4326, EPSG_3067);
    /**
     * Transformation used to transform geo coordinates from EPSG:4326 to EPSG:3067 system.
     */
    private static final CoordinateTransform TRANSFORM_FROM_3067_TO_4326 = new CoordinateTransformFactory().createTransform(EPSG_3067, EPSG_4326);

    /**
     * Transforms provided latitude and longitude represented in WGS 84 (EPSG 4326) system
     * into EPSG 3067 system.
     */
    public static double[] transformToEpsg3067(Double latitude, Double longitude) {
        return transform(TRANSFORM_FROM_4326_TO_3067, longitude, latitude);
    }

    /**
     * Transforms provided latitude and longitude represented in EPSG 3067 system into
     * WGS 84 (EPSG 4326) system.
     */
    public static double[] transformToWgs84(Double latitude, Double longitude) {
        return transform(TRANSFORM_FROM_3067_TO_4326, longitude, latitude);
    }

    private static double[] transform(CoordinateTransform coordinateTransform, Double longitude, Double latitude) {
        final ProjCoordinate output = new ProjCoordinate();
        coordinateTransform.transform(new ProjCoordinate(longitude, latitude), output);
        return new double[]{output.y, output.x};
    }

}
