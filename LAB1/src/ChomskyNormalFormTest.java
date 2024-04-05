import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChomskyNormalFormTest {

    private static ChomskyNormalForm chomskyNormalForm;
    @Test
    public void testChomskyNormalForm() {

        chomskyNormalForm.convertToCNF();
        assertTrue(chomskyNormalForm.getNonTerminals().containsAll(Set.of("S", "A", "B", "C")));
        assertTrue(chomskyNormalForm.getTerminals().containsAll(Set.of("a", "b")));
        assertEquals(7, chomskyNormalForm.getRules().size());

        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("S")).containsAll(List.of( "a", "AC", "BC", "AS", "b")));
        assertFalse(new HashSet<>(chomskyNormalForm.getRules().get("S")).containsAll(List.of("aB", "ASC")));

        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("A")).containsAll(List.of("a", "BC", "AS", "b")));
        assertFalse(new HashSet<>(chomskyNormalForm.getRules().get("A")).containsAll(List.of("ASC", "bS")));

        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("B")).contains("b"));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("C")).contains("BA"));
    }

    @Test
    public void testRemoveEpsilonProductions() {

        chomskyNormalForm.removeEpsilonProductions();
        assertTrue(chomskyNormalForm.getNonTerminals().containsAll(Set.of("S", "A", "B", "C")));
        assertTrue(chomskyNormalForm.getTerminals().containsAll(Set.of("a", "b")));
        assertEquals(5, chomskyNormalForm.getRules().size());
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("S")).containsAll(List.of("aB", "A", "AC")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("A")).containsAll(List.of("a", "ASC", "BC", "AS", "B")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("B")).containsAll(List.of("b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("C")).contains("BA"));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("E")).contains("bB"));

    }

    @Test
    public void testRemoveUnitProductions() {

        chomskyNormalForm.removeEpsilonProductions();
        chomskyNormalForm.removeUnitProductions();
        assertTrue(chomskyNormalForm.getNonTerminals().containsAll(Set.of("S", "A", "B", "C")));
        assertTrue(chomskyNormalForm.getTerminals().containsAll(Set.of("a", "b")));
        assertEquals(5, chomskyNormalForm.getRules().size());
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("S")).containsAll(List.of("aB", "a", "AC", "ASC", "BC", "AS", "b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("A")).containsAll(List.of("a", "ASC", "BC", "AS", "b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("B")).containsAll(List.of("b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("C")).contains("BA"));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("E")).contains("bB"));
    }

    @Test
    public void testEliminateInaccessibleStates() {

        chomskyNormalForm.removeEpsilonProductions();
        chomskyNormalForm.removeUnitProductions();
        chomskyNormalForm.eliminateInaccessibleStates();
        assertTrue(chomskyNormalForm.getNonTerminals().containsAll(Set.of("S", "A", "B", "C")));
        assertTrue(chomskyNormalForm.getTerminals().containsAll(Set.of("a", "b")));
        assertEquals(4, chomskyNormalForm.getRules().size());
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("S")).containsAll(List.of("aB", "a", "AC", "ASC", "BC", "AS", "b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("A")).containsAll(List.of("a", "ASC", "BC", "AS", "b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("B")).containsAll(List.of("b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("C")).contains("BA"));
        assertNull(chomskyNormalForm.getRules().get("E"));
    }

    @Test
    public void testEliminateNonProductiveRules() {

        chomskyNormalForm.removeEpsilonProductions();
        chomskyNormalForm.removeUnitProductions();
        chomskyNormalForm.eliminateInaccessibleStates();
        chomskyNormalForm.eliminateNonProductiveRules();
        assertTrue(chomskyNormalForm.getNonTerminals().containsAll(Set.of("S", "A", "B", "C")));
        assertTrue(chomskyNormalForm.getTerminals().containsAll(Set.of("a", "b")));
        assertEquals(4, chomskyNormalForm.getRules().size());
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("S")).containsAll(List.of("aB", "a", "AC", "ASC", "BC", "AS", "b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("A")).containsAll(List.of("a", "ASC", "BC", "AS", "b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("B")).containsAll(List.of("b", "bS")));
        assertTrue(new HashSet<>(chomskyNormalForm.getRules().get("C")).contains("BA"));
    }

    @BeforeEach
    public void createGrammar() {
        Grammar grammar = new Grammar();
        Set<String> characters = new HashSet<>(List.of("S", "A", "B", "C", "E"));
        grammar.setNonTerminals(characters);
        characters = new HashSet<>(List.of("a", "b"));
        grammar.setTerminals(characters);
        grammar.setStartSymbol("S");
        HashMap<String, List<String>> rules = new HashMap<>();
        rules.put("S", List.of("aB", "AC"));
        rules.put("A", List.of("a", "ASC", "BC"));
        rules.put("B", List.of("b", "bS"));
        rules.put("C", List.of("", "BA"));
        rules.put("E", List.of("bB"));

        grammar.setRules(rules);

        chomskyNormalForm = new ChomskyNormalForm(grammar);
    }
}