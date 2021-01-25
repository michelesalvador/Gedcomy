package gedcomy;

import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Visitor;

public class VisitatoreSesso extends Visitor {
	
	// Questo funziona ma non è qui il potere del Visitor
	public int sesso = 0;
	@Override
    public boolean visit( Person p ) {
		for( EventFact fatto : p.getEventsFacts() ) {
			if( fatto.getDisplayType() == "Sex" ) {
				//l( this.getNames().get(0).getDisplayValue() +" > "+ fatto.getDisplayType() +": '"+ fatto.getValue() +"'" );
				if( fatto.getValue().equals("M") )
					sesso = 1;
				else if( fatto.getValue().equals("F") )
					sesso = 2;
			}			
		}
		s.l( "Sesso: " + sesso );
		return false;
    }
}