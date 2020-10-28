package org.osgeo.proj4j.parser;

import org.osgeo.proj4j.UnsupportedParameterException;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Proj4Keyword {

    public static final String datum = "datum";
    public static final String ellps = "ellps";
    public static final String proj = "proj";
    public static final String towgs84 = "towgs84";
    public static final String units = "units";
    public static final String zone = "zone";
    private static final String no_defs = "no_defs";
    private static final String wktext = "wktext";

    private static Set<String> supportedParams = null;

    private static synchronized Set supportedParameters() {
        if (supportedParams == null) {
            supportedParams = new TreeSet<>();
            supportedParams.add(datum);
            supportedParams.add(ellps);
            supportedParams.add(proj);
            supportedParams.add(towgs84);
            supportedParams.add(units);
            supportedParams.add(zone);
            supportedParams.add(no_defs);     // no-op
            supportedParams.add(wktext);      // no-op
        }
        return supportedParams;
    }

    private static boolean isSupported(String paramKey) {
        return supportedParameters().contains(paramKey);
    }

    private static void checkUnsupported(String paramKey) {
        if (!isSupported(paramKey)) {
            throw new UnsupportedParameterException(paramKey + " parameter is not supported");
        }
    }

    public static void checkUnsupported(Collection params) {
        for (Object s : params) {
            checkUnsupported((String) s);
        }
    }
}
