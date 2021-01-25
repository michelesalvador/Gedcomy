package gedcomy;

import java.lang.reflect.InvocationTargetException;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.Source;

public class Fonte {
	
	Fonte( Gedcom gc, String id ) {
		Source f = gc.getSource(id);
		s.l( id +"\t"+ U.quanteCitazioni(f,gc) );
		if( f.getAbbreviation() != null ) metti( "Breve", f.getAbbreviation() );
		if( f.getTitle() != null ) metti( "Titolo", f.getTitle() );
		if( f.getAuthor() != null ) metti( "Autore", f.getAuthor() );
		if( f.getPublicationFacts() != null ) metti( "Pubblicazione", f.getPublicationFacts() );
		if( f.getType() != null ) metti( "Tipo", f.getType() );
		if( f.getDate() != null ) metti( "Data", f.getDate() );	// sempre null nel mio Gedcom
		if( f.getText() != null ) {
			metti( "Testo", f.getText().replaceAll("\n", " ") );
		}
		for( Note n : U.trovaNote(f,gc) )
			metti( "Nota", n.getValue() );
		if( f.getRepository(gc) != null ) metti( "Archivio", f.getRepository(gc).getName() );
		for( Media m : f.getAllMedia(gc) )
			media( m );
	
		/* Nel mio Gedcom questi sono sempre tutti null:
		f.getCallNumber() 
		f.getItalic()
		f.getNoteRefs()		restituisce una List<NoteRef>
		f.getMediaType()
		f.getParen()
		f.getReferenceNumber()	REFN  'Custom Id' in Family Historian
		f.getRin()
		f.getUid()
		f.getAllMedia(gc).get(0).getType()	*/
		String[] altriMetodi = { "CallNumber", "Italic", "MediaType", "Paren", "ReferenceNumber", "Rin", "Uid" };
		for( String metodo : altriMetodi ) {
			try { 
				String qualcosa = (String) f.getClass().getMethod( "get" + metodo ).invoke(f);
				if( qualcosa != null )
					metti( metodo, qualcosa ); 
			} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
				e.printStackTrace();
			}
		}
		if( !f.getExtensions().isEmpty() )
			metti( "Altro", U.trovaEstensioni(f.getExtensions()) );
		metti( "Modifica", f.getChange().getDateTime().getValue() + " - " + f.getChange().getDateTime().getTime() );
		s.l("----------------------------------");
	}
	
	void metti( String tit, String cosa ) {
		s.l( "|"+ tit +"|\n"+ cosa );
	}
	
	void media( Media m ) {
		s.l( m.getFile() );
		if( m.getFormat() != null ) s.l( m.getFormat() );
	}
}
