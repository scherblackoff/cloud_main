import java.util.List;

public class ServerFiles extends AbstractMessage{

    private List<String> serverFiles;
    private String login;

    public ServerFiles(String login){
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public List<String> getServerFiles() {
        return serverFiles;
    }

    public void setServerFiles(List<String> serverFiles) {
        this.serverFiles = serverFiles;
    }
}
