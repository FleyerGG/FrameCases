package ru.fleyer.framecases.database;

import com.zaxxer.hikari.HikariDataSource;
import ru.fleyer.framecases.FrameCases;
import ru.fleyer.framecases.logs.LogData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseConstructor {
    public static DatabaseConstructor INSTANCE = new DatabaseConstructor();
    FrameCases instance = FrameCases.getInstance();
    HikariDataSource hikari = instance.getHikari();
    String table = FrameCases.getInstance().config().yaml().getString("mysql.table");

    public void START(){
        createTable("cases","player VARCHAR(40) NOT NULL");
        createTable("cases_history","casename VARCHAR(40)", "player VARCHAR(40)", "prize VARCHAR(40)", "datetime BIGINT");
        for (String s : instance.config().yaml().getConfigurationSection("cases").getKeys(false)){
            addColumn("cases", s + "_amount INT DEFAULT 0");
            addColumn("cases", s + "_opens INT DEFAULT 0");
        }
    }
    public void createTable(String table,String ... column){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS " + table + " (id INT NOT NULL AUTO_INCREMENT, " + String.join(", ", column) + ", PRIMARY KEY ( id ) );")){
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addColumn(String table, String column){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("ALTER TABLE " + table + " ADD " + column)){
            statement.executeUpdate();

        } catch (SQLException e) {
            if (true) return;
            e.printStackTrace();
        }
    }

    public int[] getCase(String player, String caseName){
        int[] arr = new int[2];
        try (Connection connection = hikari.getConnection();
            PreparedStatement statement = prepareStatement(connection,"SELECT " + caseName + "_amount, " + caseName + "_opens FROM cases WHERE player=?", ps -> {
                ps.setString(1,player);
                ps.execute();
            });
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()){
                arr = new int[]{resultSet.getInt(caseName + "_amount"), resultSet.getInt(caseName + "_opens")};
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public boolean containsPlayer(String player){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = prepareStatement(connection,"SELECT * FROM cases WHERE player=?",ps -> {
                 ps.setString(1,player);
                 ps.execute();
             });
             ResultSet resultSet = statement.executeQuery()){
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void giveCase(String player, String caseName, int amount){
        try (Connection connection = hikari.getConnection();
             PreparedStatement INSERT = connection.prepareStatement("INSERT INTO cases(`player`, `" + caseName + "_amount`) VALUES (?,?)");
             PreparedStatement UPDATE = connection.prepareStatement("UPDATE cases SET `" + caseName + "_amount` = " + caseName + "_amount + " + amount + " WHERE player=?")){



            if (containsPlayer(player)){
                UPDATE.setString(1,player);
                UPDATE.executeUpdate();
            }else {
                INSERT.setString(1,player);
                INSERT.setInt(2,amount);
                INSERT.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void decKey(String player, String caseName){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE cases SET " + caseName + "_amount = " + caseName + "_amount - 1, " + caseName + "_opens = " + caseName + "_opens + 1 WHERE player=?")){
            statement.setString(1,player);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCaseHistory(String player,String caseName, String prize){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO cases_history (casename, player, prize, datetime) VALUES (?,?,?,?)")){
            statement.setString(1,caseName);
            statement.setString(2,player);
            statement.setString(3,prize);
            statement.setLong(4,new Date().getTime());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<LogData> getCaseHistory (String caseName){
        ArrayList<LogData> list = new ArrayList<>();
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = prepareStatement(connection,"SELECT * FROM cases_history WHERE casename = ? ORDER BY datetime DESC LIMIT 5",ps -> {
                 ps.setString(1,caseName);
                 ps.execute();
             });
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()){
                list.add(new LogData(caseName,resultSet.getString("player"),resultSet.getString("prize"),resultSet.getLong("datetime")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }
    public static PreparedStatement prepareStatement(Connection connection, String sql, PreparedStatementSetter setter) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        setter.setValues(ps);
        return ps;
    }
}
