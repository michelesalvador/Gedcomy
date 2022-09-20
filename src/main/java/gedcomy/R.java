package gedcomy;

public class R {

	Strings string;

	public R() {
		string = new Strings();
	}

	class Strings {
		int approximate = 1;
		int calculated = 2;
		int estimated = 3;
		int after = 4;
		int before = 5;
		int between_and = 6; // <--------- NEW!
		int from = 7;
		int to = 8;
		int from_to = 9;
		int between = 10;
		int and = 11;
	}
	
	String[] texts = {"", "Approximate", "Calculated", "Estimated", "After", "Before", "Between… and", "From", "To", "From… to",
			"Between", "And" // <--------- NEW!
	};
}

