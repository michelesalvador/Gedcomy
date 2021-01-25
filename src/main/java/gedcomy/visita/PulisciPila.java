// Strettamente connesso a TrovaPila, individua gli oggetti da tenere nella pila

package gedcomy.visita;

import org.folg.gedcom.model.Change;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Header;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Repository;
import org.folg.gedcom.model.Source;
import org.folg.gedcom.model.SourceCitation;
import org.folg.gedcom.model.Visitor;
import gedcomy.s;

public class PulisciPila extends Visitor {

	private Object scopo;
	public boolean trovato = false;
	
	public PulisciPila( Object scopo ) {
		this.scopo = scopo;
	}
	
	boolean opera( Object oggetto ) {
		if( oggetto.equals(scopo) )
			trovato = true;
		return true;
	}

	@Override
	public boolean visit( Header oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Person oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Family oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Name oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( EventFact oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Media oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( SourceCitation oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Note oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Source oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Repository oggetto ) {
		return opera(oggetto);
	}
	@Override
	public boolean visit( Change oggetto ) {
		return opera(oggetto);
	}
}
