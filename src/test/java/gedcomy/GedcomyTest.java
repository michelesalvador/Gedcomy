package gedcomy;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Calendar;

import org.folg.gedcom.model.ChildRef;
import org.folg.gedcom.model.Family;
//import org.folg.gedcom.model.DataEvent;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.ParentFamilyRef;
import org.folg.gedcom.model.ParentRelationship;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Source;
import org.folg.gedcom.model.SourceCitation;
import org.folg.gedcom.model.SourceCitation.DataTagContents;
import org.folg.gedcom.parser.ModelParser;
//import org.folg.gedcom.model.SourceData;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.PeriodType;
import org.junit.Test;
import org.xml.sax.SAXParseException;

public class GedcomyTest {
	
	@Test
	public void test() throws Exception {
		pedigree();
		//sourceData();
		//utilDate();
		//testaDate();
		//java8Time();
	}

	private void pedigree() {
		Gedcom gedcom = open("pedigree.ged");
		Family family = gedcom.getFamily("F1");

		/* FatherRelationship e MotherRelationship corrispondono ai tag non standard _FREL e _MREL
		for(ChildRef childRef: family.getChildRefs()) {
			ParentRelationship parentRelationship = new ParentRelationship();
			parentRelationship.setValue("???");
			childRef.setFatherRelationship(parentRelationship);
			childRef.setMotherRelationship(parentRelationship);
			s.l(childRef.getFatherRelationship().getValue(), childRef.getMotherRelationship().getValue());
		}*/

		// Relationship type
		for( Person child : family.getChildren(gedcom) ) {
			for( ParentFamilyRef parentFamilyRef : child.getParentFamilyRefs() ) {
				parentFamilyRef.getRef();
				s.l(parentFamilyRef.getRelationshipType());
			}
		}
	}

