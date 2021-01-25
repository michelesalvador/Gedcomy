package gedcomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;
//import org.folg.gedcom.model.Visitor;

class Diagramma {
	
	void sl(Object linea) {
		System.out.println(linea);
	}
	void s(Object parola) {
		System.out.print(parola);
	}
	
	private Gedcom gc;
	private int gente;
	LinearLayout scatola = new LinearLayout();	// solo per simulare android studio
	
	Diagramma( Gedcom gc ) {
		this.gc = gc;
		//v.visit(gc);
	}

	// Un paio di classi giusto per simulare android studio
	class View {
		Person esso;
		View vista;
		void addView( View v ) {
			vista = v;
		}
		View findViewById( int id ) {
			return new View();
		}
	}
	class LinearLayout extends View {}
	
	// Il libro mastro in cui inserire le coppie di View da collegare con linee
	List<Corda> rete;
	class Corda {
		View origine;
		LinearLayout fine;
		Corda( View origine, LinearLayout fine ) {
			this.origine = origine;
			this.fine = fine;
		}
	}
	
	// prima versione, parte dall'individuo al centro dell'albero
	void disegna0( String id ) {
		Person centro = gc.getPerson(id);	// U.trovaRadice(gc);
		sl( "{{{ "+ U.essenza(centro) +" }}}" );
		for( Family famil : centro.getSpouseFamilies(gc) ) {
			List<Person> mariti = famil.getHusbands(gc);
			mariti.remove(centro);
			for( Person marito : mariti ) {
				sl( "{{ "+ U.essenza(marito) +" }}" );
			}
			List<Person> mogli = famil.getWives(gc);
			mogli.remove(centro);
			for( Person moglie : mogli )
				sl( "{{ "+ U.essenza(moglie) +" }}" );
			
			for( Person figlio : famil.getChildren(gc) ) {
				sl( "  ("+ U.essenza(figlio) +")" );
				discendenti( figlio );
			}
		}
		// genitori
		if( !centro.getParentFamilies(gc).isEmpty() ) {	// qui ci va eventuale scelta di QUALI genitori mostrare
			Family fam = centro.getParentFamilies(gc).get(0);
		
			for( Person pa : fam.getHusbands(gc) ) {
				sl( "[[ "+ U.essenza(pa) +" ]]" );
				antenati( pa );
			}
			for( Person ma : fam.getWives(gc) ) {
				sl( "[[ "+ U.essenza(ma) +" ]]" );
				antenati( ma );
			}
				
			// fratelli
			List<Person> fratelli = fam.getChildren(gc);
			fratelli.remove(centro);
			for( Person fratello : fratelli ) {
				sl( " {"+ U.essenza(fratello) +"}" );
				discendenti(fratello);
			}
		}
		sl("------------------------------------------------");
	}
	
