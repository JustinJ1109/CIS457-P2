public class FileData {
    public String fileName;
    public String description;
    public UserData user;

    public FileData(UserData user, String fileName, String description) {
        this.user = user;
        this.description = description;
        this.fileName = fileName;
    }

    public UserData getUser() {
        return this.user;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFileName() {
        return this.fileName;
    }
}

