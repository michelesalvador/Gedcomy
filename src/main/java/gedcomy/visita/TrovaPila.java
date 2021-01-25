// Visitatore che produce una lista di tutti gli oggetti tra il record capostipite e l'oggetto di cui mi interessa ricostruire la pila gerarchica

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
import org.folg.gedcom.model.Visitable;
import org.folg.gedcom.model.Visitor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import gedcomy.s;

public class TrovaPila extends Visitor {

	public List<Passo> traccia = new ArrayList<>();
	private Object scopo;
	private boolean trovato;

	public TrovaPila( Object scopo ) {
		this.scopo = scopo;
	}
	
	private boolean opera( Object oggetto, String tag, boolean capostipite ) {
		if( !trovato ) {
			if( capostipite )
				traccia.clear();
			Passo passo = new Passo();
			passo.oggetto = oggetto;
			passo.tag = tag;
			traccia.add(passo);
			/*for( Passo pas : traccia )
				s.l( pas.tag +" "+ pas.oggetto.getClass().getName() );
			s.l("-------------");*/
		}
		if( oggetto.equals(scopo) ) {
			Iterator<Passo> passi = traccia.iterator();
			while( passi.hasNext() ) {
				PulisciPila pulitore = new PulisciPila( scopo );
				((Visitable)passi.next().oggetto).accept( pulitore );
				if( !pulitore.trovato )
					passi.remove();
			}
			trovato = true;
		}
		return true;
	}

	@Override
	public boolean visit( Header passo ) {
		return opera(passo,"HEAD",true);
	}
	@Override
	public boolean visit( Person passo ) {
		return opera(passo,"INDI",true);
	}
	@Override
	public boolean visit( Family passo ) {
		return opera(passo,"FAM",true);
	}
	@Override
	public boolean visit( Name passo ) {
		return opera(passo,"NAME",false);
	}
	@Override
	public boolean visit( EventFact passo ) {
		return opera(passo,passo.getTag(),false);
	}
	@Override
	public boolean visit( Media passo ) {
		return opera(passo,"OBJE",passo.getId()!=null);
	}
	@Override
	public boolean visit( SourceCitation passo ) {
		return opera(passo,"SOUR",false);
	}
	@Override
	public boolean visit( Note passo ) {
		return opera(passo,"NOTE",passo.getId()!=null);
	}
	@Override
	public boolean visit( Source passo ) {
		return opera(passo,"SOUR",true);
	}
	@Override
	public boolean visit( Repository passo ) {
		return opera(passo,"REPO",true);
	}
	@Override
	public boolean visit( Change passo ) {
		return opera(passo,"CHAN",false);
	}
	
	public class Passo {
		public Object oggetto;
		public String tag;
	}
}
