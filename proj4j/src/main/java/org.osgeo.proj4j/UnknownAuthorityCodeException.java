package org.osgeo.proj4j;

/**
 * Signals that an authority code is unknown
 * and cannot be mapped to a CRS definition.
 *
 * @author mbdavis
 */
class UnknownAuthorityCodeException extends Proj4jException {
    UnknownAuthorityCodeException(String message) {
        super(message);
    }
}
