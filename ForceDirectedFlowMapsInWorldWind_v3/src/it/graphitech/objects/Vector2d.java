package it.graphitech.objects;

public class Vector2d {

	private double x;

	private double y;

	// -------------------------------------------------------------------------
	// Constructors:
	// -------------------------------------------------------------------------

	public Vector2d() {
		x = 0;
		y = 0;
	}

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
		
		//arrotonda();
	}

	public Vector2d(Position xy) {
		this.x = xy.x;
		this.y = xy.y;
		
		//arrotonda();
	}
	
	public Vector2d(double[] c) {
		this.x = c[0];
		this.y = c[1];
		
		//arrotonda();
	}

	/**
	 * Takes the difference between 2 coordinates to calculate the vector. (Vector = Coordinate2 - Coordinate1)
	 *
	 * @param c1
	 *            First coordinate
	 * @param c2
	 *            Second coordinate
	 */
	public Vector2d(double[] c1, double[] c2) {
		this.x = c1[0] - c2[0];
		this.y = c1[1] - c2[1];
		
	//	arrotonda();
	}

	
	public Vector2d(Position c1, Position c2) {
		this.x = c1.x - c2.x;
		this.y = c1.y - c2.y;
		
		//arrotonda();
	}
	
	/**
	 * Takes the difference between 2 coordinates to calculate the vector. (Vector = Coordinate2 - Coordinate1)
	 *
	 * @param c1
	 *            First coordinate
	 * @param c2
	 *            Second coordinate
	 */
	public Vector2d(int[] c1, int[] c2) {
		this.x = c1[0] - c2[0];
		this.y = c1[1] - c2[1];
		
		//arrotonda();
	}
	// -------------------------------------------------------------------------
	// Class specific functions:
	// -------------------------------------------------------------------------

	/**
	 * Add another vector to this one.
	 *
	 * @param vector2d
	 *            The other vector.
	 */
	public void add(Vector2d vector2d) {
		x += vector2d.x;
		y += vector2d.y;
	}

	/**
	 * Subtract another vector from this one.
	 *
	 * @param vector2d
	 *            The other vector.
	 */
	public void subtract(Vector2d vector2d) {
		x -= vector2d.x;
		y -= vector2d.y;
	}

	/**
	 * Scale this vector.
	 *
	 * @param xFactor
	 *            Scale the X-factor with this value.
	 * @param yFactor
	 *            Scale the Y-factor with this value.
	 */
	public void scale(double xFactor, double yFactor) {
		this.x *= xFactor;
		this.y *= yFactor;
		//arrotonda();
	}

	public void scale(double factor){
		this.x *= factor;
		this.y *= factor;
		//arrotonda();
	}
	
	/**
	 * Translate this vector.
	 *
	 * @param xDist
	 *            Translate the X-factor with x.
	 * @param yDist
	 *            Translate the Y-factor with y.
	 */
	public void translate(double xDist, double yDist) {
		this.x += xDist;
		this.y += yDist;
		//arrotonda();
	}

	/**
	 * Calculate the distance between 2 vector by using Pythagoras' formula.
	 *
	 * @param vector2d
	 *            The other vector.
	 * @returns The distance between these 2 vectors as a Double.
	 */
	public double distance(Vector2d vector2d) {
		double a = vector2d.x - x;
		double b = vector2d.y - y;
		return Math.sqrt(a * a + b * b);
	}

	/**
	 * Normalize this vector.
	 */
	public void normalize() {
		double len = this.length();
		if (len == 0) {
			return;
		}
		x /= len;
		y /= len;
		
		//arrotonda();
	}

	/**
	 * Return the length of this vector. (Euclides)
	 *
	 * @returns The length as a Double.
	 */
	public double length() {
		double len = (x * x) + (y * y);
		return Math.sqrt(len);
	}

	/**
	 * Calculates a vector's cross product.
	 *
	 * @param vector2D
	 *            The second vector.
	 */
	public double cross(Vector2d vector2D) {
		return x * vector2D.y - y * vector2D.x;
		
		
	}

	/**
	 * Return this vector object as a string.
	 */
	public String toString() {
		return "Vector2D(" + x + ", " + y + ")";
	}

	// -------------------------------------------------------------------------
	// Getters and setters.
	// -------------------------------------------------------------------------

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
//	  public  void arrotonda(){
//		  x=arrotondaPerDifetto(x, 5);
//		  y=arrotondaPerDifetto(y, 5);
//	  }
//			  
//	  // ARROTONDAMENTO PER DIFETTO
//	   public static double arrotondaPerDifetto (double value, int numCifreDecimali) {
//	      double temp = Math.pow(10, numCifreDecimali);
//	      return Math.floor(value * temp) / temp;
//	   }
	// ARROTONDAMENTO CLASSICO
//	   public static double arrotonda(double value, int numCifreDecimali) {
//	      double temp = Math.pow(10, numCifreDecimali);
//	      return Math.round(value * temp) / temp;
//	   }
//	
}