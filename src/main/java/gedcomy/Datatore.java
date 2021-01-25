package gedcomy;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Datatore {

	Data data1;
	Data data2;
	String frase; // Quella che andrà tra parentesi
	int tipo; // da 0 a 10
	String[] paterni = { "d MMM yyy", "d M yyy", "MMM yyy", "M yyy", "d MMM", "yyy" };
	String G_M_A = paterni[0];
	String G_m_A = paterni[1];
	String M_A = paterni[2];
	String m_A = paterni[3];
	String G_M = paterni[4];
	String A = paterni[5];
	static String[] mesiGedcom = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	static String[] prefissi = { "", "ABT", "CAL", "EST", "AFT", "BEF", "BET", "FROM", "TO", "FROM", "(" }; // Todo "INT"
	static String[] suffissi = { "B.C.", "BC", "BCE" };
	
	public Datatore( String dataGc ) {
		data1 = new Data();
		data2 = new Data();
		analizza( dataGc );
	}

	class Data {
		Date date;
		SimpleDateFormat format;
		boolean negativa;
		boolean doppia;
		
		Data() {
			DateFormatSymbols simboliFormato = new DateFormatSymbols();
			simboliFormato.setShortMonths( mesiGedcom );
			//simboliFormato.setEras( ere );
			format = new SimpleDateFormat();
			format.setDateFormatSymbols( simboliFormato );
			//format.setLenient(false); // ?
			//format.setTimeZone( TimeZone.getTimeZone("UTC") );
			/* Se non si setta la timezone viene usata quella del device, e alla fine non ci sono sfasamenti di ore.
			 * Settare la timezone influisce sul parsing della data in ingresso, nel senso che cambia il fuso orario del parsing,
			 * e bisogna poi settare la timezone anche formattando la data in uscita per non avere sfasamenti di ore.
			 */
		}
		
		// Prende una data Gedcom esatta e ci farcisce gli attributi della classe Data
		void scanna( String dataGc ) {
			//Locale locale = new Locale("en");
			
			// Riconosce se la data è B.C. e rimuove il suffisso 
			for( String suffix : suffissi ) {
				if( dataGc.endsWith(suffix) ) {
					negativa = true;
					dataGc = dataGc.substring(0, dataGc.indexOf(suffix)).trim();
					break;
				}
			}
			dataGc = dataGc.replaceAll("[\\\\_\\-|.,;:?'\"#^&*°+=~()\\[\\]{}]", " "); // tutti tranne '/'
			
			// Distingue una data con anno doppio 1712/1713 da una data tipo 17/12/1713
			if( dataGc.indexOf('/') > 0 ) {
				String[] tata = dataGc.split("[/ ]");
				//s.l(tata[tata.length-2]);
				if( tata.length > 1 && tata[tata.length-2].length() < 3 && U.soloNumeri( tata[tata.length-2] ) <= 12 )
					dataGc = dataGc.replace('/', ' ');
				else
					doppia = true;
			}
			
			for( String forma : paterni ) {
				format.applyPattern( forma );
				try {
					date = format.parse( dataGc );
				//	s.l( "'"+ format.toPattern() + "' -> \"" + dataGc +"\"" );
					break;
				} catch( ParseException e ) {
					//s.l( "'"+ format.toPattern() + "' " + e.getLocalizedMessage() );
				}
			}
			if( format.toPattern().equals(G_m_A) )
				format.applyPattern( G_M_A );
			if( format.toPattern().equals(m_A) )
				format.applyPattern( M_A );
			
			// Rende la data effettivamente negativa
			if( negativa ) cambiaEra();
		}
		
		// Se la data è antecedente alla nascita del Cristo
		/*boolean negativa() {
			if( date != null )
				return date.getTime() < tempoZero;
			return false;
		}*/

		// Rende la data BC oppure AD coerentemente con il boolean 'negativa'
		void cambiaEra() {
			if( date != null ) {
				// Tentativo di specchiare i millisecondi, parzialmente riuscito
				//long milli = tempoZero - ( date.getTime() - tempoZero ) - milliAnno - 24*60*60*1000;
				//date.setTime( milli );
				
				// La data viene riparsata cambiandogli l'era
				SimpleDateFormat sdf = new SimpleDateFormat(G_M_A + " G", Locale.US);
				//sdf.setTimeZone( TimeZone.getTimeZone("UTC") );
				String data = sdf.format(date);
				if( negativa ) // AD -> BC
					data = data.replace("AD", "BC");
				else // BC -> AD
					data = data.replace("BC", "AD");
				try {
					date = sdf.parse(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public String toString() {
			if( date != null ) {
				DateFormat sdf = new SimpleDateFormat("d MMM yyyy G HH:mm:ss", Locale.US);
				//sdf.setTimeZone( TimeZone.getTimeZone("UTC") ); // Setta la timezone dell'output, necessario se è stata settata anche nel parsing
				String txt = sdf.format(date);
				/*GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
				calendar.setTime(date);
				//txt += " " + calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
				//txt += calendar.isLeapYear(calendar.get(Calendar.YEAR)) ? " B." : ""; // Qualcosa non va nelle date BC, ma non importa
				//txt += "\t" + date.getTime(); // Millisecondi */
				return txt;
			} else
				return frase;
		}
	}
	
	// Riconosce il tipo di data e crea la classe Data
	public void analizza( String dataGc ) {

		dataGc = dataGc.trim();
		String dataGcMaiusc = dataGc.toUpperCase();
		
		// Riconosce i tipi diversi da 0 e converte la stringa in Data
		for( int t = 1; t < prefissi.length; t++  ) {
			if( dataGcMaiusc.startsWith(prefissi[t]) ) {
				tipo = t;
				if( t == 6 && dataGcMaiusc.contains("AND") ) { // BET... AND
					//s.l( dataGcMaiusc.indexOf("BET") +" "+ dataGcMaiusc.indexOf("AND") );
					if( dataGcMaiusc.indexOf("AND") > dataGcMaiusc.indexOf("BET")+4 )
						data1.scanna( dataGcMaiusc.substring( 4, dataGcMaiusc.indexOf("AND")-1 ));
					if( dataGcMaiusc.length() > dataGcMaiusc.indexOf("AND")+3 )
						data2.scanna( dataGcMaiusc.substring( dataGcMaiusc.indexOf("AND")+4 ));
				} else if( t == 7 && dataGcMaiusc.contains("TO") ) { // FROM... TO
					tipo = 9;
					if( dataGcMaiusc.indexOf("TO") > dataGcMaiusc.indexOf("FROM")+5 )
						data1.scanna( dataGcMaiusc.substring( 5, dataGcMaiusc.indexOf("TO")-1 ));
					if( dataGcMaiusc.length() > dataGcMaiusc.indexOf("TO")+2 )
						data2.scanna( dataGcMaiusc.substring( dataGcMaiusc.indexOf("TO")+3 ) );
				} else if( t == 10 ) { // Data frase tra parentesi
					//data1.scanna( dataGc.substring( 1, dataGc.indexOf(")") ) ); // Ripristina date del tipo 0 messe tra parentesi
					if( dataGc.endsWith(")") )
						frase = dataGc.substring( 1, dataGc.indexOf(")") );
					else
						frase = dataGc;
				} else if( dataGcMaiusc.length() > prefissi[t].length() ) // Altri prefissi seguiti da qualcosa
					data1.scanna( dataGcMaiusc.substring( prefissi[t].length() + 1 ) );
				/*else // Prefisso da solo
					data1.scanna( "" );*/
				break;
			}
		}
		// Rimane da provare il tipo 0, altrimenti diventa una frase
		if( tipo == 0 && !dataGc.isEmpty() ) {
			data1.scanna( dataGc );
			if( data1.date == null ) {
				frase = dataGc;
				tipo = 10;
			}
		}
	}
	

	// Scrive solo l'anno semplificato alla mia maniera
	public String scriviAnno() {
		String anno = "";
		if( data1.date != null && !data1.format.toPattern().equals(G_M) ) {
			DateFormat formatoAnno = new SimpleDateFormat("yyy");
			Date dateUnoCopy = (Date) data1.date.clone(); // Clona 'date' per poter cambiare l'anno
			if( data1.doppia )
				dateUnoCopy.setYear( data1.date.getYear() + 1 );
			anno = formatoAnno.format( dateUnoCopy );
			if( data1.negativa )
				anno = "-"+anno;
			if( tipo >= 1 && tipo <= 3 )
				anno += "?";
			if( tipo == 4 || tipo == 7 )
				anno += "→";
			if( tipo == 5 )
				anno = "←"+anno;
			if( tipo == 8 )
				anno = "→"+anno;
			if( (tipo == 6 || tipo == 9) && data2.date != null && !data2.format.toPattern().equals(G_M) ) {
				if( data2.doppia )
					data2.date.setYear( data2.date.getYear() + 1 );
				String anno2 = formatoAnno.format( data2.date );
				if( data2.negativa )
					anno2 = "-"+anno2;					
				if( !anno.equals(anno2) ) {
					if( !anno2.startsWith("-") && anno2.length() > 3
							&& anno2.substring(0,2).equals(anno.substring(0,2)) ) // se sono dello stesso secolo
						anno2 = anno2.substring( anno2.length()-2 ); // prende solo gli anni
					anno += (tipo==6 ? "~" : "→") + anno2;
				}
			}
		}
		return anno;
	}
}
