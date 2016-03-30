package blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Blob {

    public static void main(String[] args) {
        //read();
        write();
    }

    private static void write() {
        MySqlConnector instance = MySqlConnector.getInstance();
        Connection conn = instance.connect();
        PreparedStatement pre = null;
        try {
            File file = new File("E:\\11.jpg");
            FileInputStream fin = new FileInputStream(file);

            pre = conn.prepareStatement("insert into images (pic) values(?)");
            pre.setBinaryStream(1, fin, (int) file.length());
            pre.executeUpdate();

        } catch (SQLException | IOException ex) {
            Logger.getLogger(Blob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Blob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void read() {
        MySqlConnector instance = MySqlConnector.getInstance();
        Connection conn = instance.connect();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from images");
            while (rs.next()) {
                int id = rs.getInt("deparment");

                File image = new File("E:\\" + id + ".png");
                FileOutputStream fos = new FileOutputStream(image);

                byte[] buffer = new byte[1];
                InputStream is = rs.getBinaryStream("pic");
                while (is.read(buffer) > 0) {
                    fos.write(buffer);
                }
                fos.close();
            }

        } catch (SQLException | IOException ex) {
            Logger.getLogger(Blob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Blob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
