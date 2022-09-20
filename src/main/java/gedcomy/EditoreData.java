package gedcomy;

import java.util.Date;
import java.util.Locale;
import gedcomy.Datatore.Data;
import gedcomy.constants.Kind;

class EditoreData {
	
	Datatore datatore;
	Data data1;
	Data data2;
	
	EditoreData( String dataGc ) {
		s.l( "\"" + dataGc + "\"" );

		datatore = new Datatore( dataGc );
		data1 = datatore.data1;
		data2 = datatore.data2;

		// Produce un valore fittizio per data2 se non è già stata scannata prima
		/*if( data2 == null || data2.date == null ) {
			//data2 = scanna( String.valueOf( data.date.getYear() + 1910 ) );
			data2.scanna( "" );
		}*/
		
		// Trasfromazione della data
		//data1.date.setDate( 15 );
		//data1.date.setMonth( 3 );
		//data1.date.setYear( 2505 - 1900 );
		//tipo = 9;	if( data2 == null ) data2 = scanna( "0" );
		
		genera();
	}
	
	// Ricostruzione della stringa
	void genera() {
		String rifatta;
		if( datatore.kind == Kind.EXACT ) {
			rifatta = rifai(data1);
		} else if( datatore.kind == Kind.BETWEEN_AND )
			rifatta = "BET " + rifai(data1) + " AND " + rifai(data2);
		else if( datatore.kind == Kind.FROM_TO )
			rifatta = "FROM " + rifai(data1) + " TO " + rifai(data2);
		else if( datatore.kind == Kind.PHRASE )
			//rifatta = datatore.frase;
			rifatta = "(" + datatore.frase + ")"; // mette le parentesi intorno a una data-frase
		else
			rifatta = datatore.kind.prefix + " " + rifai(data1);

		s.p( datatore.kind +": " );
		if( data1 != null && data1.date != null )
			s.p( data1.date.getDate() + " " + Datatore.mesiGedcom[data1.date.getMonth()] + " " + (data1.date.getYear()+1900) + (data1.negativa?" n.":"") + (data1.doppia?" d.":"") );
		else
			s.p( "null" );
		if( data2 != null && data2.date != null )
			s.l( " - " + data2.date.getDate() + " " + Datatore.mesiGedcom[data2.date.getMonth()] + " " + (data2.date.getYear()+1900) + (data2.negativa?" n.":"") + (data2.doppia?" d.":"") );
		else
			s.l( " - null");
		s.l("<" + rifatta + "> <" + datatore.writeDate(true) + "> <" + datatore.writeDate(false) + ">");
		s.l("<" + datatore.writeDateLong() + ">");
		//s.l( "[" + data1.format.toPattern() +"]  ["+ data2.format.toPattern() +"]" );
		
		/* Calendario per sapere il numero di giorni in un mese
		Calendar calenda = new GregorianCalendar( data1.date.getYear()+1900, data1.date.getMonth(), data1.date.getDate() );
		s.l( calenda.get(Calendar.DAY_OF_MONTH) + "  " + calenda.get(Calendar.MONTH) + "  " + calenda.get(Calendar.YEAR));
		s.l( calenda.getActualMaximum(Calendar.DAY_OF_MONTH) );*/
		
		s.l( "-------" );
	}
	
	// Scrive la singola data esatta in base al formato
	String rifai( Data data ) {
		String fatta = "";
		if( data.date != null ) {
			// Date con l'anno doppio
			if( data.doppia ) {
				//String secondoAnno = String.valueOf(data.date.getYear()+1901);
				Date unAnnoDopo = new Date();
				unAnnoDopo.setYear( data.date.getYear() + 1 );
				String secondoAnno = String.format( Locale.ENGLISH, "%tY", unAnnoDopo );
				//s.l(secondoAnno+" "+secondoAnno.length());
				fatta = data.format.format( data.date ) +"/"+ secondoAnno.substring( 2 );
			} else // Le altre date normali
				fatta = data.format.format( data.date );
		}
		/*if( fatta.endsWith(Datatore.ere[1]) )
			fatta = fatta.replace(Datatore.ere[1], "").trim();*/
		if( data.negativa )
			fatta += " B.C.";
		return fatta;
	}
}
