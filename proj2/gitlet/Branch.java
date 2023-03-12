package gitlet;

import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;

import java.io.File;
import java.io.Serializable;

public class Branch {

  public static final File BRANCHE_DIR = Utils.join(Repository.GITLET_DIR, "branch");

  public static void setCommitId(String branchName, String commitIdSHA1) {
    File branchFile = Utils.join(BRANCHE_DIR, branchName);
    Utils.writeContents(branchFile, commitIdSHA1);
  }

  public static void setCommitId(String branchName, Object obj) {
    byte[] bytes = serialize((Serializable) obj);
    String temp = sha1(bytes);
    setCommitId(branchName, temp);
  }

//  public static void setRemoteCommitId(String remoteGitPath, String remoteBranchName,
//      String commitId) {
//    File remoteBranchFile = Utils.join(remoteGitPath, remoteBranchName);
//    Utils.writeContents(remoteBranchFile, commitId);
//  }
//
  public static String getCommitId(String branchName) {
    File branchFile = Utils.join(BRANCHE_DIR, branchName);
    if (!branchFile.exists()) {
      return null;
    } else {
      return Utils.readContentsAsString(branchFile);
    }
  }
//
//  public static String getRemoteCommitId(String remoteGitPath, String remoteBranchName) {
//    File branchFile = Utils.join(remoteGitPath + "/branch", remoteBranchName);
//    if (!branchFile.exists()) {
//      return null;
//    } else {
//      return Utils.readContentsAsString(branchFile);
//    }
//  }

}
