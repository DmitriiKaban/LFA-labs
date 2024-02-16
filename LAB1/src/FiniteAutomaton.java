import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class FiniteAutomaton {

    private Set<Character> statesQ; // non-terminals + null
    private Set<Character> alphabetSigma; // terminals
    private Set<Transition> transitions; // A -> aB, (A,a)=B, A->a, (A,a)=empty string
    private char startStateQ0; // start symbol
    private final Character finalStateF = null; // empty string


    public FiniteAutomaton(Grammar grammar) {

        statesQ = grammar.getNonTerminals();
        statesQ.add(null);
        alphabetSigma = grammar.getTerminals();
        startStateQ0 = grammar.getStartSymbol();
        transitions = new HashSet<>();

        Set<String> rules = grammar.getRules().keySet();
        for (String rule : rules) {
            List<String> transitionTo = grammar.getRules().get(rule);

            for (String transition : transitionTo) {
                // A -> B
                if (transition.length() == 1 && grammar.getNonTerminals().contains(transition.charAt(0))) {
                    transitions.add(new Transition(rule.charAt(0), null, transition.charAt(0)));
                }
                // A -> a
                if (transition.length() == 1 && grammar.getTerminals().contains(transition.charAt(0))) {
                    transitions.add(new Transition(rule.charAt(0), transition.charAt(0), null));
                }

                // A -> aB
                if (transition.length() == 2 && grammar.getTerminals().contains(transition.charAt(0)) && grammar.getNonTerminals().contains(transition.charAt(1))) {
                    transitions.add(new Transition(rule.charAt(0), transition.charAt(0), transition.charAt(1)));
                }
            }
        }
    }

    public boolean checkString(String string) {

        Character currentState = startStateQ0;
        return checkString(string, currentState);
    }

    public boolean checkString(String string, Character currentState) {

//        System.out.println("String: " + string + ", current state: " + currentState);
        if (string.isEmpty() && currentState == null) {
            return true;
        }

        boolean found = false;
        for (Transition transition : transitions) {
            if (transition.getFromState().equals(currentState) && transition.getWithSymbol() == string.charAt(0)) {
                if (transition.getToState() == null && string.length() > 1) {
                    continue;
                }
                currentState = transition.getToState();
                found = checkString(string.substring(1), currentState);
            }

            if (found) return true;
        }

        return false;
    }

    public class Transition {
        private Character fromState;
        private Character withSymbol;
        private Character toState;

        public Transition(Character fromState, Character withSymbol, Character toState) {
            this.fromState = fromState;
            this.withSymbol = withSymbol;
            this.toState = toState;
        }

        public Character getFromState() {
            return fromState;
        }

        public Character getWithSymbol() {
            return withSymbol;
        }

        public Character getToState() {
            return toState;
        }

        @Override
        public String toString() {
            return "(" + fromState +
                    "," + withSymbol +
                    ")=" + toState;
        }
    }


    public Set<Transition> getTransitions() {
        return transitions;
    }
}
