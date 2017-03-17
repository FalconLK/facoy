import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Ari
 */
public class ProblemA {
    static String in = "src/a.in";
    static String out = "src/a.out";
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner scan = new Scanner(new File(in));
        BufferedWriter write = new BufferedWriter(new FileWriter(out));
        int numlines = scan.nextInt();
        int numlevels = 0;
        Level[] levels = new Level[1000];
        Level temp;
        for(int x = 0; x < numlines; x++) {
            numlevels = scan.nextInt();
            for(int y = 0; y < numlevels; y++) {
                scan.nextInt();
            }
            for(int y = 0; y < numlevels; y++) {
                levels[y] = new Level(y,scan.nextInt());
            }
            
            for(int a = 0; a < numlevels - 1; a++) {
                for(int b = a + 1; b < numlevels; b++) {
                    if(levels[a].diechance < levels[b].diechance) {
                        temp = levels[a];
                        levels[a] = levels[b];
                        levels[b] = temp;
                    }
                    else if(levels[a].diechance == levels[b].diechance && 
                            levels[a].id > levels[b].id) {
                        temp = levels[a];
                        levels[a] = levels[b];
                        levels[b] = temp;
                    }
                }
            }
            
            write.write("Case #" + (x+1) + ":");
            for(int y = 0; y < numlevels; y++) {
                write.write(" " + levels[y].id);
            }
            write.write("\n");
        }
        scan.close();
        write.close();
    }
    
    private static class Level {
        public int id;
        public int diechance;
        public Level(int id, int die) {
            this.id = id;
            this.diechance = die;
        }
    }
}
