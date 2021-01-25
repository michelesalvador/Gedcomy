package gedcomy;

import java.util.ArrayList;
import java.util.List;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.SpouseRef;

class Famiglia extends Family {
	
	protected Family famiglia;

	Famiglia( Family famiglia ) {
		this.famiglia = famiglia;
	}

	private List<Persona> getMembri( Gedcom gedcom, List<? extends SpouseRef> memberRefs ) {
		List<Persona> members = new ArrayList<>();
		for( SpouseRef memberRef : memberRefs ) {
			Persona member = (Persona) memberRef.getPerson(gedcom);
			if( member != null )
				members.add(member);
		}
		return members;
	}/**/
	
	public List<Persona> getMariti(Gedcom gedcom) {
		return getMembri( gedcom, getHusbandRefs() );
	}
	
	public List<Persona> getMogli(Gedcom gedcom) {
		return getMembri( gedcom, getWifeRefs() );
	}
	
	public List<Persona> getFigli( Gedcom gedcom ) {
		return getMembri( gedcom, getChildRefs() );
	}
}
