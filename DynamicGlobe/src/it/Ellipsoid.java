/* Geodesy by Mike Gavaghan
 * 
 * http://www.gavaghan.org/blog/free-source-code/geodesy-library-vincentys-formula/
 * 
 * This code may be freely used and modified on any personal or professional
 * project.  It comes with no warranty.
 *
 * BitCoin tips graciously accepted at 1FB63FYQMy7hpC2ANVhZ5mSgAZEtY1aVLf
 */
package it;

import java.io.Serializable;

/**
 * Encapsulation of an ellipsoid, and declaration of common reference ellipsoids.
 * @author Mike Gavaghan
 */
public class Ellipsoid implements Serializable
{
   /** Semi major axis (meters). */
   private final double mSemiMajorAxis;

   /** Semi minor axis (meters). */
   private final double mSemiMinorAxis;

   /** Flattening. */
   private final double mFlattening;

   /** Inverse flattening. */
   private final double mInverseFlattening;

   /**
    * Construct a new Ellipsoid.  This is private to ensure the values are
    * consistent (flattening = 1.0 / inverseFlattening).  Use the methods 
    * fromAAndInverseF() and fromAAndF() to create new instances.
    * @param semiMajor
    * @param semiMinor
    * @param flattening
    * @param inverseFlattening
    */
   private Ellipsoid(double semiMajor, double semiMinor, double flattening, double inverseFlattening)
   {
     mSemiMajorAxis = semiMajor;
     mSemiMinorAxis = semiMinor;
     mFlattening = flattening;
     mInverseFlattening = inverseFlattening;
   }

   /** The WGS84 ellipsoid. */
   static public final Ellipsoid WGS84 = fromAAndInverseF(6378137.0, 298.257223563);

  

   /**
    * Build an Ellipsoid from the semi major axis measurement and the inverse flattening.
    * @param semiMajor semi major axis (meters)
    * @param inverseFlattening
    * @return
    */
   static public Ellipsoid fromAAndInverseF(double semiMajor, double inverseFlattening)
   {
     double f = 1.0 / inverseFlattening;
     double b = (1.0 - f) * semiMajor;

     return new Ellipsoid(semiMajor, b, f, inverseFlattening);
   }

   /**
    * Get semi-major axis.
    * @return semi-major axis (in meters).
    */
   public double getSemiMajorAxis()
   {
     return mSemiMajorAxis;
   }

   /**
    * Get semi-minor axis.
    * @return semi-minor axis (in meters).
    */
   public double getSemiMinorAxis()
   {
     return mSemiMinorAxis;
   }

   /**
    * Get flattening
    * @return
    */
   public double getFlattening()
   {
     return mFlattening;
   }

   /**
    * Get inverse flattening.
    * @return
    */
   public double getInverseFlattening()
   {
     return mInverseFlattening;
   }
}
