package gedcomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.folg.gedcom.model.Extensions;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.GedcomTag;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.parser.ExtensionsTypeAdapter;
import org.folg.gedcom.parser.GedcomTypeAdapter;
import org.folg.gedcom.parser.TreeParser;
//import org.folg.gedcom.parser.JsonParser;
import org.folg.gedcom.tools.CompareGedcom2Gedcom;
import org.folg.gedcom.tools.Gedcom2Gedcom;
import org.xml.sax.SAXParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

//import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

public class Confronto {

	@SuppressWarnings("deprecation")
	public Confronto(Gedcom gc) {
		
		/*File file = new File("..\\esempi\\linea.ged");
		 Gedcom2Gedcom gc2gc = new Gedcom2Gedcom();
		 try {
			Gedcom2Gedcom.main(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		 gc2gc.convertGedcom(file);
		 CompareGedcom2Gedcom comparaGc2gc = new CompareGedcom2Gedcom();
		 comparaGc2gc.convertAndCompareGedcom(file );
		gc2gc.warning( msg, lineNumber);
		 */
		String str = "ab";
		String str2 = "ab";
		s.l( str.hashCode() +" "+ str2.hashCode() );	// hanno sempre lo stesso hashCode
		s.l( str.equals(str2) );	//true

		Person per = new Person();
		//per.setId("1"); non cambia il risultato
		Person per2 = new Person();
		//per2.setId("1");
		s.l( per.hashCode() +" "+ per2.hashCode() );	// sempre diversi
		s.l( per.equals(per2) );	// false
		
		String percorso2 = "..\\esempi\\famiglia2.ged";
		Gedcom gc2 = null;
		try {
			//Gedcomy.salvaJson( percorso2 );
			gc2 = Gedcomy.openJson( percorso2 );
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		if( !gc.equals(gc2) ) {
			if( !gc.getHeader().equals(gc2.getHeader()) ) {
				mostra( gc2.getHeader() );
			}
			if( !gc.getPeople().equals(gc2.getPeople()) ) {
				for( Person p : gc.getPeople() ) {
					Person p2 = gc2.getPerson(p.getId());
					if( p2 == null )
						s.l( "Eliminato: " + U.essenza(p) );
					else if( p.equals(p2) )
						mostra( U.essenza(p) +"\t"+ U.essenza(p2) );
				}
			}
		}
		
		
		String gcStr = null;
		String gcStr2 = null;
		try {
			gcStr = FileUtils.readFileToString( new File("..\\esempi\\famiglia.ged.json") );
			gcStr2 = FileUtils.readFileToString( new File("..\\esempi\\famiglia2.ged.json") );
		} catch( Exception e) {}
		
		// Con GSON
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		/*JsonElement o1 = parser.parse("{a : {a : 2}, b : 2}");	ok
		JsonElement o2 = parser.parse("{b : 2, a : {a : 2}}");*/
		JsonElement o1 = parser.parse( gcStr );
		JsonElement o2 = parser.parse( gcStr2 );
		s.l( o1.hashCode() +" "+ o2.hashCode() );	// 2 file identici producono hashCode identici
		s.l( o1.equals(o2) );	// true
		
		/* da file GED a tree-based object model attraverso TreeParser, ok
		try {
			File f = new File("..\\esempi\\linea.ged");
			TreeParser tp = new TreeParser();
			List<GedcomTag> gtList = tp.parseGedcom(f);
			for( GedcomTag gctg : gtList )
			  display(gctg);	
		} catch( Exception e ) {}*/
		
		// Problema: JsonParser non include un convertitore da json a tree-model
		org.folg.gedcom.parser.JsonParser fjp = new org.folg.gedcom.parser.JsonParser();
		//fjp.
		
		/*Gson gson = new GsonBuilder()
		                 .registerTypeAdapter(Gedcom.class, new GedcomTypeAdapter())
		                 .create();
		Type tipoLista = new TypeToken<ArrayList<GedcomTag>>(){}.getType();
		List<GedcomTag> gtList = gson.fromJson( gcStr, tipoLista );
		for( GedcomTag gt : gtList )
			display(gt);
		
		JsonElement je = new com.google.gson.JsonParser().parse(gcStr);
		JsonObject root = je.getAsJsonObject();
		JsonElement je2 = root.get("arr");
		Gson gson = new Gson();
		GedcomTag gt = gson.fromJson( gcStr, GedcomTag.class );
		s.l(gt.toString());
		display(gt);
		*/
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GedcomTag.class, new GedcomTagTypeAdapter());
		Gson gson = gsonBuilder.create();
		GedcomTag gt = gson.fromJson( gcStr, GedcomTag.class );
		GedcomTag gt2 = gson.fromJson( gcStr2, GedcomTag.class );
		s.l(gt.equals(gt2));	// true se i due file sono identici
		display(gt);
		display(gt2);
	}
	
	void display( GedcomTag gt ) {
		  System.out.print( gt.getTag() + ": " );
		  if( gt.getValue() != null )
		    System.out.println( gt.getValue() );
		  else if( gt.getId() != null )
		    System.out.println( gt.getId() );
		  else if( gt.getRef() != null )
		    System.out.println( gt.getRef() );
		  List<GedcomTag> gtList2 = gt.getChildren();
		  for( GedcomTag gt2 : gtList2 )
		    display( gt2 );
		}
	
	void mostra( Object cosa ) {
		//s.l( cosa.toString() );
	}

}
