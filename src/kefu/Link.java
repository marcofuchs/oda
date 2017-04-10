/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import fu.keys.LSIClass;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marco
 */
public final class Link
{
    private static int nextId = 0;
    
    private final int id;
    private Crossing firstCrossing;
    private Crossing lastCrossing;
    private Domain domain;
    private Way[] ways;
    private int length;
    private int linkType;
    private int speedLimit;    
    private OneWay oneWay;
    private boolean linkDetailsLoaded = false;
    private LSIClass lsiClass;

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return the firstCrossing
     */
    public Crossing getFirstCrossing()
    {
        return firstCrossing;
    }

    /**
     * @return the lastCrossing
     */
    public Crossing getLastCrossing()
    {
        return lastCrossing;
    }

    /**
     * @return the domain
     */
    public Domain getDomain()
    {
        return domain;
    }

    /**
     * @return the ways
     */
    public Way[] getWays()
    {
        return ways;
    }

    /**
     * @return the length
     */
    public int getLength()
    {
        return length;
    }

    /**
     * @return the linkType
     */
    public int getLinkType()
    {
        return linkType;
    }

    /**
     * @return the speedLimit
     */
    public int getSpeedLimit()
    {
        return speedLimit;
    }

    /**
     * @return the oneWay
     */
    public OneWay getOneWay()
    {
        return oneWay;
    }
    
