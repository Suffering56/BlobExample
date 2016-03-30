package blob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

public class MySqlConnector {

    private MySqlConnector() {
        //private!
    }

    /**
     * SingleTone.
     *
     * @return Новый экземпляр MySqlConnector при первом вызове данного метода.
     * При повторном вызове будет вовзращен уже существующий экземпляр.
     */
    public static MySqlConnector getInstance() {
        if (instance == null) {
            instance = new MySqlConnector();
        }
        return instance;
    }

    /**
     * Попытка установления соединения с БД MySQL. В случае ошибок на экране
     * появится информационное окно, с сообщением об ошибке и дополнительных
     * рекомендациях.
     *
     * @return объект Connection в случае успешного установления соединения.
     * null - в случае возникновения каких-либо ошибок.
     */
    public Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке драйвера MySQL (Class.forName). \r\n"
                    + "ex.toString(): " + ex.toString(), "MySqlConnector.connect()", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "");

        String url = "jdbc:mysql://" + "localhost" + ":3306" + "/" + "mystore";

        try {
            connection = DriverManager.getConnection(url, properties);
        } catch (SQLException ex) {
            disconnect();
            String errorMsg = "Неизвестная ошибка.";

            if (ex.toString().contains("Communications link failure")) {
                errorMsg = "Ошибка доступа к серверу MySQL. Возможные причины:\r\n"
                        + "На вашем компьютере отсутствует соединение с интернетом.\r\n"
                        + "Указан неверный IP-адрес или порт сервера. Проверьте настройки MySQL.\r\n"
                        + "На сервере произошли технические неполадки. Обратитесь к системному администратору.";
            } else if (ex.toString().contains("Access denied")) {
                errorMsg = "Неверный логин и/или пароль. Проверьте настройки MySQL.";
            } else if (ex.toString().contains("Unknown database")) {
                errorMsg = "Не удается найти указанную базу данных. Проверьте настройки MySQL.";
            }
            JOptionPane.showMessageDialog(null, "MySQL. Ошибка при установлении соединения с базой данных.\r\n"
                    + errorMsg + "\r\n" + "ex.toString(): " + ex.toString(), "MySqlConnector.connect()", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    /**
     * Попытка корректного закрытия соединения с БД MySQL. В случае ошибки будет
     * отображено соответствующее сообщение. В любом случае переменной
     * connection будет присвоено значение null.
     *
     * @return
     */
    public boolean disconnect() {
        boolean result = false;
        try {
            if (connection != null) {
                connection.close();
            }
            result = true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка при закрытии соединения: <connection.close()>. \r\n"
                    + "ex.toString(): " + ex, "MySqlConnector.disconnect()", JOptionPane.ERROR_MESSAGE);
        } finally {
            connection = null;
        }
        return result;
    }

    private static MySqlConnector instance = null;
    private Connection connection;
}
