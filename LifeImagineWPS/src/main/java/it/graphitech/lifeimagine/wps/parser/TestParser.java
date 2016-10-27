package it.graphitech.lifeimagine.wps.parser;

public class TestParser {

	public static void main(String[] args) throws Exception {
		//SU
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/su?service=WFS&request=GetFeature&version=2.0.0&typeNames=su-vector:AreaStatisticalUnit";
		//WFSParser parser = new WFSParser(new Type("su-vector:AreaStatisticalUnit", null), null);
		
		//GE
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/ge?service=WFS&request=GetFeature&version=2.0.0&typeNames=ge:MappedFeature&count=1000";
		//WFSParser parser = new WFSParser(new Type("ge:MappedFeature", null), new Type("ge:NaturalGeomorphologicFeature", null));
		
		//PD
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/pd?service=WFS&request=GetFeature&version=2.0.0&typeNames=pd:StatisticalDistribution";
		//WFSParser parser = new WFSParser(new Type("pd:StatisticalDistribution", null), new Type("su-vector:AreaStatisticalUnit", null));
		
		//NZ_OE_RL
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:ObservedEvent";
		//WFSParser parser = new WFSParser(new Type("nz-core:ObservedEvent", Type.Reg.RL), null);
		//NZ_HA_RL
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:HazardArea&count=1000";
		//WFSParser parser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RL), null);
		
		//NZ_EE_RL_CH
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl_ch?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:ExposedElement";
		//WFSParser parser = new WFSParser(new Type("nz-core:ExposedElement", null), null, 3044, Type.Geom.POINT);
		//NZ_EE_RL_TR
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl_tr?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:ExposedElement";
		//WFSParser parser = new WFSParser(new Type("nz-core:ExposedElement", null), null, 3044, Type.Geom.POLYGON);
		
		//NZ_OE_RT
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rt?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:ObservedEvent&count=1000";
		//WFSParser parser = new WFSParser(new Type("nz-core:ObservedEvent", Type.Reg.RT), null);
		//NZ_HA_RT1
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rt_bm?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:HazardArea&count=1000";
		//WFSParser parser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RT_BM), new Type("nz-core:ObservedEvent", Type.Reg.RT));
		//NZ_HA_RT2
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/nz_rt_tn?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:HazardArea&count=1000";
		//WFSParser parser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RT_TN), new Type("nz-core:ObservedEvent", Type.Reg.RT));
		
		//LC_RT
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/lc_ls?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit&count=1000";
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/lc_sc?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit";
		//WFSParser parser = new WFSParser(new Type("lcv:LandCoverUnit", Type.Reg.RT), null);
		
		//LC_RL1
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/lc_ct?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit";
		//WFSParser parser = new WFSParser(new Type("lcv:LandCoverUnit", Type.Reg.RL_CT), null);
		//LC_RL2
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/lc_tg_12?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit&count=1000";
		//String s = "http://lifeimagine.graphitech-projects.com/deegree/services/lc_tg_09?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit&count=1000";
		//WFSParser parser = new WFSParser(new Type("lcv:LandCoverUnit", Type.Reg.RL_TG), null);
		
		//SimpleFeatureCollection feature = parser.parseWFS(s);
		System.out.println("TEST OK");
	}
}