    public Link(int id)
    {
        this.id = id;
        
        try
        {
            LoadLinkDetails();
        } catch (Exception ex)
        {
            Logger.getLogger(Link.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<String> names = new ArrayList<>();
    private void LoadLinkDetails() throws Exception
    {
        if (linkDetailsLoaded)
        {
            throw new Exception("Link is already loaded");
        }
        
        try
        {
            domain = Datareader.ReadDomain(Datareader.GetDomainOfLink(id));
            ways = Datareader.getWaysOfLink(domain, id);
            length = Datareader.getLinkLength(id);
            speedLimit = Datareader.getMaxSpeed(id);
            oneWay = Datareader.getOneWayInformation(id);
            
            firstCrossing = Datareader.LoadCrossingFrom(id);
            lastCrossing = Datareader.LoadCrossingTo(id);
            
            lsiClass = Datareader.getLSIClassOfLink(id);
            
            linkDetailsLoaded = true;
            
            int streetDriverLimit = ObjectTypes.getLimit(lsiClass.lsiClass);
            
            if (streetDriverLimit < speedLimit || speedLimit == 0)
            {
                speedLimit = streetDriverLimit;
            }
            
            // TODO: Auf LSIClass umstellen, um Speedlimit zu bekommen.
            //if (speedLimit == 0)
            //{
                //if (domain.getName().startsWith("A "))
                //{
                    //// Auf der Autobahn fahren wir 150
                  //  speedLimit = 150;
                //}
                //else
                //{
                    // Meistens maximal 100
                    //speedLimit = 100;
                    //for (Character singlechar : domain.getName().toCharArray())
                    //{
                        //if (!"ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789".contains(singlechar.toString()))
                        //{
                        //    // Enthält nicht nur Großbuchstaben und Zahlen => Straßenname => Innerorts => 50
                      //      speedLimit = 50;
                    //        break;
                  //      }
                //    }
              //  }
            //}
        } 
        catch (Exception ex)
        {
            Logger.getLogger(Link.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Crossing[] CreateCrossing(double minutes)
    {
        double maxSpeedMeterPerMinute = speedLimit * 1000 / (double)60;
        
        // Wir werden auf diesem Weg fertig.
        // Berechnen, wo genau!
        Way[] linkWays = ways;
        List<Crossing> allCrossings = new ArrayList<>();

        for (Way fWay : linkWays)
        {
            double a2 = fWay.getFirstX() - fWay.getSecondX();
            a2 *= a2;
            double b2 = fWay.getFirstY() - fWay.getSecondY();
            b2 *= b2;

            //Pythagoras!
            double wayLenght = Math.sqrt(a2 + b2);

            double driveTimeForWay = wayLenght / maxSpeedMeterPerMinute;
            minutes -= driveTimeForWay;

            if (minutes > 0)
            {
                nextId -= 1;
                Crossing crn = new Crossing(nextId, fWay.getSecondX(), fWay.getSecondY(), false);
                crn.setSpeedLimitIfLink(speedLimit);
                //crn.setSpeedLimitIfLink((int)minutes);
                allCrossings.add(crn);
            }
            else if (minutes == 0)
            {
                nextId -= 1;
                Crossing crn = new Crossing(nextId, fWay.getSecondX(), fWay.getSecondY(), false);
                crn.setSpeedLimitIfLink(speedLimit);
                //crn.setSpeedLimitIfLink((int)minutes);
                allCrossings.add(crn);
                
                Crossing[] retval = new Crossing[allCrossings.size()];
                
                for (int i = 0; i < allCrossings.size(); i++)
                {
                    retval[i] = allCrossings.get(i);
                }
                
                return retval;
            }
            else if (minutes < 0)
            {
                // Wir werden auf einem Teil des Weges fertig.
                double part = (minutes + driveTimeForWay) / driveTimeForWay;

                int smallerLat = fWay.getFirstX();
                if (fWay.getSecondX() < smallerLat)
                {
                    smallerLat = fWay.getSecondX();
                }

                int smallerLon = fWay.getFirstY();
                if (fWay.getSecondY() < smallerLon)
                {
                    smallerLon = fWay.getSecondY();
                }

                int fX = fWay.getFirstX() - smallerLat;
                int fY = fWay.getFirstY() - smallerLon;
                int sX = fWay.getSecondX()- smallerLat;
                int sY = fWay.getSecondY()- smallerLon;

                int pX;
                if (fX > 0)
                {
                    pX = fX - (int)(part * fX) + smallerLat;
                }
                else 
                {
                    pX = (int)(part * sX + smallerLat);
                }

                int pY;
                if (fY > 0)
                {
                    pY = fY - (int)(part * fY) + smallerLon;
                }
                else 
                {
                    pY = (int)(part * sY + smallerLon);
                }
                
                nextId -= 1;
                Crossing crn = new Crossing(nextId, pX, pY, false);
                crn.setSpeedLimitIfLink(speedLimit);
                //crn.setSpeedLimitIfLink(0);
                allCrossings.add(crn);
                
                Crossing[] retval = new Crossing[allCrossings.size()];
                
                for (int i = 0; i < allCrossings.size(); i++)
                {
                    retval[i] = allCrossings.get(i);
                }
                
                return retval;
            }
        }
        
        return null;
    }
    
    public Crossing[] getWaysAsCrossings()
    {        
        Crossing[] ncr = new Crossing[ways.length - 1];
        for (int i = 0; i < ways.length - 1; i++)
        {
            nextId -= 1;
            ncr[i] = new Crossing(nextId, ways[i].getSecondX(), ways[i].getSecondY(), false);
        }
        
        return ncr;
    }
    
    public Crossing getAlternativCrossing(Crossing cr, double minutes)
    {
        if (cr.getId() == firstCrossing.getId())
        {
            lastCrossing.setMaxMinutesOnCrossing(minutes + getDurationToDrive());
            lastCrossing.setSpeedLimitIfLink(speedLimit);
            return lastCrossing;
        }
        else  
        {
            firstCrossing.setMaxMinutesOnCrossing(minutes + getDurationToDrive());
            firstCrossing.setSpeedLimitIfLink(speedLimit);
            return firstCrossing;
        }
    }
    
    public double getDurationToDrive()
    {
        double maxSpeedMeterPerMinute = speedLimit * 1000 / (double)60;
        
        return (double) length / maxSpeedMeterPerMinute;
    }
}
