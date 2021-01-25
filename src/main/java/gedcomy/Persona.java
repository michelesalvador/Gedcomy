//import org.folg.gedcom.model.EventFact;

package gedcomy;

import java.util.List;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;

class Persona extends Person {
	
	protected Person personaDecorata;
	
	Persona(){}
	
	Persona( Person personaDecorata ) {
        this.personaDecorata = personaDecorata;
    }
	
	@Override
	public List<Family> getParentFamilies(Gedcom gedcom) {
		return personaDecorata.getParentFamilies(gedcom);	//Delegation
	}
    
    
	/*// Restituisce il sesso: 0 ignoto, 1 maschio, 2 femmina*/
	int sesso() {
		for( EventFact fatto : this.getEventsFacts() ) {
			if( fatto.getDisplayType() == "Sex" ) {
				//l( this.getNames().get(0).getDisplayValue() +" > "+ fatto.getDisplayType() +": '"+ fatto.getValue() +"'" );
				if( fatto.getValue().equals("M") )
					return 1;
				else if( fatto.getValue().equals("F") )
					return 2;
			}			
		}
		return 0;
	}
	
}
