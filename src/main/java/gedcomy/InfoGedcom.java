package gedcomy;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Header;

// info dall'header del file gedcom
class InfoGedcom {
	
	InfoGedcom( Gedcom gc ) {
		Header h = gc.getHeader();
		s.l( "File: "+ h.getFile() +"\nCodifica: "+ h.getCharacterSet().getValue()
			+"\nProgramma: "+ h.getGenerator().getName()
			+"\nVersione: "+ h.getGenerator().getVersion()
		);
		if( h.getGenerator().getGeneratorCorporation() != null )
			s.l( "Azienda: "+ h.getGenerator().getGeneratorCorporation().getValue() );
		if( h.getGedcomVersion() != null )
			s.l( "Versione Gedcom: "+ h.getGedcomVersion().getVersion() ); 
		s.l( "Destinazione: "+ h.getDestination() 	// ma anche no
			+"\nRadice: "+ U.essenza( gc.getPerson( U.valoreTag(h.getExtensions(),"_ROOT") ) )
		);
		//if( h.getSubmitter(gc) != null )
			//s.l( "Autore: "+ h.getSubmitter(gc).getName() );
		
		U.trovaEstensioni( h.getExtensions() );
		
		s.l("-----------------------------------------------------");
	}
}
