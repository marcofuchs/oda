/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.ParseException;
import fu.util.ConcaveHullGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Erstellt die konkave Hülle und kann die Suche nach Geschäften initiieren.
 *
 * @author marco
 */
public class CoordCalculator {
    private static final GeometryFactory geomfact = new GeometryFactory();

    /**
     * Erzeugt aus den gegebenen Koordinaten eine konkave Hülle
     *
     * @param coords Alle Koordinaten
     * @return eine Liste von Punkten, die die Ecken der konkaven Hülle
     * darstellen
     */
    public static ArrayList<double[]> createConcaveHull(List<Coordinate> coords) {
        if (coords.size() <= 1) {
            // Kein Polygon => Generierung der konkaven Hülle würde scheitern.
            System.out.println("FEHLER: Es wurde maximal ein einziger Punkt auf der Karte gefunden.");
            System.out.println("Das ist zu wenig, um ein Polygon zu erzeugen und das Programm erfolgreich auszuführen.");
            System.out.println("Programm wird beendet.");
            return null;
        }

        // Koordinaten in die benötigte Form (ArrayList aus Double-Arrays) umwandeln
        ArrayList<double[]> latlons = new ArrayList<>();
        for (Coordinate coord : coords) {
            double[] ll = new double[2];
            ll[0] = (double) coord.x / 1000000;
            ll[1] = (double) coord.y / 1000000;
            latlons.add(ll);
        }

        if (coords.size() == 2) {
            // Kein Polygon => Generierung der konkaven Hülle würde scheitern.
            // Aber wir können mit der Linie weiterarbeiten.
            System.out.println("Fertig.");
            return latlons;
        }

        // Berechnung der konkaven Hülle
        double alpha = 0.03;
        System.out.println("Berechne die konkave Huelle über " + latlons.size() + " Punkte...");
        ArrayList<double[]> concaveHull = ConcaveHullGenerator.concaveHull(latlons, alpha);
        System.out.println("Fertig.");

        return concaveHull;
    }

    /**
     * Sucht aus der Datenbank alle Objekte der gewünschten LSIClasses heraus,
     * die innerhalb der Hülle liegen.
     *
     * @param geoServDbConn Verbindung zur Datenbank, aus der die Geschäfte
     * gelesen werden sollen
     * @param hull die Hülle, in der die Objekte liegen sollen
     * @param lowerbound untere Grenze der LSIClasses
     * @param upperbound obere Grenze der LSIClasses
     * @return Sämtliche Geschäfte des gewünschten Typen innerhalb der Hülle
     * @throws NumberFormatException
     * @throws SQLException
     * @throws ParseException
     */
    public static List<Location> calculateReachableLocations(DbConnection geoServDbConn, List<double[]> hull, int lowerbound, int upperbound) throws NumberFormatException, SQLException, ParseException {
        System.out.println("Suche nach erreichbaren Zielobjekten...");

        // Umwandeln der Koordinaten in die benötigte Form (Longitude, Latitude)
        List<Coordinate> coords = new ArrayList<>();
        hull.forEach((point)
                -> {
            coords.add(new Coordinate(point[1], point[0]));
        });

        if (coords.size() < 4) {
            // Nicht genug Koordinaten, um Geschäfte zu suchen.
            // Vier Punkte werden benötigt.
            System.out.println("WARNUNG: Die Suche nach Geschäften wird übersprungen, da weniger als vier Punkte gefunden wurden.");
            return new ArrayList<>();
        }

        Coordinate[] coordArray = coords.toArray(new Coordinate[coords.size()]);

        // Suchen der Objekte in der Datenbank
        Geometry polygon = geomfact.createPolygon(geomfact.createLinearRing(coordArray), new LinearRing[0]);
        List<Location> allStoresInRange = geoServDbConn.getPossibleInRangeObjects(lowerbound, upperbound, polygon);
        System.out.println("Fertig.");

        return allStoresInRange;
    }

    private CoordCalculator() {
    }
}
