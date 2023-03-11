package gitlet;

// TODO: any imports you need here

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


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
    private String hash;
    private String message;
    private Date timeStamp;
    private HashMap<String, String> blobs;

    public Commit() {
        this.message = "initial commit";
        this.timeStamp = new Date(0);
    }

    public Commit(String firstParent, String secondParent, String hash, String message,
        HashMap<String, String> blobs) {
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.hash = hash;
        this.message = message;
        this.timeStamp = new Date();
        this.blobs = blobs;
    }

    private String getSHA1(){
//        ByteArrayOutputStream temp = new ByteArrayOutputStream();
//        ObjectOutputStream oos = null;
//        try {
//            oos = new ObjectOutputStream(temp);
//            oos.writeObject(this);
//            oos.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(0);
//        }
//        return Utils.sha1((Object) bos.toByteArray());
        return null;
    }

    public String getFirstParent() {
        return this.firstParent;
    }

    public String getSecondParent() {
        return this.secondParent;
    }

    public String getHash() {
        return this.hash;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commit)) {
            return false;
        }
        Commit commit = (Commit) o;
        return Objects.equals(firstParent, commit.firstParent) && Objects.equals(
            secondParent, commit.secondParent) && Objects.equals(hash, commit.hash)
            && Objects.equals(message, commit.message) && Objects.equals(timeStamp,
            commit.timeStamp) && Objects.equals(blobs, commit.blobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstParent, secondParent, hash, message, timeStamp, blobs);
    }

    @Override
    public String toString() {
        return "Commit{" +
            "firstParent='" + firstParent + '\'' +
            ", secondParent='" + secondParent + '\'' +
            ", hash='" + hash + '\'' +
            ", message='" + message + '\'' +
            ", timeStamp=" + timeStamp +
            ", blobs=" + blobs +
            '}';
    }
}
