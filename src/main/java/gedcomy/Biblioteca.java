package gedcomy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Source;


class Biblioteca {
	
	Gedcom gc;
	int quante;
	
	Biblioteca( Gedcom gc, int ordine ) {
		this.gc = gc;
		List<Source> listaFonti = gc.getSources();
		for( Source fonte : listaFonti ) {
			U.aggiornaTag( fonte, "_CITAZIONI", String.valueOf(U.quanteCitazioni(fonte,gc)) );
			//if( !fonte.getAllMedia(gc).isEmpty() )
			//s.p( fonte.getReferenceNumber() +" " );
		}
		Collections.sort( listaFonti, new Comparator<Source>() {
		    public int compare( Source f1, Source f2 ) {
		    	switch( ordine ) {
			    	case 0:	// Ordina per id
			    		return Integer.parseInt(f1.getId().substring(1)) - Integer.parseInt(f2.getId().substring(1));
			    	case 1:	// Ordine alfabeto
					   	/*if( f1.getNames().size() == 0 )	// i nomi null vanno in fondo
					   		return (f2.getNames().size() == 0) ? 0 : 1;
					   	if( f2.getNames().size() == 0 )
					   		return -1;*/
			    		return U.titoloFonte(f1).toUpperCase().compareTo( U.titoloFonte(f2).toUpperCase() );
			    	case 2:	// Ordina per numero di citazioni
			    		//return quanteCitazioni(f1) - quanteCitazioni(f2);	// questo rallenta parecchio
			    		return Integer.parseInt( U.valoreTag(f2.getExtensions(),"_CITAZIONI") ) - 
			    				Integer.parseInt( U.valoreTag(f1.getExtensions(),"_CITAZIONI") );
			    }
				return 0;
		    }
		});
		for( Source fon : listaFonti )
			System.out.format( "%5s %3s  %s\n", fon.getId(), U.valoreTag(fon.getExtensions(),"_CITAZIONI"), U.titoloFonte(fon) );
	}
}
