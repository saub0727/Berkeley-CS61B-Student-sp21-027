package gitlet;

import static gitlet.Blob.blobsDir;
import static gitlet.Commit.COMMITS_DIR;
import static gitlet.Utils.*;

import java.io.File;
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
        TreeMap<String, String> currentCommitBlobsMapSha1 = currentCommit.getBlobsMapSha1();

        Blob addedFileBlob = new Blob(readContents(addedFile), fileName);
        String prevFileSHA1 = prevStaging.getAdditionMapName().get(fileName);
        String addedFileSHA1 = addedFileBlob.getBlobSHA1();
        // situation: if compared with current commit, nothing changed, same SHA1, return
        if (currentCommitBlobsMapSha1 != null && currentCommitBlobsMapSha1.containsKey(addedFileSHA1)){return;}
        // situation: same name, same SHA1
        if (prevStaging.getAdditionMapSha1() != null && prevStaging.getAdditionMapSha1().containsKey(addedFileSHA1)){return;}
        // situation: same name, diff SHA1
        // if it has identical name with previous version, update the staging
        if (prevStaging.getAdditionMapName() != null && prevStaging.getAdditionMapName().containsKey(fileName)){
            prevStaging.rmAdditionMapName(fileName);
            prevStaging.rmAdditionMapSha1(prevFileSHA1);
            prevStaging.addRemovalMap(prevFileSHA1, fileName);
        }
        // situation: new name
        prevStaging.addAdditionMapName(fileName, addedFileSHA1);
        prevStaging.addAdditionMapSha1(addedFileSHA1, fileName);
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
        TreeMap<String, String> curStagingAdditionMapName = curStaging.getAdditionMapName();
        TreeMap<String, String> curStagingAdditionMapSha1 = curStaging.getAdditionMapSha1();
        TreeMap<String, String> curStagingRemovalMap = curStaging.getRemovalMap();
        if (curStaging.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit currentCommit = HEAD.getCurCommit();
        List<String> allFileNames = plainFilenamesIn(blobsDir);
        if (curStagingRemovalMap != null){
            for (String item : curStagingRemovalMap.keySet()){
                if (allFileNames != null && allFileNames.contains(item)){
                    if (currentCommit.getBlobsMapSha1() != null && currentCommit.getBlobsMapSha1().containsKey(item)){
                        continue;
                    }
                    curStagingRemovalMap.remove(item);
                    continue;
                }
                curStagingRemovalMap.remove(item);
                File temp = new File(item);
                temp.delete();
            }
        }
        Commit newCommit = new Commit(HEAD.getCurCommitID(), null, message,
            curStagingAdditionMapName, curStagingAdditionMapSha1, curStagingRemovalMap);
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

        Commit currentCommit = HEAD.getCurCommit();
        Staging prevStaging = Staging.load();
        Blob rmFileBlob = new Blob(readContents(rmFile), fileName);
        String rmFileSHA1 = rmFileBlob.getBlobSHA1();
        // check if in current commit first
        if (currentCommit.getBlobsMapSha1() != null && currentCommit.getBlobsMapSha1().containsKey(rmFileSHA1)){
            Utils.join(CWD, fileName).delete();
            prevStaging.addRemovalMap(rmFileSHA1, fileName);
            prevStaging.save();
        } else if (prevStaging.getAdditionMapName() != null && prevStaging.getAdditionMapName().containsKey(fileName)){
            String prevFileSHA1 = prevStaging.getAdditionMapName().get(fileName);
            prevStaging.rmAdditionMapName(fileName);
            prevStaging.rmAdditionMapSha1(prevFileSHA1);
            prevStaging.addRemovalMap(prevFileSHA1, fileName);
            prevStaging.save();
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
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
        if (!checkExistsMovingForward("global-log")){System.exit(0);}
        if (!validateNumArgs(1, args)){System.exit(0);}
        List<String> commitIdList = Utils.plainFilenamesIn(Commit.COMMITS_DIR);
        for (String commitId : commitIdList) {
            Commit commit = HEAD.getCommitById(commitId);
            System.out.println(commit);
            System.out.println();
        }
    }

    public static void find(String... args) {
        if (!checkExistsMovingForward("find")){System.exit(0);}
        if (!validateNumArgs(2, args)){System.exit(0);}
        String message = args[1];
        TreeSet<String> ans = new TreeSet<>();
        List<String> commitIdList = Utils.plainFilenamesIn(Commit.COMMITS_DIR);
        for (String commitId : commitIdList) {
            Commit commit = HEAD.getCommitById(commitId);
            if (commit.getMessage().equals(message)){
                ans.add(commitId);
            }
        }
        for (String item : ans) {
            System.out.println(item);
            System.out.println();
        }
    }

    public static void status(String... args) {
        if (!checkExistsMovingForward("status")){System.exit(0);}
        if (!validateNumArgs(1, args)){System.exit(0);}
        Staging prevStaging = Staging.load();
        System.out.println("=== Branches ===");
        List<String> BranchList = Utils.plainFilenamesIn(Branch.BRANCHE_DIR);
        String curBranch = HEAD.getCurBranch();
        for (String item : BranchList){
            if (item.equals(curBranch)){
                System.out.print("*");
            }
            System.out.println(item);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String item : prevStaging.getAdditionMapName().keySet()){
            System.out.println(item);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String item : prevStaging.getRemovalMap().values()){
            System.out.println(item);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeMap<String, String> modifiedList = new TreeMap<>();
        TreeMap<String, String> deletedList = new TreeMap<>();
        Commit curCommit = HEAD.getCurCommit();
        for (String commitSha1 : curCommit.getBlobsMapSha1().keySet()){
            String commitFileName = curCommit.getBlobsMapSha1().get(commitSha1);
            File checkFileChanged = join(CWD, commitFileName);
            if (!checkFileChanged.exists()){
                if (prevStaging.getRemovalMap().keySet() != null &&
                    !prevStaging.getRemovalMap().keySet().contains(commitSha1)){
                    deletedList.put(commitFileName, commitSha1);
                }
            }
            Blob checkFileChangedBlob = new Blob(readContents(checkFileChanged), commitFileName);
            String checkFileChangedSHA1 = checkFileChangedBlob.getBlobSHA1();
            if (!checkFileChangedSHA1.equals(commitSha1)){
                if (prevStaging.getAdditionMapSha1().keySet() != null && !prevStaging.getAdditionMapSha1().keySet().contains(commitSha1)){
                    modifiedList.put(commitFileName, checkFileChangedSHA1);
                }
            }
        }
        for (String commitSha1 : prevStaging.getAdditionMapSha1().keySet()){
            String tempFileName = prevStaging.getAdditionMapSha1().get(commitSha1);
            File checkFileChanged = join(CWD, tempFileName);
            if (!checkFileChanged.exists()){
                deletedList.put(tempFileName, commitSha1);
            }
            Blob checkFileChangedBlob = new Blob(readContents(checkFileChanged), tempFileName);
            String checkFileChangedSHA1 = checkFileChangedBlob.getBlobSHA1();

            if (!checkFileChangedSHA1.equals(commitSha1)){
                modifiedList.put(tempFileName, checkFileChangedSHA1);
            }
        }
        for (String item : deletedList.keySet()){
            System.out.print(item);
            System.out.print(" (deleted)");
        }
        for (String item : modifiedList.keySet()){
            System.out.print(item);
            System.out.print(" (modified)");
            System.out.println();
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        TreeMap<String, String> untrackedList = new TreeMap<>();
        List<String> CWDList = Utils.plainFilenamesIn(CWD);
        for (String fileName : CWDList){
            File checkFileChanged = join(CWD, fileName);
            Blob tempBlob = new Blob(readContents(checkFileChanged), fileName);
            String checkFileChangedSHA1 = tempBlob.getBlobSHA1();
            if (prevStaging.getAdditionMapSha1().keySet() != null &&
                !prevStaging.getAdditionMapSha1().keySet().contains(checkFileChangedSHA1)
                && curCommit.getBlobsMapSha1().keySet() != null &&
                !curCommit.getBlobsMapSha1().keySet().contains(checkFileChangedSHA1)){
                untrackedList.put(fileName, checkFileChangedSHA1);
            }
        }
        for (String item : untrackedList.keySet()){
            System.out.println(item);
        }
        System.out.println();
    }
}