	// seconda versione: per prima cosa va al nonno paterno, poi riempie le file di generazioni
	void disegna( String id ) {
		rete = new ArrayList<>();
		// risale ai nonni
		Person centro = gc.getPerson(id);
		Globale.individuo = centro.getId();	// memorizza centro del diagramma
		if( !centro.getParentFamilies(gc).isEmpty() ) {	// qui ci va eventuale scelta di QUALI genitori mostrare se centro ha più genitori
			Family famiglia = centro.getParentFamilies(gc).get(0);
			// Ramo paterno
			Person padre = null;
			View nodoNonniPaterni = null;
			if( !famiglia.getHusbands(gc).isEmpty() ) {
				padre = famiglia.getHusbands(gc).get(0);
				nodoNonniPaterni = nonni( padre );
				ziiCugini( padre, nodoNonniPaterni );
				altriMatrimoni( padre, famiglia, null );	// ma nodoGenitori ?????
			}
			// Inizia ramo materno
			Person madre = null;
			View nodoNonniMaterni = null;
			View nodoGenitori;
			if( !famiglia.getWives(gc).isEmpty() ) {
				madre = famiglia.getWives(gc).get(0);
				nodoNonniMaterni = nonni( madre );
			}
			// Genitori
			if( padre != null && madre != null )
				nodoGenitori = schedaDoppia( 2, padre, madre, nodoNonniPaterni, nodoNonniMaterni, 0, false );
			else if( padre != null )
				nodoGenitori = schedaSingola( 2, padre, nodoNonniPaterni, false, false );
			else
				nodoGenitori = schedaSingola( 2, madre, nodoNonniMaterni, false, false );
			// Fratelli (tra cui centro), figli e nipoti
			for( Person fratello : famiglia.getChildren(gc) ) {
				View nodoFratello = inserisci( 3, fratello, nodoGenitori, false );
				if( !fratello.getSpouseFamilies(gc).isEmpty() ) {
					Family famig = fratello.getSpouseFamilies(gc).get(0);
					for( Person figlio : famig.getChildren(gc) ) {
						View nodoFiglio = inserisci( 4, figlio, nodoFratello, false );
						if( !figlio.getSpouseFamilies(gc).isEmpty() ) {
							Family fam = figlio.getSpouseFamilies(gc).get(0);
							for( Person nipote : fam.getChildren(gc) )
								inserisci( 5, nipote, nodoFiglio, true );
						}
					}
				}
			}
			// Completa ramo materno
			if( madre != null ) {	
				altriMatrimoni( madre, famiglia, nodoGenitori );	// ma nodoGenitori è sbagliato, deve essere di UN SOLO genitore
				ziiCugini( madre, nodoNonniMaterni );
			}
		}
		/*s.l("- - - - - - - - - - - - - - - - - - - - - - - -");
		for( Corda corda : rete ) {
			if( corda.origine != null && corda.fine != null )
				s.l( corda.origine +" -> "+ corda.fine );
				//sl( corda.origine.vista.esso.getNames().get(0).getDisplayValue() +" -> "+ corda.fine.esso.getNames().get(0).getDisplayValue() );
		}*/
		s.l("------------------------------------------------");
	}
	
	// colloca una o DUE Person nel diagramma
	private View inserisci( int generazione, Person egli, View nodoSopra, boolean conDiscendenti ) {	
		View nodo = null;
		if( !egli.getSpouseFamilies(gc).isEmpty() ) {
			Family fam = egli.getSpouseFamilies(gc).get(0);
			if( U.sesso(egli)==1 && !fam.getWives(gc).isEmpty() )	// Maschio ammogliato
				nodo = schedaDoppia( generazione, egli, fam.getWives(gc).get(0), nodoSopra, null, 2, conDiscendenti );
			else if( U.sesso(egli)==2 && !fam.getHusbands(gc).isEmpty() )	// Femmina ammogliata
				nodo = schedaDoppia( generazione, fam.getHusbands(gc).get(0), egli, null, nodoSopra, 1, conDiscendenti );
			else
				nodo = schedaSingola( generazione, egli, nodoSopra, false, conDiscendenti );	// senza sesso (o senza coniuge?)
		} else
			nodo = schedaSingola( generazione, egli, nodoSopra, false, conDiscendenti );
		return nodo;
	}
	
	// Inserisce la scheda di una persona nel diagramma
	private LinearLayout schedaSingola( int generazione, Person egli, View nodoSopra, boolean conAntenati, boolean conDiscendenti ) {
		s.p( generazione +": " );
		LinearLayout nodo = new Schedina( egli, conAntenati, conDiscendenti );
		rete.add( new Corda( nodoSopra, nodo ) );
		return nodo;
	}
	
