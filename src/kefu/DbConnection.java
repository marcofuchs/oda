package kefu;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
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
 * @author marco Behandelt die Verbindung zur Datenbank zum Abrufen der
 * ObjektInformationen(Nicht navData)
 */
public class DbConnection {
    private static Connection connection;
    private static boolean alreadyConnected = false;

    /**
     *
     * @param connectionString die Anbindung an den DatenbankServer
     * @return ob das Verbinden mit der DB erfolgreich war
     */
    public static boolean connect(String connectionString) {
        try {
            System.out.println("Verbinde mit Datenbank...");
            DBUtil.parseDBparams(connectionString);
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            LSIClassCentreDB.initFromDB(connection);

            System.out.println("Fertig.");
            return alreadyConnected = true;
        } catch (Exception ex) {
            System.out.println("FEHLER: Verbindung zur Datenbank konnte nicht hergestellt werden.");
            System.out.println("Exception:");
            System.out.println(ex);
            System.out.println("Programm wird beendet.");
            return alreadyConnected = false;
        }
    }

    /**
     * Findet alle Objekte, die innerhalb der gegebenen Geometrie liegen, in der Datenbank
     * 
     * @param lowerBound Die Untergrenze der gewuenschten Objekttypen
     * @param upperBound Die Obergrenze der gewuenschten Objekttypen
     * @param polygon die Geometrie des Bereiches aus dem die Objekte geladen
     * werden sollen
     * @return Alle Objekte die sich in der gewuenschten Geometrie befinden
     * @throws SQLException
     * @throws ParseException
     */
    public static List<Location> getPossibleInRangeObjects(int lowerBound, int upperBound, Geometry polygon) throws SQLException, ParseException {
        if (!alreadyConnected) {
            return null;
        }

        List<Location> list = new ArrayList<>();
        ResultSet resultSet;
        Statement statement;

        Envelope boundingBox = polygon.getEnvelopeInternal();

        statement = connection.createStatement();
        statement.setFetchSize(5000);

        String query = "SELECT realname, bounding_circle_lat, bounding_circle_lon, geodata_line, geodata_point, gao_geometry FROM domain WHERE ((lsiclass1 BETWEEN "
                + (lowerBound) + " AND " + (upperBound) + ") OR (lsiclass2 BETWEEN "
                + (lowerBound) + " AND " + (upperBound) + ") OR (lsiclass3 BETWEEN "
                + (lowerBound) + " AND " + (upperBound) + ")) AND ("
                + SQL.createIndexQuery(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxX(), boundingBox.getMinY(), SQL.COMPLETELY_INSIDE) + ")";

        resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String realname = resultSet.getString(1);
            byte[] geodata = resultSet.getBytes(4);

            if (geodata == null) {
                geodata = resultSet.getBytes(5);
            }
            if (geodata == null) {
                geodata = resultSet.getBytes(6);
            }
            if (geodata == null) {
                continue;
            }
            Geometry geom = SQL.wkb2Geometry(geodata);

            if (geom.within(polygon)) {
                list.add(new Location(geom, realname));
            }
        }
        resultSet.close();

        return list;
    }

    /**
     * Finalisiert und schliesst die Verbindung
     */
    public static void close() {
        try {
            if (connection == null || connection.isClosed()) {
                return;
            }

            connection.close();
        } catch (SQLException ex) {
        }
    }

    private DbConnection() {
    }
}
