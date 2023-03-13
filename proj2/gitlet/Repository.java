package gitlet;

import static gitlet.Blob.blobsDir;
import static gitlet.Commit.COMMITS_DIR;
import static gitlet.Utils.*;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;


/** Represents a gitlet repository.

 *  does at a high level.
 *
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

    /**
     * It will have a single branch: master, which initially
     * points to this initial commit, and master will be the current branch.
     * all repositories will automatically share this commit (they
     * will all have the same UID) and all commits in all repositories will trace back to it.
     */

    private static boolean validateNumArgs(int n, String... args) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }

    private static boolean checkExistsMovingForward(String type){
        if (!GITLET_DIR.exists() && !type.equals("init")){
            System.out.println("Not in an initialized Gitlet directory.");
            return Boolean.FALSE;
        } else if (GITLET_DIR.exists() && type.equals("init")){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public static void init(String... args) {
        if (!validateNumArgs(1, args)){
            System.exit(0);
        }
        if (!checkExistsMovingForward("init")){
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        blobsDir.mkdir();
        COMMITS_DIR.mkdir();
        Branch.BRANCHE_DIR.mkdir();
//        Remote.REMOTE_DIR.mkdir();
        Staging stagingArea = new Staging();
        stagingArea.save();

        HEAD.saveBranch("master");
        Commit initialCommit = new Commit();
        initialCommit.save(HEAD.getCurBranch());
    }

    public static void add(String... args){
        if (!checkExistsMovingForward("add")){System.exit(0);}
        if (!validateNumArgs(2, args)){System.exit(0);}
        String fileName = args[1];
        // relative path, so this can trigger the file;
        File addedFile = join(CWD, fileName);
        if (!addedFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Staging prevStaging = Staging.load();
        Commit currentCommit = HEAD.getCurCommit();
        TreeSet<String> currentCommitBlobsSet = currentCommit.getBlobsSet();

        Blob addedFileBlob = new Blob(readContents(addedFile), fileName);
        String prevFileSHA1 = prevStaging.getAdditionMap().get(fileName);
        String addedFileSHA1 = addedFileBlob.getBlobSHA1();
        // situation: if compared with current commit, nothing changed, same SHA1, return
        if (currentCommitBlobsSet != null && currentCommitBlobsSet.contains(addedFileSHA1)){return;}
        // situation: same name, same SHA1
        if (prevStaging.getAdditionSet() != null && prevStaging.getAdditionSet().contains(addedFileSHA1)){return;}
        // situation: same name, diff SHA1
        // if it has identical name with previous version, update the staging
        if (prevStaging.getAdditionMap() != null && prevStaging.getAdditionMap().containsKey(fileName)){
            prevStaging.rmAdditionMap(fileName);
            prevStaging.rmAdditionSet(prevFileSHA1);
            prevStaging.addRemovalSet(prevFileSHA1);
        }
        // situation: new name
        prevStaging.addAdditionMap(fileName, addedFileSHA1);
        prevStaging.addAdditionSet(addedFileSHA1);
        addedFileBlob.save();
        prevStaging.save();
    }



    public static void commit(String... args){
        if (args.length == 1){
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (!validateNumArgs(2, args)){
            System.exit(0);
        }
        String message = args[1];
        // If no files have been staged, abort. Print the message No changes added to the commit.
        Staging curStaging = Staging.load();
        TreeMap<String, String> curStagingAdditionMap = curStaging.getAdditionMap();
        TreeSet<String> curStagingAdditionSet = curStaging.getAdditionSet();
        TreeSet<String> curStagingRemovalSet = curStaging.getRemovalSet();
        if (curStaging.checkEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit currentCommit = HEAD.getCurCommit();
        List<String> allFileNames = plainFilenamesIn(blobsDir);
        if (curStagingRemovalSet != null){
            for (String item : curStagingRemovalSet){
                if (allFileNames != null && allFileNames.contains(item)){
                    if (currentCommit.getBlobsSet() != null && currentCommit.getBlobsSet().contains(item)){
                        continue;
                    }
                    curStagingRemovalSet.remove(item);
                    continue;
                }
                curStagingRemovalSet.remove(item);
                File temp = new File(item);
                temp.delete();
            }
        }
        Commit newCommit = new Commit(HEAD.getCurCommitID(), null, message,
            curStagingAdditionMap, curStagingAdditionSet, curStagingRemovalSet);
        newCommit.save(HEAD.getCurBranch());
        curStaging.clear();
        curStaging.save();

        /*
         * Saves a snapshot of tracked files in the current commit and staging area
         * so they can be restored at a later time, creating a new commit. The commit
         * is said to be tracking the saved files. By default, each commit’s snapshot
         * of files will be exactly the same as its parent commit’s snapshot of files
         * ; it will keep versions of files exactly as they are, and not update them.
         * A commit will only update the contents of files it is tracking that have
         * been staged for addition at the time of commit, in which case the commit
         * will now include the version of the file that was staged instead of the
         * version it got from its parent. A commit will save and start tracking any
         * files that were staged for addition but won’t be tracked by its parent.
         * Finally, files tracked in the current commit may be untracked in the
         * new commit as a result being staged for removal by the rm command (below).

         * The bottom line: By default a commit has the same file contents as its
         * parent. Files staged for addition and removal are the updates to the
         * commit. Of course, the date (and likely the message) will also different
         * from the parent.
         */
    }

    public static void rm(String... args){
        if (!checkExistsMovingForward("rm")){
            System.exit(0);
        }
        if (args.length != 2){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String fileName = args[1];
        // relative path, so this can trigger the file;
        File rmFile = join(CWD, fileName);
        // If the file is neither staged nor tracked by the head commit
        if (Boolean.FALSE){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        Commit currentCommit = HEAD.getCurCommit();
        Staging prevStaging = Staging.load();
        Blob rmFileBlob = new Blob(readContents(rmFile), fileName);
        String prevFileSHA1 = prevStaging.getAdditionMap().get(fileName);
        String rmFileSHA1 = rmFileBlob.getBlobSHA1();
        // check if in current commit first
        if (currentCommit.getBlobsSet() != null && currentCommit.getBlobsSet().contains(rmFileSHA1)){
            Utils.join(CWD, fileName).delete();
            prevStaging.addRemovalSet(rmFileSHA1);
            prevStaging.save();
            return;
        }
        // in staging:
        if (prevStaging.getAdditionMap() != null && prevStaging.getAdditionMap().containsKey(fileName)){
            prevStaging.rmAdditionMap(fileName);
            prevStaging.rmAdditionSet(prevFileSHA1);
            prevStaging.addRemovalSet(prevFileSHA1);
            prevStaging.save();
        }
    }

    public static void log(String... args) {
        if (!checkExistsMovingForward("log")){System.exit(0);}
        if (!validateNumArgs(1, args)){System.exit(0);}
        Commit curCommit = HEAD.getCurCommit();
        String curCommitID = HEAD.getCurCommitID();
        while (true){
            System.out.println("===");
            System.out.println("commit " + curCommitID);
            if (curCommit.getSecondParent() != null){
                Formatter ans = new Formatter();
                ans.format("Merge: %7s %7s", curCommit.getFirstParent(),
                    curCommit.getSecondParent());
                System.out.println(ans);
            }
            System.out.println("Date: " + curCommit.getTimeStamp());
            System.out.println(curCommit.getMessage());
            System.out.println();
            if (curCommit.getFirstParent() == null){break;}
            curCommitID = curCommit.getFirstParent();
            curCommit = HEAD.getCommitById(curCommitID);
        }
    }


    public static void globalLog(String... args) {
        if (!checkExistsMovingForward("log")){System.exit(0);}
        if (!validateNumArgs(1, args)){System.exit(0);}
        List<String> commitIdList = Utils.plainFilenamesIn(Commit.COMMITS_DIR);
        for (String commitId : commitIdList) {
            Commit commit = HEAD.getCommitById(commitId);
            System.out.println(commit);
        }
    }




}
