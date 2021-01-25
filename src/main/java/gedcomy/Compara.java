package gedcomy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.folg.gedcom.model.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Compara {

	public Compara() {
		try {
			/* aggiornaDate() riscritto con joda time, non mi piace
			DateTime dataTempo = new DateTime();
		    org.joda.time.DateTime nowJoda = new org.joda.time.DateTime();
		    DateTimeFormatter formatoData = DateTimeFormat.forPattern("d MMM yyyy").withLocale(Locale.ENGLISH);
		    dataTempo.setValue( formatoData.print(nowJoda).toUpperCase() );
		    dataTempo.setTime( nowJoda.toString("HH:mm:ss") );
		    s.l( dataTempo.getValue() +" - "+dataTempo.getTime() );*/
		    
			Date now = new Date(); // buon vecchio java.util
			s.l(now);
			String[] idZone = { "UTC", "Europe/Rome", "Asia/Kolkata", "Pacific/Chatham", "America/Sao_Paulo", "Pacific/Marquesas" };
			// trova l'offset cioè la differenza di ore da GMT, un numero tipo: 0, 2, -3, 530, 1245, -930...
			for( String idZona : idZone ) {
			    DateTimeZone dtz = DateTimeZone.forID( idZona );
			    DateTimeZone.setDefault( dtz ); 
			    DateTimeZone zona = DateTimeZone.getDefault();
			    //DateTime now = dtf1.parseDateTime("30Jul2013");	//.withZone( zona );
			    int millisecondi = zona.getOffset( now.getTime() );
			    int tempo = millisecondi / 3600000;
			    int minuti = millisecondi / 60000 % 60;
			    if( minuti != 0 )
			    	tempo = tempo * 100 + minuti;
			    s.l( tempo );
			}
		    
			// Riconduce una serie di date planetarie al fuso italiano per capire se sono posteriori alla data di condivisione
			TimeZone.setDefault( TimeZone.getTimeZone("Europe/Rome") );
			SimpleDateFormat formatoDataId = new SimpleDateFormat( "yyyyMMddHHmmss", Locale.ENGLISH );
			Date dataCondivisione = formatoDataId.parse( "20190920141500" );
			s.l( "Data condivisione:\t"+dataCondivisione);
			SimpleDateFormat formato = new SimpleDateFormat( "d MMM yyyy HH:mm:ss", Locale.ENGLISH );
			String[] date = { "20 SEP 2019 09:00:00", "20 SEP 2019 14:15:00", "21 SEP 2019 01:15:00", "20 SEP 2019 14:45:00", "20 SEP 2019 10:00:00" };
			String[] zone = { "America/Sao_Paulo", "Europe/Rome", "Pacific/Chatham", "Europe/Rome", "America/Sao_Paulo" };
			for( int i = 0; i < date.length; i++ ) {
				TimeZone fuso = TimeZone.getTimeZone(zone[i]);
				formato.setTimeZone( fuso );
				Date dataOggetto = formato.parse( date[i] );
				int millisecondi = fuso.getOffset(dataOggetto.getTime());
				long oreSfaso = TimeUnit.MILLISECONDS.toHours( millisecondi );
				int minuti = millisecondi / 60000 % 60;
				s.l( date[i] +"\t"+ dataOggetto+"\t"+dataOggetto.after(dataCondivisione)+"\t"+ (oreSfaso>0?"+":"")+oreSfaso+","+minuti +"\t"+fuso.getID()+" - "+fuso.getDisplayName() );
			}
			
			//for( String tzn : TimeZone.getAvailableIDs() ) s.l(tzn);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
