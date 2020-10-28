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

package org.osgeo.proj4j.util;

import org.osgeo.proj4j.InvalidValueException;

public class ProjectionMath {

    public final static double PI = Math.PI;
    public final static double HALFPI = Math.PI / 2.0;
    private final static double TWOPI = Math.PI * 2.0;

    public static double asin(double v) {
        if (Math.abs(v) > 1.)
            return v < 0.0 ? -Math.PI / 2 : Math.PI / 2;
        return Math.asin(v);
    }

    public static double acos(double v) {
        if (Math.abs(v) > 1.)
            return v < 0.0 ? Math.PI : 0.0;
        return Math.acos(v);
    }

    public static double normalizeLongitude(double angle) {
        if (Double.isInfinite(angle) || Double.isNaN(angle))
            throw new InvalidValueException("Infinite longitude");
        while (angle > Math.PI)
            angle -= TWOPI;
        while (angle < -Math.PI)
            angle += TWOPI;
        return angle;
    }

    private final static double C00 = 1.0;
    private final static double C02 = .25;
    private final static double C04 = .046875;
    private final static double C06 = .01953125;
    private final static double C08 = .01068115234375;
    private final static double C22 = .75;
    private final static double C44 = .46875;
    private final static double C46 = .01302083333333333333;
    private final static double C48 = .00712076822916666666;
    private final static double C66 = .36458333333333333333;
    private final static double C68 = .00569661458333333333;
    private final static double C88 = .3076171875;
    private final static int MAX_ITER = 10;

    public static double[] enfn(double es) {
        double t;
        double[] en = new double[5];
        en[0] = C00 - es * (C02 + es * (C04 + es * (C06 + es * C08)));
        en[1] = es * (C22 - es * (C04 + es * (C06 + es * C08)));
        en[2] = (t = es * es) * (C44 - es * (C46 + es * C48));
        en[3] = (t *= es) * (C66 - es * C68);
        en[4] = t * es * C88;
        return en;
    }

    public static double mlfn(double phi, double sphi, double cphi, double[] en) {
        cphi *= sphi;
        sphi *= sphi;
        return en[0] * phi - cphi * (en[1] + sphi * (en[2] + sphi * (en[3] + sphi * en[4])));
    }

    public static double inv_mlfn(double arg, double es, double[] en) {
        double s, t, phi, k = 1. / (1. - es);

        phi = arg;
        for (int i = MAX_ITER; i != 0; i--) {
            s = Math.sin(phi);
            t = 1. - es * s * s;
            phi -= t = (mlfn(phi, s, Math.cos(phi), en) - arg) * (t * Math.sqrt(t)) * k;
            if (Math.abs(t) < 1e-11)
                return phi;
        }
        return phi;
    }

    /* SECONDS_TO_RAD = Pi/180/3600 */
    public static final double SECONDS_TO_RAD = 4.84813681109535993589914102357e-6;
    public static final double MILLION = 1000000.0;
}
