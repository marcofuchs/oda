package kefu;

import fu.keys.LSIClass;
import fu.keys.LSIClassCentre;
import java.util.HashMap;
import java.util.Map;

/**
 * Handhabt die Typen und Geschwindigkeitsbegrenzungen der Links
 *
 * @author marco
 */
public class StreetTypes {
    private static final Map<Integer, LSIClass> streetTypes = new HashMap<>();
    private static final Map<Integer, Integer> speedLimits = new HashMap<>();

    /**
     * Lädt die zugehörige LSI-Klasse zu einem Link und gibt diese zurück
     *
     * @param id
     * @return die zugehoerige LSIKlasse
     */
    public static LSIClass getLsiClass(int id) {
        if (!streetTypes.containsKey(id)) {
            streetTypes.put(id, LSIClassCentre.lsiClassByID(id));
        }

        return streetTypes.get(id);
    }

    /**
     * Findet das Speedlimit des entsprechenden Links heraus
     *
     * @param id
     * @return die Geschwindigkeitsbegrenzung einer LSIKlasse
     */
    public static Integer getSpeedLimit(int id) {
        switch (id) {
            case 34110000:
                // Autobahn
                return 120;
            case 34120000:
                // Kraftfahrstrasse
                return 100;
            case 34131000:
                // Bundesstrasse
                return 80;
            case 34132000:
                // sekundaere Landstrasse
                return 70;
            case 34133000:
                // tertiaere Landstrasse
                return 60;
            case 34134000:
                // unklassifizierte Landstrasse
                return 60;
            case 34130000:
                // unspezifizierte Landstrasse
                return 50;
            case 34141000:
                // Innerortstrasse
                return 40;
            case 32711000:
                // Baustelle (Verkehr)
                return 45;
            case 34176000:
                // Kreisverkehr
                return 20;
            case 34142000:
                // Verkehrsberuhigter Bereich
                return 8;
            case 34171000:
                // Anschlussstelle (Autobahn)
                return 70;
            case 34173000:
                // Anschlussstelle (Bundesstrasse)
                return 50;
            case 34172000:
                // Anschlussstelle (Kraftfahrstrasse)
                return 50;
            case 34174000:
                // Anschlussstelle (sekundaer)
                return 50;
            case 34175000:
                // Anschlussstelle (tertiaer)
                return 40;
            default:
                return 45;
        }
    }

    private StreetTypes() {
    }
}
