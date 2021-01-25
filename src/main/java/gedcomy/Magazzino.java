// Elenco degli archivi (Repository)

package gedcomy;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Repository;
import org.folg.gedcom.model.Source;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Magazzino {

    Gedcom gc;

	Magazzino( Gedcom gc, int ordine ) {
		this.gc = gc;
		List<Repository> listArchivi = gc.getRepositories();
		for( Repository rep : listArchivi ) {
			if( rep.getExtension("fonti") == null )
				rep.putExtension( "fonti", quanteFonti(rep,gc) );
		}
		Collections.sort( listArchivi, new Comparator<Repository>() {
		    public int compare( Repository r1, Repository r2 ) {
		    	switch( ordine ) {
			    	case 1:	// Ordina per id
			    		return Integer.parseInt(r1.getId().substring(1)) - Integer.parseInt(r2.getId().substring(1));
			    	case 2:	// Ordine alfabeto
			    		return r1.getName().compareToIgnoreCase(r2.getName());
			    	case 3:	// Ordina per numero di fonti
			    		return (int)r2.getExtension("fonti") - (int)r1.getExtension("fonti");
			    }
				return 0;
		    }
		});
		
		for( Repository rep : listArchivi ) {
			s.p( rep.getId() +"  "+ rep.getName() );
			s.l( "\t" + rep.getExtension("fonti") );
		}
	}

	// Conta quante fonti sono presenti nel tal archivio
	static int quanteFonti( Repository rep, Gedcom gc ) {
		int quante = 0;
		for( Source fon : gc.getSources() ) {
			if( fon.getRepository(gc) != null )
				if( fon.getRepository(gc).equals(rep) )
					quante++;
		}
		return quante;
	}
}
