package gedcomy;

import java.util.ArrayList;
import java.util.List;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Source;
import org.folg.gedcom.model.Visitor;

public class VisitatoreListaMedia extends Visitor {
	
	// Qui si esprime il potere del Visitor!
	
	public List<Media> listaMedia = new ArrayList<>();

	@Override
	public boolean visit( Gedcom gc ) {
		listaMedia.addAll( gc.getMedia() );	// rastrella gli oggetti media
		return true;
	}
	@Override
	public boolean visit( Person p ) {
		listaMedia.addAll( p.getMedia() );	// media locali di tutti gli individui
		return true;
	}
	@Override
	public boolean visit( Source s ) {
		listaMedia.addAll( s.getMedia() );	// media locali di tutte le fonti
		return true;
	}
	// E così via per tutti i tipi di cui voglio collezionare i media
	@Override
	public boolean visit( Name n ) {
		listaMedia.addAll( n.getMedia() );
		return true;
	}
	@Override
	public boolean visit( EventFact e ) {
		listaMedia.addAll( e.getMedia() );
		return true;
	}
}