
import Animation.Shake;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Controller implements Initializable {

    public ListView<String> listViewClient;
    public ListView<String> listViewServer;


    public List<String> filesServer = new ArrayList<>();
    public List<String> filesClient = new ArrayList<>();
    public TextField searchField;
    public Button selectButton;
    public MenuItem exitItem;
    public Menu sortButton;
    public Button uploadButton;
    public Button deleteButton;
    public Button downloadButton;
    private String path;
    private User user;
    private final Network network = new Network();

    public Network getNetwork() {
        return network;
    }

    public void initialize(URL location, ResourceBundle resources) {
        //Пока клиент не вошел, он не может увидитеть кнопку сортировки и кнопку выхода
        sortButton.setVisible(false);
        exitItem.setVisible(false);
        network.start();
    }

    public void upload(ActionEvent actionEvent) {
        String file = listViewClient.getSelectionModel().getSelectedItem();
        if (!filesServer.contains(file) && listViewClient.getSelectionModel().getSelectedItem() != null) {
            try {
                FileToUpload fileToUpload = new FileToUpload(Paths.get(path + "/" + file));
                fileToUpload.setUserLogin(user.getLogin());
                network.sendMessage(fileToUpload);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> listViewServer.getItems().add(file));
                filesServer.add(file);
            }
        }else{
            Shake uploadAnim = new Shake(uploadButton);
            uploadAnim.playAnim();
        }
    }

    public void download(ActionEvent actionEvent) {
        String file = listViewServer.getSelectionModel().getSelectedItem();
        if (!filesClient.contains(file) && filesServer.contains(file) &&
                listViewServer.getSelectionModel().getSelectedItem() != null) {
            try {
                FileToDownload fileToDownload = new FileToDownload(file);
                fileToDownload.setNameUser(user.getLogin());
                network.sendMessage(fileToDownload);
                FileToUpload fileToUpload = (FileToUpload) network.readMessage();
                Files.write(Paths.get(path + "/" + fileToUpload.getFileName()),
                        fileToUpload.getFileData(), StandardOpenOption.CREATE);
                listViewClient.getItems().add(file);
                filesClient.add(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Shake downloadAnim = new Shake(downloadButton);
            downloadAnim.playAnim();
        }
    }


    public void refresh() {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            if (filesClient.size() != files.length) {
                updateClient(files);
            } else {
                for (File fileClient :
                        files) {
                    if (!filesClient.contains(fileClient.getName())) {
                        updateClient(files);
                        break;
                    }
                }
            }
        }

        List<String> filesFromServer = getFilesServer();
        if (filesFromServer != null) {
            if (filesServer.size() != filesFromServer.size()) {
                updateServer(filesFromServer);
            } else {
                for (String fileFromServer :
                        filesFromServer) {
                    if (!filesServer.contains(fileFromServer)) {
                        updateServer(filesFromServer);
                        return;
                    }
                }
            }
        }
    }

    public List<String> getFilesServer(){
        ServerFiles serverFiles = new ServerFiles(user.getLogin());
        network.sendMessage(serverFiles);
        try {
            serverFiles = (ServerFiles) network.readMessage();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return serverFiles.getServerFiles();
    }


    public void updateClient(File[] files) {
        filesClient.clear();
        listViewClient.getItems().clear();
        for (File fileForListClient :
                files) {
            filesClient.add(fileForListClient.getName());
            listViewClient.getItems().add(fileForListClient.getName());
        }
    }

    public void updateServer(List<String> filesFromServer) {
        listViewServer.getItems().clear();
        listViewServer.getItems().addAll(filesFromServer);
        filesServer.addAll(filesFromServer);
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

    public void init(User user) {
        this.user = user;
        this.path = "client/src/main/resources/" + user.getLogin() + "_dir";
        exitItem.setVisible(true);
        sortButton.setVisible(true);
        refresh();
    }


    public void exit(ActionEvent actionEvent) {
        listViewServer.getItems().clear();
        listViewClient.getItems().clear();
        filesServer.clear();
        filesClient.clear();
        sortButton.setVisible(false);
        exitItem.setVisible(false);
    }

    public void select(ActionEvent actionEvent) {
        if (listViewClient.getItems().contains(searchField.getText())
                || (listViewServer.getItems().contains(searchField.getText()))) {
            listViewClient.getSelectionModel().select(searchField.getText());
            listViewServer.getSelectionModel().select(searchField.getText());
        } else {
            Shake searchAnim = new Shake(searchField);
            searchAnim.playAnim();
        }
    }

    public void delete(ActionEvent actionEvent) {
        if(listViewServer.getSelectionModel().getSelectedItem() != null) {
            String file = listViewServer.getSelectionModel().getSelectedItem();
            FileToDelete fileToDelete = new FileToDelete(file);
            fileToDelete.setUserName(user.getLogin());
            network.sendMessage(fileToDelete);
            filesServer.remove(file);
            listViewServer.getItems().remove(file);
        }else{
            Shake deleteAnim = new Shake(deleteButton);
            deleteAnim.playAnim();
        }
    }

    public void update(ActionEvent actionEvent) {
        if (!listViewServer.getItems().isEmpty() || !listViewClient.getItems().isEmpty()) {
            Platform.runLater(this::refresh);
        }
    }
}

