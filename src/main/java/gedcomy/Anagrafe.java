package gedcomy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;

public class Anagrafe {
	
	Anagrafe( Gedcom gc, int ordine ) {
		List<Person> listaTutti = gc.getPeople();
		Collections.sort( listaTutti, new Comparator<Person>() {
		    public int compare(Person p1, Person p2) {
		    	switch( ordine ) {
			    	case 0:	// ordina per ID Gedcom
			    		return Integer.parseInt(p1.getId().substring(1)) - Integer.parseInt(p2.getId().substring(1));
			    	case 1:	// ordina per cognome
					   	if( p1.getNames().size() == 0 )	// i nomi null vanno in fondo
					   		return (p2.getNames().size() == 0) ? 0 : 1;
					   	if( p2.getNames().size() == 0 )
					   		return -1;
					    return cognomeNome(p1).compareTo( cognomeNome(p2) );
			    	case 2:	// ordina per anno
					    return annoBase(p1) - annoBase(p2);
			    }
				return 0;
		    }
		});
		for( Person uno : listaTutti ) {
			String id, nome="", anni="", luoghi="";
			id = uno.getId();
			if( uno.getNames().size() > 0 )
				nome = uno.getNames().get(0).getDisplayValue().replaceAll("/","");
			anni = U.dueAnni(uno, true);
			luoghi = dueLuoghi(uno);
			//System.out.format( "%s\n%s  %s\n", nome, anni, luoghi );
			System.out.format( "%5s  %-40s %-20s %s\n", id, nome, anni, luoghi );
		}
	}

	// Restituisce una stringa con cognome e nome attaccati e maiuscoli: 'SALVADORMICHELE'
	private String cognomeNome(Person tizio) {
		String tutto = tizio.getNames().get(0).getValue().toUpperCase();
		String cognome = " ";
		if( tutto.lastIndexOf("/") - tutto.indexOf("/") > 1 )	// se c'è un cognome tra i due '/'
			cognome = tutto.substring( tutto.indexOf("/")+1, tutto.lastIndexOf("/") );
		tutto = cognome.concat( tutto.substring(0,tutto.indexOf("/")) );
		//sl(tutto);
		return tutto;
	}

	// riceve una Person e restituisce un annno base della sua esistenza
	private int annoBase( Person p ) {
		int anno = 9999;
		for( EventFact unFatto : p.getEventsFacts() ) {
			if( unFatto.getDate() != null ) {
				String data = unFatto.getDate();
				data = data.substring( data.lastIndexOf(" ")+1 );	// prende l'anno che sta in fondo alla data
				if( data.contains("/") )	// gli anni tipo '1711/12' vengono semplificati in '1712'
					data = data.substring(0,2).concat( data.substring(data.length()-2,data.length()) );
				if( data.matches("[0-9]+") ) {
					anno = Integer.parseInt(data);
					break;
				}
			}
		} 
		return anno;
	}
	
	// riceve una persona e restitusce i due luoghi: iniziale - finale
	public String dueLuoghi( Person p ) {
		String luoghi = "";
		List<EventFact> fatti = p.getEventsFacts();
		for( EventFact unFatto : fatti ) {
			if( unFatto.getPlace() != null ) {
				luoghi = togliVirgole( unFatto.getPlace() );
				break;
			}
		}
		for(int i=fatti.size()-1; i>=0; i-- ) {
			String secondoLuogo = fatti.get(i).getPlace();
			if( secondoLuogo != null ) {
				secondoLuogo = togliVirgole(secondoLuogo);
				if( !secondoLuogo.equals(luoghi) )
					luoghi = luoghi.concat(" - ").concat(secondoLuogo);
				break;
			} 
		}
		return luoghi;
	}
	
	// riceve un luogo stile Gedcom e restituisce il primo nome tra le virgole
	private String togliVirgole( String luogo ) {
		// salta le virgole iniziali per luoghi tipo ',,,England'
		int iniz = 0;
		for( char c : luogo.toCharArray() ) {
			if( c!=',' && c!=' ' )
				break;
			iniz++;
		}
		luogo = luogo.substring(iniz);
		if( luogo.indexOf(",") > 0 )
			luogo = luogo.substring( 0, luogo.indexOf(",") );
		return luogo;
	}
}
