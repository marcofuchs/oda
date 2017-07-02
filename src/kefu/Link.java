package kefu;

import com.vividsolutions.jts.geom.Coordinate;
import fu.keys.LSIClass;
import java.util.ArrayList;
import java.util.List;
import nav.NavData;

/**
 * Stellt eine Verbindung zwischen zwei Kreuzungen dar
 * @author marco
 */
public class Link {
    private final int id;
    private Crossing lastCrossing;
    private Domain domain;
    private Way[] ways;
    private int length;
    private int speedLimit;
    private boolean goesCounterOneWay;
    private boolean linkDetailsLoaded = false;
    private LSIClass lsiClass;
    private double latLongDifferenceOfAllWays = 0;

    /**
     * Gibt zurück, ob der Link entgegen einer Einbahnstraße läuft
     * @return
     */
    public boolean goesCounterOneWay() {
        return goesCounterOneWay;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the lastCrossing
     */
    public Crossing getLastCrossing() {
        return lastCrossing;
    }

    /**
     * Findet heraus, wie lange es dauert, diesen Link komplett abzufahren
     * @return
     */
    public double getDriveDuration() {
        double maxSpeedMeterPerMinute = speedLimit * 1000 / (double) 60;

        return (double) length / maxSpeedMeterPerMinute;
    }

    /**
     * Erzeugt einen neuen Link mit der gegebenen Id.
     * @param id
     */
    private Link(int id) {
        this.id = id;
    }

    /**
     * Erzeugt anhand der Id den entsprechenden Link
     * 
     * @param navData NavData
     * @param id Id der Straße
     * @param first die Kreuzung, von der aus man in die Straße reinfährt
     * @return
     */
    public static Link readLink(NavData navData, int id, Crossing first) {
        Link newLink = new Link(id);
        newLink.loadLinkDetails(navData, first);

        return newLink;
    }

    /**
     * Lädt die Details des Links
     * @param navData
     * @param first Kreuzung, von der aus man in den Link reinfährt
     */
    private void loadLinkDetails(NavData navData, Crossing first) {
        if (linkDetailsLoaded) {
            // Alle Details wurden schon geladen.
            return;
        }

        domain = Domain.getDomainOfLink(navData, id);
        ways = loadWays(navData);
        length = navData.getLengthMeters(id);
        goesCounterOneWay = navData.goesCounterOneway(id);
        lsiClass = StreetTypes.getLsiClass(navData.getLSIclass(id));
        speedLimit = navData.getMaxSpeedKMperHours(id);

        lastCrossing = Crossing.create(navData, navData.getCrossingIDTo(id), first.getId());

        // Wichtige Überprüfungen zu den Wegen
        if (ways[0].getFirstX() != first.getLat() && ways[0].getFirstY() != first.getLon()) {
            // => Die Domain hat die Wege entgegen unserer Reihenfolge gespeichert.
            // => Umdrehen erforderlich, weil wir die Wege sonst falsch abfahren.
            Way[] revertedWays = new Way[ways.length];
            for (int i = ways.length - 1; i >= 0; i--) {
                revertedWays[ways.length - i - 1] = new Way(ways[i].getSecondX(), ways[i].getSecondY(), ways[i].getFirstX(), ways[i].getFirstY());
            }

            ways = revertedWays;
        }

        // Berechnung der Länge der Wege
        for (Way way : ways) {
            latLongDifferenceOfAllWays += way.getLength();
        }

        for (Way way : ways) {
            if (latLongDifferenceOfAllWays > 0) {
                way.setLength(way.getLength() * length / latLongDifferenceOfAllWays);
            } else {
                way.setLength(0);
            }
        }

        // Entscheidung, wie schnell wir auf dieser Straße fahren
        int streetDriverLimit = StreetTypes.getSpeedLimit(lsiClass.lsiClass);
        if (streetDriverLimit < speedLimit || speedLimit == 0) {
            speedLimit = streetDriverLimit;
        }

        linkDetailsLoaded = true;
    }

    /**
     * Fährt den Weg bis zur angegebenen Maximalzeit ab und zählt ab der angegebenen Startzeit
     * @param time Startzeit
     * @param maxTime Maximale Zeit
     * @return Koordinaten der Wegpunkte
     */
    public List<Coordinate> drive(double time, double maxTime) {
        List<Coordinate> coords = new ArrayList<>();
        double maxSpeedMeterPerMinute = speedLimit * 1000 / (double) 60;

        for (Way way : ways) {
            double driveTimeForWay = way.getLength() / maxSpeedMeterPerMinute;
            time += driveTimeForWay;

            if (time <= maxTime) {
                coords.add(new Coordinate(way.getSecondX(), way.getSecondY()));

                if (time == maxTime) {
                    // Punktlandung => Muss man nicht mehr weiterrechnen
                    break;
                }
            } else {
                // Die Zeit wird auf diesem Way verbraucht!
                // => Endkoordinate finden.
                coords.add(driveWayUntilTimeIsOver(time, maxTime, driveTimeForWay, way));
                break;
            }
        }

        return coords;
    }

    /**
     * Findet heraus, wie lange man auf dem Weg noch fahren kann, bis die Maximalzeit erreicht ist und setzt dort eine Koordinate, die er zurückgibt.
     * 
     * @param currentTime aktuelle Zeit, aber der gezählt werden soll
     * @param maxTime Maximalzeit
     * @param driveTimeForWay Zeit, die es braucht, um den Weg ganz abzufahren
     * @param fWay der zu fahrende Weg
     * @return
     */
    private Coordinate driveWayUntilTimeIsOver(double currentTime, double maxTime, double driveTimeForWay, Way fWay) {
        // Anteil des Weges, der noch innerhalb des Zeitlimits erreicht werden kann, errechnen.
        double part = (driveTimeForWay - currentTime + maxTime) / driveTimeForWay;

        int smallerLat = fWay.getFirstX();
        if (fWay.getSecondX() < smallerLat) {
            smallerLat = fWay.getSecondX();
        }

        int smallerLon = fWay.getFirstY();
        if (fWay.getSecondY() < smallerLon) {
            smallerLon = fWay.getSecondY();
        }

        // Um die Wegstrecke anteilig zu berechnen, müssen wir vorher erst die Latitude und die Longitude, 
        // die sich beide teilen, rausrechnen,
        // weil wir sonst falsche Werte rausbekommen
        int fX = fWay.getFirstX() - smallerLat;
        int fY = fWay.getFirstY() - smallerLon;
        int sX = fWay.getSecondX() - smallerLat;
        int sY = fWay.getSecondY() - smallerLon;

        
        // Berechnung der beiden Endkoordinaten
        int pX;
        if (fX > 0) {
            pX = fX - (int) (part * fX) + smallerLat;
        } else {
            pX = (int) (part * sX + smallerLat);
        }

        int pY;
        if (fY > 0) {
            pY = fY - (int) (part * fY) + smallerLon;
        } else {
            pY = (int) (part * sY + smallerLon);
        }

        return new Coordinate(pX, pY);
    }

    /**
     * Lädt sämtliche Wege, die dieser Link umspannt
     * 
     * @param navData
     * @return
     */
    private Way[] loadWays(NavData navData) {
        int firstWayNumber = navData.getDomainPosNrFrom(id);
        int secondWayNumber = navData.getDomainPosNrTo(id);

        return domain.getWaysPart(firstWayNumber, secondWayNumber);
    }
}
