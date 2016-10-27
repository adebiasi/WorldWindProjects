package it.graphitech.render.cubicCurve;


public class NaturalCubicClosed 
{
	
	/*
	 * calculates the closed natural cubic spline that interpolates x[0], x[1],
	 * ... x[n] The first segment is returned as C[0].a + C[0].b*u + C[0].c*u^2 +
	 * C[0].d*u^3 0<=u <1 the other segments are in C[1], C[2], ... C[n]
	 */
	protected Cubic[] calcNaturalCubic( int n, double[] fa )
	{
		double[] w = new double[ n + 1 ];
		double[] v = new double[ n + 1 ];
		double[] y = new double[ n + 1 ];
		double[] D = new double[ n + 1 ];
		/*
		 * Solves the equation [4 1 1] [D[0]] [3(x[1] - x[n]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [1 1 4] [D[n]] [3(x[0] - x[n-1])]
		 * 
		 * by decomposing the matrix into upper triangular and lower matrices
		 * and then back sustitution. See Spath "Spline Algorithms for Curves
		 * and Surfaces" pp. 19-21. The D[i] are the derivatives at the knots.
		 */
		double z = 1.0f / 4.0f;
		w[ 1 ] = v[ 1 ] = z;
		y[ 0 ] = z * 3 * (fa[ 1 ] - fa[ n ]);

		double H = 4;
		double F = 3 * (fa[ 0 ] - fa[ n - 1 ]);
		double G = 1;

		for ( int k = 1; k < n; k++ )
		{
			v[ k + 1 ] = z = 1 / (4 - v[ k ]);
			w[ k + 1 ] = -z * w[ k ];
			y[ k ] = z * (3 * (fa[ k + 1 ] - fa[ k - 1 ]) - y[ k - 1 ]);
			H = H - G * w[ k ];
			F = F - G * y[ k - 1 ];
			G = -v[ k ] * G;
		}
		H = H - (G + 1) * (v[ n ] + w[ n ]);
		y[ n ] = F - (G + 1) * y[ n - 1 ];

		D[ n ] = y[ n ] / H;
		D[ n - 1 ] = y[ n - 1 ] - (v[ n ] + w[ n ]) * D[ n ];

		for ( int k = n - 2; k >= 0; k-- )
		{
			D[ k ] = y[ k ] - v[ k + 1 ] * D[ k + 1 ] - w[ k + 1 ] * D[ n ];
		}

		// compute the coefficients of the cubics
		Cubic[] aPoly = new Cubic[ n + 1 ];
		for ( int k = 0; k < n; k++ )
		{
			aPoly[ k ] = new Cubic( fa[ k ], D[ k ], 3 * (fa[ k + 1 ] - fa[ k ]) - 2 * D[ k ] - D[ k + 1 ], 2
				* (fa[ k ] - fa[ k + 1 ]) + D[ k ] + D[ k + 1 ] );
		}
		aPoly[ n ] = new Cubic( fa[ n ], D[ n ], 3 * (fa[ 0 ] - fa[ n ]) - 2 * D[ n ] - D[ 0 ], 2 * (fa[ n ] - fa[ 0 ]) + D[ n ]
			+ D[ 0 ] );

		return aPoly;
	}

}