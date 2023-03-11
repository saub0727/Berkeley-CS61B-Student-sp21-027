package gitlet;

import static gitlet.Utils.join;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
  public static final File blobs = join(Repository.GITLET_DIR, ".blobs");
  private final byte[] contents;
  private final String blobSHA1;

  public Blob(byte[] contents) {
    this.contents = contents;
    this.blobSHA1 = Utils.sha1(this.contents);
  }

  public byte[] getContents() {
    return this.contents;
  }

  public String getBlobSHA1() {
    return this.blobSHA1;
  }

  public void save() {
    Utils.writeContents(Utils.join(blobs, blobSHA1), contents);
  }
}
