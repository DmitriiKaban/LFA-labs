import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Grammar myGrammar = initiateGrammar();
        FiniteAutomaton myAutomaton = new FiniteAutomaton(myGrammar);

        testGrammar(myGrammar, myAutomaton);
    }

    private static Grammar initiateGrammar() {

        Grammar myGrammar = new Grammar();
        Set<Character> characters = new HashSet<>(List.of('S', 'I', 'J', 'K'));
        myGrammar.setNonTerminals(characters);
        characters = new HashSet<>(List.of('a', 'b', 'c', 'e', 'n', 'f', 'm'));
        myGrammar.setTerminals(characters);
        myGrammar.setStartSymbol('S');
        HashMap<String, List<String>> rules = new HashMap<>();
        rules.put("S", List.of("cI"));
        rules.put("I", List.of("bJ", "fI", "eK", "e"));
        rules.put("J", List.of("nJ", "cS"));
        rules.put("K", List.of("m", "nK"));
        myGrammar.setRules(rules);

        return myGrammar;
    }

    private static void testGrammar(Grammar myGrammar, FiniteAutomaton myAutomaton) {

        for (int i = 0; i < 5; i++) {
            String generatedString = myGrammar.generateString();
            System.out.println(myAutomaton.checkString(generatedString));

            System.out.println(myAutomaton.checkString(generatedString + "e"));
        }
    }
}