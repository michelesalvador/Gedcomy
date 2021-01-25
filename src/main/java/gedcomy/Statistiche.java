// Dati sull'albero famigliare

package gedcomy;

import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;

public class Statistiche {

	Statistiche( Gedcom gc ) {
		String stat = "Individui: "+ gc.getPeople().size()
			+"\nFamiglie: "+ gc.getFamilies().size()
			+"\nGenerazioni: "+ quanteGenerazioni(gc)
			+"\nFonti: "+ gc.getSources().size()
			+"\nMedia: "+ gc.getMedia().size();
		s.l(stat);
	}
	
	public static int quanteGenerazioni( Gedcom gc ) {
		if( gc.getPeople().isEmpty() )
			return 0;
		//gc.createIndexes();
		risaliGenerazioni( U.trovaRadice(gc), gc, 0 );
		return 1 - genMin + genMax;
	}
	
	static int genMin = 0;
	static int genMax = 0;
	
	// riceve una Person e trova il numero della generazione di antenati più remota
	static void risaliGenerazioni( Person p, Gedcom gc, int gen  ) {
		if( gen < genMin )
			genMin = gen;
		// aggiunge l'estensione per indicare che è passato da questa Persona
		/*Map<String,Object> estensioni = new HashMap<String,Object>();
		estensioni.put( "GENERAZ", gen );
		p.setExtensions(estensioni);*/
		p.putExtension( "gen", gen );
		//s.l( "["+ p.getExtension("gen") +"] "+ gen +" "+ U.essenza(p) );
		
		// se è un capostipite va a contare le generazioni di discendenti
		if( p.getParentFamilies(gc).isEmpty() )
			discendiGenerazioni( p, gc, gen );
		
		for( Family f : p.getParentFamilies(gc) ) {
			/*if( !f.getHusbands(gc).isEmpty() || !f.getWives(gc).isEmpty() )	// se c'è almeno un genitore
				gen--;*/
			// intercetta eventuali fratelli del capostipite
			if( f.getHusbands(gc).isEmpty() && f.getWives(gc).isEmpty() ) {
				for( Person frate : f.getChildren(gc) )
					if( frate.getExtension("gen") == null )
						discendiGenerazioni( frate, gc, gen );
			}
			for( Person padre : f.getHusbands(gc) )
				if( padre.getExtension("gen") == null )
					risaliGenerazioni( padre, gc, gen-1 );
			for( Person madre : f.getWives(gc) )
				if( madre.getExtension("gen") == null )
					risaliGenerazioni( madre, gc, gen-1 );
		}
	}
	
	// riceve una Person e trova il numero della generazione più remota di discendenti
	static void discendiGenerazioni( Person p, Gedcom gc, int gen ) {
		if( gen > genMax )
			genMax = gen;
		/*Map<String,Object> estensioni = new HashMap<String,Object>();
		estensioni.put( "GENERAZ", gen );
		p.setExtensions(estensioni);*/
		p.putExtension( "gen", gen );
		//s.l( "\t{"+ p.getExtension("gen") +"} "+ gen +" "+ U.essenza(p) );
		for( Family fam : p.getSpouseFamilies(gc) ) {
			// individua anche la famiglia dei coniugi
			for( Person moglie : fam.getWives(gc) )
				if( moglie.getExtension("gen") == null )
					risaliGenerazioni( moglie, gc, gen );
			for( Person marito : fam.getHusbands(gc) )
				if( marito.getExtension("gen") == null )
					risaliGenerazioni( marito, gc, gen );

			for( Person figlio : fam.getChildren(gc) )
				discendiGenerazioni( figlio, gc, gen+1 );
		}
	}
}
