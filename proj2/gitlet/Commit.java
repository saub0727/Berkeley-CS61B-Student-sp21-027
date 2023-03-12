package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;


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
    private TreeMap<String, String> blobsMap;
    private TreeSet<String> blobsSet;
    private TreeSet<String> rmBlobsSet;

    public Commit() {
        this.firstParent = null;
        this.secondParent = null;
        this.message = "initial commit";
        this.timeStamp = new Date(0);
        this.blobsMap = null;
        this.blobsSet = null;
        this.rmBlobsSet = null;
    }

    public Commit(String firstParent, String secondParent, String message,
        TreeMap<String, String> blobsMap, TreeSet<String> blobsSet, TreeSet<String> rmBlobsSet) {
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.message = message;
        this.timeStamp = new Date();
        this.blobsMap = blobsMap;
        this.blobsSet = blobsSet;
        this.rmBlobsSet = rmBlobsSet;
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

    public TreeMap<String, String> getBlobsMap() {
        return blobsMap;
    }

    public TreeSet<String> getBlobsSet() {
        return blobsSet;
    }

    public TreeSet<String> getRmBlobsSet() {
        return rmBlobsSet;
    }

    public void save() {
        File currentCommitFile = join(COMMITS_DIR, sha1(this));
        Utils.writeObject(currentCommitFile, this);
    }

//    public Commit load(){
//        File currentCommitFile = join(COMMITS_DIR, sha1(this));
//        return Utils.readObject(currentCommitFile, Commit.class);
//    }

    // override toString
}
