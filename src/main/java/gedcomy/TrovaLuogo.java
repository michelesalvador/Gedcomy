package gedcomy;

import org.geonames.FeatureClass;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class TrovaLuogo {
	
	TrovaLuogo() throws Exception {
		
		WebService.setUserName("michelesalvador");
		  
		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		//searchCriteria.setQ("pasian schiavonesco");
		//searchCriteria.setNameStartsWith("lombardy");
		searchCriteria.setName("lombardy");
		//searchCriteria.setLanguage("it");
		searchCriteria.setStyle(Style.FULL);
		//searchCriteria.setFuzzy(0.7);
		searchCriteria.setMaxRows(10);
		searchCriteria.setFeatureClass(FeatureClass.P);
		searchCriteria.setFeatureClass(FeatureClass.A);
		ToponymSearchResult searchResult = WebService.search(searchCriteria);
		
		for (Toponym topo : searchResult.getToponyms()) {
			String str = topo.getName(); // Toponimo
			if(topo.getAdminName4() != null && !topo.getAdminName4().equals(str))
				str += ", " + topo.getAdminName4(); // Paese
			if(topo.getAdminName3() != null && !str.contains(topo.getAdminName3()))
				str += ", " + topo.getAdminName3(); // Comune
			if(topo.getAdminName2() != null && !topo.getAdminName2().isEmpty() && !str.contains(topo.getAdminName2()))
				str += ", " + topo.getAdminName2(); // Provincia
			if(!str.contains(topo.getAdminName1()))
				str += ", " + topo.getAdminName1(); // Regione
			if(!str.contains(topo.getCountryName()))
				str += ", " + topo.getCountryName(); // Nazione
			s.l( str,"\n\t"+ topo.getName(), "5:",topo.getAdminName5(), "4:",topo.getAdminName4(), "3:",topo.getAdminName3(),
					"2:",topo.getAdminName2(), "1:",topo.getAdminName1(),"C:",topo.getCountryName());
		}
	}
}
