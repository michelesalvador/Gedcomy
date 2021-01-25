// Scrivi in breve

package gedcomy;

public class s {
	
	public static void l( Object... objects) {
		String str = "";
		for(Object obj : objects)
			str += obj + " ";
		System.out.println(str);
	}
	
	public static void p( Object parola ) {
		System.out.print( parola );
	}
	
}