	private Gedcom open(String fileName) {
		try {
			URL url = ClassLoader.getSystemResource(fileName);
			String path = URLDecoder.decode(url.getFile(), "UTF-8"); // per sostituire i %20 con normali spazi
			File gedcomFile = new File(path);
			ModelParser modelParser = new ModelParser();
			Gedcom gedcom = modelParser.parseGedcom(gedcomFile);
			gedcom.createIndexes();
			return gedcom;
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	void java8Time() {
		// Il nuovo Java 8 Time
		LocalDate date = LocalDate.of(2014, Month.SEPTEMBER, 1);
		s.l(date.getDayOfMonth(), date.getMonth(), date.getYear()); // 2014
		
		String dataGc = "mar 1235";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM y");
		YearMonth annoMese = YearMonth.parse( dataGc, formatter);
		s.l( annoMese, formatter.format(annoMese) );
		
		dataGc = "31 mar 235 BC";
		formatter = DateTimeFormatter.ofPattern("d MMM y G");
		LocalDate dataLocale = LocalDate.parse( dataGc, formatter);
		s.l( dataLocale, dataLocale.getEra() );  // default ISO_LOCAL_DATE
        s.l( formatter.format( dataLocale ) );
		
		String expectedDate = new GregorianCalendar(2018, 0, 0).toZonedDateTime()
			      .format( DateTimeFormatter.ofPattern("d MMM uuuu") );
		s.l("28 Jul 2018  " + expectedDate  );
	}
	
	void utilDate() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("", Locale.US);
		format.setTimeZone( TimeZone.getTimeZone("UTC") ); // Setta la timezone dell'otput, altrimenti è quella del computer
		format.applyPattern("d MMM yyyy G HH:mm:ss");
		Date date = new Date();
		s.l( format.format(date), date.getTime() );
		date = new Date(0);
		s.l( format.format(date), date.getTime() );
		try {
			date = format.parse( "1 Jan 1 AD 00:00:00" );
			s.l( format.format(date), date.getTime() );
			//date = format.parse( "23 Apr 123 BC 12:30:30" );
		} catch (ParseException e) {
			e.printStackTrace();
		}
	/*	s.l( format.format(date) );
		date.setYear( 50  );
		s.l(date);
		date.setYear(-50);
		s.l(date);
		date.setYear( -1899 );
		s.l( date.getTime() );
		s.l( format.format(date) );
		s.l( format.format(new Date(Long.MIN_VALUE)) );*/
		// Anni bisestili
		for( int i=1; i <= 31; i++ ) {
			/*String anno = Math.abs(i) + (i <= 0 ? " BC" : " AD");
			SimpleDateFormat sdf = new SimpleDateFormat("yyy G", Locale.US);
			sdf.setTimeZone( TimeZone.getTimeZone("UTC") );
			Date date = sdf.parse(anno);
			sdf.applyPattern("d MMM y G");
			String txt = sdf.format(date);
			GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
			calendar.setTime(date);
			txt += "  " + calendar.get(Calendar.YEAR);
			txt += " " + calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			txt += calendar.isLeapYear(calendar.get(Calendar.YEAR)) ? " B." : "";
			txt += "\t" + date.getTime(); // Millisecondi
			date.ne
			s.l(txt);*/
			String data = i + " JAN 1581";
			new Datatore(data);
			//s.l(datatore.data1);
		}
	}
	
	void testaDate() {
		// Date varie da comprendere
		String[] dateGc = {
			"", " 1986 ", "MAr 78 ", " abt  29 feb 2000 ", "7 AUG", " APR  ", 
			"BEF", "  Bet 29 feb 005 and 5 may 123 ", "BET 13 NOV 1333", "BET 22 FEB 1500 AND", "BET 1 AUG 2005 AND quaquaqua",
			"bet 800 and 15 may 805", "bet 1701/02 and 1756/1757", "BET jun 2000 and 19.1999", "BET 7 AUG 1974 AND 31 DEC 1974", "BET 11 12 1975 and 25 12 1975",
			"Bet 1550 bc and 1510 B.C.", "BET 1 AUG 200 BC AND 23 AUG 200 BC", "bet 2000 bc and 2000", "BET 500 and 5010",
			"from 34/1234", "FROM 1111 TO", "   FROM 1001 to 1001  ", "to 15 1544", "FROM 33 DEC 1099 TO -2 jan 1101",
			"AFT cippirimerlo...", "(JAn 1458)", "BE 7 Jan 1913", "(solo una parentesi", "  (  Vera frase ) ",
			"15/05/1970", "12/1713", "14\"4.1970",
			"3 feb 1715/16", "jan 1699/00 ", "12 1440/41", "from DEC 1699/700 to 15 mar 1752/", "jan 11/3", "4 mar 05/06", "TOAUG 1595/96",
			"1 JAN 1", "0 B.C.", "1BC", "apr 34 BC", "1-2-123B.C.", "12 AUG 3456BCE", "-039 BCE",
			"ABT 3BC", "CAL 43 BC", "FROM aug 123b.C.", "TO -1 B.C.", "BET 10BCE AND 5 BCE", "BET 1-Apr 0125 BCE AND 003BC",
			"21 APR 753 B.C.", "1/2 BC", "apr 10/11 BC", "6 aug 100/101 BC", "from 1000/01B.C. TO 020/21 BCE", "ABT 28 NOV 050/51 B.C."
		};
		for( String dataGc : dateGc )
			new EditoreData( dataGc );
	}

	void sourceData() throws SAXParseException, IOException, URISyntaxException {
		Gedcomy gedcomy = new Gedcomy();
	   // URL gedcomUrl = getClass().getClassLoader().getResource("Source DATA.ged");
		//System.out.println( getClass().getResource(".").getPath()); 
	   // assertNotNull(gedcomUrl);
		URL url = ClassLoader.getSystemResource("Source DATA.ged");
		String percorso = URLDecoder.decode( url.getFile(), "UTF-8" ); // per sostituire i %20 con normali spazi
		s.l("percorso " + percorso);
	    File gedcomFile = new File( percorso );
		Gedcom gc = gedcomy.apriGedcom(gedcomFile);
		assertNotNull(gc);
		gc.createIndexes();
		
		Person person = gc.getPerson("I1");
		assertNotNull(person);
		assertNotNull(person.getSourceCitations());
		assertEquals(person.getSourceCitations().size(), 1);
		
		SourceCitation citation = person.getSourceCitations().get(0);
		assertNotNull(citation);
		assertEquals(citation.getDate(), "12 feb 1900");
		assertEquals(citation.getText(), "Text from source\n" + "Second line of text from source");
		/*DataTagContents contents = citation.getDataTagContents();
		assertNotNull(contents);
		s.l(contents.DATE);
		s.l(contents.TEXT);*/
		
		Source source = gc.getSource("S1");
		assertNotNull(source);
	/*	SourceData data = source.getSourceData();
		assertNotNull(data);
		assertEquals(data.getAgency(), "Madison County Court, State of Connecticut");*/
		/*DataEvent event = data.getDataEvents(); TODO da mettere a posto con la lista dei Data Event
		assertNotNull(event);
		assertEquals(event.getValue(), "BIRT, DEAT, MARR");
		assertEquals(event.getPlace(), "Madison, Connecticut");
		assertEquals(event.getDate(), "FROM Jan 1820 TO DEC 1825");*/
	/*	Note nota = data.getNotes().get(0);
		assertNotNull(nota);
		assertEquals(nota.getValue(), "Note to source data\n" + "on multiple lines");
		
		s.l( data.getExtensions());*/
	}
}
