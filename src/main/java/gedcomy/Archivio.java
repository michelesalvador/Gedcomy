// Dettagli di un archivio

package gedcomy;

import org.folg.gedcom.model.Address;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.Repository;

public class Archivio {
	
	Archivio( Gedcom gc, String id ) {
		Repository a = gc.getRepository(id);
		s.l( id + "\t" + Magazzino.quanteFonti(a,gc) );	//a.getExtension("fonti")
		if( a.getName() != null ) metti( "Nome", a.getName() );
		if( a.getAddress() != null ) metti( "Indirizzo", indirizzo(a.getAddress()) );
		if( a.getWww() != null ) metti( "Sito web", a.getWww() );
		if( a.getEmail() != null ) metti( "Email", a.getEmail() );
		if( a.getPhone() != null ) metti( "Telefono", a.getPhone() );
		if( a.getFax() != null ) metti( "Fax", a.getFax() );
		if( a.getRin() != null ) metti( "Rin", a.getRin() );
		if( a.getValue() != null ) metti( "Value?", a.getValue() );
		for( Note n : U.trovaNote(a,gc) )
			metti( "Nota", n.getValue() );
		if( a.getExtension("folg.more_tags") != null )
			metti( "Altro", U.trovaEstensioni(a.getExtensions()) );
		metti( "Modificato", a.getChange().getDateTime().getValue() + " - " + a.getChange().getDateTime().getTime() );
		s.l("----------------------------------");
	}
	
	void metti( String tit, String cosa ) {
		s.l( "  " + tit + "\n" + cosa );
	}
	
	String indirizzo( Address ind ) {
		String txt = "";
		if( ind.getValue() != null )
			txt = ind.getValue();
		if( ind.getValue()!=null && ind.getAddressLine1()!=null )
			txt += "\n";
		if( ind.getAddressLine1() != null )
			txt += ind.getAddressLine1();
		if( ind.getAddressLine2() != null )
			txt += "\n" + ind.getAddressLine2();
		if( ind.getAddressLine3() != null )
			txt += "\n" + ind.getAddressLine3();
		if( ind.getPostalCode()!=null || ind.getCity()!=null || ind.getState()!=null || ind.getCountry()!=null )
			txt += "\n";
		if( ind.getPostalCode() != null ) txt += ind.getPostalCode() + " ";
		if( ind.getCity() != null ) txt += ind.getCity() + " ";
		if( ind.getState() != null ) txt += ind.getState() + " ";
		if( ind.getCountry() != null ) txt += ind.getCountry();
		return txt;
	}
}
