
import java.util.*;

class MyToken {
    private String value;
    private Boolean isTerminal;
    private HashSet<MyToken> firstSet;
    private HashSet<MyToken> followSet;
    private Boolean isNullable = null;

    public MyToken(String value, Boolean isTerminal) {
        this.value = value;
        this.isTerminal = isTerminal;
    }

    public String getValue() {
        return value;
    }

    public Boolean isTerminal() {
        return isTerminal;
    }

    public void setIsTerminal(Boolean terminal) {
        isTerminal = terminal;
    }

    HashSet<MyToken> getFirstSet() {
        return firstSet;
    }

    void setFirstSet(HashSet<MyToken> firstSet) {
        this.firstSet = firstSet;
    }

    HashSet<MyToken> getFollowSet() {
        return followSet;
    }

    void setFollowSet(HashSet<MyToken> followSet) {
        this.followSet = followSet;
    }

    Boolean isNullable() {
        return isNullable;
    }

    void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyToken MyToken = (MyToken) o;
        return value.equals(MyToken.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

}

class ProductionRule {
    private MyToken nonTerminal;
    private ArrayList<MyToken> rule;

    ProductionRule(MyToken nonTerminal, ArrayList<MyToken> rule) {
        this.nonTerminal = nonTerminal;
        this.rule = rule;
    }

    MyToken getNonTerminal() {
        return nonTerminal;
    }

    ArrayList<MyToken> getWords() {
        return rule;
    }
}

class LL1Grammar {
    private ArrayList<ProductionRule> rules = new ArrayList<>();
    private ArrayList<MyToken> openList = new ArrayList<>();
    private HashMap<MyToken, ArrayList<ProductionRule>> nonTerminals = new HashMap<>();

    private boolean isNullable(MyToken word) {
        if (word.getValue().equals("#"))
            return true;
        if (word.isTerminal())
            return false;

        if (word.isNullable() != null)
            return word.isNullable();

        for (ProductionRule rule : nonTerminals.get(word)) {
            boolean b = true;
            for (MyToken w : rule.getWords()) {
                if (!isNullable(w)) {
                    b = false;
                    break;
                }
            }
            if (b) {
                word.setNullable(true);
                return true;
            }
        }
        word.setNullable(false);
        return false;
    }

    private HashSet<MyToken> SetFirst(MyToken nonTerminal) {
        if (nonTerminal.getFirstSet() != null)
            return nonTerminal.getFirstSet();

        HashSet<MyToken> set = new HashSet<>();

        for (ProductionRule rule : nonTerminals.get(nonTerminal)) {
            for (MyToken w : rule.getWords()) {
                if (w.isTerminal()) {
                    if (!w.getValue().equals("#")) {
                        w.setIsTerminal(true);
                        set.add(w);
                    }
                    break;
                }
                w.setIsTerminal(false);
                set.addAll(SetFirst(w));
                if (!isNullable(w))
                    break;
            }
        }
        nonTerminal.setFirstSet(set);
        return set;
    }

    private HashSet<MyToken> SetFollow(MyToken nonTerminal) {
        if (nonTerminal.getFollowSet() != null)
            return nonTerminal.getFollowSet();

        HashSet<MyToken> set = new HashSet<>();

        if (nonTerminal.getValue().equals("S")) {
            set.add(new MyToken("$", true));
            nonTerminal.setFollowSet(set);
            return set;
        }
        openList.add(nonTerminal);

        for (ProductionRule rule : rules) {
            boolean hasRule = rule.getWords().contains(nonTerminal);
            if (hasRule) {
                int index = rule.getWords().indexOf(nonTerminal);
                while (index < rule.getWords().size()) {
                    if (index == rule.getWords().size() - 1) {
                        if (!openList.contains(rule.getNonTerminal()))
                            set.addAll(SetFollow(rule.getNonTerminal()));
                        break;
                    }
                    index++;
                    MyToken word = rule.getWords().get(index);
                    if (word.isTerminal()) {
                        if (!word.getValue().equals("#")) {
                            word.setIsTerminal(true);
                            set.add(word);
                        }
                        break;
                    }
                    word.setIsTerminal(false);
                    set.addAll(SetFirst(word));
                    if (!isNullable(word))
                        break;
                }
            }
        }
        openList.clear();
        nonTerminal.setFollowSet(set);
        return set;
    }


    public static void main(String[] args) {
        LL1Grammar ll1Grammar = new LL1Grammar();

        Queue<MyToken> nonTerminalsQueue = new LinkedList<>();
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        while (true) {

            String rule = scanner.nextLine();
            //End of rules
            if (rule.equals("finish")) {
                break;
            }

            String nonT = rule.split(":=")[0].trim();

            MyToken nt = new MyToken(nonT, false);
            if (!ll1Grammar.nonTerminals.containsKey(nt)) {
                ll1Grammar.nonTerminals.put(nt, new ArrayList<>());
                nonTerminalsQueue.add(nt);
            }
            String[] words = rule.split(":=")[1].trim().split(" ");
            ArrayList<MyToken> list = new ArrayList<>();
            for (String s : words) {
                if (s.toLowerCase().equals(s)) {
                    MyToken t = new MyToken(s, true);
                    list.add(t);
                } else
                    list.add(new MyToken(s, false));
            }

            ProductionRule productionRule = new ProductionRule(nt, list);
            ll1Grammar.rules.add(productionRule);
            ll1Grammar.nonTerminals.get(nt).add(productionRule);
        }

        // Build nullable set
        for (MyToken nt : nonTerminalsQueue) {
            ll1Grammar.isNullable(nt);
        }

        // Build first set
        for (MyToken nt : nonTerminalsQueue) {
            ll1Grammar.SetFirst(nt);
        }

        // Build follow set
        for (MyToken nt : nonTerminalsQueue) {
            ll1Grammar.SetFollow(nt);
        }

        System.out.println("Nullable:");
        for (MyToken nt : nonTerminalsQueue) {
            System.out.println(nt + ":  " + (nt.isNullable() ? "nullable" : "not nullable"));
        }

        System.out.println("Firsts:");
        for (MyToken nt : nonTerminalsQueue) {
            System.out.println(nt + ":  " + Arrays.toString(nt.getFirstSet().toArray()));
        }

        System.out.println("Follows:");
        for (MyToken nt : nonTerminalsQueue) {
            System.out.println(nt + ":  " + Arrays.toString(nt.getFollowSet().toArray()));
        }

    }
}