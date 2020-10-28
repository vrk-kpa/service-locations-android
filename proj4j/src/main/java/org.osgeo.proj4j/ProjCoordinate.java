package org.osgeo.proj4j;

/**
 * Stores a the coordinates for a position
 * defined relative to some {@link CoordinateReferenceSystem}.
 * The coordinate is defined via X, Y, and optional Z ordinates.
 * Provides utility methods for comparing the ordinates of two positions and
 * for creating positions from Strings/storing positions as strings.
 * <p>
 * The primary use of this class is to represent coordinate
 * values which are to be transformed
 * by a {@link CoordinateTransform}.
 */
public class ProjCoordinate {

    /**
     * The X ordinate for this point.
     * <p>
     * Note: This member variable
     * can be accessed directly. In the future this direct access should
     * be replaced with getter and setter methods. This will require
     * refactoring of the Proj4J code base.
     */
    public double x;

    /**
     * The Y ordinate for this point.
     * <p>
     * Note: This member variable
     * can be accessed directly. In the future this direct access should
     * be replaced with getter and setter methods. This will require
     * refactoring of the Proj4J code base.
     */
    public double y;

    /**
     * The Z ordinate for this point.
     * If this variable has the value <tt>Double.NaN</tt>
     * then this coordinate does not have a Z value.
     * <p>
     * Note: This member variable
     * can be accessed directly. In the future this direct access should
     * be replaced with getter and setter methods. This will require
     * refactoring of the Proj4J code base.
     */
    public double z;

    /**
     * Creates a ProjCoordinate with default ordinate values.
     */
    public ProjCoordinate() {
        this(0.0, 0.0);
    }

    /**
     * Creates a ProjCoordinate using the provided double parameters.
     * The first double parameter is the x ordinate (or easting),
     * the second double parameter is the y ordinate (or northing).
     * This constructor is used to create a "2D" point, so the Z ordinate
     * is automatically set to Double.NaN.
     */
    public ProjCoordinate(double argX, double argY) {
        this.x = argX;
        this.y = argY;
        this.z = Double.NaN;
    }

    /**
     * Sets the value of this coordinate to
     * be equal to the given coordinate's ordinates.
     *
     * @param p the coordinate to copy
     */
    public void setValue(ProjCoordinate p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    public void clearZ() {
        z = Double.NaN;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ProjCoordinate)) {
            return false;
        }
        ProjCoordinate p = (ProjCoordinate) other;
        return !(x != p.x) && !(y != p.y);
    }

    /**
     * Gets a hashcode for this coordinate.
     *
     * @return a hashcode for this coordinate
     */
    @Override
    public int hashCode() {
        //Algorithm from Effective Java by Joshua Bloch [Jon Aquino]
        int result = 17;
        result = 37 * result + hashCode(x);
        result = 37 * result + hashCode(y);
        return result;
    }

    /**
     * Computes a hash code for a double value, using the algorithm from
     * Joshua Bloch's book <i>Effective Java"</i>
     *
     * @return a hashcode for the double value
     */
    private static int hashCode(double x) {
        long f = Double.doubleToLongBits(x);
        return (int) (f ^ (f >>> 32));
    }

    /**
     * Returns a string representing the ProjPoint in the format:
     * <tt>ProjCoordinate[X Y Z]</tt>.
     * <p>
     * Example:
     * <pre>
     *    ProjCoordinate[6241.11 5218.25 12.3]
     * </pre>
     */
    @Override
    public String toString() {
        return "ProjCoordinate[" +
                this.x +
                " " +
                this.y +
                " " +
                this.z +
                "]";
    }

    public boolean hasValidZOrdinate() {
        return !Double.isNaN(this.z);
    }

}