	// Inserisce la scheda di una coppia nel diagramma
	// conAntenati: 0 nessuno, 1 lui, 2 lei, 3 entrambi
	private LinearLayout schedaDoppia( int generazione, Person lui, Person lei, View nodoSopraLui, View nodoSopraLei, int conAntenati, boolean conDiscendenti ) {
		//sl( generazione +": "+ U.nome(lui) +" "+ U.dueAnni(lui) +" / "+ U.nome(lei) +" "+ U.dueAnni(lei) );
		boolean conAntenatiLui = false;
		boolean conAntenatiLei = false;
		if( conAntenati == 1 || conAntenati == 3 )
			conAntenatiLui = true;
		if( conAntenati == 2 || conAntenati == 3 )
			conAntenatiLei = true;
		LinearLayout nodo = new LinearLayout();
		s.p( generazione +": " );
		LinearLayout schedinaLui = new Schedina( lui, conAntenatiLui, false );
		nodo.addView( schedinaLui );
		rete.add( new Corda( nodoSopraLui, schedinaLui ) );
		String annoMatrimonio = null;
		for( EventFact ef : lui.getSpouseFamilies(gc).get(0).getEventsFacts() ) {
			if( ef.getDisplayType().equals("Marriage") )
				annoMatrimonio = U.soloAnno( ef.getDate() );
		}
		if( annoMatrimonio != null ) sl( annoMatrimonio );
		s.p( "   " );
		LinearLayout schedinaLei = new Schedina( lei, conAntenatiLei, false );
		nodo.addView( schedinaLei );
		rete.add( new Corda( nodoSopraLei, schedinaLei ) );
		if( conDiscendenti )
			discendenti( lui );
		return nodo;
	}
		
	class Schedina extends LinearLayout {
		Person egli;
		Schedina( Person egli, boolean conAntenati, boolean conDiscendenti ) {
			this.egli = egli;
			this.esso = egli;
			if( !egli.getAllMedia(gc).isEmpty() )
				s.p( U.percorsoMedia(egli.getAllMedia(gc).get(0)) +"  " );
			s.l( U.nome(egli) +" "+ U.dueAnni(egli, true) );
			if( Globale.individuo.equals( egli.getId() ) )
				s.l( "   [CENTRO DEL DIAGRAMMA]" );
			if( conAntenati )
				antenati( egli );
			if( conDiscendenti )
				discendenti( egli );
		}
	}
	
	/* Un blocco ripetitivo per inserire nonni, zii e cugini
	private void nonniZiiCugini( Person genitore ) {
		if( !genitore.getParentFamilies(gc).isEmpty() ) {
			Family fam = genitore.getParentFamilies(gc).get(0);
			// Nonni
			if( !fam.getHusbands(gc).isEmpty() && !fam.getWives(gc).isEmpty() )	// ci sono entrambi i nonni
				schedaDoppia( 1, fam.getHusbands(gc).get(0), fam.getWives(gc).get(0), 3, false );
			else if( !fam.getHusbands(gc).isEmpty() )
				schedaSingola( 1, fam.getHusbands(gc).get(0), true, false );
			else if( !fam.getWives(gc).isEmpty() )
				schedaSingola( 1, fam.getWives(gc).get(0), true, false );
			// Zii
			List<Person> zii = fam.getChildren(gc);
			zii.remove(genitore);
			for( Person zio : zii ) {
				inserisci( 2, zio, true, false );
				if( !zio.getSpouseFamilies(gc).isEmpty() ) {
					fam = zio.getSpouseFamilies(gc).get(0);
					for( Person cugino : fam.getChildren(gc) )
						inserisci( 3, cugino, true, true );
				}
			}
		}
	}*/
	
	// Un blocco ripetitivo per inserire i nonni
	private View nonni( Person genitore ) {
		View nodoNonni = null;
		if( !genitore.getParentFamilies(gc).isEmpty() ) {
			Family fam = genitore.getParentFamilies(gc).get(0);
			// Nonni
			if( !fam.getHusbands(gc).isEmpty() && !fam.getWives(gc).isEmpty() )	// ci sono entrambi i nonni
				nodoNonni = schedaDoppia( 1, fam.getHusbands(gc).get(0), fam.getWives(gc).get(0), null, null, 3, false );
			else if( !fam.getHusbands(gc).isEmpty() )
				nodoNonni = schedaSingola( 1, fam.getHusbands(gc).get(0), null, true, false );
			else if( !fam.getWives(gc).isEmpty() )
				nodoNonni = schedaSingola( 1, fam.getWives(gc).get(0), null, true, false );
		}
		return nodoNonni;
	}
	
