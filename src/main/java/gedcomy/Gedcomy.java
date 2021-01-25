package gedcomy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.folg.gedcom.model.Change;
import org.folg.gedcom.model.DateTime;
import org.folg.gedcom.model.Extensions;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Media;
import org.folg.gedcom.model.MediaContainer;
import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.model.Repository;
import org.folg.gedcom.model.Source;
import org.folg.gedcom.model.Visitor;
import org.folg.gedcom.parser.JsonParser;
import org.folg.gedcom.parser.ModelParser;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXParseException;
import gedcomy.visita.TrovaPila;
import gedcomy.visita.TrovaPila.Passo;

public class Gedcomy {
	
	//public static void main(String[] args) throws Exception {
	public void faiCose(Gedcom gc) {

		/*String percorso = "..\\esempi\\famiglia.ged";
		if( ! new File(percorso).isFile() )
			percorso = "..\\".concat(percorso);	// per avviarlo da PowerShell*/
		
		//salvaJson( percorso );
		//Gedcom gc = openJson( percorso );
		
		for( Person p : gc.getPeople() )
			s.l( U.essenza( p ) );/**/
		
		/*Note nota = gc.getPerson("I1").getNames().get(2).getSourceCitations().get(0).getNotes().get(0)
				.getSourceCitations().get(0).getNotes().get(1);
		Note nota = gc.getPerson("I1").getEventsFacts().get(1).getSourceCitations().get(0).getNotes().get(1);
		TrovaPila trovaPila = new TrovaPila(nota);
		gc.accept( trovaPila );
		s.l("Bava:");
		for( TrovaPila.Passo pas : trovaPila.traccia )
			s.l( pas.tag +" "+ pas.oggetto.getClass().getName() );*/
		
		//new Confronto( gc );
		
		VisitatoreSesso visitore = new VisitatoreSesso();
		Person p = gc.getPerson("I1");
		p.accept( visitore );	// ok
		
		VisitatoreListaMedia visitaMedia = new VisitatoreListaMedia();
		gc.accept( visitaMedia );
		for( Media m : visitaMedia.listaMedia ) {
			s.l( m.getId() );
			s.l( m.getTitle() );
			s.l( m.getFile() );
			s.l("--------");
		}/**/
		
		/* Estensioni
		Person p = gc.getPerson( "I1" );
		Source f = gc.getSource("S9");
		Extensions estensi = new Extensions();
		estensi.setExtensions( f.getExtensions() );
		s.l( estensi.getExtensions().toString() );
		estensi.put( "ALTRO",  "ciao"  );
		p.setExtensions( estensi.getExtensions() );
		for( String est : estensi.getKeys() ) {
			s.p( "'" + est +"'  " );
		}
		p.putExtension( "ancora", "bello" );
		p.putExtension( "ancora", 12 );
		s.l( "\n"+ p.getExtensions() );
		s.l( p.getExtension("ALTRO") );*/
		
		
		/* Data cambiamento
		DateTime dataTempo = new DateTime();
		Date now = new Date();
		/*SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH); equivalente e più lungo
		String data = sdf.format(now).toUpperCase();
		sdf = new SimpleDateFormat("HH:mm:ss");
		String tempo = sdf.format(now);
		dataTempo.setValue( data );		
		dataTempo.setTime( tempo );
		s.l(now+"\n"+data+" "+tempo);
		* /
		dataTempo.setValue( String.format(Locale.ENGLISH,"%te %<Tb %<tY",now) );
		dataTempo.setTime( String.format("%tT",now) );
		Change chan = new Change();
		chan.setDateTime(dataTempo);
		Person p = new Person();
		p.setChange(chan);
		s.l( p.getChange().getDateTime().getValue() +" - "+ p.getChange().getDateTime().getTime() );
		*/
		
		//Globale.gc = gc;
		
		/*Diagramma d = new Diagramma( gc );
		d.disegna( "I1" );
		d.disegna( "I2" );
		d.disegna( "I5" );
		d.disegna( "I1024" );*/
		
		//U.trovaRadice( gc );
		//new InfoGedcom( gc );
		//new Statistiche( gc );
		//new Anagrafe( gc, 1 );
		//new Galleria(gc);
		//new Biblioteca( gc, 0 );
		//new Magazzino( gc, 3 );
		/*for( Repository arc : gc.getRepositories() ) {
			new Archivio( gc, arc.getId() );
		}*/
		//new Podio( gc );
		//s.l( gc.getSubmitter().getName() );
		//new Compara();
		//new TrovaLuogo();
		
		/*Individuo p1 = new Individuo( gc, "I5" );
		Individuo p2 = new Individuo( gc, "I14" );
		Individuo p3 = new Individuo( gc, "I592" );
		p1.eventi();
		p2.eventi();
		p3.eventi();
		p1.famiglia();
		p2.famiglia();
		p3.famiglia();
		p1.media();*/
		
		/*new Fonte( gc, "S1" );
		new Fonte( gc, "S9" );
		new Fonte( gc, "S31" );
		new Fonte( gc, "S42" );
		new Fonte( gc, "S238" );*/
		
		/*String idEliminando = "I1";
		Person uno = gc.getPerson(idEliminando);
		elimina(gc, idEliminando, uno);
		s.l(gc.getPeople().size()+" "+U.essenza(uno));*/
 	}

	public Gedcom apriGedcom(File gedcomFile) throws SAXParseException, IOException {
		ModelParser modelParser = new ModelParser();
		return modelParser.parseGedcom(gedcomFile);
	}
	
	static void elimina(Gedcom gc,  String idEliminando, Person eliminando) {
		List<Person> gente = gc.getPeople();
		//Person eliminando = gc.getPerson( idEliminando );
		s.l(gente.size()+" "+U.essenza(eliminando));
		gente.remove( eliminando );
		eliminando = null;
		//gc.createIndexes();
		s.l(gente.size()+" "+U.essenza(eliminando));
	}

	static void salvaJson( String percorso ) throws SAXParseException, IOException {
		File gcFile = new File(percorso);	
		ModelParser mp = new ModelParser();
		Gedcom gc = mp.parseGedcom(gcFile);
		gc.createIndexes();	// crea gli indici per poter ricavare i dati a partire dagli id tipo 'I1' 'F23' 'S456'		
		// Percorso della cartella da cui ha caricato il gedcom
		String percorsoCartella = new File( gcFile.getCanonicalPath() ).getParent();
		s.l( "percorsoCartella: " + percorsoCartella );
		//U.aggiornaTag( gc.getHeader(), "_CARTELLA", percorsoCartella );
		Globale.preferenze.put( "main_dir", percorsoCartella );
		// Scrive il Json
		PrintWriter pw = new PrintWriter( percorso + ".json" );
		JsonParser jp = new JsonParser();
		pw.print( jp.toJson(gc) );
		pw.close();
	}

	/* precedente OK
	static Gedcom openJson( String path ) throws IOException {
	    File file = new File( path + ".json" );
	    FileReader reader = new FileReader(file);
        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        String content = new String(chars).trim(); // trim evita l'errore di lettura del fine file
        reader.close();
        JsonParser jp = new JsonParser();
        return jp.fromJson(content);
	}*/
	static Gedcom openJson(String path) throws IOException {
		String content = FileUtils.readFileToString(new File(path + ".json"), "UTF-8");
		return new JsonParser().fromJson(content);
	}
}
