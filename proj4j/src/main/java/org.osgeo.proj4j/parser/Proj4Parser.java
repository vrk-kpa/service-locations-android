package org.osgeo.proj4j.parser;

import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.InvalidValueException;
import org.osgeo.proj4j.Registry;
import org.osgeo.proj4j.datum.Datum;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.proj.TransverseMercatorProjection;
import org.osgeo.proj4j.units.Unit;
import org.osgeo.proj4j.units.Units;
import org.osgeo.proj4j.util.ProjectionMath;

import java.util.HashMap;
import java.util.Map;

public class Proj4Parser {
    private Registry registry;

    public Proj4Parser(Registry registry) {
        this.registry = registry;
    }

    public CoordinateReferenceSystem parse(String name, String[] args) {
        if (args == null)
            return null;

        Map params = createParameterMap(args);
        Proj4Keyword.checkUnsupported(params.keySet());
        DatumParameters datumParam = new DatumParameters();
        parseDatum(params, datumParam);
        parseEllipsoid(params, datumParam);
        Datum datum = datumParam.getDatum();
        Ellipsoid ellipsoid = datum.getEllipsoid();
        Projection proj = parseProjection(params, ellipsoid);
        return new CoordinateReferenceSystem(name, datum, proj);
    }

    /**
     * Creates a {@link Projection}
     * initialized from a PROJ.4 argument list.
     */
    private Projection parseProjection(Map params, Ellipsoid ellipsoid) {
        Projection projection = null;

        String s;
        s = (String) params.get(Proj4Keyword.proj);
        if (s != null) {
            projection = registry.getProjection(s);
            if (projection == null)
                throw new InvalidValueException("Unknown projection: " + s);
        }

        projection.setEllipsoid(ellipsoid);

        s = (String) params.get(Proj4Keyword.units);
        if (s != null) {
            Unit unit = Units.findUnits(s);
            if (unit != null) {
                projection.setFromMetres(1.0 / unit.value);
                projection.setUnits(unit);
            }
        }

        // this must be done last, since behaviour depends on other params being set (eg +south)
        if (projection instanceof TransverseMercatorProjection) {
            s = (String) params.get(Proj4Keyword.zone);
            if (s != null)
                ((TransverseMercatorProjection) projection).setUTMZone(Integer
                        .parseInt(s));
        }

        projection.initialize();

        return projection;
    }

    private void parseDatum(Map params, DatumParameters datumParam) {
        String towgs84 = (String) params.get(Proj4Keyword.towgs84);
        if (towgs84 != null) {
            double[] datumConvParams = parseToWGS84(towgs84);
            datumParam.setDatumTransform(datumConvParams);
        }

        String code = (String) params.get(Proj4Keyword.datum);
        if (code != null) {
            Datum datum = registry.getDatum(code);
            if (datum == null)
                throw new InvalidValueException("Unknown datum: " + code);
            datumParam.setDatum(datum);
        }

    }

    private double[] parseToWGS84(String paramList) {
        String[] numStr = paramList.split(",");

        if (!(numStr.length == 3 || numStr.length == 7)) {
            throw new InvalidValueException("Invalid number of values (must be 3 or 7) in +towgs84: " + paramList);
        }
        double[] param = new double[numStr.length];
        for (int i = 0; i < numStr.length; i++) {
            // TODO: better error reporting
            param[i] = Double.parseDouble(numStr[i]);
        }
        if (param.length > 3) {
            // optimization to detect 3-parameter transform
            if (param[3] == 0.0
                    && param[4] == 0.0
                    && param[5] == 0.0
                    && param[6] == 0.0
                    ) {
                param = new double[]{param[0], param[1], param[2]};
            }
        }

        /*
         * PROJ4 towgs84 7-parameter transform uses
         * units of arc-seconds for the rotation factors,
         * and parts-per-million for the scale factor.
         * These need to be converted to radians and a scale factor.
         */
        if (param.length > 3) {
            param[3] *= ProjectionMath.SECONDS_TO_RAD;
            param[4] *= ProjectionMath.SECONDS_TO_RAD;
            param[5] *= ProjectionMath.SECONDS_TO_RAD;
            param[6] = (param[6] / ProjectionMath.MILLION) + 1;
        }

        return param;
    }

    private void parseEllipsoid(Map params, DatumParameters datumParam) {
        String code = (String) params.get(Proj4Keyword.ellps);
        if (code != null) {
            Ellipsoid ellipsoid = registry.getEllipsoid(code);
            if (ellipsoid == null)
                throw new InvalidValueException("Unknown ellipsoid: " + code);
            datumParam.setEllipsoid(ellipsoid);
        }
    }

    private Map createParameterMap(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (String arg1 : args) {
            String arg = arg1;
            // strip leading "+" if any
            if (arg.startsWith("+")) {
                arg = arg.substring(1);
            }
            int index = arg.indexOf('=');
            if (index != -1) {
                String key = arg.substring(0, index);
                String value = arg.substring(index + 1);
                params.put(key, value);
            } else {
                params.put(arg, null);
            }
        }
        return params;
    }

}
