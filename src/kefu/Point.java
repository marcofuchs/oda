/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marco
 */
class Point
{
    private static List<Point> allPoints = new ArrayList<>();
    
    private int lat;
    private int lon;

    /**
     * @return the lat
     */
    public int getLat()
    {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(int lat)
    {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public int getLon()
    {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(int lon)
    {
        this.lon = lon;
    }
    
    public Point(int lat, int lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * @return the allPoints
     */
    public static List<Point> getAllPoints()
    {
        return allPoints;
    }
    
    public static Boolean ListContainsPoint(int lat, int lon)
    {
        if (allPoints.stream().anyMatch((allPoint) -> (allPoint.lat == lat && allPoint.lon == lon)))
        {
            return true;
        }
        
        return false;
    }
}
