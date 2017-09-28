/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author vadim
 */
public class DataAccessor {
    private Connection connection ;
    public DataAccessor(String driverClassName, String dbURL, String user, String password)  {
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(dbURL, user, password);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public List<FirmEntity> getFirmList()  {
        try {
            Statement statement = connection.createStatement();
            ResultSet result_set = statement.executeQuery("select * from firms");       
            List<FirmEntity> firmList = new ArrayList<>();
            while (result_set.next()) {
                String id = result_set.getString("id");
                String name = result_set.getString("name");
                FirmEntity firm=new FirmEntity(Integer.valueOf(id), name);
                firmList.add(firm);
            }
            return firmList ;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
}
