// Attrezzi utili per tutto il programma

package gedcomy;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.ExtensionContainer;
import org.folg.gedcom.model.Family;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.GedcomTag;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.NoteContainer;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Source;
import org.folg.gedcom.model.SourceCitation;
import org.folg.gedcom.model.SourceCitationContainer;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;

import gedcomy.Datatore.Data;

public class U {
	
	static void l(Object linea) {
		System.out.println(linea);
	}
	static void p(Object parola) {
		System.out.print(parola);
	}
	
	// restituisce la Person iniziale di un Gedcom
	static Person trovaRadice( Gedcom gc ) {
		if( valoreTag(gc.getHeader().getExtensions(),"_ROOT") != null )
			return gc.getPerson( valoreTag(gc.getHeader().getExtensions(),"_ROOT") );
		else if( !gc.getPeople().isEmpty() )
			return gc.getPeople().get(0);
		return null;
	}
	
	// riceve una Person e restituisce stringa con nome e anni essenziali della persona
	static String essenza( Person p ) {
		String egli = "";
		if( p != null ) {
			if( !p.getNames().isEmpty() )
				egli = p.getNames().get(0).getDisplayValue().replaceAll("/","");
			egli += " " + dueAnni( p, true );
			//if( valoreTag(Globale.gc.getHeader().getExtensions(),"_ROOT").equals( p.getId() ) ) {
			/*if( Globale.individuo.equals( p.getId() ) )
				egli += " <- CENTRO DEL DIAGRAMMA";*/
			//egli += " " + p.hashCode();
		}
		return egli;
	}
	
	// Restituisce il nome di una Person
	static String nome( Person p ) {
		String nome = "";
		if( !p.getNames().isEmpty() )
			nome = p.getNames().get(0).getDisplayValue().replaceAll("/","");
		return nome;
	}
	
	// Riceve una Person e restituisce il sesso: 0 ignoto, 1 maschio, 2 femmina
	static int sesso( Person p ) {
		for( EventFact fatto : p.getEventsFacts() ) {
			if( fatto.getDisplayType() == "Sex" ) {
				//l( p.getNames().get(0).getDisplayValue() +" > "+ fatto.getDisplayType() +": '"+ fatto.getValue() +"'" );
				if( fatto.getValue().equals("M") )
					return 1;
				else if( fatto.getValue().equals("F") )
					return 2;
			}			
		}
		return 0;
	}

	static boolean morto( Person p ) {
		for( EventFact fatto : p.getEventsFacts() ) {
			if( fatto.getDisplayType().equals("Death") || fatto.getDisplayType().equals("Burial") ) {
				//s.l( p.getNames().get(0).getDisplayValue() +" > "+ fatto.getDisplayType() +": '"+ fatto.getValue() +"'" );
				//if( fatto.getValue().equals("Y") )
				return true;
			}
		}
		return false;
	}

	// Riceve una Person e restituisce una stringa con gli anni di nascita e morte e l'età eventualmente
	static String dueAnni( Person p, boolean conEta ) {
		String anni = "";
		Datatore inizio = null, fine = null;
		for( EventFact unFatto : p.getEventsFacts() ) {
			if( unFatto.getTag() != null && unFatto.getTag().equals("BIRT") && unFatto.getDate() != null ) {
				inizio = new Datatore( unFatto.getDate() );
				anni = inizio.writeDate(true);
				break;
			}
		}
		for( EventFact unFatto : p.getEventsFacts() ) {
			if( unFatto.getTag() != null && unFatto.getTag().equals("DEAT") && unFatto.getDate() != null ) {
				fine = new Datatore( unFatto.getDate() );
				if( !anni.isEmpty() )
					anni += " – ";
				anni += fine.writeDate(true);
				break;
			}
		}
		// Aggiunge l'età tra parentesi
		if( conEta && inizio != null && inizio.isSingleKind() ) { // tipi di date puntiformi
			LocalDate dataInizio = new LocalDate( inizio.data1.date ); // converte in joda time
			// Se è ancora vivo la fine è adesso
			if( fine == null && dataInizio.isBefore(LocalDate.now()) && Years.yearsBetween(dataInizio,LocalDate.now()).getYears() < 120 && !U.morto(p) )
				fine = new Datatore( String.format(Locale.ENGLISH,"%te %<Tb %<tY",new Date()) ); // un po' assurdo dover qui passare per Datatore...			
			if( fine != null && fine.isSingleKind() ) {
				LocalDate dataFine = new LocalDate( fine.data1.date );
				String misura = "";
				int eta = Years.yearsBetween( dataInizio, dataFine ).getYears();
				if( eta < 2 ) {
					eta = Months.monthsBetween( dataInizio, dataFine ).getMonths();
					misura = " mesi"; // todo traduci
					if( eta < 2 ) {
						eta = Days.daysBetween( dataInizio, dataFine ).getDays();
						misura = " giorni";
					}
				}
				if( eta >= 0 )
					anni += "  (" + eta + misura + ")";
				else
					anni += "  (?)";
			}
		}
		return anni;
	}

