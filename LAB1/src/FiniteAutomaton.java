import jdk.jfr.TransitionTo;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class FiniteAutomaton {

    private Set<Character> statesQ; // non-terminals + null
    private Set<Character> alphabetSigma; // terminals
    private Set<Transition> transitions; // A -> aB, (A,a)=B, A->a, (A,a)=empty string
    private char startStateQ0; // start symbol
    private Character finalStateF; // empty string

    public FiniteAutomaton() {
    }

    public FiniteAutomaton(Grammar grammar) {

        statesQ = grammar.getNonTerminals();
        statesQ.add(null);
        alphabetSigma = grammar.getTerminals();
        startStateQ0 = grammar.getStartSymbol();
        transitions = new HashSet<>();
        finalStateF = null;

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

    public boolean isDeterministic() {

        Set<String> fromAndWithSymbols = new HashSet<>();

        for (Transition transition: transitions) {

            String currentFromAndWith = transition.getFromState().toString() + transition.getWithSymbol().toString();
            if (fromAndWithSymbols.contains(currentFromAndWith)) {
                return false;
            }
            fromAndWithSymbols.add(currentFromAndWith);
        }

        return true;
    }

    public boolean checkString(String string, Character currentState) {

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


//    Variant 6
//    Q = {q0,q1,q2,q3,q4},
//            ∑ = {a,b},
//    F = {q4},
//    δ(q0,a) = q1,
//    δ(q1,b) = q1,
//    δ(q1,b) = q2,
//    δ(q2,b) = q3,
//    δ(q3,a) = q1,
//    δ(q2,a) = q4.

    public FiniteAutomaton convertToDFA() {

        if (isDeterministic()) {
            return this;
        }

        FiniteAutomaton dfa = new FiniteAutomaton();


        return null;
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

    public Set<Character> getStatesQ() {
        return statesQ;
    }

    public void setStatesQ(Set<Character> statesQ) {
        this.statesQ = statesQ;
    }

    public Set<Character> getAlphabetSigma() {
        return alphabetSigma;
    }

    public void setAlphabetSigma(Set<Character> alphabetSigma) {
        this.alphabetSigma = alphabetSigma;
    }

    public void setTransitions(Set<Transition> transitions) {
        this.transitions = transitions;
    }

    public char getStartStateQ0() {
        return startStateQ0;
    }

    public void setStartStateQ0(char startStateQ0) {
        this.startStateQ0 = startStateQ0;
    }
    public void setFinalState(char finalState) {
        this.finalStateF = finalState;
    }

    public Character getFinalStateF() {
        return finalStateF;
    }
}
