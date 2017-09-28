/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    
    public ParamsEntity getParamsByFirmId(int id)  {
        try {
            ParamsEntity entity =null;
            PreparedStatement st = connection.prepareStatement(
              "SELECT * FROM params_firm WHERE firm=? ORDER BY id DESC LIMIT 1");
            st.setString(1, String.valueOf(id));
            ResultSet result_set = st.executeQuery();       
            while (result_set.next()) {
                int start_row = result_set.getInt("start_row");
                int max_row = result_set.getInt("max_row");
                int count_col = result_set.getInt("count_col");
                int summ_col = result_set.getInt("summ_col");
                int artikul_col = result_set.getInt("artikul_col");
                entity =new ParamsEntity(start_row, max_row, 
                                                count_col, summ_col, artikul_col);
            }
            return entity ;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
}
