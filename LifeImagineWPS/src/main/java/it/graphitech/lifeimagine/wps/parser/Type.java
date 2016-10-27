package it.graphitech.lifeimagine.wps.parser;

import java.util.Arrays;
import java.util.List;

public class Type {
	
	private String feature;
	
	private String geometryTag;
	
	private List<String> attributes;
	
	public enum Geom {
		POINT, LINESTRING, POLYGON
	}
	
	//RL: RegioneLiguria, RL1: CinqueTerre, RL2: Tigullio
	//RT: RegioneToscana, RT1: BacinoMagra, RT2: ToscanaNord
	public enum Reg {
		RL, RL_CT, RL_TG,
		RT, RT_BM, RT_TN 
	}
	
	public Type(String featureType, Reg reg) {
		this.feature = featureType;
		
		if(featureType.equals("ge:MappedFeature")) {
			this.geometryTag = "ge:shape";
			this.attributes = Arrays.asList(
					"geometry",
					"mappingFrame",
					"localId",
					"namespace",
					"versionId",
					"naturalGeomorphologicFeatureType",
					"activity"
			);
		}
		else if(featureType.equals("lcv:LandCoverUnit")) {
			this.geometryTag = "lcv:geometry";
			switch (reg) {
				case RL_CT:
					this.attributes = Arrays.asList(
						"localId",
						"namespace",
						"geometry",
						"class1",
						"class2",
						"coveredPercentage",
						"observationDate"
					);
					break;
				case RL_TG:
					this.attributes = Arrays.asList(
						"localId",
						"namespace",
						"geometry",
						"class1",
						"class2",
						"coveredPercentage"	
					);
					break;
				case RT:
					this.attributes = Arrays.asList(
						"localId",
						"namespace",
						"geometry",
						"class1",
						"observationDate1",
						"class2",
						"observationDate2"
					);
					break;
				default: break;
			}
		}
		else if(featureType.equals("nz-core:ExposedElement")) {
			this.geometryTag = "nz-core:geometry";
			this.attributes = Arrays.asList(
				"localId",
				"namespace",
				"versionId",
				"beginLifespanVersion",
				"endLifespanVersion",
				"geometry",
				"hazardCategory",
				"quantitativeValue",
				"exposedElementCategory"
			);
		}
		else if(featureType.equals("nz-core:HazardArea")) {
			this.geometryTag = "nz-core:geometry";
			switch (reg) {
				case RL:
					this.attributes = Arrays.asList(
						"beginLifeSpanVersion",
						"determinationMethod",
						"localId",
						"namespace",
						"versionId",
						"hazardCategory",
						"specificHazardType",
						"source",
						"geometry",
						"qualitativeLikelihood",
						"description"
					);
					break;
				case RT_BM:
					this.attributes = Arrays.asList(
						"determinationMethod",
						"namespace1",
						"versionId1",
						"hazardCategory1",
						"localId",
						"namespace2",
						"versionId2",
						"hazardCategory2",
						"specificHazardType",
						"geometry2",
						"qualitativeValue",
						"assessmentMethod",
						"geometry1",
						"qualitativeLikelihood"
					);
					break;
				case RT_TN:
					this.attributes = Arrays.asList(
						"beginLifeSpanVersion",
						"determinationMethod",
						"localId1",
						"namespace1",
						"versionId1",
						"hazardCategory1",
						"localId2",
						"namespace2",
						"versionId2",
						"hazardCategory2",
						"specificHazardType",
						"geometry2",
						"qualitativeValue",
						"assessmentMethod",
						"geometry1",
						"qualitativeLikelihood",
						"description",
						"name"
					);
					break;
				default: break;
			}
		}
		else if(featureType.equals("nz-core:ObservedEvent")) {
			this.geometryTag = "nz-core:geometry";
			switch (reg) {
				case RL:
					this.attributes = Arrays.asList(
						"beginLifeSpanVersion",
						"localId",
						"namespace",
						"versionId",
						"nameOfEvent",
						"hazardCategory",
						"validFrom",
						"validTo",
						"geometry",
						"quantitativeValue"
					);
					break;
				case RT:
					this.attributes = Arrays.asList( 
						"localId",
						"namespace",
						"versionId",
						"hazardCategory",
						"specificHazardType",
						"geometry",
						"qualitativeValue",
						"assessmentMethod"
					);
					break;
				default: break;
			}
		}
		else if(featureType.equals("pd:StatisticalDistribution")) {
			this.geometryTag = "pd:areaOfDissemination";
			this.attributes = Arrays.asList(
				"value",
				"status",
				"localId",
				"namespace",
				"beginLifespanVersion",
				"geometry",
				"areaValue"
			);
		}
		else if(featureType.equals("su-vector:AreaStatisticalUnit")) {
			this.geometryTag = "su-vector:geometry";
			this.attributes = Arrays.asList(
				"localId",
				"namespace",
				"beginLifespanVersion",
				"geometry",
				"areaValue"
			);
		}
		else {
			this.geometryTag = null;
			this.attributes = null;
		}
	}

	public String getFeature() {
		return feature;
	}
	
	public String getGeometryTag() {
		return geometryTag;
	}
	
	public List<String> getAttributes() {
		return attributes;
	}
	
	public boolean attributesContains(String s, int index) {
		return (this.attributes.get(index).contains(s));
	}
}
