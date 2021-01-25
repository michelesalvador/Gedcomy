package gedcomy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Source;

class Galleria {
	
	Gedcom gc;
	List<Media> listaMedia = new ArrayList<>();
	
	Galleria( Gedcom gc ) {
		this.gc = gc;
		// Tutte le immagini del Gedcom
		for( Media m : gc.getMedia() ) {	// media record in tutto il gedcom
			listaMedia.add( m );
		}
		for( Person p : gc.getPeople() ) {	// local media negli individui
			for( Media m : p.getMedia() )
				listaMedia.add( m );
		}
		for( Source f : gc.getSources() ) {	// local media nelle fonti
			for( Media m : f.getMedia() )
				listaMedia.add( m );
		}
		// Dopo averle collezionate le mostra
		for( Media m : listaMedia ) {
			if(m.getId() != null ) s.l( m.getId()	 	// 'O1' solo per Multimedia Records
					+"  "+ popolarita(m) );
			if( m.getTitle() != null ) metti( "Titolo", m.getTitle() ); 
			String percorso = U.percorsoMedia(m);
			if( percorso != null )
				s.l( "IMMAGINE " + percorso );	// C:\Users\Michele\Documents\app\esempi\Angelina Guadagnoli.jpg
			if( m.getFile() != null ) metti( "File originale", m.getFile() );		// Angelina Guadagnoli.jpg
			if( m.getFormat() != null ) metti( "Formato", m.getFormat() );		// jpeg
			String[] altriMetodi = { "Primary", "Type", "Scrapbook", "SlideShow", "Blob" };
			for( String metodo : altriMetodi ) {
				try { 
					String qualcosa = (String) m.getClass().getMethod( "get" + metodo ).invoke(m);
					if( qualcosa != null )
						metti( metodo, qualcosa ); 
				} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
					e.printStackTrace();
				}
			}
			s.l( m.getFileTag() );	// FILE o _FILE
			for( Note nota : m.getAllNotes(gc) )	// Lista di NOTE: sia inline che referenziate
				metti( "Nota", nota.getValue() );
			if( m.getChange() != null )
				metti( "Modificato", m.getChange().getDateTime().getValue() +" - "+ m.getChange().getDateTime().getTime() );
			if( !m.getExtensions().isEmpty() )
				metti( "Altro", U.trovaEstensioni(m.getExtensions()) );
			s.l("----------------------");
		}
	}

	void metti( String tit, String cosa ) {
		s.l( "* " + tit + " *\n" + cosa );
	}
	
	int popolarita( Media med ) {
		int quante = 0;
		for( Media m : gc.getMedia() ) {	// media record in tutto il gedcom
			if( m.equals(med) )
				quante++;
		}
		for( Person p : gc.getPeople() ) {	// local media negli individui
			for( Media m : p.getMedia() )
				if( m.equals(med) )
					quante++;
		}
		for( Source f : gc.getSources() ) {	// local media nelle fonti
			for( Media m : f.getMedia() )
				if( m.equals(med) )
					quante++;
		}
		return quante;
	}
}
