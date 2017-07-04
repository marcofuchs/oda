package kefu;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.ParseException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marco
 */
public class Isochrone {
    /**
     * @param args command line arguments
     * @throws java.sql.SQLException
     * @throws com.vividsolutions.jts.io.ParseException
     */
    public static void main(String[] args) throws SQLException, ParseException {
        if (args.length != 7) {
            System.out.println("FEHLER: Anzahl der Argumente passt nicht. Programm wird beendet.");
            return;
        }

        int lat = (int) (Double.parseDouble(args[2]) * 1000000);
        int lon = (int) (Double.parseDouble(args[3]) * 1000000);
        int mins = Integer.parseInt(args[4]);
        int lowerBound = Integer.parseInt(args[5]);
        int upperBound = Integer.parseInt(args[6]);

        System.out.println("Generiere NavData...");

        IsochroneCalculator calculator = new IsochroneCalculator(args[1]);
        if (!calculator.initializeNavData()) {
            // NavData konnte nicht initialisiert werden.
            return;
        }

        List<Coordinate> coords = calculator.createIsochrone(lat, lon, (double) mins);
        
        if (coords == null) {
            return;
        }

        ArrayList<double[]> concaveHull = CoordCalculator.createConcaveHull(coords);

        if (concaveHull == null) {
            // Maximal eine einzige Koordinate wurde gefunden
            // => Programm kann nicht erfolgreich beendet werden, da mindestens zwei benötigt werden,
            //    bzw drei, wenn man auch eine Fläche erhalten möchte.
            return;
        }

        DbConnection geoServDbConn = new DbConnection(args[0]);
        if (!geoServDbConn.connect()) {
            // Keine Datenbankverbindung => Wir können nicht weitermachen.
            return;
        }

        List<Location> reachableLocations = CoordCalculator.calculateReachableLocations(geoServDbConn, concaveHull, lowerBound, upperBound);

        geoServDbConn.close();

        Writer.writePoints(lat, lon, mins, concaveHull, reachableLocations, lowerBound, upperBound);

        System.out.println("Berechnungen wurden abgeschlossen, Programm wurde beendet.");
    }
}
