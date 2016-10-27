package it.graphitech.trafficSimulator.renderableManager;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.WWMath;
import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.entities.SegmentInfo;
import it.graphitech.trafficSimulator.renderable.PathExtArea;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreetsAnalysisManager {

	static final RenderableLayer streetsAnalysisLayer = new RenderableLayer();

	public static void updateStreetAnalysisLayer(
			ConcurrentHashMap<String, SegmentInfo> segmentsInfo) {

		Enumeration<SegmentInfo> segments = segmentsInfo.elements();

		while (segments.hasMoreElements()) {

			SegmentInfo segm = segments.nextElement();
			PathExtArea path = segm.analysisPath;
			double value = segm.total_counter;

			path.setIncrementElevation(value / 10);
			path.getAttributes().setOutlineMaterial(Material.YELLOW);
			path.getAttributes().setOutlineWidth(0.1);
			path.setExtrude(true);
		}

	}

	public static void updateStreetAnalysisLayer(SegmentInfo segmentsInfo,
			double value, double value2) {

		PathExtArea path = segmentsInfo.analysisPath;

		path.setIncrementElevation(value / 10);
		path.getAttributes().setOutlineWidth(0.1);
		path.setExtrude(true);
		Color c = getColor((int) value2);
		path.getAttributes().setOutlineMaterial(new Material(c));
		path.getAttributes().setInteriorMaterial(new Material(c));

	}

	private static Color getColor(int value) {

		if (value > CustomizableVariables.max_cars_per_path) {
			value = CustomizableVariables.max_cars_per_path;
		}
		return createColorGradientAttributes(value, 0,
				CustomizableVariables.max_cars_per_path,
				CustomizableVariables.minHue, CustomizableVariables.maxHue);

	}

	private static Color createColorGradientAttributes(final double value,
			double minValue, double maxValue, double minHue, double maxHue) {
		double hueFactor = WWMath.computeInterpolationFactor(value, minValue,
				maxValue);
		Color color = Color.getHSBColor(
				(float) WWMath.mixSmooth(hueFactor, minHue, maxHue), 1f, 1f);
		double opacity = WWMath.computeInterpolationFactor(value, minValue,
				minValue + (maxValue - minValue) * 0.1);
		Color rgbaColor = new Color(color.getRed(), color.getGreen(),
				color.getBlue(), (int) (255 * opacity));
		return rgbaColor;
	}

	public static void initStreetsAnalysisLayer(
			Map<String, PathExtArea> analysisPaths) {

		streetsAnalysisLayer.setName("StreetsAnalysisLayer");
		GlobalInstances.getWwd().getModel().getLayers()
				.add(streetsAnalysisLayer);

		streetsAnalysisLayer.setPickEnabled(false);

		for (PathExtArea path : analysisPaths.values()) {

			ShapeAttributes attrs = new BasicShapeAttributes();

			Color col = Color.RED;

			attrs.setOutlineMaterial(new Material(col));
			attrs.setOutlineWidth(2d);
			path.setAttributes(attrs);
			path.setFollowTerrain(true);
			attrs.setInteriorOpacity(0.5);
			streetsAnalysisLayer.addRenderable(path);
		}
	}

}
