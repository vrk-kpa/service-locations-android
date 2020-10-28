package org.osgeo.proj4j;

import org.osgeo.proj4j.datum.Datum;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.proj.LongLatProjection;
import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.proj.TransverseMercatorProjection;

import java.util.HashMap;
import java.util.Map;

/**
 * Supplies predefined values for various library classes
 * such as {@link Ellipsoid}, {@link Datum}, and {@link Projection}.
 *
 * @author Martin Davis
 */
public class Registry {

    Registry() {
        super();
        initialize();
    }

    private final static Datum[] datums = {Datum.WGS84};

    public Datum getDatum(String code) {
        for (Datum datum : datums) {
            if (datum.getCode().equals(code)) {
                return datum;
            }
        }
        return null;
    }

    private final static Ellipsoid[] ellipsoids = {Ellipsoid.GRS80};

    public Ellipsoid getEllipsoid(String name) {
        for (Ellipsoid ellipsoid : ellipsoids) {
            if (ellipsoid.shortName.equals(name)) {
                return ellipsoid;
            }
        }
        return null;
    }

    private Map<String, Class> projRegistry;

    private void register(String name, Class cls) {
        projRegistry.put(name, cls);
    }

    public Projection getProjection(String name) {
        Class cls = projRegistry.get(name);
        if (cls != null) {
            try {
                Projection projection = (Projection) cls.newInstance();
                if (projection != null)
                    projection.setName(name);
                return projection;
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private synchronized void initialize() {
        // guard against race condition
        if (projRegistry != null) {
            return;
        }
        projRegistry = new HashMap<>();
        register("longlat", LongLatProjection.class);
        register("utm", TransverseMercatorProjection.class);
    }

}
