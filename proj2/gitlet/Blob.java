package gitlet;

import static gitlet.Utils.join;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
  public static final File blobsDir = join(Repository.GITLET_DIR, "blobs");
  private final byte[] contents;
  private final String blobSHA1;
  private final String fileName;

  public Blob(byte[] contents, String fileName) {
    this.contents = contents;
    this.blobSHA1 = Utils.sha1(contents);
    this.fileName = fileName;
  }

  public byte[] getContents() {
    return this.contents;
  }

  public String getBlobSHA1() {
    return this.blobSHA1;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void save() {
    File blobs = join(blobsDir, getBlobSHA1());
    Utils.writeContents(blobs, this.getContents());
  }
}
