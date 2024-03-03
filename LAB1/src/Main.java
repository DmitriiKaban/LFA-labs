import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        //lab1();
        lab2();
    }

    private static void lab2() {
        // TODO: Implement some functionality that would convert an NDFA to a DFA
//        FiniteAutomaton lab2FA = initiateAutomaton();
//        // 2-nd task
//        Grammar grammarLab2 = new Grammar(lab2FA);
//        // 1-st task
//        System.out.println(grammarLab2.getChomskyType());
//        // 3-rd task
//        System.out.println("Is determenistic: " + lab2FA.isDeterministic());
//        // 4-th task
//        FiniteAutomaton dfa = lab2FA.convertToDFA();
//        // 5-th task
//        SwingUtilities.invokeLater(() -> new FiniteAutomatonVisualization(lab2FA));



        Grammar myGrammar = initiateGrammar();
        FiniteAutomaton myAutomaton = new FiniteAutomaton(myGrammar);
        System.out.println(myAutomaton.getTransitions());
        SwingUtilities.invokeLater(() -> new FiniteAutomatonVisualization(myAutomaton));
        FiniteAutomaton dfa = myAutomaton.convertToDFA();
        System.out.println(dfa.getStatesQ());
        SwingUtilities.invokeLater(() -> new FiniteAutomatonVisualization(dfa));

        FiniteAutomaton fa = initiateAutomaton();
        SwingUtilities.invokeLater(() -> new FiniteAutomatonVisualization(fa));

    }

    private static void lab1() {
        Grammar myGrammar = initiateGrammar();
        FiniteAutomaton myAutomaton = new FiniteAutomaton(myGrammar);

        myGrammar = new Grammar(myAutomaton);
        myAutomaton = new FiniteAutomaton(myGrammar);

        testGrammar(myGrammar, myAutomaton);

        System.out.println(myGrammar.getChomskyType());

        System.out.println(myGrammar.getRules());
        myGrammar = new Grammar(myAutomaton);
        System.out.println(myGrammar.getRules());
    }

    private static Grammar initiateGrammar() {

        Grammar myGrammar = new Grammar();
        Set<String> characters = new HashSet<>(List.of("S", "I", "J", "K"));
        myGrammar.setNonTerminals(characters);
        characters = new HashSet<>(List.of("a", "b", "c", "e", "n", "f", "m"));
        myGrammar.setTerminals(characters);
        myGrammar.setStartSymbol("S");
        HashMap<String, List<String>> rules = new HashMap<>();
        rules.put("S", List.of("cI"));
        rules.put("I", List.of("bJ", "fI", "eK", "e"));
        rules.put("J", List.of("nJ", "cS"));
        rules.put("K", List.of("m", "nK"));
        myGrammar.setRules(rules);

        return myGrammar;
    }

    private static FiniteAutomaton initiateAutomaton() {


        Set<String> states = new HashSet<>(List.of("q0", "q1", "q2", "q3", "q4"));

        Set<FiniteAutomaton.Transition> transitions = new HashSet<>();
        FiniteAutomaton finiteAutomaton = new FiniteAutomaton();  // Create an instance of FiniteAutomaton

        transitions.add(finiteAutomaton.new Transition("q0", "a", "q1"));
        transitions.add(finiteAutomaton.new Transition("q1", "a", "q1"));
        transitions.add(finiteAutomaton.new Transition("q1", "b", "q2"));
        transitions.add(finiteAutomaton.new Transition("q2", "b", "q3"));
        transitions.add(finiteAutomaton.new Transition("q3", "b", "q1"));
        transitions.add(finiteAutomaton.new Transition("q2", "a", "q4"));

        finiteAutomaton.setFinalStateF("q4");
        finiteAutomaton.setStatesQ(states);
        finiteAutomaton.setAlphabetSigma(new HashSet<>(List.of("a", "b")));
        finiteAutomaton.setTransitions(transitions);
        finiteAutomaton.setStartStateQ0("q0");


        return finiteAutomaton;
    }

    private static void testGrammar(Grammar myGrammar, FiniteAutomaton myAutomaton) {

        for (int i = 0; i < 5; i++) {
            String generatedString = myGrammar.generateString();
            System.out.println(myAutomaton.checkString(generatedString));

            System.out.println(myAutomaton.checkString(generatedString + "e"));
        }
    }
}