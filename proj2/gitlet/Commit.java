package gitlet;

// TODO: any imports you need here

import static gitlet.Branch.BRANCHE_DIR;
import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;


/** Represents a gitlet commit object.
 *  Immutable
 *
 *  does at a high level.
 *
 *  @author Jasper
 */
public class Commit implements Serializable {
    /*
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public static final File COMMITS_DIR = Utils.join(Repository.GITLET_DIR, "commits");
    private String firstParent;
    private String secondParent;
    private String message;
    private Date timeStamp;
    private TreeMap<String, String> blobsMapName;
    private TreeMap<String, String> blobsMapSha1;
    private TreeMap<String, String> rmBlobsMap;

    public Commit() {
        this.firstParent = null;
        this.secondParent = null;
        this.message = "initial commit";
        this.timeStamp = new Date(0);
        this.blobsMapName = new TreeMap<>();
        this.blobsMapSha1 = new TreeMap<>();
        this.rmBlobsMap = new TreeMap<>();
    }

    public Commit(String firstParent, String secondParent, String message,
        TreeMap<String, String> blobsMapName, TreeMap<String, String> blobsMapSha1,
        TreeMap<String, String> rmBlobsMap) {
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.message = message;
        this.timeStamp = new Date();
        this.blobsMapName = blobsMapName;
        this.blobsMapSha1 = blobsMapSha1;
        this.rmBlobsMap = rmBlobsMap;
    }

    public String getFirstParent() {
        return firstParent;
    }

    public String getSecondParent() {
        return secondParent;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public TreeMap<String, String> getBlobsMapName() {
        return blobsMapName;
    }

    public TreeMap<String, String> getBlobsMapSha1() {
        return blobsMapSha1;
    }

    public TreeMap<String, String> getRmBlobsMap() {
        return rmBlobsMap;
    }

    public void save(String branchName) {
        byte[] bytes = serialize(this);
        String temp = sha1(bytes);
        File currentCommitFile = join(COMMITS_DIR, temp);
        File branchFile = Utils.join(BRANCHE_DIR, branchName);
        Utils.writeObject(currentCommitFile, this);
        Utils.writeContents(branchFile, temp);
    }

    @Override
    public String toString() {
        return "Commit{" +
            "firstParent='" + firstParent + '\'' +
            ", secondParent='" + secondParent + '\'' +
            ", message='" + message + '\'' +
            ", timeStamp=" + timeStamp +
            ", blobsMapName=" + blobsMapName +
            ", blobsMapSha1=" + blobsMapSha1 +
            ", rmBlobsMap=" + rmBlobsMap +
            '}';
    }
}
