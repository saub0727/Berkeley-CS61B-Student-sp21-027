package gitlet;

import static gitlet.Commit.COMMITS_DIR;
import static gitlet.Utils.join;

import java.io.File;

public class HEAD {

  public static final File HEAD_FILE = Utils.join(Repository.GITLET_DIR, "HEAD");

  public static void saveBranch(String branchName) {
    Utils.writeContents(HEAD_FILE, branchName);
  }

  public static String getCurBranch() {
    return Utils.readContentsAsString(HEAD_FILE);
  }

  public static String getCurCommitID(){
    return Branch.getCommitId(getCurBranch());
  }

  public static Commit getCurCommit(){
    return getCommitById(getCurCommitID());
  }

  public static Commit getCommitById(String SHA1){
    File currentCommitFile = Utils.join(COMMITS_DIR, SHA1);
    Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
    return currentCommit;
  }

}
