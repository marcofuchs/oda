/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nav.NavData;

/**
 *
 * @author marco
 */
public class IsochroneCalculator {
    private NavData navData;
    private final String cacheFile;
    private final double notificationInterval = 0.5;
    private double lastNotification = 0;

    /**
     * Konstruktor des IsochroneCalculators
     *
     * @param cacheFile
     */
    public IsochroneCalculator(String cacheFile) {
        this.cacheFile = cacheFile;
    }

    /**
     * Startet die Initialisierung der NavData-Klasse
     *
     * @return
     */
    public boolean initializeNavData() {
        try {
            navData = new NavData(cacheFile, true);
            return true;
        } catch (Exception ex) {
            System.out.println("FEHLER: NavData konnte nicht generiert werden. Bitte prüfen, ob der Cache-File korrekt angegeben ist.");
            System.out.println("Exception:");
            System.out.println(ex);
            System.out.println("Programm wird beendet.");
            return false;
        }
    }

    /**
     * Berechnet, wie weit man innerhalb von x Minuten fahren kann
     *
     * @param startLat Startpunkt: Latitude
     * @param startLon Startpunkt: Longitude
     * @param minutes Zahl der Minuten, die man fahren darf
     * @return
     */
    public List<Coordinate> createIsochrone(int startLat, int startLon, double minutes) {
        System.out.println("Berechne Isochrone...");

        // Dafür sorgen, dass der angegebene Startpunkt immer im Polygon enthalten ist
        List<Coordinate> allReachableCoords = new ArrayList<>();
        allReachableCoords.add(new Coordinate(startLat, startLon));

        Crossing start = Crossing.readNearesCrossingFromNavData(navData, startLat, startLon);
        
        if (start == null) {
            System.out.println("FEHLER: Zum angegebenen Punkt konnte keine nächste Kreuzung gefunden werden.");
            System.out.println("Möglicherweise befindet der angegebene Startpunkt außerhalb der Karte.");
            System.out.println("Berechnung kann nicht durchgeführt werden.");
            System.out.println("Programm wird beendet.");
            return null;
        }

        Map<Integer, Crossing> closed = new HashMap<>();
        MapQueue opened = new MapQueue();

        opened.add(start);

        do {
            // Das erste Crossing in der offenen Liste ist nicht mehr schneller zu erreichen.
            Crossing current = opened.peek();

            if (current.getMaxDurationToReachCrossing() > lastNotification + notificationInterval) {
                lastNotification += notificationInterval;
                System.out.println("Alle innerhalb von " + lastNotification + " erreichbaren Punkte wurden durchlaufen.");
            }

            if (current.getMaxDurationToReachCrossing() > minutes) {
                // Abbruchbedingung: Das nächste Crossing ist bereits außerhalb der Grenze und kann nicht mehr enthalten sein.
                System.out.println("Alle innerhalb der Zielzeit erreichbaren Punkte wurden durchlaufen.");
                System.out.println("Fertig.");
                return allReachableCoords;
            }

            opened.remove(current);

            // Aktuelles Crossing expandieren und neue offene in die Liste einfügen
            // Außerdem Fahrtwege mitschreiben, damit gefahrene Straßen in der Geometrie berücksichtigt werden
            allReachableCoords.addAll(expand(current, opened, closed, minutes));

            closed.put(current.getId(), current);
        } while (!opened.isEmpty());

        // Wir haben sämtliche erreichbaren Punkte auf der Karte gefunden, aber das Zeitlimit nicht überschritten
        System.out.println("Alle erreichbaren Punkte wurden durchlaufen, das Zeitlimit wurde aber noch nicht erreicht.");
        System.out.println("Fertig.");
        return allReachableCoords;
    }

    /**
     * Expandiert den aktuellen Knoten und gibt außerdem alle bis zum Zeitlimit
     * fahrbaren Ziele des aktuellen Knotens zurück
     *
     * @param current aktueller Knoten
     * @param opened alle offenen Knoten
     * @param closed alle geschlossenen Knoten
     * @param maxMinutes maximal verfügbare Minutenzahl des Knotens
     * @return Koordinaten der von diesem Knoten aus fahrbaren Straßen innerhalb
     * des Zeitlimits
     */
    private List<Coordinate> expand(Crossing current, MapQueue opened, Map<Integer, Crossing> closed, double maxMinutes) {
        List<Coordinate> reachableCoords = new ArrayList<>();

        CrossingConnection[] neighbours = current.getNeighbours(navData);

        for (CrossingConnection neighbourConnection : neighbours) {
            Crossing neighbour = neighbourConnection.getTarget();

            if (neighbour.getId() != current.getAncestorId()) {
                // Hier sind wir noch nicht gefahren.
                // => Den Weg nachfahren und die gefundenen Koordinaten hinzufügen.
                //    Dadurch wird sichergestellt, dass gefahrene Straßen nicht außerhalb des Polygons liegen
                List<Coordinate> nCoords = neighbourConnection.driveUpToMinutes(current.getMaxDurationToReachCrossing(), maxMinutes);
                reachableCoords.addAll(nCoords);
            }

            if (closed.containsKey(neighbour.getId())) {
                // Nachbar bereits ein geschlossener Knoten 
                // => Ignorieren
                continue;
            }

            if (opened.contains(neighbour.getId()) && neighbour.getMaxDurationToReachCrossing() > opened.get(neighbour.getId()).getMaxDurationToReachCrossing()) {
                // Nachbar schon in den offenen Knoten und von woanders aus schneller erreichbar.
                // => Ignorieren
                continue;
            }

            // Nachbar ist entweder neu oder vom aktuellen Knoten aus schneller zu erreichen.
            if (opened.contains(neighbour.getId())) {
                // Knoten war bereits offen => Aus Liste entfernen, damit wir ihn mit neuer Zeit neu einsortieren können.
                opened.remove(opened.get(neighbour.getId()));
            }

            opened.add(neighbour);
        }

        return reachableCoords;
    }
}
