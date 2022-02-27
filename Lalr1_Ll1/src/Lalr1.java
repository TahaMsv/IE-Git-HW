import java.util.*;

public class Lalr1 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        int m = input.nextInt();

        String[] terminals = new String[m];
        for (int i = 0; i < m; i++) {
            terminals[i] = input.next();
        }

        String[][] table = new String[n][m];
        HashSet<Integer> Gs = new HashSet<Integer>();
        for (int i = 0; i < n; i++) {
//            String line = input.nextLine();
//            String[] actions = line.split("\t");
            for (int j = 0; j < m; j++) {
                table[i][j] = input.next();
                if (table[i][j].startsWith("G")) {
                    int grammarNumber = Integer.parseInt(table[i][j].substring(1));
                    Gs.add(grammarNumber);
                }
            }

        }

        HashMap<Integer, Integer> actionsToReplace = new HashMap<Integer, Integer>();
        Set<Integer> removableRows = new HashSet<>();
        for (int i = 0; i < n; i++) {
            boolean firstTime = true;
            boolean allActionsAreReduce = true;
            String lastR = "";
            for (int j = 0; j < m; j++) {
                if (table[i][j].startsWith("S") || table[i][j].startsWith("G") || table[i][j].startsWith("ACC")) {
                    allActionsAreReduce = false;
                    break;
                } else if (table[i][j].startsWith("R")) {
                    if (firstTime) {
                        lastR = table[i][j];
                        firstTime = false;
                    } else {
                        if (!lastR.equals(table[i][j])) {
                            allActionsAreReduce = false;
                            break;
                        }
                    }
                }
            }
            if (!lastR.equals("") && allActionsAreReduce && !Gs.contains(i+1)) {
                int reduceNumber = Integer.parseInt(lastR.substring(1));
                actionsToReplace.put(i +1 , reduceNumber);
                removableRows.add(i);
            }
        }

        for (Map.Entry<Integer, Integer> entry : actionsToReplace.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            String replacement = "S" + (key);
            String target = "SR" + (value);

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (table[i][j].equals(replacement)) {
                        table[i][j] = target;
                    }
                }
            }
        }

        String[][] optimizedTable = new String[n - removableRows.size()][m];

        for (int i = 0; i < m; i++) {
            System.out.print(terminals[i] + '\t');
        }
        System.out.println();
        int removedRows = 0;
        for (int i = 0; i < n; i++) {
            if (!removableRows.contains(i)) {
                for (int j = 0; j < m; j++) {
                    optimizedTable[i - removedRows][j] = table[i][j];
                    System.out.print(optimizedTable[i - removedRows][j] + '\t');
                }
                System.out.println();
            } else {
                removedRows++;
            }

        }
    }
}
