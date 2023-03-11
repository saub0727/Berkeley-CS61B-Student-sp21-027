package gitlet;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.join;

import java.io.File;
import java.io.Serializable;

public class Staging implements Serializable {

  public static final File staging = join(Repository.GITLET_DIR, ".staging");
  public static final File stagingAdd = join(staging, ".stagingAdd");
  public static final File stagingRemove = join(staging, ".stagingRemove");

}
