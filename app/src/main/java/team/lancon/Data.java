package team.lancon;

import java.io.Serializable;
import java.util.List;
import java.util.HashMap;

public class Data implements Serializable, Cloneable {

    public String message;
    public String sendersUserIp, receiversUserIp;
    private List<HashMap<String, Object>> userList;

    // Fields for file transfer
    private String fileName;
    private long fileSize;
    private String fileType;
    private byte[] fileData;

    public void setUserList(List<HashMap<String, Object>> userList) {
        this.userList = userList;
    }

    public List<HashMap<String, Object>> getUserList() {
        return userList;
    }

    // File transfer methods
    public void setFileData(String fileName, long fileSize, String fileType, byte[] fileData) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}