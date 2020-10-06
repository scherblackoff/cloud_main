import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final String path = "server/src/main/resources/server_dir/";
    private static DataOutputStream os;
    private static DataInputStream is;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Server Start");
            Socket socket = serverSocket.accept();
            System.out.println("Client accept");
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String fileName = is.readUTF();
                if (fileName.equals("./getFilesList")) {
                    getFilesList();
                }else if(fileName.contains("./doDownload/")){
                    doDownload(fileName);
                }else {
                    doUpload(fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Данный метод реализовал так же как передачу файла серверу от клиента, сомневаюсь,
    чот это оптимальный способ передать данные т.к. не нравится реализация определения сервера,
    что данные с него хотят закачать обратно
     */

    private static void doDownload(String fileName) throws IOException {
        File file = new File(fileName.replace("./doDownload/", ""));
        File current = new File(path  + file);
        os.writeLong(current.length());
        FileInputStream fis = new FileInputStream(current);
        int tmp;
        byte[] buffer = new byte[8192];
        while ((tmp = fis.read(buffer)) != -1) {
            os.write(buffer, 0, tmp);
        }
    }
    private static void doUpload(String fileName) throws IOException {
        File file = new File(path + fileName);
        if (file.exists()) {
            System.out.println("ERROR");
        }
        FileOutputStream fos = new FileOutputStream(file);
        long fileLength = is.readLong();
        byte[] buffer = new byte[8192];
        System.out.println("Wait " + fileLength + " bytes");
        for (int i = 0; i < (fileLength + 8191) / 8192; i++) {
            int cnt = is.read(buffer);
            fos.write(buffer, 0, cnt);
        }
        System.out.println("File " + "[" + fileName + "]" + " saved");
        fos.close();
    }
    private static void getFilesList() throws IOException {
        File dir = new File(path);
        String[] files = dir.list();
        if (files != null) {
            os.writeInt(files.length);
            for (String file : files) {
                os.writeUTF(file);
            }
        } else {
            os.writeInt(0);
        }
        os.flush();
    }
}

