package gedcomy;

import java.util.List;
import org.folg.gedcom.model.Address;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Person;

public class Individuo {
	
	private Gedcom gc;
	private String id;
	
	Individuo( Gedcom gc, String id ) {
		this.gc = gc;
		this.id = id;
	}

	void media() {
		Person uno = gc.getPerson(id);
		for( Media m : uno.getAllMedia(gc) ) {
			if( m.getTitle() != null )
				s.p( m.getTitle() +"  " );
			s.l( m.getFormat() +"  "+ U.percorsoMedia(m) );
		}
	}

	void eventi() {
		s.l( id );
		Person uno = gc.getPerson(id);
		for( Name nome : uno.getNames() ) {
			s.p( nome.getDisplayValue().replaceAll("/","") );
			if( nome.getNickname() != null )
				s.p( " \""+ nome.getNickname() +"\"" );
			s.l("");
			U.trovaFonti(nome, gc);
		}
		List<EventFact> listaFatti = uno.getEventsFacts();
		for( EventFact fatto : listaFatti ) {
			if( fatto.getType() != null )
				s.p( fatto.getType() +":" );	// Custom event/attribute
			else
				s.p( fatto.getDisplayType() +":" );	// Standard event/attribute
			if( fatto.getValue() != null )
				s.p( " "+ fatto.getValue() );
			if( fatto.getDate() != null )
				s.p( " "+ fatto.getDate() );
			if( fatto.getPlace() != null )
				s.p( " "+ fatto.getPlace() );
			Address indirizzo = fatto.getAddress();
			if( indirizzo != null )
				s.p( ", "+ indirizzo.getValue() );
			if( fatto.getCause() != null )
				s.p( " "+ fatto.getCause() );
			s.l("");
			U.trovaFonti( fatto, gc );
		}
		U.trovaNote(uno,gc);
		List<Media> listaMedia = uno.getMedia();
		for( Media medium : listaMedia ) {
			s.l( medium.getTitle() +" "+ medium.getFileTag() +" "+ medium.getFormat() +" : "+ medium.getFile() +" "+ medium.getType() );
			if( medium.getAllNotes(gc).size() > 0 )
				s.l( medium.getAllNotes(gc).get(0).getValue() );
		}
		U.trovaEstensioni(uno.getExtensions());
		s.l("--------------------------------------------");
	}
	
	void famiglia() {
		Person uno = gc.getPerson(id);
		s.l( U.nome(uno) );	// id +" "+ uno.getNames().get(0).getValue().replaceAll("/","") );
		// Famiglie di origine: genitori e fratelli
 		List<Family> listaFamiglie = uno.getParentFamilies(gc);
		for( Family famiglia : listaFamiglie  ) {
			for( Person padre : famiglia.getHusbands(gc) )
				tessera( padre, "Padre", 0 );
			for( Person madre : famiglia.getWives(gc) )
				tessera( madre, "Madre", 0 );
			for( Person fratello : famiglia.getChildren(gc) )	// solo i figli degli stessi due genitori, non i fratellastri
				if( !fratello.equals(uno) )
					tessera( fratello, null, 1 );
			U.trovaNote(famiglia,gc);
			U.trovaEstensioni(famiglia.getExtensions());
		}
		// Fratellastri e sorellastre
		for( Family famiglia : uno.getParentFamilies(gc) ) {
			for( Person padre : famiglia.getHusbands(gc) ) {
				List<Family> famigliePadre = padre.getSpouseFamilies(gc);
				famigliePadre.removeAll( listaFamiglie );
				for( Family fam : famigliePadre )
					for( Person fratellastro : fam.getChildren(gc) )
						tessera( fratellastro, null, 2 );
			}
			for( Person madre : famiglia.getWives(gc) ) {
				List<Family> famiglieMadre = madre.getSpouseFamilies(gc);
				famiglieMadre.removeAll( listaFamiglie );
				for( Family fam : famiglieMadre )
					for( Person fratellastro : fam.getChildren(gc) )
						tessera( fratellastro, null, 2 );
			}
		}
		// Coniugi e figli
		for( Family famiglia : uno.getSpouseFamilies(gc) ) {
			//if( uno.getEventsFacts().get(0).getValue().equals("F") 
			if( U.sesso(uno) == 1 )
				for( Person moglie : famiglia.getWives(gc) )
					tessera( moglie, "Moglie", 0 );
			else
				for( Person marito : famiglia.getHusbands(gc) )
					tessera( marito, "Marito", 0 );
			for( Person figlio : famiglia.getChildren(gc) ) {
				tessera( figlio, null, 3 );
			}
			U.trovaNote(famiglia,gc);
			U.trovaEstensioni(famiglia.getExtensions());
		}
		U.trovaEstensioni(uno.getExtensions());
		s.l("--------------------------------------------");
	}

	void tessera( Person p, String ruolo, int relazione ) {
		if( U.morto(p) ) s.p( "/ " );
		s.l( U.nome(p) );
		if( !p.getAllMedia(gc).isEmpty() )
			s.l( "  "+ U.percorsoMedia(p.getAllMedia(gc).get(0)) );
		if( ruolo == null ) {
			switch( relazione ) {
				case 1: 
					ruolo = ( U.sesso(p)==2 )? "Sorella" : "Fratello";
					break;
				case 2:
					ruolo = ( U.sesso(p)==2 )? "Sorellastra" : "Fratellastro";
					break;
				case 3:
					ruolo = ( U.sesso(p)==2 )? "Figlia" : "Figlio";
			}
		}
		s.l( "    "+ ruolo +"  "+ U.dueAnni(p, true) );
	}
}
