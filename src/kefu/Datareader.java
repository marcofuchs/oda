/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import fu.keys.LSIClass;
import fu.keys.LSIClassCentre;
import nav.NavData;

/**
 *
 * @author marco
 */
public class Datareader
{
    private static NavData data;
    private static String cacheFile;
    
    public static Boolean createStaticNavData(String newCacheFile)
    {
        try
        {
            cacheFile = newCacheFile;
            System.out.println("Lade Cache-File: " + cacheFile);
            data = new NavData(cacheFile, true);
            return true;
        } catch (Exception ex)
        {
            return false;
        }
    }
    
    public static void disposeStaticNavData()
    {
        data = null;
    }
    
    public static Crossing ReadNearestCrossing(int lat, int lon)
    {        
        int crossingId = data.getNearestCrossing(lat, lon);
        int crossingLat = data.getCrossingLatE6(crossingId);
        int crossingLon = data.getCrossingLongE6(crossingId);
        boolean crossingIsolated = data.isIsolatedCrossing(crossingId);
        
        return new Crossing(crossingId, crossingLat, crossingLon, crossingIsolated);
    }
    
    public static Link[] ReadLinksOfCrossing(Crossing crossing)
    {
        int[] ids = data.getLinksForCrossing(crossing.getId());   
        int linkCount = ids.length;
        
        Link[] links = new Link[linkCount];
        
        for (int i = 0; i < linkCount; i++)
        {
            links[i] = new Link(ids[i]);
        }
        
        return links;
    }
    
    public static Crossing LoadCrossingFrom(int id)
    {     
        int crossingId = data.getCrossingIDFrom(id);
        int crossingLat = data.getCrossingLatE6(crossingId);
        int crossingLon = data.getCrossingLongE6(crossingId);
        boolean crossingIsolated = data.isIsolatedCrossing(crossingId);
        
        return new Crossing(crossingId, crossingLat, crossingLon, crossingIsolated);
    }
    
    public static Crossing LoadCrossingTo(int id)
    {     
        int crossingId = data.getCrossingIDTo(id);
        int crossingLat = data.getCrossingLatE6(crossingId);
        int crossingLon = data.getCrossingLongE6(crossingId);
        boolean crossingIsolated = data.isIsolatedCrossing(crossingId);
        
        return new Crossing(crossingId, crossingLat, crossingLon, crossingIsolated);
    }
    
    public static int GetDomainOfLink(int linkId)
    {
        return data.getDomainID(linkId);
    }
    
    public static Domain ReadDomain(int domainId)
    {
        if (Domain.getLoadedDomains().containsKey(domainId))
        {
            // Wir haben die Domain schon mal geladen.
            // Also nehmen wir einfach die geladene.
            return Domain.getLoadedDomains().get(domainId);
        }
        
        Domain domain = new Domain(domainId);
        domain.setName(data.getDomainName(domainId));
        int[] wayXs = data.getDomainLatsE6(domainId);
        int[] wayYs = data.getDomainLongsE6(domainId);
        
        int waycount = wayXs.length - 1;
        domain.setWays(new Way[waycount]);
        
        for (int i = 0; i < waycount; i++)
        {
            Way newWay = new Way(wayXs[i], wayYs[i], wayXs[i+1], wayYs[i+1]);
            domain.getWays()[i] = newWay;
        }
        
        Domain.getLoadedDomains().put(domainId, domain);
        
        return domain;
    }
    
    public static Way[] getWaysOfLink(Domain domain, int linkId)
    {
        int firstWayNumber = data.getDomainPosNrFrom(linkId);
        int secondWayNumber = data.getDomainPosNrTo(linkId);
        
        int length = firstWayNumber - secondWayNumber;
        if (length < 0)
        {
            length *= -1;
            
            Way[] ways = new Way[length];
            for (int i = firstWayNumber; i < secondWayNumber; i++)
            {
                ways[i - firstWayNumber] = domain.getWays()[i];
            }

            return ways;
        }
        else   
        {
            Way[] ways = new Way[length];
            for (int i = secondWayNumber; i < firstWayNumber; i++)
            {
                ways[i - secondWayNumber] = domain.getWays()[i];
            }

            return ways;
        }
    }
    
    public static int getLinkLength (int linkId)
    {
        return data.getLengthMeters(linkId);
    }
    
    public static int getMaxSpeed (int linkId)
    {
        return data.getMaxSpeedKMperHours(linkId);
    }
    
    public static OneWay getOneWayInformation(int linkId)
    {
        if (data.goesCounterOneway(linkId))
        {
            return OneWay.FirstToSecond;
        }
        else if (data.goesCounterOneway(data.getReverseLink(linkId)))
        {
            return OneWay.SecondToFirst;
        }
        
        return OneWay.None;
    }
    
    public static LSIClass getLSIClassOfLink(int linkId)
    {
        int lsiId = data.getLSIclass(linkId);
        
        return ObjectTypes.getLsiClass(lsiId);
    }
}
