package exoplanet;


import exoplanet.groundstation.Bodenstation;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

//		int portPlanet = 8051;
//		Datenbankport 3306
//		String hostnamePlanet = "Localhost";
//		String hostnameStation = "Localhost";
		int portStation = 3141;
		
		Bodenstation bodenstation = new Bodenstation(portStation);
		
		System.out.println("Gebe 'showCommands' ein um alle Befehle zu sehen");
		bodenstation.showCommands();
		
		bodenstation.run();
	}

}
