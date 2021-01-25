package gedcomy;

import java.util.prefs.Preferences;
import org.folg.gedcom.model.Gedcom;

class Globale {
	static Gedcom gc;
	static String individuo;// = U.trovaRadice(gc).getId();
	static Preferences preferenze = Preferences.userRoot().node("/lab/gedcomy");
}
