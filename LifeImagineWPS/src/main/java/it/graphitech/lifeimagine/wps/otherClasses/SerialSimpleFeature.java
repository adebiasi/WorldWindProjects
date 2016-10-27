package it.graphitech.lifeimagine.wps.otherClasses;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

public class SerialSimpleFeature  implements Serializable {

	private String class1;
	private String class2;
	Object geom;
	
	
	
	public SerialSimpleFeature(String class1, String class2, Object geom) {
		super();
		this.class1 = class1;
		this.class2 = class2;
		this.geom = geom;
	}
	public String getClass1() {
		return class1;
	}
	public void setClass1(String class1) {
		this.class1 = class1;
	}
	public String getClass2() {
		return class2;
	}
	public void setClass2(String class2) {
		this.class2 = class2;
	}
	public Object getGeom() {
		return geom;
	}
	public void setGeom(Object geom) {
		this.geom = geom;
	}
	
	
	
	
	
}
