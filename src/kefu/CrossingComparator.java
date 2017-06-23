/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import java.util.Comparator;

/**
 *
 * @author marco
 */
public class CrossingComparator implements Comparator<Crossing>
{
    @Override
    public int compare (Crossing x, Crossing y)
    {
        if (x.getMaxMinutesOnCrossing() < y.getMaxMinutesOnCrossing())
        {
            return -1;
        }
        else if (x.getMaxMinutesOnCrossing() > y.getMaxMinutesOnCrossing())
        {
            return 1;
        }
        return 0;
    }
}
