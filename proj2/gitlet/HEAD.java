package gitlet;

import static gitlet.Utils.join;

import java.io.File;

public class HEAD {

  public static final File HEAD_FILE = Utils.join(Repository.GITLET_DIR, "HEAD");

  public static void save(String branchName) {
    Utils.writeContents(HEAD_FILE, branchName);
  }

  public static String load() {
    return Utils.readContentsAsString(HEAD_FILE);
  }

}
