package kefu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pp.dorenda.client2.additional.UniversalPainterWriter;

/**
 * Klasse zum Schreiben in den UniversalPainterWriter
 *
 * @author marco
 */
public class Writer {
    /**
     * Schreibt Ergebnisse der Isochrone-Berechnung in die results.txt
     *
     * @param hull Die umgebende Geometrie, die gezeichnet werden soll
     * @param reachableStores alle erreichbaren Geschäfte innerhalb der Grenzen
     * @param startlat Latitude des Startpunkts
     * @param startlon Longitude des Startpunkts
     * @param minutes Zeit, die man gefahren ist
     */
    public static void writePoints(double startlat, double startlon, int minutes, ArrayList<double[]> hull, List<Location> reachableStores) {
        System.out.println("Schreibe Ergebnisse in Datei...");

        System.out.println("Oeffne results.txt");
        UniversalPainterWriter writer;
        try {
            writer = new UniversalPainterWriter("result.txt");
        } catch (IOException ex) {
            System.out.println("FEHLER: results.txt konnte nicht geöffnet werden.");
            System.out.println("Meldung:");
            System.out.println(ex);
            return;
        }

        // Startpunkt muss immer zuerste geschrieben werden,
        // da das Kartentool beim Laden den ersten Punkt in der Datei fokussiert!
        System.out.println("Schreibe den Startpunkt.");
        writer.position(startlat / 1000000.0, startlon / 1000000.0, 0, 0, 0, 255, 10, "Start fuer " + minutes + " Minuten");

        // Konkave Hülle schreiben
        System.out.println("Schreibe results.txt -> Concave Hull");
        writer.polygon(hull, 0, 0, 255, 50);

        // Erreichbare Geschäfte schreiben
        System.out.println("Schreibe results.txt -> Locations");

        for (Location point : reachableStores) {
            writer.flag(point.getGeo().getCentroid().getY(), point.getGeo().getCentroid().getX(), (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 255, point.getName().replace('"', '\''));
        }

        System.out.println("Schliesse results.txt");
        writer.close();

        System.out.println("Fertig.");
    }

    private Writer() {
    }
}
