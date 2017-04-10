/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import fu.keys.LSIClass;
import fu.keys.LSIClassCentre;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marco
 */
public class ObjectTypes
{
    private static Map<Integer, LSIClass> oTypes = new HashMap<>();
    private static Map<Integer, Integer> speedLimits = new HashMap<>();

    /**
     * @return the oTypes
     */
    public static LSIClass getLsiClass(int id)
    {
        if (!oTypes.containsKey(id))
        {
            oTypes.put(id, LSIClassCentre.lsiClassByID(id));
        }
        
        return oTypes.get(id);
    }
    
    public static Integer getLimit(int id)
    {
        switch (id)
        {
            case 34110000:
                // Autobahn
                return 120;
            case 34141000:
                // Innerortstraße
                return 40;
            case 34133000:
                // tertiäre Landstraße
                return 60;
            case 34173000:
                // Anschlussstelle (Bundesstraße)
                return 50;
            case 34132000:
                // sekundäre Landstraße
                return 70;
            case 34120000:
                // Kraftfahrstraße
                return 100;
            case 34176000:
                // Kreisverkehr
                return 20;
            case 34172000:
                // Anschlussstelle (Kraftfahrstraße)
                return 50;
            case 32711000:
                // Baustelle (Verkehr)
                return 45;
            case 34131000:
                // Bundesstraße
                return 80;
            case 34171000:
                // Anschlussstelle (Autobahn)
                return 70;
            case 34175000:
                // Anschlussstelle (tertiär)
                return 40;
            case 34142000:
                // Verkehrsberuhigter Bereich
                return 8;
            case 34134000:
                // unklassifizierte Landstraße
                return 60;
            case 34130000:
                // unspezifizierte Landstraße
                return 50;
            case 34174000:
                // Anschlussstelle (sekundär)
                return 50;
            default: 
                return 45;
        }
    }
    
    public static Map<Integer, LSIClass> allClasses()
    {
        return oTypes;
    }
}