	// Un blocco ripetitivo per inserire zii e cugini
	private void ziiCugini( Person genitore, View nodoNonni ) {
		if( !genitore.getParentFamilies(gc).isEmpty() ) {
			Family fam = genitore.getParentFamilies(gc).get(0);
			// Zii
			List<Person> zii = fam.getChildren(gc);
			zii.remove(genitore);
			for( Person zio : zii ) {
				View nodoZii = inserisci( 2, zio, nodoNonni, false );
				if( !zio.getSpouseFamilies(gc).isEmpty() ) {
					fam = zio.getSpouseFamilies(gc).get(0);
					for( Person cugino : fam.getChildren(gc) )
						inserisci( 3, cugino, nodoZii, true );
				}
			}
		}
	}
	
	// Fratellastri nati dai matrimoni precedenti o seguenti dei genitori
	private void altriMatrimoni( Person genitore, Family famiglia, View nodoGenitore ) {
		List<Family> altreFamiglie = genitore.getSpouseFamilies(gc);
		altreFamiglie.remove( famiglia );
		for( Family altraFamiglia : altreFamiglie ) {
			sl(">>>>>");
			//if( U.sesso(genitore) == 1 )	// si potrebbe anche mostrare gli altri coniugi
				//altraFamiglia.getWives(gc).get(0);
			for( Person fratellastro : altraFamiglia.getChildren(gc) )
				inserisci( 3, fratellastro, nodoGenitore, true );
			sl("<<<<<<");
		}
	}
	
	// di una Person scrive e restituisce una coppietta di avi con il numero di antenati
	private Map<String,Integer> antenati( Person capo ) {
		Map<String,Integer> avi = new HashMap<>();
		if( !capo.getParentFamilies(gc).isEmpty() ) {
			Family fam = capo.getParentFamilies(gc).get(0);
			if( !fam.getHusbands(gc).isEmpty() ) {
				gente = 0;
				contaAntenati( fam.getHusbands(gc).get(0) );
				avi.put( "avo", gente);
			}
			if( !fam.getWives(gc).isEmpty() ) {
				gente = 0;
				contaAntenati( fam.getWives(gc).get(0) );
				avi.put( "ava", gente);
			}
		}
		if( !avi.isEmpty() )
			sl( "   " + avi );
		return avi;
	}
	
	// contatore ricorsivo degli antenati DIRETTI 
	private void contaAntenati( Person p ) {
		for( Family f : p.getParentFamilies(gc) ) {
			for( Person pa : f.getHusbands(gc) ) {
				gente++;
				//sl("\t"+ gente +" "+ U.essenza(pa) );
				contaAntenati( pa );
			}
			for( Person ma : f.getWives(gc) ) {
				gente++;
				//sl("\t"+ gente +" "+ U.essenza(ma) );
				contaAntenati( ma );
			}
		}
	}
	
	// di una Person scrive e restituisce una lista di figli col numero di rispettivi discendenti
	private List<Integer> discendenti( Person p ) {
		List<Integer> sfilza = new ArrayList<>();
		for( Family famiglia : p.getSpouseFamilies(gc) )
			for( Person nipote : famiglia.getChildren(gc) ) {
				gente = 0;
				contaDiscendenti( nipote );
				//sfilza += "  ("+ gente +")";
				sfilza.add(gente);
			}
		if( !sfilza.isEmpty() )
			sl( "   " + sfilza );	//  +" "+ p.getNames().get(0).getDisplayValue()
		return sfilza;
	}
	// conta ricorsivamente i discendenti
	private void contaDiscendenti( Person p ) {
		for( Family fam : p.getSpouseFamilies(gc) )
			for( Person figlio : fam.getChildren(gc) ) {
				gente++;
				//sl("\t"+ gente +" "+ Cosi.essenza(figlio) );
				contaDiscendenti( figlio );
			}
	}
	
	/*@Override
	public void accept(Visitor visitor) {
		if (visitor.visit(this)) {
			if (repo != null) {
				repo.accept(visitor);
         super.visitContainedObjects(visitor);
         visitor.endVisit(this);
      }
   }*/

}
