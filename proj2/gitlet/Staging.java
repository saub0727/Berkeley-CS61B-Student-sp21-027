package gitlet;

import static gitlet.Utils.join;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

public class Staging implements Serializable {

  public static final File stagingFile = join(Repository.GITLET_DIR, "staging");
  private TreeMap<String, String> additionMap;
  private TreeSet<String> additionSet;
  private TreeSet<String> removalSet;

  public Staging() {
    this.additionMap = new TreeMap<>();
    this.additionSet = new TreeSet<>();
    this.removalSet = new TreeSet<>();
  }

  public boolean checkEmpty(){
    if (this.additionSet.isEmpty() && this.removalSet.isEmpty()){
      return true;
    }
    return false;
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

  public TreeMap<String, String> getAdditionMap() {
    return this.additionMap;
  }

  public TreeSet<String> getAdditionSet() {
    return this.additionSet;
  }

  public TreeSet<String> getRemovalSet() {
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
