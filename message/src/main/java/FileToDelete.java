public class FileToDelete extends AbstractMessage {

    private String nameFile;
    private String userName;

    public FileToDelete(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
