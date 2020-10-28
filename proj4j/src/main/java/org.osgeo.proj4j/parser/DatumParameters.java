package org.osgeo.proj4j.parser;

import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.datum.Datum;
import org.osgeo.proj4j.datum.Ellipsoid;

/**
 * Contains the parsed/computed parameter values
 * which are used to create
 * the datum and ellipsoid for a {@link CoordinateReferenceSystem}.
 * This class also implements the policies for
 * which parameters take precedence
 * when multiple inconsistent ones are present.
 *
 * @author Martin Davis
 */
public class DatumParameters {

    private Datum datum = null;
    private double[] datumTransform = null;

    private Ellipsoid ellipsoid;
    private double a = Double.NaN;
    private double es = Double.NaN;

    public Datum getDatum() {
        if (datum != null)
            return datum;
        // if no ellipsoid was specified, return WGS84 as the default
        if (ellipsoid == null && !isDefinedExplicitly()) {
            return Datum.WGS84;
        }
        // if ellipsoid was WGS84, return that datum
        if (ellipsoid == Ellipsoid.WGS84)
            return Datum.WGS84;

        // otherwise, return a custom datum with the specified ellipsoid
        return new Datum("User", datumTransform, getEllipsoid(), "User-defined");
    }

    private boolean isDefinedExplicitly() {
        return !(Double.isNaN(a) || Double.isNaN(es));
    }

    private Ellipsoid getEllipsoid() {
        if (ellipsoid != null)
            return ellipsoid;
        return new Ellipsoid("user", a, es, "User-defined");
    }

    public void setDatumTransform(double[] datumTransform) {
        this.datumTransform = datumTransform;
        // force new Datum to be created
        datum = null;
    }

    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    public void setEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
        es = ellipsoid.eccentricity2;
        a = ellipsoid.equatorRadius;
    }

}