	// riceve una data in stile gedcom e restituisce l'annno semplificato alla mia maniera
	static String soloAnno( String data ) {
		String anno = data.substring( data.lastIndexOf(" ")+1 );	// prende l'anno che sta in fondo alla data
		if( anno.contains("/") )	// gli anni tipo '1711/12' vengono semplificati in '1712'
			anno = anno.substring(0,2).concat( anno.substring(anno.length()-2,anno.length()) );
		if( data.startsWith("ABT") || data.startsWith("EST") || data.startsWith("CAL") )
			anno = anno.concat("?");
		if( data.startsWith("BEF") )
			anno = "<".concat(anno);
		if( data.startsWith("AFT") )
			anno = anno.concat(">");
		if( data.startsWith("BET") ) {
			int pos = data.indexOf("AND") - 1;
			String annoPrima = data.substring( data.lastIndexOf(" ",pos-1)+1, pos );	// prende l'anno che sta prima di 'AND'
			if( !annoPrima.equals(anno) ) {
				if( annoPrima.substring(0,2).equals(anno.substring(0,2)) )		// se sono dello stesso secolo
					anno = anno.substring( anno.length()-2, anno.length() );	// prende solo gli anni
				anno = annoPrima.concat("~").concat(anno);
			}
		}
		return anno;
	}
	
	// Estrae i soli numeri da una stringa che può contenere anche lettere
	static int soloNumeri( String id ) {
		//return Integer.parseInt( id.replaceAll("\\D+","") );	// sintetico ma lento
		int num = 0;
		int x = 1;
		for( int i = id.length()-1; i >= 0; --i ){
			int c = id.charAt( i );
			if( c > 47 && c < 58 ){
				num += (c-48) * x;
				x *= 10;
			}
		}
		return num;
	}
	
	static String tuttiTag;
	static String trovaEstensioni( Map<String,Object> mappaEstensioni ) {
		tuttiTag = "";
		for( Map.Entry<String,Object> estensione : mappaEstensioni.entrySet() ) {
			//s.l(estensione);
			//s.l( estensione.getKey() );	// folg.more_tags
			//s.l( estensione.getValue() );	// [ tag:DATA [ tag:EVEN value:BIRT [ tag:DATE value:1881 tag:PLAC value:Caraglio, Cuneo ] ]]
			//s.l( estensione.getValue().getClass() );	// class java.util.ArrayList
			if( estensione.getKey().equals("folg.more_tags") ) {
				@SuppressWarnings("unchecked")
				List<GedcomTag> listaTag = (ArrayList<GedcomTag>) estensione.getValue();
				//s.l( listaTag.get(0).getClass() );	// class org.folg.gedcom.model.GedcomTag
				for( GedcomTag tag : listaTag) {
					scriviTag( tag );
				}
			}
		}
		return tuttiTag;
	}
	/* Scrive in console
	static void scriviTag( GedcomTag pacco ) {
		s.p( pacco.getTag() +": " );
		if( pacco.getValue() != null )
			s.l( pacco.getValue() );
		else if( pacco.getId() != null )
			s.l( pacco.getId() );
		else if( pacco.getRef() != null )
			s.l( pacco.getRef() );
		for( GedcomTag unPezzo : pacco.getChildren() )
			scriviTag0( unPezzo );
	}*/
	// Costruisce un testo con tutti i tag
	static void scriviTag( GedcomTag pacco ) {
		tuttiTag += pacco.getTag() +": ";
		if( pacco.getValue() != null )
			tuttiTag += pacco.getValue() +"\n";
		else if( pacco.getId() != null )
			tuttiTag += pacco.getId() +"\n";
		else if( pacco.getRef() != null )
			tuttiTag += pacco.getRef() +"\n";
		for( GedcomTag unPezzo : pacco.getChildren() )
			scriviTag( unPezzo );
	}
	
	// Restituisce il valore di un determinato tag in una estensione (GedcomTag)
	static String valoreTag( Map<String,Object> mappaEstensioni, String nomeTag ) {
		//l( mappaEstensioni );
		for( Map.Entry<String,Object> estensione : mappaEstensioni.entrySet() ) {
			@SuppressWarnings("unchecked")
			List<GedcomTag> listaTag = (ArrayList<GedcomTag>) estensione.getValue();
			//l(listaTag);
			for( GedcomTag unPezzo : listaTag ) {
				//l( unPezzo.getTag() +" "+ unPezzo.getValue() );
				if( unPezzo.getTag().equals( nomeTag ) ) {
					if( unPezzo.getId() != null )
						return unPezzo.getId();
					else if( unPezzo.getRef() != null )
						return unPezzo.getRef();
					else
						return unPezzo.getValue();
				}
			}
		}
		return null;
	}
	
