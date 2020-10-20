public class FileToDownload extends AbstractMessage {

    private String nameFile;
    private String nameUser;

    public FileToDownload(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
