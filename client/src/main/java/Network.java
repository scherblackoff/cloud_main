import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Network {

    private static Socket socket;
    private static ObjectEncoderOutputStream encoder;
    private static ObjectDecoderInputStream decoder;
    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    public void start(){
        try{
            socket = new Socket(HOST,PORT);
            encoder = new ObjectEncoderOutputStream(socket.getOutputStream());
            decoder = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(AbstractMessage msg){
        try {
            encoder.writeObject(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public AbstractMessage readMessage() throws IOException, ClassNotFoundException {
        Object object = decoder.readObject();
        return (AbstractMessage) object;
    }
}
