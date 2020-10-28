package org.osgeo.proj4j.io;

public class Proj4FileReader {

    private String[] readParametersFromFile(String name) {
        if (name.equalsIgnoreCase("3067")) {
            return new String[]{"+proj=utm", "+zone=35", "+ellps=GRS80", "+towgs84=0,0,0,0,0,0,0", "+units=m", "+no_defs"};
        } else if (name.equalsIgnoreCase("4326")) {
            return new String[]{"+proj=longlat", "+datum=WGS84", "+no_defs"};
        }
        throw new IllegalStateException("can't find parameters for name: " + name);
    }

    /**
     * Gets the list of PROJ.4 parameters which define
     * the coordinate system specified by <tt>name</tt>.
     *
     * @param crsName the name of the coordinate system
     * @return the PROJ.4 projection parameters which define the coordinate system
     */
    public String[] getParameters(String crsName) {
        int p = crsName.indexOf(':');
        if (p >= 0) {
            String id = crsName.substring(p + 1);
            return readParametersFromFile(id);
        }
        return null;
    }

}
