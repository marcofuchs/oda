/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import fu.keys.LSIClass;
import fu.util.ConcaveHullGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pp.dorenda.client2.additional.UniversalPainterWriter;

/**
 *
 * @author marco
 */
public class Writer
{
    public static void WritePoints(List<Crossing> crossings, double startlat, double startlon, int minutes)
    {
        try
        {            
            System.out.println("Oeffne results.txt");
            UniversalPainterWriter writer = new UniversalPainterWriter("result.txt");
            
            // Startpunkt muss immer zuerste geschrieben werden,
            // da das Kartentool beim Laden den ersten Punkt in der Datei fokussiert!
            System.out.println("Schreibe den Startpunkt.");
            //ArrayList<double[]> newlatlons = ConcaveHullGenerator.concaveHull(latlons, 0.2);
            writer.position(startlat / 1000000.0, startlon / 1000000.0, 0, 0, 0, 255, 10, "Start fuer " + minutes + " Minuten");
            
            ArrayList<double[]> latlons = new ArrayList<>();
            for (int i = 0; i < crossings.size(); i++)
            {
                double[] ll = new double[2];
                ll[0] = (double)crossings.get(i).getLat() / 1000000;
                ll[1] = (double)crossings.get(i).getLon() / 1000000;
                latlons.add(ll);
                
                //if (crossings.get(i).getSpeedLimitIfLink() != 0)
                {
                    //writer.position(ll[0], ll[1], 0, 0, 0, 255, 10, "" + crossings.get(i).getSpeedLimitIfLink());
                }
            }
            
            crossings.clear();
            crossings = null;
            
            System.out.println("Berechne die konkave Huelle ueber " + latlons.size() +  " Punkte.");
            ArrayList<double[]> newlatlons = ConcaveHullGenerator.concaveHull(latlons, 0.05);
            
            System.out.println("Schreibe results.txt");
            writer.polygon(newlatlons, 0, 0, 255, 50);
            
            System.out.println("Schließe results.txt");
            writer.close();
            
            for (Map.Entry<Integer, LSIClass> entry : ObjectTypes.allClasses().entrySet())
            {
                System.out.println(entry.getKey() + ": " + entry.getValue().className);
            }
            
            System.out.println("Fertig.");
        } catch (IOException ex)
        {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
