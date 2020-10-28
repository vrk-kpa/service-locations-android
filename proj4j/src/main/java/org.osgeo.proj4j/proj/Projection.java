/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.osgeo.proj4j.proj;

import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.units.Unit;
import org.osgeo.proj4j.units.Units;
import org.osgeo.proj4j.util.ProjectionMath;

/**
 * A map projection is a mathematical algorithm
 * for representing a spheroidal surface
 * on a plane.
 * A single projection
 * defines a (usually infinite) family of
 * {@link CoordinateReferenceSystem}s,
 * distinguished by different values for the
 * projection parameters.
 */
public abstract class Projection implements Cloneable {

    /**
     * The latitude of the centre of projection
     */
    double projectionLatitude = 0.0;

    /**
     * The longitude of the centre of projection, in radians
     */
    double projectionLongitude = 0.0;

    /**
     * The projection scale factor
     */
    double scaleFactor = 1.0;

    /**
     * The false Easting of this projection
     */
    double falseEasting = 0;

    /**
     * The false Northing of this projection
     */
    double falseNorthing = 0;

    /**
     * Indicates whether a Southern Hemisphere UTM zone
     */
    boolean isSouth = false;

    /**
     * The equator radius
     */
    protected double a = 0;

    /**
     * The eccentricity
     */
    protected double e = 0;

    /**
     * The eccentricity squared
     */
    double es = 0;

    /**
     * True if this projection is using a sphere (es == 0)
     */
    boolean spherical;

    /**
     * The name of this projection
     */
    protected String name = null;

    /**
     * Conversion factor from metres to whatever units the projection uses.
     */
    private double fromMetres = 1;

    /**
     * The total scale factor = Earth radius * units
     */
    double totalScale = 0;

    /**
     * falseEasting, adjusted to the appropriate units using fromMetres
     */
    private double totalFalseEasting = 0;

    /**
     * falseNorthing, adjusted to the appropriate units using fromMetres
     */
    private double totalFalseNorthing = 0;

    /**
     * units of this projection.  Default is metres, but may be degrees
     */
    protected Unit unit = null;

    // Some useful constants
    private final static double RTD = 180.0 / Math.PI;
    private final static double DTR = Math.PI / 180.0;

    Projection() {
        setEllipsoid(Ellipsoid.SPHERE);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Projects a geographic point (in radians), producing a projected result
     * (in the units of the target coordinate system).
     *
     * @param src the input geographic coordinate (in radians)
     * @param dst the projected coordinate (in coordinate system units)
     */
    public void projectRadians(ProjCoordinate src, ProjCoordinate dst) {
        double x = src.x;
        if (projectionLongitude != 0)
            x = ProjectionMath.normalizeLongitude(x - projectionLongitude);
        projectRadians(x, src.y, dst);
    }

    /**
     * Transform a geographic point (in radians),
     * producing a projected result (in the units of the target coordinate system).
     *
     * @param x   the geographic x ordinate (in radians)
     * @param y   the geographic y ordinate (in radians)
     * @param dst the projected coordinate (in coordinate system units)
     */
    private void projectRadians(double x, double y, ProjCoordinate dst) {
        project(x, y, dst);
        if (unit == Units.DEGREES) {
            // convert radians to DD
            dst.x *= RTD;
            dst.y *= RTD;
        } else {
            // assume result is in metres
            dst.x = totalScale * dst.x + totalFalseEasting;
            dst.y = totalScale * dst.y + totalFalseNorthing;
        }
    }

    /**
     * Computes the projection of a given point
     * (i.e. from geographics to projection space).
     * This should be overridden for all projections.
     *
     * @param x   the geographic x ordinate (in radians)
     * @param y   the geographic y ordinatee (in radians)
     * @param dst the projected coordinate (in coordinate system units)
     * @return the target coordinate
     */
    protected ProjCoordinate project(double x, double y, ProjCoordinate dst) {
        dst.x = x;
        dst.y = y;
        return dst;
    }

    /**
     * Inverse-transforms a point (in the units defined by the coordinate system),
     * producing a geographic result (in radians)
     *
     * @param src the input projected coordinate (in coordinate system units)
     * @param dst the inverse-projected geographic coordinate (in radians)
     */
    public void inverseProjectRadians(ProjCoordinate src, ProjCoordinate dst) {
        double x;
        double y;
        if (unit == Units.DEGREES) {
            // convert DD to radians
            x = src.x * DTR;
            y = src.y * DTR;
        } else {
            x = (src.x - totalFalseEasting) / totalScale;
            y = (src.y - totalFalseNorthing) / totalScale;
        }
        projectInverse(x, y, dst);
        if (dst.x < -Math.PI)
            dst.x = -Math.PI;
        else if (dst.x > Math.PI)
            dst.x = Math.PI;
        if (projectionLongitude != 0)
            dst.x = ProjectionMath.normalizeLongitude(dst.x + projectionLongitude);
    }

    /**
     * Computes the inverse projection of a given point
     * (i.e. from projection space to geographics).
     * This should be overridden for all projections.
     *
     * @param x   the projected x ordinate (in coordinate system units)
     * @param y   the projected y ordinate (in coordinate system units)
     * @param dst the inverse-projected geographic coordinate  (in radians)
     */
    protected void projectInverse(double x, double y, ProjCoordinate dst) {
        dst.x = x;
        dst.y = y;
    }

    /**
     * Set the name of this projection.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return toString();
    }

    @Override
    public String toString() {
        return "None";
    }

    /**
     * Set the conversion factor from metres to projected units. This is set to 1 by default.
     */
    public void setFromMetres(double fromMetres) {
        this.fromMetres = fromMetres;
    }

    public void setEllipsoid(Ellipsoid ellipsoid) {
        a = ellipsoid.equatorRadius;
        e = ellipsoid.eccentricity;
        es = ellipsoid.eccentricity2;
    }

    public void setUnits(Unit unit) {
        this.unit = unit;
    }

    /**
     * Initialize the projection. This should be called after setting parameters and before using the projection.
     * This is for performance reasons as initialization may be expensive.
     */
    public void initialize() {
        spherical = (e == 0.0);
        totalScale = a * fromMetres;
        totalFalseEasting = falseEasting * fromMetres;
        totalFalseNorthing = falseNorthing * fromMetres;
    }

}
