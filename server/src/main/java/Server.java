import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static String path = "server/src/main/resources/";

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            System.out.println("Server Start");
            Socket socket = serverSocket.accept();
            System.out.println("Client accept");
            DataInputStream is = new DataInputStream(socket.getInputStream());
            while (true){
                String fileName = is.readUTF();
                File file = new File(path + fileName);
                if(!file.exists()){
                    file.createNewFile();
                }else{
                    System.out.println("ERROR");
                }
                FileOutputStream fos = new FileOutputStream(file);
                long fileLength = is.readLong();
                byte[] buffer = new byte[256];
                System.out.println("Wait " + fileLength + " bytes");
                for (int i = 0; i < (fileLength + 255) / 256; i++) {
                    int cnt = is.read(buffer);
                    fos.write(buffer, 0, cnt);
                }
                System.out.println("File " + "[" + fileName + "]" + " saved");
                fos.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

