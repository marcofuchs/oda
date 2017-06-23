/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author marco
 */
public class Crossing
{    
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
    
    /**
     *
     * @param id
     * @param lat
     * @param lon
     * @param isIsolated
     */
    public Crossing(int id, int lat, int lon, boolean isIsolated)
    {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.isolated = isIsolated;
    }
    
    /**
     *
     * @param minutes
     * @param start
     * @return
     */
    public static Crossing[] drive(double minutes, Crossing start)
    {
        double interval = 0.5;
        double last = 0;
        
        Map<Integer, Crossing> closedDict = new HashMap();
        
        Map<Integer, Crossing> openedInts = new HashMap<>();
        Comparator<Crossing> comparator = new CrossingComparator();
        PriorityQueue<Crossing> opened = new PriorityQueue<>(10, comparator);
        
        opened.add(start);
        openedInts.put(start.id, start);
        
        while (!opened.isEmpty())
        {
            Crossing elem = opened.peek();
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

                if (openedInts.containsKey(newCrossings[i].id))
                {
                    if (newCrossings[i].maxMinutesOnCrossing < openedInts.get(newCrossings[i].id).maxMinutesOnCrossing)
                    {
                        opened.remove(openedInts.get(newCrossings[i].id));
                        openedInts.get(newCrossings[i].id).maxMinutesOnCrossing = newCrossings[i].maxMinutesOnCrossing;
                        opened.add(openedInts.get(newCrossings[i].id));
                    } 
                    
                    continue;
                }
                
                Crossing[] ncr = elem.links[i].getWaysAsCrossings();
                for (Crossing crossing : ncr)
                {
                    closedDict.put(crossing.id, crossing);
                }
                
                opened.add(newCrossings[i]);
                openedInts.put(newCrossings[i].id, newCrossings[i]);
            }
            
            // Das Element an Stelle 0 ist nicht mehr schneller zu erreichen.
            Crossing nextFastest = opened.poll();
            openedInts.remove(nextFastest.id);
            
            closedDict.put(nextFastest.id, nextFastest);
        }
        
        System.out.println("Alle innerhalb der Zeit erreichbare Punkte wurden gefunden.");
          
        return closedDict.values().toArray(new Crossing[closedDict.values().size()]);
    }
    
    /**
     *
     * @param cr
     * @param minutes
     * @return
     */
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
