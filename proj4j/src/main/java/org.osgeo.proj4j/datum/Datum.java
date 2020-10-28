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

import org.osgeo.proj4j.ProjCoordinate;


/**
 * A class representing a geodetic datum.
 * <p>
 * A geodetic datum consists of a set of reference points on or in the Earth,
 * and a reference {@link Ellipsoid} giving an approximation
 * to the true shape of the geoid.
 * <p>
 * In order to transform between two geodetic points specified
 * on different datums, it is necessary to transform between the
 * two datums.  There are various ways in which this
 * datum conversion may be specified:
 * <ul>
 * <li>A 3-parameter conversion
 * <li>A 7-parameter conversion
 * <li>A grid-shift conversion
 * </ul>
 * In order to be able to transform between any two datums,
 * the parameter-based transforms are provided as a transform to
 * the common WGS84 datum.  The WGS transforms of two arbitrary datum transforms can
 * be concatenated to provide a transform between the two datums.
 * <p>
 */
public class Datum {

    private static final int TYPE_WGS84 = 1;
    private static final int TYPE_3PARAM = 2;
    private static final int TYPE_7PARAM = 3;

    private static final double[] DEFAULT_TRANSFORM = new double[]{0.0, 0.0, 0.0};

    public static final Datum WGS84 = new Datum("WGS84", 0, 0, 0, Ellipsoid.WGS84, "WGS84");

    private String code;
    private String name;
    private Ellipsoid ellipsoid;
    private double[] transform = DEFAULT_TRANSFORM;

    private Datum(String code,
                  double deltaX, double deltaY, double deltaZ,
                  Ellipsoid ellipsoid,
                  String name) {
        this(code, new double[]{deltaX, deltaY, deltaZ}, ellipsoid, name);
    }

    public Datum(String code,
                 double[] transform,
                 Ellipsoid ellipsoid,
                 String name) {
        this.code = code;
        this.name = name;
        this.ellipsoid = ellipsoid;
        if (transform != null)
            this.transform = transform;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "[Datum-" + name + "]";
    }

    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    private int getTransformType() {
        if (transform == null) return TYPE_WGS84;

        if (isIdentity(transform)) return TYPE_WGS84;

        if (transform.length == 3) return TYPE_3PARAM;
        if (transform.length == 7) return TYPE_7PARAM;

        return TYPE_WGS84;
    }

    private static boolean isIdentity(double[] transform) {
        for (int i = 0; i < transform.length; i++) {
            // scale factor will normally be 1 for an identity transform
            if (i == 6) {
                if (transform[i] != 1.0 && transform[i] != 0.0)
                    return false;
            } else if (transform[i] != 0.0) return false;
        }
        return true;
    }

    public boolean hasTransformToWGS84() {
        int transformType = getTransformType();
        return transformType == TYPE_3PARAM || transformType == TYPE_7PARAM;
    }

    private static final double ELLIPSOID_E2_TOLERANCE = 0.000000000050;

    public boolean isEqual(Datum datum) {
        // false if tranforms are not equal
        if (getTransformType() != datum.getTransformType()) {
            return false;
        }
        // false if ellipsoids are not (approximately) equal
        if (ellipsoid.getEquatorRadius() != ellipsoid.getEquatorRadius()) {
            if (Math.abs(ellipsoid.getEccentricitySquared()
                    - datum.ellipsoid.getEccentricitySquared()) > ELLIPSOID_E2_TOLERANCE)
                return false;
        }

        // false if transform parameters are not identical
        if (getTransformType() == TYPE_3PARAM || getTransformType() == TYPE_7PARAM) {
            for (int i = 0; i < transform.length; i++) {
                if (transform[i] != datum.transform[i])
                    return false;
            }
            return true;
        }
        return true; // datums are equal
    }

    public void transformFromGeocentricToWgs84(ProjCoordinate p) {
        if (transform.length == 3) {
            p.x += transform[0];
            p.y += transform[1];
            p.z += transform[2];

        } else if (transform.length == 7) {
            double Dx_BF = transform[0];
            double Dy_BF = transform[1];
            double Dz_BF = transform[2];
            double Rx_BF = transform[3];
            double Ry_BF = transform[4];
            double Rz_BF = transform[5];
            double M_BF = transform[6];

            double x_out = M_BF * (p.x - Rz_BF * p.y + Ry_BF * p.z) + Dx_BF;
            double y_out = M_BF * (Rz_BF * p.x + p.y - Rx_BF * p.z) + Dy_BF;
            double z_out = M_BF * (-Ry_BF * p.x + Rx_BF * p.y + p.z) + Dz_BF;

            p.x = x_out;
            p.y = y_out;
            p.z = z_out;
        }
    }

    public void transformToGeocentricFromWgs84(ProjCoordinate p) {
        if (transform.length == 3) {
            p.x -= transform[0];
            p.y -= transform[1];
            p.z -= transform[2];

        } else if (transform.length == 7) {
            double Dx_BF = transform[0];
            double Dy_BF = transform[1];
            double Dz_BF = transform[2];
            double Rx_BF = transform[3];
            double Ry_BF = transform[4];
            double Rz_BF = transform[5];
            double M_BF = transform[6];

            double x_tmp = (p.x - Dx_BF) / M_BF;
            double y_tmp = (p.y - Dy_BF) / M_BF;
            double z_tmp = (p.z - Dz_BF) / M_BF;

            p.x = x_tmp + Rz_BF * y_tmp - Ry_BF * z_tmp;
            p.y = -Rz_BF * x_tmp + y_tmp + Rx_BF * z_tmp;
            p.z = Ry_BF * x_tmp - Rx_BF * y_tmp + z_tmp;
        }
    }
}
