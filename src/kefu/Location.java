package kefu;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Stellt die Geometrie eines Objekt auf der Karte dar und enthält zusätzlich noch dessen Namen
 * 
 * @author marco
 */

public class Location
{
    private Geometry geo;
    private String name;

    /**
     * @return die Geometrie des Objektes
     */
    public Geometry getGeo()
    {
        return geo;
    }

    /**
     * @param newGeo legt die Geometrie des Objektes fest
     */
    public void setGeo(Geometry newGeo)
    {
        this.geo = newGeo;
    }

    /**
     * @return der Name des Objektes
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param newName Neuen Namen fuer das Objekt festlegen
     */
    public void setName(String newName)
    {
        this.name = newName;
    }
    
    /**
     * Erzeugt ein neues Objekt mit entsprechender Geometrie und entsprechendem Namen
     * 
     * @param geo
     * @param name
     */
    public Location(Geometry geo, String name)
    {
        this.geo = geo;
        this.name = name;
    }
}