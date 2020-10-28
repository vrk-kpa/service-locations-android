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

package org.osgeo.proj4j.datum;

/**
 * A class representing a geographic reference ellipsoid
 * (or more correctly an oblate spheroid),
 * used to model the shape of the surface of the earth.
 * <p>
 * An oblate spheroid is a geometric surface formed
 * by the rotation of an ellipse about its minor axis.
 * In geodesy this is used as a convenient approximation to the
 * geoid, the true shape of the earth's surface.
 * <p>
 * An ellipsoid is defined by the following parameters:
 * <ul>
 * <li><i>a</i>, the equatorial radius or semi-major axis
 * (see {@link #getA()})
 * </ul>
 * and one of:
 * <ul>
 * <li><i>b</i>, the polar radius or semi-minor axis
 * (see {@link #getB()})
 * <li><i>f</i>, the reciprocal flattening
 * (<i>f = (a - b) / a</i>)
 * </ul>
 * In order to be used as a model of the geoid,
 * the exact positioning of an ellipsoid
 * relative to the geoid surface needs to be specified.
 * This is provided by means of a geodetic {@link Datum}.
 * <p>
 * Notable ellipsoids in common use include
 *
 * @see Datum
 */
public class Ellipsoid implements Cloneable {

    public String name;

    public String shortName;

    public double equatorRadius;

    private double poleRadius = 1.0;

    public double eccentricity = 1.0;

    public double eccentricity2 = 1.0;

    public final static Ellipsoid WGS84 = new Ellipsoid("WGS84", 6378137.0, 0.0,
            298.257223563, "WGS 84");

    public final static Ellipsoid GRS80 = new Ellipsoid("GRS80", 6378137.0, 0.0,
            298.257222101, "GRS 1980 (IUGG, 1980)");

    public final static Ellipsoid SPHERE = new Ellipsoid("sphere", 6371008.7714,
            6371008.7714, 0.0, "Sphere");

    /**
     * Creates a new Ellipsoid.
     * One of of poleRadius or reciprocalFlattening must
     * be specified, the other must be zero
     */
    private Ellipsoid(String shortName, double equatorRadius, double poleRadius,
                      double reciprocalFlattening, String name) {
        this.shortName = shortName;
        this.name = name;
        this.equatorRadius = equatorRadius;
        this.poleRadius = poleRadius;

        if (poleRadius == 0.0 && reciprocalFlattening == 0.0)
            throw new IllegalArgumentException(
                    "One of poleRadius or reciprocalFlattening must be specified");
        // don't check for only one of poleRadius or reciprocalFlattening to be
        // specified,
        // since some defs actually supply two

        // reciprocalFlattening takes precedence over poleRadius
        if (reciprocalFlattening != 0) {
            double f = 1.0 / reciprocalFlattening;
            eccentricity2 = 2 * f - f * f;
            this.poleRadius = equatorRadius * Math.sqrt(1.0 - eccentricity2);
        } else {
            eccentricity2 = 1.0 - (poleRadius * poleRadius)
                    / (equatorRadius * equatorRadius);
        }
        eccentricity = Math.sqrt(eccentricity2);
    }

    public Ellipsoid(String shortName, double equatorRadius,
                     double eccentricity2, String name) {
        this.shortName = shortName;
        this.name = name;
        this.equatorRadius = equatorRadius;
        setEccentricitySquared(eccentricity2);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public double getEquatorRadius() {
        return equatorRadius;
    }

    public double getA() {
        return equatorRadius;
    }

    public double getB() {
        return poleRadius;
    }

    private void setEccentricitySquared(double eccentricity2) {
        this.eccentricity2 = eccentricity2;
        poleRadius = equatorRadius * Math.sqrt(1.0 - eccentricity2);
        eccentricity = Math.sqrt(eccentricity2);
    }

    public double getEccentricitySquared() {
        return eccentricity2;
    }

    public boolean isEqual(Ellipsoid e) {
        return equatorRadius == e.equatorRadius && eccentricity2 == e.eccentricity2;
    }

    @Override
    public String toString() {
        return name;
    }

}
