package kefu;

import java.util.ArrayList;
import nav.NavData;

/**
 * Stellt eine Kreuzung im Straßennetz dar.
 *
 * @author marco
 */
public class Crossing {
    /**
     * Erzeugt ein neues Crossing anhand der mitgegebenen Daten
     *
     * @param navData NavData-Klasse
     * @param crossingId ID des Crossing
     * @param ancestorId ID des Vorgängers
     * @return ein neues Crossing, das als Ziel des Vorgängers genutzt werden
     * kann
     */
    public static Crossing create(NavData navData, int crossingId, int ancestorId) {
        int crossingLat = navData.getCrossingLatE6(crossingId);
        int crossingLon = navData.getCrossingLongE6(crossingId);
        boolean crossingIsolated = navData.isIsolatedCrossing(crossingId);

        return new Crossing(crossingId, crossingLat, crossingLon, crossingIsolated, ancestorId);
    }

    /**
     * Liest zu einer gegebenen Latitude und Longitude das naechstgelegene
     * Crossing und gibt dieses zurueck.
     *
     * @param navData NavData-Klasse
     * @param lat die Latitude
     * @param lon die Longitude
     * @return das naeheste Crossing zu gegebener Latitude und Longitude
     */
    public static Crossing readNearesCrossingFromNavData(NavData navData, int lat, int lon) {
        int crossingId = navData.getNearestCrossing(lat, lon);
        if (crossingId == -1) {
            // Kein nächstes Crossing gefunden.
            // Mögliche Ursache:
            // Punkt befindet sich nicht auf mitgegebener Karte.
            return null;
        }
        int crossingLat = navData.getCrossingLatE6(crossingId);
        int crossingLon = navData.getCrossingLongE6(crossingId);
        boolean crossingIsolated = navData.isIsolatedCrossing(crossingId);
        return new Crossing(crossingId, crossingLat, crossingLon, crossingIsolated, -1);
    }
    private final int id;
    private final int lat;
    private final int lon;
    private CrossingConnection[] targets = null;
    private int ancestorId = -1;

    private double maxDurationToReachCrossing = 0;

    /**
     *
     * Konstruktor des Crossings
     *
     * @param id
     * @param lat
     * @param lon
     * @param isIsolated
     * @param ancestorId
     */
    private Crossing(int id, int lat, int lon, boolean isIsolated, int ancestorId) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.ancestorId = ancestorId;
    }

    /**
     * @return gibt die Latitude zurueck
     */
    public int getLat() {
        return lat;
    }

    /**
     * @return gibt die Longitude zurueck
     */
    public int getLon() {
        return lon;
    }

    /**
     * @return gibt den eindeutigen Identifizierer der Crossing-Instanz zurueck
     */
    public int getId() {
        return id;
    }

    /**
     * @return the maxDurationToReachCrossing
     */
    public double getMaxDurationToReachCrossing() {
        return maxDurationToReachCrossing;
    }

    /**
     * @param maxDurationToReachCrossing the maxDurationToReachCrossing to set
     */
    public void setMaxDurationToReachCrossing(double maxDurationToReachCrossing) {
        this.maxDurationToReachCrossing = maxDurationToReachCrossing;
    }

    /**
     * @return the ancestorId
     */
    public int getAncestorId() {
        return ancestorId;
    }

    /**
     * Sucht die vom Crossing aus erreichbaren Nachbarn, die nicht der Vorgänger
     * des Crossings sind.
     *
     * @param navData
     * @return eine Liste der Nachbarn
     */
    public CrossingConnection[] getNeighbours(NavData navData) {
        if (targets != null) {
            // Wir haben die Nachbarn schon mal gesucht.
            return targets;
        }

        // Teil 1: Alle Links der Reihe nach analysieren
        int[] ids = navData.getLinksForCrossing(getId());
        int linkCount = ids.length;

        Link[] links = new Link[linkCount];

        for (int i = 0; i < linkCount; i++) {
            links[i] = Link.readLink(navData, ids[i], this);
        }

        // Teil 2: benachbarte Crossings laden und Verbindungen erzeugen
        ArrayList<CrossingConnection> neighbours = new ArrayList<>();
        for (Link link : links) {
            if (link.goesCounterOneWay()) {
                // Weg führt entgegen einer Einbahnstraße, wir dürfen hier also nicht fahren.
                // Also nehmen wir ihn nicht in die Liste der validen Verbindungen auf.
                continue;
            }

            Crossing lastCrossing = link.getLastCrossing();
            lastCrossing.setMaxDurationToReachCrossing(maxDurationToReachCrossing + link.getDriveDuration());
            neighbours.add(new CrossingConnection(lastCrossing, link));
        }

        targets = neighbours.toArray(new CrossingConnection[neighbours.size()]);
        return targets;
    }
}
