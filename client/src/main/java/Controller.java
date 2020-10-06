
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    public ListView<String> listViewClient;
    public ListView<String> listViewServer;


    public List<String> filesServer = new ArrayList<>();
    public List<String> filesClient = new ArrayList<>();
    private DataOutputStream os;
    private DataInputStream is;
    private String path;
    private User user;
    private DatabaseHandler dbHandler = new DatabaseHandler();


    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) {
        String file = listViewClient.getSelectionModel().getSelectedItem();
        if (!filesServer.contains(file)) {
            try {
                filesServer.add(file);
                os.writeUTF(file);
                File current = new File(path + "/" + file);
                os.writeLong(current.length());
                FileInputStream fis = new FileInputStream(current);
                int tmp;
                byte[] buffer = new byte[8192];
                while ((tmp = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, tmp);
                }
                dbHandler.uploadFileDb(file, user);
                listViewServer.getItems().add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(file);
        } else {
            System.out.println("File already exist");
        }
    }

    public void download(ActionEvent actionEvent) {
        String file = listViewServer.getSelectionModel().getSelectedItem();
        if (!filesClient.contains(file) && filesServer.contains(file)) {
            try {
                os.writeUTF("./doDownload/" + file);
                os.flush();
                File fileFromServer = new File(path + "/" + file);
                FileOutputStream fos = new FileOutputStream(fileFromServer);
                long fileLength = is.readLong();
                byte[] buffer = new byte[8192];
                System.out.println("Wait " + fileLength + " bytes");
                for (int i = 0; i < (fileLength + 8191) / 8192; i++) {
                    int cnt = is.read(buffer);
                    fos.write(buffer, 0, cnt);
                }
                System.out.println("File " + "[" + file + "]" + " saved");
                fos.close();
                listViewClient.getItems().add(file);
                filesClient.add(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update(ActionEvent actionEvent) {
        if (!listViewServer.getItems().isEmpty() || !listViewClient.getItems().isEmpty()) {
            listViewServer.getItems().clear();
            listViewClient.getItems().clear();
            refresh();
            System.out.println(user.getPassword());
        }
        System.out.println(path);
    }



    /*Метод для получения файлов с сервера если убрать личные файлы пользователей

    private List<String> getServerFiles() throws IOException {
        os.writeUTF("./getFilesList");
        os.flush();
        int listSize = is.readInt();
        for (int i = 0; i < listSize; i++) {
            String fileName = is.readUTF();
            if (!filesServer.contains(fileName)) {
                filesServer.add(fileName);
            }
        }
        return filesServer;
    }*/

    public void refresh() {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            for (File currentFile : files) {
                String name = currentFile.getName();
                /*Long size = currentFile.length();
                int nameLength = name.length();*/
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(name);

                /*Не понимаю почему при выводе разное расстояние от имени файла до его размера

               for (int i = 0; i < (70 - nameLength); i++) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(size + "byte");*/
                listViewClient.getItems().add(String.valueOf(stringBuilder));
                if (!filesClient.contains(name)) {
                    filesClient.add(name);
                }
            }
        }/*try and catch нужен если захотим закачивать названия с сервера
        try {*/
            listViewServer.getItems().addAll(dbHandler.getServerFiles(user));
            filesServer.addAll(dbHandler.getServerFiles(user));
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void sortWords(ActionEvent actionEvent) {
        if (!listViewClient.getItems().isEmpty() || !listViewServer.getItems().isEmpty()) {
            Collections.sort(filesClient);
            Collections.sort(filesServer);
            listViewClient.getItems().clear();
            listViewServer.getItems().clear();
            listViewClient.getItems().addAll(filesClient);
            listViewServer.getItems().addAll(filesServer);
        }
    }

    public void signIn(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("signIn.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        SignInController childrenSignInController = loader.getController();
        childrenSignInController.setParent(this);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public void signUp(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("signUp.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        SignUpController childrenSignUpController = loader.getController();
        childrenSignUpController.setParent(this);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public void init(User user){
        System.out.println(user.getLogin());
        this.user = user;
        this.path = "client/src/main/resources/" + user.getLogin() + "_dir";
        refresh();
    }
}
