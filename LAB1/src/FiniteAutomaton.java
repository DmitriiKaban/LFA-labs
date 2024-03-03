import jdk.jfr.TransitionTo;

import java.util.*;
import java.util.stream.Collectors;

public class FiniteAutomaton {

    private Set<String> statesQ; // non-terminals + null
    private Set<String> alphabetSigma; // terminals
    private Set<Transition> transitions; // A -> aB, (A,a)=B, A->a, (A,a)=empty string
    private String startStateQ0; // start symbol
    private String finalStateF; // empty string

    public FiniteAutomaton() {
    }

    public FiniteAutomaton(Grammar grammar) {
        statesQ = grammar.getNonTerminals();
        statesQ.add("FINAL"); // Assuming null represents epsilon
        alphabetSigma = grammar.getTerminals();
        startStateQ0 = grammar.getStartSymbol();
        transitions = new HashSet<>();
        finalStateF = "FINAL"; // You might want to set this based on the grammar rules

        // from state
        Set<String> fromStates = grammar.getRules().keySet();
        for (String currentFrom : fromStates) {

            List<String> transitionTo = grammar.getRules().get(currentFrom);

            for (String withAndTo : transitionTo) {
                String fromState = currentFrom.charAt(0) + "";

                if (withAndTo.length() == 1 && grammar.getNonTerminals().contains(withAndTo.charAt(0) + "")) {
                    // A -> B
                    String toState = withAndTo;
                    transitions.add(new Transition(fromState, null, toState));
                } else if (withAndTo.length() == 1 && grammar.getTerminals().contains(withAndTo.charAt(0) + "")) {
                    // A -> a
                    String withSymbol = withAndTo;
                    transitions.add(new Transition(fromState, withSymbol, finalStateF));
                } else if (withAndTo.length() == 2 && grammar.getTerminals().contains(withAndTo.charAt(0) + "") && grammar.getNonTerminals().contains(withAndTo.charAt(1) + "")) {
                    // A -> aB
                    String withSymbol = withAndTo.charAt(0) + "";
                    String toState = withAndTo.charAt(1) + "";
                    transitions.add(new Transition(fromState, withSymbol, toState));
                } else if (withAndTo.length() == 0) {
                    // A -> ε (epsilon)
                    transitions.add(new Transition(fromState, null, null));
                }
            }
        }

    }

    public boolean checkString(String string) {

        String currentState = startStateQ0;
        return checkString(string, currentState);
    }

    public boolean isDeterministic() {

        Set<String> fromAndWithSymbols = new HashSet<>();

        for (Transition transition : transitions) {

            String currentFromAndWith = transition.getFromState().toString() + transition.getWithSymbol().toString();
            if (fromAndWithSymbols.contains(currentFromAndWith)) {
                return false;
            }
            fromAndWithSymbols.add(currentFromAndWith);
        }

        return true;
    }

    public boolean checkString(String string, String currentState) {

        if (string.isEmpty() && currentState == null) {
            return true;
        }

        boolean found = false;
        for (Transition transition : transitions) {

            if (transition.getFromState().equals(currentState) && Objects.equals(transition.getWithSymbol(), string.charAt(0) + "")) {
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


    public FiniteAutomaton convertToDFA() {
        if (isDeterministic()) {
            return this;
        }

        FiniteAutomaton dfa = new FiniteAutomaton();
        dfa.setStartStateQ0(startStateQ0);

        Set<String> newStates = new HashSet<>();
        newStates.add(startStateQ0);

        Set<Transition> newTransitions = new HashSet<>();

        Queue<String> statesToCheck = new LinkedList<>();
        statesToCheck.add(startStateQ0);

        do {

            String currentState = statesToCheck.poll();

            if (currentState == null) {
                break;
            }

            List<String> currentStates = List.of(currentState.split(" "));
            Map<String, Set<String>> withSymbolIntoState = new HashMap<>();

            for (String state : currentStates) {
                for (Transition transition : transitions) {
                    if (transition.getFromState().equals(state)) {
                        String withSymbol = transition.getWithSymbol();
                        String toState = transition.getToState();

                        if (withSymbolIntoState.containsKey(withSymbol)) {
                            withSymbolIntoState.get(withSymbol).add(toState);
                        } else {
                            withSymbolIntoState.put(withSymbol, new HashSet<>(List.of(toState)));
                        }
                    }
                }
            }

            for (String symbol : withSymbolIntoState.keySet()) {
                Set<String> statesForSymbol = withSymbolIntoState.get(symbol);

                if (statesForSymbol != null && !statesForSymbol.isEmpty()) {
                    String newState = statesForSymbol.stream()
                            .filter(Objects::nonNull)
                            .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
                            .collect(Collectors.joining(" "));

                    if (!newState.isEmpty() && !newStates.contains(newState)) {
                        newStates.add(newState);
                        newTransitions.add(new Transition(currentState, symbol, newState));
                        statesToCheck.add(newState);
                    }
                }
            }

        } while (!newStates.isEmpty());

        System.out.println("New transitions: " + newTransitions);
        dfa.setStatesQ(newStates);
        dfa.setTransitions(newTransitions);
        return dfa;
    }


    public class Transition {
        private String fromState;
        private String withSymbol;
        private String toState;

        public Transition(String fromState, String withSymbol, String toState) {
            this.fromState = fromState;
            this.withSymbol = withSymbol;
            this.toState = toState;
        }

        public String getFromState() {
            return fromState;
        }

        public String getWithSymbol() {
            return withSymbol;
        }

        public String getToState() {
            return toState;
        }

        @Override
        public String toString() {
            return "(" + fromState +
                    "," + withSymbol +
                    ")=" + toState;
        }
    }

    public Set<String> getStatesQ() {
        return statesQ;
    }

    public void setStatesQ(Set<String> statesQ) {
        this.statesQ = statesQ;
    }

    public Set<String> getAlphabetSigma() {
        return alphabetSigma;
    }

    public void setAlphabetSigma(Set<String> alphabetSigma) {
        this.alphabetSigma = alphabetSigma;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Set<Transition> transitions) {
        this.transitions = transitions;
    }

    public String getStartStateQ0() {
        return startStateQ0;
    }

    public void setStartStateQ0(String startStateQ0) {
        this.startStateQ0 = startStateQ0;
    }

    public String getFinalStateF() {
        return finalStateF;
    }

    public void setFinalStateF(String finalStateF) {
        this.finalStateF = finalStateF;
    }


}
