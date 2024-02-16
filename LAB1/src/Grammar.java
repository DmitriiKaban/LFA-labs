import java.util.*;

public class Grammar {

    private Set<Character> nonTerminals;
    private Set<Character> terminals;
    private Character startSymbol;
    private Map<String, List<String>> rules;

    public String generateString() {

        List<Character> currentNonTerminals = new java.util.ArrayList<>(List.of(startSymbol));
        StringBuilder result = new StringBuilder();
        StringBuilder transformations = new StringBuilder();
        result.append(startSymbol);
        transformations.append(startSymbol);
        Random rand = new Random();

        while (!currentNonTerminals.isEmpty()) {

            int randIndex = rand.nextInt(currentNonTerminals.size());

            int placeToReplace = result.indexOf(currentNonTerminals.get(randIndex).toString());

            int randIndexToReplace = rand.nextInt(rules.get(currentNonTerminals.get(randIndex).toString()).size());
            String valueToReplace = rules.get(currentNonTerminals.get(randIndex).toString()).get(randIndexToReplace);

            result.replace(placeToReplace, placeToReplace + 1, valueToReplace);

            transformations.append(" -> ").append(result);

            currentNonTerminals.remove(randIndex);
            for (char c: valueToReplace.toCharArray()) {
                if (nonTerminals.contains(c)) {
                    currentNonTerminals.add(c);
                }
            }
        }
        System.out.println(transformations);
        return result.toString();
    }

    public void setNonTerminals(Set<Character> s) {
        nonTerminals = s;
    }
    public void setTerminals(Set<Character> s) {
        terminals = s;
    }

    public void setRules(HashMap<String, List<String>> rules) {
        this.rules = rules;
    }

    public void setStartSymbol(char s) {
        this.startSymbol = s;
    }

    public Set<Character> getNonTerminals() {
        return nonTerminals;
    }

    public Set<Character> getTerminals() {
        return terminals;
    }

    public Character getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(Character startSymbol) {
        this.startSymbol = startSymbol;
    }

    public Map<String, List<String>> getRules() {
        return rules;
    }

    public void setRules(Map<String, List<String>> rules) {
        this.rules = rules;
    }
}

