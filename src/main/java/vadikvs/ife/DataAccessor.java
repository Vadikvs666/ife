/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vadim
 */
public class DataAccessor {

    private Connection connection;

    public DataAccessor(String driverClassName, String dbURL, String user, String password) {
        try {
            Class.forName(driverClassName);
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");
            connection = DriverManager.getConnection(dbURL, properties);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<FirmEntity> getFirmList() {
        try {
            Statement statement = connection.createStatement();
            ResultSet result_set = statement.executeQuery("select * from firms");
            List<FirmEntity> firmList = new ArrayList<>();
            while (result_set.next()) {
                String id = result_set.getString("id");
                String name = result_set.getString("name");
                FirmEntity firm = new FirmEntity(Integer.valueOf(id), name);
                firmList.add(firm);
            }
            return firmList;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ParamsEntity getParamsByFirmId(int id) {
        try {
            ParamsEntity entity = null;
            PreparedStatement st = connection.prepareStatement(
                    "SELECT * FROM params_excel WHERE firm=? ORDER BY id DESC LIMIT 1");
            st.setString(1, String.valueOf(id));
            ResultSet result_set = st.executeQuery();
            while (result_set.next()) {
                int start_row = result_set.getInt("start_row");
                int max_row = result_set.getInt("max_row");
                int count_col = result_set.getInt("count_col");
                int summ_col = result_set.getInt("summ_col");
                int artikul_col = result_set.getInt("articul_col");
                int name_col = result_set.getInt("name_col");
                int number_col = result_set.getInt("number_col");
                int number_row = result_set.getInt("number_row");
                entity = new ParamsEntity(start_row, max_row,
                        count_col, summ_col, artikul_col,
                        name_col, number_col, number_row, id);
            }
            return entity;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean insertParams(ParamsEntity entity) {
        try {

            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO params_excel "
                    + "(start_row,max_row,count_col,summ_col,articul_col,"
                    + "number_col,number_row,firm,name_col) "
                    + "VALUES(?,?,?,?,?,?,?,?,?)");
            st.setInt(1, entity.getStart_row());
            st.setInt(2, entity.getMax_row());
            st.setInt(3, entity.getCount_col());
            st.setInt(4, entity.getSumm_col());
            st.setInt(5, entity.getArtikul_col());
            st.setInt(6, entity.getNumber_col());
            st.setInt(7, entity.getNumber_row());
            st.setInt(8, entity.getFirm());
            st.setInt(9, entity.getName_col());
            return st.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertIfe(Ife entity) {
        try {

            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO ife "
                    + "(created_at,firm,addition,doc_number,data, hash)"
                    + "VALUES(?,?,?,?,?,?)");
            st.setDate(1, Date.valueOf(entity.getCreatedAt()));
            st.setInt(2, entity.getFirm());
            st.setFloat(3, entity.getAddition());
            st.setString(4, entity.getDocNumber());
            st.setString(5, entity.getData());
            st.setString(6, entity.getHash());
            return st.execute();

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
