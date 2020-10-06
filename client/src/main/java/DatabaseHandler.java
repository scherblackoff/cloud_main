
import jdk.nashorn.internal.objects.annotations.Where;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends Configs{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException{
        String connectionString = "jdbc:mysql://" + dbHost + ":" +
                dbPort + "/" + dbName +
                "?useUnicode=true&serverTimezone=UTC&useSSL=false";
        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }


    public void signUpUser(User user){
        String insert = "INSERT INTO " + Const.CLIENTS_TABLE + "(" + Const.CLIENTS_LOGIN + "," +
                Const.CLIENTS_PASSWORD + ")" + "VALUES(?,?)";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, user.getLogin());
            prSt.setString(2, user.getPassword());
            prSt.executeUpdate();
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    //Метод проверки что ползователь с таким логином уже существует
    public boolean clientIsExist(String login){
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Const.CLIENTS_TABLE + " WHERE " + Const.CLIENTS_LOGIN + "= ?";
        try {
             PreparedStatement prSt = getDbConnection().prepareStatement(select);
             prSt.setString(1, login);
             resultSet = prSt.executeQuery();
            if (resultSet.next()){
                return true;
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public ResultSet getUser(User user){
        ResultSet resultSet = null;

        String select = "SELECT * FROM " + Const.CLIENTS_TABLE + " WHERE " + Const.CLIENTS_LOGIN + "= ? AND "
                + Const.CLIENTS_PASSWORD + " = ?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getLogin());
            prSt.setString(2, user.getPassword());
            resultSet = prSt.executeQuery();
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }

        return resultSet;
    }


    /*Данный метод собирался использовать для предоставления файлов принадлежащих именно авторизированному пользователю
    но возникла проблема с передачей информации о user из SignInController в Controller в связи с чем делать это не
    удалось */


    public List<String> getServerFiles(User user){
        ResultSet resultSet = null;
        List<String> serverFiles = new ArrayList<>();
        String select = "SELECT " + Const.FILES_TABLE + "." + Const.FILES_NAME_FILE + " FROM " + Const.FILES_TABLE +
        " INNER JOIN " + Const.CLIENTS_TABLE + " ON " + Const.FILES_TABLE + "." + Const.FILES_ID_CLIENT +
        " = " + Const.CLIENTS_TABLE + "." + Const.CLIENTS_ID_CLIENT + " WHERE " + Const.CLIENTS_LOGIN + " = ?";

        try{
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getLogin());
            resultSet = prSt.executeQuery();
            while (resultSet.next()){
                serverFiles.add(resultSet.getString(1));
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return serverFiles;
    }


    //Метод получения id пользователя что бы сортировать файлы в директории сервера для каждого пользователя

    public String getUserId(User user){
        int id = 0;
        ResultSet resultSet = null;
        String select = "SELECT " + Const.CLIENTS_TABLE + "." + Const.CLIENTS_ID_CLIENT + " FROM " + Const.CLIENTS_TABLE +
                " WHERE " + Const.CLIENTS_LOGIN + " = ?";
        try{
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getLogin());
            resultSet = prSt.executeQuery();
            while (resultSet.next()){
                id = resultSet.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(id);
    }

    //Метода добавления файла в бд

    public void uploadFileDb(String fileName, User user){
        String insert = "INSERT INTO " + Const.FILES_TABLE + "(" + Const.FILES_NAME_FILE + "," +
                Const.FILES_ID_CLIENT + ")" + "VALUES(?,?)";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, fileName);
            prSt.setString(2, getUserId(user));
            prSt.executeUpdate();
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
