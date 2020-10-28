package org.osgeo.proj4j;

public class Main {

    public static void main(String[] args) {
        System.out.println("elo");

        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem _3067 = crsFactory.createFromName("EPSG:3067");
        CoordinateReferenceSystem _4326 = crsFactory.createFromName("EPSG:4326");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(_4326, _3067);

        ProjCoordinate p1 = new ProjCoordinate();
        ProjCoordinate p2 = new ProjCoordinate();
        p1.x = 28.066667;
        p1.y = 69.7;

        transform.transform(p1, p2);

        System.out.println(p2);
    }

}
