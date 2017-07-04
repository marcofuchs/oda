package kefu;

import java.util.HashMap;
import java.util.Map;
import nav.NavData;

/**
 * Stellt eine längere Straße dar.
 *
 * @author marco
 */
public class Domain {
    private static final Map<Integer, Domain> loadedDomains = new HashMap<>();

    /**
     * Lädt die Domain, zu der ein Link gehört, und gibt diese zurück
     *
     * @param navData NavData
     * @param linkId die ID des Links, zu dem die Domain gefunden werden soll
     * @return Die zugehörige Domain
     */
    public static Domain getDomainOfLink(NavData navData, int linkId) {
        int domainId = navData.getDomainID(linkId);

        if (loadedDomains.containsKey(domainId)) {
            return loadedDomains.get(domainId);
        }

        Domain newDomain = new Domain(domainId);
        newDomain.loadDomainDetails(navData);
        loadedDomains.put(domainId, newDomain);

        return newDomain;
    }

    private int id;
    private String name;
    private Way[] ways;

    /**
     * Erzeugt eine neue Domain mit der zugehörigen Id
     *
     * @param id Die ID der Domain
     */
    private Domain(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ways
     */
    public Way[] getWays() {
        return ways;
    }

    /**
     * @param ways the ways to set
     */
    public void setWays(Way[] ways) {
        this.ways = ways;
    }

    private void loadDomainDetails(NavData navData) {
        name = navData.getDomainName(id);
        int[] wayXs = navData.getDomainLatsE6(id);
        int[] wayYs = navData.getDomainLongsE6(id);

        int waycount = wayXs.length - 1;
        ways = new Way[waycount];

        for (int i = 0; i < waycount; i++) {
            Way newWay = new Way(wayXs[i], wayYs[i], wayXs[i + 1], wayYs[i + 1]);
            ways[i] = newWay;
        }
    }

    /**
     * Gibt eine Teilstrecke auf der Domain zurück
     *
     * @param firstWayNumber Index der ersten Wegstrecke
     * @param secondWayNumber Index der letzten Wegstrecke
     * @return
     */
    public Way[] getWaysPart(int firstWayNumber, int secondWayNumber) {
        int length = firstWayNumber - secondWayNumber;
        if (length < 0) {
            length *= -1;

            Way[] waysPart = new Way[length];
            for (int i = firstWayNumber; i < secondWayNumber; i++) {
                waysPart[i - firstWayNumber] = this.ways[i];
            }

            return waysPart;
        } else {
            Way[] waysPart = new Way[length];
            for (int i = secondWayNumber; i < firstWayNumber; i++) {
                waysPart[i - secondWayNumber] = this.ways[i];
            }

            return waysPart;
        }
    }
}
