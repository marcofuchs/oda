/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import fu.esi.SQL;
import fu.keys.LSIClassCentreDB;
import fu.util.DBUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marco
 */
public class DbConnection
{
    private static Connection connection;
    private static boolean alreadyConnected = false;

    /**
     *
     * @param connectionString
     * @return
     */
    public static boolean Connect(String connectionString)
    {
        try {
            DBUtil.parseDBparams(connectionString);
            connection=DBUtil.getConnection();
            connection.setAutoCommit(false);
            LSIClassCentreDB.initFromDB(connection);

            return alreadyConnected = true;
        }
        catch (Exception e) {
            return alreadyConnected = false;
        }
    }

    /**
     *
     * @param lowerBound
     * @param upperBound
     * @param polygon
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    public static List<kefu.Point> GetPossibleInRangeObjects(int lowerBound, int upperBound, Geometry polygon) throws SQLException, ParseException
    {
        if (!alreadyConnected)
        {
            return null;
        }

        List<kefu.Point> list = new ArrayList<>();
        ResultSet resultSet;
        Statement statement;

        Envelope boundingBox = polygon.getEnvelopeInternal();

        statement=connection.createStatement();
        statement.setFetchSize(5000);

        String query = "SELECT realname, bounding_circle_lat, bounding_circle_lon, geodata_line, geodata_point, gao_geometry FROM domain WHERE ((lsiclass1 BETWEEN "
            + (lowerBound) + " AND " + (upperBound) + ") OR (lsiclass2 BETWEEN "
            + (lowerBound) + " AND " + (upperBound) + ") OR (lsiclass3 BETWEEN "
            + (lowerBound) + " AND " + (upperBound)+ ")) AND ("
            + SQL.createIndexQuery(boundingBox.getMinX(),boundingBox.getMaxY(),boundingBox.getMaxX(),boundingBox.getMinY(), SQL.COMPLETELY_INSIDE) + ")"
            ;

        resultSet =
            statement.executeQuery(query);//"SELECT * FROM domain WHERE lsiclass1 = " + lowerBound);

        int cnt = 0;
        GeometryFactory geomfact=new GeometryFactory();

        while (resultSet.next()) {
            String realname=resultSet.getString(1);
            //double lat=resultSet.getDouble(2);
            //double lon=resultSet.getDouble(3);
            byte[] geodata=resultSet.getBytes(4);

            //System.out.println(realname + ": " + lat + "; " + lon);

            if (geodata == null)
            {
                geodata=resultSet.getBytes(5);
            }
            if (geodata == null)
            {
                geodata=resultSet.getBytes(6);
            }
            if (geodata == null)
            {
                continue;
            }
            Geometry geom=SQL.wkb2Geometry(geodata);

            if (geom.within(polygon)) {                       // Exact geometrisch testen, ob die Geometry im Dreieck liegt
                                list.add(new kefu.Point(geom, realname));
                cnt++;
             }
        }
        resultSet.close();

        return list;
    }

    /**
     *
     */
    public static void Close()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                return;
            }

            connection.close();
        } catch (SQLException ex){
        }
    }

    private DbConnection()
    {
    }
}
