import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class Importer_Grid {

    public static void main(String[] args) {
        System.out.println("Grid Importer Start!!");
        String filename = System.getProperty("i", "unknown");
        NetcdfFile ncfile = null;
        try {
            ncfile = NetcdfDataset.openFile(filename, null);
            processNetcdfFile(ncfile);
        } catch (IOException ioe) {
            log("trying to open " + filename, ioe);
        } finally {
            if (null != ncfile) try {
                ncfile.close();
            } catch (IOException ioe) {
                log("trying to close " + filename, ioe);
            }
        }
    }

    private static void processNetcdfFile(NetcdfFile ncfile) {
        System.out.println("Processing grid...");
        ucar.ma2.Array net_lats = readVariable(ncfile, System.getProperty("lat"));
        ucar.ma2.Array net_lons = readVariable(ncfile, System.getProperty("lon"));
        String mask = System.getProperty("landmask", "none");
        ucar.ma2.Array net_mask = null;
        if (mask != "none") {
            net_mask = readVariable(ncfile, System.getProperty("landmask"));
        }
        int[] lat_shape = net_lats.getShape();
        int[] lon_shape = net_lons.getShape();
        ucar.ma2.Index lat_index = net_lats.getIndex();
        ucar.ma2.Index lon_index = net_lons.getIndex();
        if (lat_shape.length == 2) {
            for (int i = 0; i < lat_shape[0]; i++) {
                for (int j = 0; j < lon_shape[1]; j++) {
                    float lats = net_lats.getFloat(lat_index.set(i, j));
                    float lons = net_lons.getFloat(lon_index.set(i, j));
                    if (mask != "none") {
                        int zmask = (int) net_mask.getFloat(lat_index.set(i, j));
                        storeGrid(lats, lons, zmask);
                    } else {
                        storeGrid(lats, lons);
                    }
                }
            }
        } else if (lat_shape.length == 1) {
            for (int i = 0; i < lat_shape[0]; i++) {
                for (int j = 0; j < lon_shape[0]; j++) {
                    float lats = net_lats.getFloat(lat_index.set(i));
                    float lons = net_lons.getFloat(lon_index.set(j));
                    if (mask != "none") {
                        ucar.ma2.Index mask_index = net_mask.getIndex();
                        int zmask = (int) net_mask.getFloat(mask_index.set(i, j));
                        storeGrid(lats, lons, zmask);
                    } else {
                        storeGrid(lats, lons);
                    }
                }
            }
        }
    }

    public static void storeGrid(float lat, float lon) {
        storeGrid(lat, lon, -999);
    }

    public static void storeGrid(float lat, float lon, int landmask) {
        BigDecimal big_lat = new BigDecimal(lat);
        BigDecimal big_lon = new BigDecimal(lon);
        String zLat = big_lat.setScale(6, 6).toString();
        String zLon = big_lon.setScale(6, 6).toString();
        try {
            String url = "jdbc:postgresql://155.206.19.246/ODM-Gamma";
            Class.forName("org.postgresql.Driver");
            Connection db = DriverManager.getConnection(url, "postgis", "");
            Statement st = db.createStatement();
            st.executeUpdate("INSERT INTO mdm.grid (point_geom, landmask) VALUES (GeomFromText('POINT(" + zLon + " " + zLat + ")',4326)," + landmask + ")");
            st.close();
            db.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Caught ClassNotFoundException: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Caught SQLException: " + e.getMessage());
        }
    }

    private static void log(String string, IOException ioe) {
        System.out.println(string);
    }

    private static ucar.ma2.Array readVariable(NetcdfFile ncfile, String varName) {
        Variable v = ncfile.findVariable(varName);
        if (null == v) {
            ucar.ma2.Array none = null;
            return none;
        }
        try {
            int[] varShape = v.getShape();
            int[] origin = new int[varShape.length];
            int[] size = null;
            if (varShape.length == 2) {
                size = new int[] { varShape[0], varShape[1] };
            } else {
                size = new int[] { varShape[0] };
            }
            try {
                ucar.ma2.Array data = v.read(origin, size);
                return data;
            } catch (InvalidRangeException ioe) {
                System.out.println("Processing dataset...");
            }
        } catch (IOException ioe) {
            log("trying to read " + varName, ioe);
        }
        ucar.ma2.Array none = null;
        return none;
    }
}
