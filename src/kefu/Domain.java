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
public class Domain
{
    private static Map<Integer, Domain> loadedDomains = new HashMap<>();
    
    private int id;
    private String name;
    private Way[] ways;

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the ways
     */
    public Way[] getWays()
    {
        return ways;
    }

    /**
     * @param ways the ways to set
     */
    public void setWays(Way[] ways)
    {
        this.ways = ways;
    }

    /**
     * @return the loadedDomains
     */
    public static Map<Integer, Domain> getLoadedDomains()
    {
        return loadedDomains;
    }
    
    public Domain(int id)
    {
        this.id = id;
    }
}
