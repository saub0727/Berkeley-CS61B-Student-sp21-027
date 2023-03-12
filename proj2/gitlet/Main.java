package gitlet;

import static gitlet.Repository.GITLET_DIR;

import java.io.File;
import java.time.LocalDateTime;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  java gitlet.Main add hello.txt
     */
    public static void main(String... args) {
        // TODO: what if args is empty?
        if (args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init(args);
                break;
            case "add":
                Repository.add(args);
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case  "commit":
                Repository.commit(args);
                break;
            case  "log":
                Repository.log();
                break;
            default:
                if (!GITLET_DIR.exists()){
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;
        }
    }
}
