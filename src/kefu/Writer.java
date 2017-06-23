/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.ParseException;
import fu.util.ConcaveHullGenerator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pp.dorenda.client2.additional.UniversalPainterWriter;

/**
 *
 * @author marco
 */
public class Writer
{
    private static final GeometryFactory geomfact=new GeometryFactory();

    /**
     *
     * @param crossings
     * @param startlat
     * @param startlon
     * @param minutes
     * @param lowerbound
     * @param upperbound
     * @throws SQLException
     * @throws ParseException
     */
    public static void WritePoints(Crossing[] crossings, double startlat, double startlon, int minutes, String lowerbound, String upperbound) throws SQLException, ParseException
    {
        try
        {            
            System.out.println("Oeffne results.txt");
            UniversalPainterWriter writer = new UniversalPainterWriter("result.txt");
            
            // Startpunkt muss immer zuerste geschrieben werden,
            // da das Kartentool beim Laden den ersten Punkt in der Datei fokussiert!
            System.out.println("Schreibe den Startpunkt.");
            writer.position(startlat / 1000000.0, startlon / 1000000.0, 0, 0, 0, 255, 10, "Start fuer " + minutes + " Minuten");
            
            ArrayList<double[]> latlons = new ArrayList<>();
            for (Crossing crossing : crossings)
            {
                double[] ll = new double[2];
                ll[0] = (double) crossing.getLat() / 1000000;
                ll[1] = (double) crossing.getLon() / 1000000;
                latlons.add(ll);
            }
            
            crossings = null;
            
            double alpha = 0.01;
            
            System.out.println("Berechne die konkave Huelle ueber " + latlons.size() +  " Punkte.");
            ArrayList<double[]> newlatlons = ConcaveHullGenerator.concaveHull(latlons, alpha);
            
            List<Coordinate> coords = new ArrayList<>();
            newlatlons.forEach((point) ->
            {
                coords.add(new Coordinate(point[1], point[0]));
            });

            coords.add(coords.get(0));

            Coordinate[] coordArray = new Coordinate[coords.size()];
            coords.toArray(coordArray);
            //coords[3]=coords[0];
            Geometry polygon = geomfact.createPolygon(geomfact.createLinearRing(coordArray),new LinearRing[0]);//geomfact.createLinearRing(coordArray),new LinearRing[0]);

            List<Point> geoms = DbConnection.GetPossibleInRangeObjects(Integer.parseInt(lowerbound), Integer.parseInt(upperbound), polygon);
            
            
            System.out.println("Schreibe results.txt");
            writer.polygon(newlatlons, 0, 0, 255, 50);
            
            geoms.forEach((geom) ->
            {
                writer.flag(geom.getGeo().getCentroid().getY(), geom.getGeo().getCentroid().getX(), (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 255, geom.getName().replace('"', '\''));
            });
            
            System.out.println("Schließe results.txt");
            writer.close();
            
            System.out.println("Fertig.");
        } catch (IOException ex)
        {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Writer()
    {
    }
}
