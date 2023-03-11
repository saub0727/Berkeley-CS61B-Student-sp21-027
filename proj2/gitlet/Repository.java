package gitlet;

import static gitlet.Staging.*;
import static gitlet.Utils.*;

import java.io.File;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /*
     * TODO: add instance variables here.
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEAD = join(CWD, "HEAD");

    /**
     * It will have a single branch: master, which initially
     * points to this initial commit, and master will be the current branch.
     * all repositories will automatically share this commit (they
     * will all have the same UID) and all commits in all repositories will trace back to it.
     */

    private static boolean checkExistsMovingForward(String type){
        if (GITLET_DIR.exists() && type.equals("init")){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return Boolean.FALSE;
        } else if (!GITLET_DIR.exists() && !type.equals("init")){
            System.out.println("Not in an initialized Gitlet directory.");
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public static void init(String... args){
        if (args.length > 1){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!checkExistsMovingForward("init")){
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        Staging.stagingRemove.mkdirs();
        Staging.stagingAdd.mkdir();
        Blob.blobs.mkdir();
        Commit.COMMITS_DIR.mkdir();
        File initialCommitFile = Utils.join(Commit.COMMITS_DIR, "initialCommit");
        Commit initialCommit = new Commit();
        Utils.writeObject(initialCommitFile, initialCommit);
    }

    public static void add(String... args){
        if (args.length != 2){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!checkExistsMovingForward("add")){
            System.exit(0);
        }
        String fileName = args[1];
        // relative path, so this can trigger the file;
        File newFile = Utils.join(CWD, fileName);
        if (!newFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        //Adds a copy of the file as it currently exists to the staging area
        // (see the description of the commit command). For this reason, adding
        // a file is also called staging the file for addition. Staging an
        // already-staged file overwrites the previous entry in the staging
        // area with the new contents. The staging area should be somewhere
        // in .gitlet. If the current working version of the file is identical
        // to the version in the current commit, do not stage it to be added,
        // and remove it from the staging area if it is already there (as can
        // happen when a file is changed, added, and then changed back to it’s
        // original version). The file will no longer be staged for removal
        // (see gitlet rm), if it was at the time of the command.
        Blob newBlob = new Blob(readContents(newFile));
        String newBlobSHA1 = newBlob.getBlobSHA1();



        Commit currentCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));
        Staging stagingArea = staging.load();
        // If the current working version of the file is identical to the version in the current
        // commit, do not stage it to be added
        if (newBlobId.equals(currentCommit.getBlobId(fileName))) {
            stagingArea.clear();
            stagingArea.save();
            return;
        }
        // and remove it from the staging area if it is already there
        if (newBlobId.equals(stagingArea.getAddition().get(fileName))) {
            stagingArea.getAddition().remove(fileName);
        }
        stagingArea.getAddition().put(fileName, newBlobId);
        // The file will no longer be staged for removal (see gitlet rm), if it was at the time
        // of the command.
        stagingArea.getRemoval().remove(fileName);
        newBlob.save();
        stagingArea.save();

    }

    public static void commit(String... args){
        if (args.length == 1){
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (args.length > 2){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        // If no files have been staged, abort. Print the message No changes added to the commit.

    }

    public static void log() {
    }



}
