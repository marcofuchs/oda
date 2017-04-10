/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marco
 */
public class Crossing
{
    private static List<Crossing> alreadyDrivenCrossings = new ArrayList<>();
    
    private final int id;
    private final int lat;
    private final int lon;
    private final boolean isolated;
    private Link[] links;
    
    private int speedLimitIfLink = 0;
    
    private double maxMinutesOnCrossing = 0;
    
    /**
     * @return the x
     */
    public int getLat()
    {
        return lat;
    }

    /**
     * @return the y
     */
    public int getLon()
    {
        return lon;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return the isIsolated
     */
    public boolean isIsolated()
    {
        return isolated;
    }

    /**
     * @return the links
     */
    public Link[] getLinks()
    {
        return links;
    }
    
    public Crossing(int id, int lat, int lon, boolean isIsolated)
    {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.isolated = isIsolated;
    }
    
    public static List<Crossing> drive(double minutes, Crossing start)
    {
        double interval = 0.5;
        double last = 0;
        
        Map<Integer, Crossing> closedDict = new HashMap();
        
        List<Double> openedInts = new ArrayList<>();
        List<Crossing> opened = new ArrayList<>();
        
        opened.add(start);
        openedInts.add((double) start.id);
        
        while (!opened.isEmpty() && opened.get(0).maxMinutesOnCrossing < minutes)
        {
            Crossing elem = opened.get(0);
            if (elem.maxMinutesOnCrossing > last + interval)
            {
                last += interval;
                System.out.println("Alle innerhalb von " + last + " Minuten erreichbaren Kreuzungen wurden gefunden.");
            }
            
            Crossing[] newCrossings = getNextCrossings(elem, elem.maxMinutesOnCrossing);
            for (int i = 0; i < newCrossings.length; i++)
            {
                if (newCrossings[i].maxMinutesOnCrossing > minutes)
                {
                    // Hier ansetzen und stattdessen eine "Kreuzung" erzeugen, 
                    // die genau nach 15 Minuten erreichbar ist.
                    
                    Crossing[] rcr = elem.links[i].CreateCrossing(minutes - elem.maxMinutesOnCrossing);
                    
                    for (Crossing crossing : rcr)
                    {
                        crossing.maxMinutesOnCrossing = minutes;
                        closedDict.put(crossing.id, crossing);
                    }
                    
                    continue;
                }
                
                if (closedDict.containsKey(newCrossings[i].id))
                {
                    continue;
                }

                if (openedInts.contains((double) newCrossings[i].id))
                {
                    // Es gibt bereits einen schnelleren Weg zu diesem Crossing.
                    continue;
                }
                
                //Crossing[] ncr = elem.links[i].getWaysAsCrossings();
                //for (Crossing crossing : ncr)
                //{
                  //  closedDict.put(crossing.id, crossing);
                //}
                
                Boolean added = false;
                for (int j = 0; j < opened.size(); j++)
                {
                    if (newCrossings[i].maxMinutesOnCrossing < opened.get(j).maxMinutesOnCrossing)
                    {
                        opened.add(j, newCrossings[i]);
                        openedInts.add((double) newCrossings[i].id);
                        added = true;
                        break;
                    }
                }
                
                if (!added)
                {
                    opened.add(newCrossings[i]);
                    openedInts.add((double) newCrossings[i].id);
                }
            }
            
            // Das Element an Stelle 0 ist nicht mehr schneller zu erreichen.
            Crossing nextFastest = opened.remove(0);
            openedInts.remove((double) nextFastest.id);
            
            closedDict.put(nextFastest.id, nextFastest);
        }
        
        if (opened.isEmpty())
        {
            System.out.println("Alle innerhalb der Zeit erreichbare Punkte wurden gefunden.");
        }
        
        List<Crossing> closed = new ArrayList<>();
        closedDict.forEach((key, value) -> closed.add(value));
        
        return closed;
    }
    
    public static Crossing[] getNextCrossings(Crossing cr, double minutes)
    {
        cr.links = Datareader.ReadLinksOfCrossing(cr);
        
        Crossing[] crn = new Crossing[cr.links.length];
        for (int i = 0; i < cr.links.length; i++)
        {
            crn[i] = cr.links[i].getAlternativCrossing(cr, minutes);
        }
        
        return crn;
    }

    /**
     * @return the maxMinutesOnCrossing
     */
    public double getMaxMinutesOnCrossing()
    {
        return maxMinutesOnCrossing;
    }

    /**
     * @param maxMinutesOnCrossing the maxMinutesOnCrossing to set
     */
    public void setMaxMinutesOnCrossing(double maxMinutesOnCrossing)
    {
        this.maxMinutesOnCrossing = maxMinutesOnCrossing;
    }

    /**
     * @return the speedLimitIfLink
     */
    public int getSpeedLimitIfLink()
    {
        return speedLimitIfLink;
    }

    /**
     * @param speedLimitIfLink the speedLimitIfLink to set
     */
    public void setSpeedLimitIfLink(int speedLimitIfLink)
    {
        this.speedLimitIfLink = speedLimitIfLink;
    }
}
