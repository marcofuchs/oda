/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import java.util.List;

/**
 *
 * @author marco
 */
public class Isochrone 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // TODO code application logic here
        if (args.length == 0 || !Datareader.createStaticNavData(args[1]))
        {
            System.out.println("NavData konnte nicht erzeugt werden. Programm wird beendet.");
            return;
        }
        
        double lat = Double.parseDouble(args[2]) * 1000000;
        double lon = Double.parseDouble(args[3]) * 1000000;
        int mins = Integer.parseInt(args[4]);//80;//
        
        Crossing cr = Datareader.ReadNearestCrossing((int)lat, (int)lon);
        // cr.driveMinutes(mins, null);
        List<Crossing> allPoints = Crossing.drive(mins, cr);
        
        Datareader.disposeStaticNavData();
        //List<Point> points = Point.getAllPoints();
        
        Writer.WritePoints(allPoints, lat, lon, mins);
    }
    
    public static int[] Astar()
    {
        return null;
    }
}
