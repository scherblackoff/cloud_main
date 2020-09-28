import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView<String> listViewClient;
    public ListView<String> listViewServer;
    private DataOutput os;
    private DataInputStream is;
    private String currentFile;
    private String path = "client/src/main/resources/client_dir";
    private String pathServer = "server/src/main/resources/server_dir";

    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            File file = new File(path);
            String[] files = file.list();
            File fileServer = new File(pathServer);
            String[] filesServer = fileServer.list();
            if (files != null) {
                for (String name : files) {
                    listViewClient.getItems().add(name);
                }
            }
            if (filesServer != null){
                for(String name : filesServer){
                    listViewServer.getItems().add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) {
        String file = listViewClient.getSelectionModel().getSelectedItem();
        try {
            os.writeUTF(file);
            File current = new File(path + "/" + file);
            os.writeLong(current.length());
            FileInputStream fis = new FileInputStream(current);
            int tmp;
            byte[] buffer = new byte[256];
            while ((tmp = fis.read(buffer)) != -1) {
                os.write(buffer, 0, tmp);
            }
            listViewServer.getItems().add(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(file);
    }
}
