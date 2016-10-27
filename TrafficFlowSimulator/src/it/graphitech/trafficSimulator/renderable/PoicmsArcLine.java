package it.graphitech.trafficSimulator.renderable;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;

import gov.nasa.worldwind.render.Polyline;

/**
 * This class extends the class gov.nasa.worldwind.render.Polyline.
 * 
 * It generate arcs from a point to another.
 * 
 * @author a.debiasi
 * 
 */
public class PoicmsArcLine extends Polyline {
	private int SEGMENTS = 20;
	private int WIDTH = 2;

	public PoicmsArcLine() {

	}

	/**
	 * It creates the arc composed by a list of intermediate points
	 * 
	 * @param source
	 * @param destination
	 * @param color
	 */
	public void init(Position source, Position destination, Color color) {
		try {
			if (color == null) {
				Random random = new Random();
				setColor(new Color(random.nextFloat(), random.nextFloat(),
						random.nextFloat()));
			} else {
				setColor(color);
			}

			double distance = Position.ellipsoidalDistance(source, destination,
					Earth.WGS84_EQUATORIAL_RADIUS, Earth.WGS84_POLAR_RADIUS);

			LinkedList<Position> points = new LinkedList<Position>();

			for (int i = 0; i < SEGMENTS; i++) {
				double interpolation = (double) i / (SEGMENTS - 1);
				double arcHeight = (0.5 - interpolation) * 2.0;
				arcHeight = 1.0 - (arcHeight * arcHeight);
				Position p = Position.interpolate(interpolation, source,
						destination);

				p = new Position(p.latitude, p.longitude,
						(arcHeight * distance * (0.1)) + p.elevation);
				points.add(p);

			}
			this.setLineWidth(WIDTH);
			this.setAntiAliasHint(ANTIALIAS_NICEST);
			this.setPositions(points);
		} catch (Exception e) {
		}
		;

	}

	public PoicmsArcLine(Position source, Position destination, Color color,
			int width) {

		this.WIDTH = width;
		init(source, destination, color);
	}

}
