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
import gedcomy.constants.Format;
import gedcomy.constants.Kind;

public class Datatore {

	Data data1;
	Data data2;
	String frase; // Quella che andrà tra parentesi
	Kind kind;
	static String[] mesiGedcom = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
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
			
			for( String forma : Format.PATTERNS ) {
				format.applyPattern( forma );
				try {
					date = format.parse( dataGc );
				//	s.l( "'"+ format.toPattern() + "' -> \"" + dataGc +"\"" );
					break;
				} catch( ParseException e ) {
					//s.l( "'"+ format.toPattern() + "' " + e.getLocalizedMessage() );
				}
			}
			if( isFormat(Format.D_m_Y) )
				format.applyPattern( Format.D_M_Y );
			if( isFormat(Format.m_Y) )
				format.applyPattern( Format.M_Y );
			
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
				SimpleDateFormat sdf = new SimpleDateFormat(Format.D_M_Y + " G", Locale.US);
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

		boolean isFormat(String format) {
			return this.format.toPattern().equals(format);
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
	public void analizza(String dataGc) {

		dataGc = dataGc.trim();
		if( dataGc.isEmpty() ) {
			kind = Kind.EXACT;
			return;
		}
		// Riconosce i tipi diversi da EXACT e converte la stringa in Data
		String dataGcMaiusc = dataGc.toUpperCase();
		for( int i = Kind.APPROXIMATE.ordinal(); i < Kind.values().length; i++ ) {
			Kind k = Kind.values()[i];
			if( dataGcMaiusc.startsWith(k.prefix) ) {
				kind = k;
				if( k == Kind.BETWEEN_AND && dataGcMaiusc.contains("AND") ) {
					//s.l( dataGcMaiusc.indexOf("BET") +" "+ dataGcMaiusc.indexOf("AND") );
					if( dataGcMaiusc.indexOf("AND") > dataGcMaiusc.indexOf("BET") + 4 )
						data1.scanna(dataGcMaiusc.substring(4, dataGcMaiusc.indexOf("AND") - 1));
					if( dataGcMaiusc.length() > dataGcMaiusc.indexOf("AND") + 3 )
						data2.scanna(dataGcMaiusc.substring(dataGcMaiusc.indexOf("AND") + 4));
				} else if( k == Kind.FROM && dataGcMaiusc.contains("TO") ) {
					kind = Kind.FROM_TO;
					if( dataGcMaiusc.indexOf("TO") > dataGcMaiusc.indexOf("FROM") + 5 )
						data1.scanna(dataGcMaiusc.substring(5, dataGcMaiusc.indexOf("TO") - 1));
					if( dataGcMaiusc.length() > dataGcMaiusc.indexOf("TO") + 2 )
						data2.scanna(dataGcMaiusc.substring(dataGcMaiusc.indexOf("TO") + 3));
				} else if( k == Kind.PHRASE ) { // Phrase date between parenthesis
					// data1.scanna( dataGc.substring( 1, dataGc.indexOf(")") ) ); // Ripristina date del tipo 0 messe tra parentesi
					if( dataGc.endsWith(")") )
						frase = dataGc.substring(1, dataGc.indexOf(")"));
					else
						frase = dataGc;
				} else if( dataGcMaiusc.length() > k.prefix.length() ) // Altri prefissi seguiti da qualcosa
					data1.scanna(dataGcMaiusc.substring(k.prefix.length() + 1));
				/*else // Prefisso da solo
					data1.scanna( "" );*/
				break;
			}
		}
		// Rimane da provare il kind EXACT, altrimenti diventa una frase
		if( kind == null ) {
			data1.scanna(dataGc);
			if( data1.date != null ) {
				kind = Kind.EXACT;
			} else {
				frase = dataGc;
				kind = Kind.PHRASE;
			}
		}
	}
	
	/** Write the short date, adorned with some modifiers
	 * @param yearOnly The year only or the whole date with 3-chars month
	 * @return The date well written
	 */
	public String writeDate(boolean yearOnly) {
		String text = "";
		if( data1.date != null && !(data1.isFormat(Format.D_M) && yearOnly) ) {
			Locale locale = Locale.getDefault();
			DateFormat dateFormat = new SimpleDateFormat(yearOnly ? Format.Y : data1.format.toPattern(), locale);
			Date dateOne = (Date)data1.date.clone(); // Cloned so the year of a double date can be modified without consequences
			if( data1.doppia )
				dateOne.setYear(data1.date.getYear() + 1);
			text = dateFormat.format(dateOne);
			if( data1.negativa )
				text = "-" + text;
			if( kind == Kind.APPROXIMATE || kind == Kind.CALCULATED || kind == Kind.ESTIMATED )
				text += "?";
			else if( kind == Kind.AFTER || kind == Kind.FROM )
				text += "→";
			else if( kind == Kind.BEFORE )
				text = "←" + text;
			else if( kind == Kind.TO )
				text = "→" + text;
			else if( (kind == Kind.BETWEEN_AND || kind == Kind.FROM_TO) && data2.date != null ) {
				Date dateTwo = (Date)data2.date.clone();
				if( data2.doppia )
					dateTwo.setYear(data2.date.getYear() + 1);
				dateFormat = new SimpleDateFormat(yearOnly ? Format.Y : data2.format.toPattern(), locale);
				String second = dateFormat.format(dateTwo);
				if( data2.negativa )
					second = "-" + second;
				if( !second.equals(text) ) {
					if( !data1.negativa && !data2.negativa ) {
						if( !yearOnly && data1.isFormat(Format.D_M_Y) && data1.format.equals(data2.format)
								&& dateOne.getMonth() == dateTwo.getMonth() && dateOne.getYear() == dateTwo.getYear() ) { // Same month and year
							text = text.substring(0, text.indexOf(' '));
						} else if( !yearOnly && data1.isFormat(Format.D_M_Y) && data1.format.equals(data2.format)
								&& dateOne.getYear() == dateTwo.getYear() ) { // Same year
							text = text.substring(0, text.lastIndexOf(' '));
						} else if( !yearOnly && data1.isFormat(Format.M_Y) && data1.format.equals(data2.format)
								&& dateOne.getYear() == dateTwo.getYear() ) { // Same year
							text = text.substring(0, text.indexOf(' '));
						} else if( (yearOnly || (data1.isFormat(Format.Y) && data1.format.equals(data2.format))) // Two years only
								&& ((text.length() == 4 && second.length() == 4 && text.substring(0, 2).equals(second.substring(0, 2))) // of the same century
								 || (text.length() == 3 && second.length() == 3 && text.substring(0, 1).equals(second.substring(0, 1)))) ) {
							second = second.substring(second.length() - 2); // Keeps the last two digits
						}
					}
					text += (kind == Kind.BETWEEN_AND ? "~" : "→") + second;
				}
			}
		}
		return text;
	}

	R R = new R();
	String getString(int id) {
		return R.texts[id];
	}

	// Plain text of the date in local language
	public String writeDateLong() {
		String txt = "";
		int pre = 0;
		switch( kind ) {
			case APPROXIMATE: pre = R.string.approximate; break;
			case CALCULATED: pre = R.string.calculated; break;
			case ESTIMATED: pre = R.string.estimated; break;
			case AFTER: pre = R.string.after; break;
			case BEFORE: pre = R.string.before; break;
			case BETWEEN_AND: pre = R.string.between; break;
			case FROM:
			case FROM_TO: pre = R.string.from; break;
			case TO: pre = R.string.to;
		}
		if( pre > 0 )
			txt = getString(pre) + " ";
		if( data1.date != null ) {
			Locale locale = Locale.getDefault();
			DateFormat dateFormat = new SimpleDateFormat(data1.format.toPattern().replace("MMM","MMMM"), locale);
			txt += dateFormat.format(data1.date);
			// Uppercase initial
			if( kind == Kind.EXACT && data1.isFormat(Format.M_Y) )
				txt = txt.substring(0, 1).toUpperCase() + txt.substring(1);
			if( data1.doppia ) {
				String year2 = String.valueOf(data1.date.getYear() + 1901);
				txt += "/" + year2.substring(year2.length() - 2);
			}
			if( data1.negativa )
				txt += " B.C.";
			if( kind == Kind.BETWEEN_AND || kind == Kind.FROM_TO ) {
				txt += " " + getString(kind == Kind.BETWEEN_AND ? R.string.and : R.string.to).toLowerCase();
				if( data2.date != null ) {
					dateFormat = new SimpleDateFormat(data2.format.toPattern().replace("MMM", "MMMM"), locale);
					txt += " " + dateFormat.format(data2.date);
					if( data2.doppia ) {
						String year2 = String.valueOf(data2.date.getYear() + 1901);
						txt += "/" + year2.substring(year2.length() - 2);
					}
					if( data2.negativa )
						txt += " B.C.";
				}
			}
		} else if( frase != null ) {
			txt = frase;
		}
		return txt;
	}

	// Kinds of date that represent a single event in time
	boolean isSingleKind() {
		return kind == Kind.EXACT || kind == Kind.APPROXIMATE || kind == Kind.CALCULATED || kind == Kind.ESTIMATED;
	}
}
