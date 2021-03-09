import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileToUpload extends AbstractMessage{

    private final String fileName;
    private final byte[] fileData;
    private String userLogin;

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public FileToUpload(Path path) throws IOException {
        this.fileName = path.getFileName().toString();
        this.fileData = Files.readAllBytes(path);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }
}
