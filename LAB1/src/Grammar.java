import java.util.*;
import java.util.regex.Pattern;

public class Grammar {

    private Set<Character> nonTerminals;
    private Set<Character> terminals;
    private Character startSymbol;
    private Map<String, List<String>> rules;

    public Grammar() {
    }

    public Grammar(FiniteAutomaton fa) {

        startSymbol = fa.getStartStateQ0();
        nonTerminals = fa.getStatesQ();
        terminals = fa.getAlphabetSigma();
        rules = new HashMap<>();

        for (FiniteAutomaton.Transition t : fa.getTransitions()) {

            String toState = t.getToState() == null ? "" : t.getToState().toString();
            if (rules.containsKey(t.getFromState().toString())) {
                rules.get(t.getFromState().toString()).add(t.getWithSymbol().toString() + toState);
            } else {
                rules.put(t.getFromState().toString(), new ArrayList<>(List.of(t.getWithSymbol().toString() + toState)));
            }
        }
    }

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
            for (char c : valueToReplace.toCharArray()) {
                if (nonTerminals.contains(c)) {
                    currentNonTerminals.add(c);
                }
            }
        }
        System.out.println(transformations);
        return result.toString();
    }

    public String getChomskyType() {

        Set<String> states = rules.keySet();
        boolean notSecondType = false;

        // check for 0 TYPE
        for (String fromState : states) {

            char[] chars = fromState.toCharArray();

            if (fromState.length() > 1)
                notSecondType = true;

            for (char c : chars) {
                if (terminals.contains(c)) {
                    return "TYPE 0";
                }
            }
        }

        if (notSecondType)
            return "TYPE I";


        // check for III TYPE
        boolean leftHandRule = true, rightHandRule = true;

        Pattern type3RightHandRule = Pattern.compile("^[a-z][A-Z]$");
        Pattern type3LeftHandRule = Pattern.compile("^[A-Z][a-z]$");


        boolean currentValue = true;
        for (String fromState : states) {

            List<String> toState = rules.get(fromState);
            for (String s : toState) {

                currentValue = false;

                // check A-> aB
                if (!Pattern.matches(type3RightHandRule.pattern(), s) && s.length() > 1) {
                    rightHandRule = false;
                    currentValue = true;
                }
                // check A-> Ba
                if (!Pattern.matches(type3LeftHandRule.pattern(), s) && s.length() > 1) {
                    leftHandRule = false;
                    currentValue = true;
                }

                // check A->a
                if (s.length() == 1) {
                    currentValue = true;
                }

                if (!currentValue) {
                    break;
                }
            }
        }

        if ((rightHandRule && !leftHandRule) || (leftHandRule && !rightHandRule))
            if (currentValue)
                return "TYPE III";


        Pattern secondTypePattern = Pattern.compile("([a-z]+)?([A-Z]+)?([a-z]+)?");
        boolean secondType = true;

        for (String fromState : states) {

            List<String> toState = rules.get(fromState);

            for (String s : toState) {
                if (!Pattern.matches(secondTypePattern.pattern(), s))
                    secondType = false;
            }
        }

        if (secondType)
            return "TYPE II";

        return "TYPE I";
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

