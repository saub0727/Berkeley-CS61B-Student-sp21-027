package gitlet;

import static gitlet.Utils.join;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Staging implements Serializable {

  public static final File stagingFile = join(Repository.GITLET_DIR, "staging");
  private HashMap<String, String> additionMap;
  private HashSet<String> additionSet;
  private HashSet<String> removalSet;

  public Staging() {
    this.additionMap = new HashMap<>();
    this.additionSet = new HashSet<>();
    this.removalSet = new HashSet<>();
  }

  public void save() {
    Utils.writeObject(stagingFile, this);
  }

  public static Staging load(){
    return Utils.readObject(stagingFile, Staging.class);
  }
  public void clear(){
    this.additionMap.clear();
    this.additionSet.clear();
    this.removalSet.clear();
  }

  public HashMap<String, String> getAdditionMap() {
    return this.additionMap;
  }

  public HashSet<String> getAdditionSet() {
    return this.additionSet;
  }

  public HashSet<String> getRemovalSet() {
    return this.removalSet;
  }

  public void rmAdditionMap(String key) {
    this.additionMap.remove(key);
  }

  public void rmAdditionSet(String SHA1) {
    this.additionSet.remove(SHA1);
  }

  public void addAdditionMap(String key, String value) {
    this.additionMap.put(key, value);
  }

  public void addAdditionSet(String SHA1) {
    this.additionSet.add(SHA1);
  }

  public void addRemovalSet(String item) {
    this.removalSet.add(item);
  }
}
