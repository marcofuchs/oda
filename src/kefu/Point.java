/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author marco
 */
class Point
{
    private Geometry geo;
    
    private String name;

    /**
     * @return the lat
     */
    public Geometry getGeo()
    {
        return geo;
    }

    /**
     * @param lat the lat to set
     */
    public void setGeo(Geometry newGeo)
    {
        this.geo = newGeo;
    }

    /**
     * @return the lon
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param lon the lon to set
     */
    public void setName(String newName)
    {
        this.name = newName;
    }
    
    Point(Geometry geo, String name)
    {
        this.geo = geo;
        this.name = name;
    }
}
