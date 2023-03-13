package gitlet;

import static gitlet.Utils.join;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;


public class Staging implements Serializable {

  public static final File stagingFile = join(Repository.GITLET_DIR, "staging");
  private TreeMap<String, String> additionMapName;
  private TreeMap<String, String> additionMapSha1;
  private TreeMap<String, String> removalMap;

  public Staging() {
    this.additionMapName = new TreeMap<>();
    this.additionMapSha1 = new TreeMap<>();
    this.removalMap = new TreeMap<>();
  }

  public boolean isEmpty(){
    return this.additionMapSha1.isEmpty() && this.removalMap.isEmpty();
  }

  public void save() {
    Utils.writeObject(stagingFile, this);
  }

  public static Staging load(){
    return Utils.readObject(stagingFile, Staging.class);
  }

  public void clear(){
    this.additionMapName.clear();
    this.additionMapSha1.clear();
    this.removalMap.clear();
  }

  public TreeMap<String, String> getAdditionMapName() {
    return additionMapName;
  }

  public TreeMap<String, String> getAdditionMapSha1() {
    return additionMapSha1;
  }

  public TreeMap<String, String> getRemovalMap() {
    return removalMap;
  }

  public void rmAdditionMapName(String key) {
    this.additionMapName.remove(key);
  }

  public void rmAdditionMapSha1(String SHA1) {
    this.additionMapSha1.remove(SHA1);
  }

  public void addAdditionMapName(String fileName, String SHA1) {
    this.additionMapName.put(fileName, SHA1);
  }

  public void addAdditionMapSha1(String SHA1, String fileName) {
    this.additionMapSha1.put(SHA1, fileName);
  }

  public void addRemovalMap(String SHA1, String fileName) {
    this.removalMap.put(SHA1, fileName);
  }

//  public static void getFileNameBySha1(String sha1) {
//    for (String item : Staging.load().getRemovalSet()){
//      System.out.println();
//    }
//  }
}
