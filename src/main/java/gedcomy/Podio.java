// Elenco degli Autori (Submitter)

package gedcomy;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Submitter;

public class Podio {

	public Podio( Gedcom gc ) {
		for( Submitter s : gc.getSubmitters() ) {
			metti( "Id", s.getId() );
			metti( "Valore", s.getValue() );	// ?
			metti( "Nome", s.getName() );
			metti( "Indirizzo", s.getAddress().getDisplayValue() );
			metti( "Sito web", s.getWww() );
			metti( "Email", s.getEmail() );
			metti( "Telefono", s.getPhone() );
			metti( "Fax", s.getFax() );
			metti( "Fax", s.getRin() );
			metti( "Lingua", s.getLanguage() );
			metti( "Rin", s.getRin() );
			metti( "Altro", s.getExtensions() );
			metti( "Modificato", s.getChange() );
			System.out.println("-------------------");
		}
	}

	void metti( String titolo, Object testo ) {
		if( testo != null )
			s.l( titolo +": "+ testo.toString() );
	}
}