	// Aggiorna il REF di un tag nelle estensioni di un oggetto:  tag:"_ROOT"  ref:"I123"
	@SuppressWarnings("unchecked")
	static void aggiornaTag( Object obj, String nomeTag, String ref ) {
		//List<GedcomTag> listaTag = new ArrayList<>();	ok
		//List<GedcomTag> listaTag = (ArrayList<GedcomTag>) ((ExtensionContainer) obj).getExtension("");	no
		//
		String chiave = "gedcomy_tags";
		List<GedcomTag> listaTag = new ArrayList<>();
		boolean aggiungi = true;
		Map<String,Object> mappaEstensioni = ((ExtensionContainer) obj).getExtensions();	// ok
		//s.l( "Map<String,Object> <"+ mappaEstensioni +">" );
		if( !mappaEstensioni.isEmpty() ) {
			chiave = (String) mappaEstensioni.keySet().toArray()[0];	// chiave = 'folg.more_tags'
			//s.l( mappaEstensioni.size() +": "+ chiave );	
			listaTag = (ArrayList<GedcomTag>) mappaEstensioni.get( chiave );
			// Aggiorna tag esistente
			for( GedcomTag gct : listaTag ) {
				if( gct.getTag().equals(nomeTag) ) {
					gct.setRef( ref );
					aggiungi = false;
				}
			}
		}
		// Aggiunge nuovo tag
		if( aggiungi ) {
			GedcomTag tag = new GedcomTag( null, nomeTag, null );
			tag.setValue( ref );
			listaTag.add( tag );
		}
		//s.l( "List<GedcomTag> " + listaTag );
		((ExtensionContainer) obj).putExtension( chiave, listaTag );
	}

	@SuppressWarnings("unchecked")
	static List<Note> trovaNote( Object cosa, Gedcom gc ) {
		List<Note> listaNote = null;
		try { listaNote = (List<Note>) cosa.getClass().getMethod("getNotes").invoke(cosa);
		} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e)
			{ e.printStackTrace(); }
		for( Note nota : listaNote ) {
			//s.l( nota.getValue() );
			trovaFonti( nota, gc );
		}
		return listaNote;
	}
	
	@SuppressWarnings("unchecked")
	static void trovaFonti( Object cosa, Gedcom gc ) {
		List<SourceCitation> listaFonti = null;
		try {
			listaFonti = (List<SourceCitation>) cosa.getClass().getMethod("getSourceCitations").invoke(cosa);
		} catch (NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) 
			{ e.printStackTrace(); }

		for( SourceCitation citazione : listaFonti ) {
			if( citazione.getSource(gc) != null )
				s.l( "\t"+ citazione.getSource(gc).getTitle() );
			else
				s.l( "\t"+ citazione.getValue() );	// per SOUR note
		}
	}
	
	// Cerca il media con diverse combinazioni di percorso
	static String percorsoMedia( Media m ) {
		// Percorso FILE (quello nel gedcom)
		if( new File( m.getFile() ).exists() )
			return m.getFile();
		//String cartella = U.valoreTag( gc.getHeader().getExtensions(), "_CARTELLA" ) + File.separator;
		String cartella = Globale.preferenze.get( "main_dir", "") + File.separator;
		// Cartella del .ged + percorso FILE
		String percorsoRicostruito = cartella + m.getFile();
		if( new File( percorsoRicostruito ).exists() )
			return percorsoRicostruito;
		// File nella stessa cartella del gedcom
		String percorsoFile = cartella + new File( m.getFile() ).getName();
		if( new File( percorsoFile ).exists() )
			return percorsoFile;
		return m.getFile();
	}
	
	// Restituisce il titolo della fonte
	static String titoloFonte( Source fon ) {
		String tit = "";
		if( fon.getAbbreviation() != null )
			tit = fon.getAbbreviation();
		else if( fon.getTitle() != null )
			tit = fon.getTitle();
		else if( fon.getText() != null ) {
			tit = fon.getText().replaceAll("\n", " ");
			tit = tit.length() > 35 ? tit.substring(0,35)+"…" : tit;
		}
		return tit;
	}
	
	// Restituisce quante volte una fonte viene citata nel Gedcom
	static int quante;
	static int quanteCitazioni( Source fon, Gedcom gc ) {
		quante = 0;
		for( Person p : gc.getPeople() ) {
			cita( p, fon );
			for( Name n : p.getNames() )
				cita( n, fon );
			for( EventFact ef : p.getEventsFacts() )
				cita( ef, fon );
		}
		for( Family f : gc.getFamilies() ) {
			cita( f, fon );
			for( EventFact ef : f.getEventsFacts() )
				cita( ef, fon );
		}
		for( Note n : gc.getNotes() )
			cita( n, fon );
		return quante;
	}
	
	// riceve un Object (Person, Name, EventFact...) e conta quante volte è citata la fonte
	private static void cita( Object ogg, Source fonte ) {
		List<SourceCitation> listaSc;
		if( ogg.getClass().toString().contains("Note") )	// se è una Nota
			listaSc = ((Note) ogg).getSourceCitations();
		else {
			for( Note n : ((NoteContainer) ogg).getNotes() )
				cita( n, fonte );
			listaSc = ((SourceCitationContainer) ogg).getSourceCitations();
		}
		for( SourceCitation sc : listaSc ) {
			if( sc.getRef() != null )
				if( sc.getRef().equals(fonte.getId()) )
					quante++;
		}
	}
}