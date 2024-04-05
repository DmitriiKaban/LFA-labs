import java.util.*;

public class ChomskyNormalForm extends Grammar {

    public ChomskyNormalForm(Grammar myGrammar) {

        super();
        this.setNonTerminals(myGrammar.getNonTerminals());
        this.setTerminals(myGrammar.getTerminals());
        this.setStartSymbol(myGrammar.getStartSymbol());
        this.setRules(myGrammar.getRules());
    }

    public void convertToCNF() {

        // Step 1: Remove ε (epsilon) productions
        removeEpsilonProductions();

        // Step 2: Remove unit productions
        removeUnitProductions();

        // Step 3: Eliminate inaccessible states
        eliminateInaccessibleStates();

        // Step 4: Eliminate non-productive rules
        eliminateNonProductiveRules();

        // Step 5: Convert to Chomsky Normal Form
        convertToChomskyNormalForm();
    }

    public void convertToChomskyNormalForm() {
        Map<String, String> terminalNonTerminals = new HashMap<>();
        Map<String, String> newNonTerminals = new HashMap<>();
        Map<String, String> nonTerminalMapping = new HashMap<>();
        int newNonTerminalCounter = 0;

        Map<String, List<String>> updatedRules = new HashMap<>(getRules());


        for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
            String fromState = entry.getKey();
            List<String> toStates = entry.getValue();
            List<String> newToStates = new ArrayList<>();
            for (String production : toStates) {
                if (production.length() > 2) {
                    String newNonTerminal = nonTerminalMapping.get(production);
                    if (newNonTerminal == null) {
                        newNonTerminal = generateNewNonTerminal(newNonTerminals, newNonTerminalCounter++, production);
                        newNonTerminals.put(newNonTerminal, production.substring(1));
                        nonTerminalMapping.put(production, newNonTerminal);
                    }
                    newToStates.add(production.charAt(0) + newNonTerminal);
                } else {
                    newToStates.add(production);
                }
            }
            updatedRules.put(fromState, newToStates);
        }

        // Step 2: Replace long productions with new non-terminals
        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
            updatedRules.put(entry.getKey(), List.of(entry.getValue()));
        }

        // Step 3: Replace single terminal productions with new non-terminals
        for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
            String fromState = entry.getKey();
            List<String> toStates = entry.getValue();
            List<String> newToStates = new ArrayList<>();
            for (String production : toStates) {
                if (production.length() == 2 && Character.isLowerCase(production.charAt(0)) && Character.isUpperCase(production.charAt(1))) {
                    String terminal = production.substring(0, 1);
                    String nonTerminal = terminalNonTerminals.getOrDefault(terminal, null);
                    if (nonTerminal == null) {
                        nonTerminal = generateNewNonTerminal(newNonTerminals, newNonTerminalCounter++, terminal);
                        terminalNonTerminals.put(terminal, nonTerminal);
                    }
                    newToStates.add(nonTerminal + production.charAt(1));
                } else {
                    newToStates.add(production);
                }
            }
            updatedRules.put(fromState, newToStates);
        }

        // Step 4: Add all new non-terminals to the final set of rules
        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
            String nonTerminal = entry.getKey();
            String terminal = entry.getValue();
            updatedRules.put(nonTerminal, List.of(terminal));
        }

        setRules(updatedRules);
    }


    private String generateNewNonTerminal(Map<String, String> newNonTerminals, int counter, String symbols) {
        String newNonTerminal = "X" + counter;
        newNonTerminals.put(newNonTerminal, symbols);
        return newNonTerminal;
    }


    public void eliminateNonProductiveRules() {

        // Step 1: Identify productive states
        Set<String> productiveStates = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : getRules().entrySet()) {
            String fromState = entry.getKey();
            List<String> toStates = entry.getValue();
            for (String production : toStates) {
                boolean valid = true;
                for (char state : production.toCharArray()) {
                    if (Character.isUpperCase(state) && !productiveStates.contains(state + "")) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    productiveStates.add(fromState);
                    break;
                }
            }
        }

        // Step 2: Remove non-productive states
        Map<String, List<String>> updatedRules = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : getRules().entrySet()) {
            String fromState = entry.getKey();
            if (productiveStates.contains(fromState)) {
                List<String> toStates = entry.getValue();
                List<String> updatedToStates = new ArrayList<>();
                for (String production : toStates) {
                    boolean valid = true;
                    for (char state : production.toCharArray()) {
                        if (Character.isUpperCase(state) && !productiveStates.contains(state + "")) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        updatedToStates.add(production);
                    }
                }
                updatedRules.put(fromState, updatedToStates);
            }
        }

        // Update the rules with the modified ones
        setRules(updatedRules);
    }

    public void eliminateInaccessibleStates() {

        // Step 1: Identify accessible states
        Set<String> accessibleStates = new HashSet<>();
        accessibleStates.add(getStartSymbol());
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, List<String>> entry : getRules().entrySet()) {
                String fromState = entry.getKey();
                List<String> toStates = entry.getValue();
                if (accessibleStates.contains(fromState)) {
                    for (String production : toStates) {
                        for (char state : production.toCharArray()) {
                            if (Character.isUpperCase(state) && !accessibleStates.contains(state + "")) {
                                accessibleStates.add(state + "");
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        // Step 2: Remove inaccessible states
        Map<String, List<String>> updatedRules = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : getRules().entrySet()) {
            String fromState = entry.getKey();
            if (accessibleStates.contains(fromState)) {
                List<String> toStates = entry.getValue();
                List<String> updatedToStates = new ArrayList<>();
                for (String production : toStates) {
                    boolean valid = true;
                    for (char state : production.toCharArray()) {
                        if (Character.isUpperCase(state) && !accessibleStates.contains(state + "")) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        updatedToStates.add(production);
                    }
                }
                updatedRules.put(fromState, updatedToStates);
            }
        }

        // Update the rules with the modified ones
        setRules(updatedRules);
    }

    public void removeUnitProductions() {
        Map<String, List<String>> updatedRules = new HashMap<>(getRules());

        // Step 3: Repeat Step 1 and 2 until all unit productions are removed
        boolean changed;
        do {
            changed = false;

            // Step 1: To remove X->Y add production X->a to the grammar rule whenever Y->a occurs
            for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
                String fromState = entry.getKey();
                List<String> toStates = entry.getValue();
                List<String> newToStates = new ArrayList<>();
                for (String production : toStates) {
                    if (production.length() == 1 && Character.isUpperCase(production.charAt(0))) {
                        List<String> unitProductions = updatedRules.get(production);
                        if (unitProductions != null) {
                            newToStates.addAll(unitProductions);
                            changed = true;
                        }
                    } else {
                        newToStates.add(production);
                    }
                }
                updatedRules.put(fromState, newToStates);
            }

            // Step 2: Now delete X->Y from the grammar
            for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
                String fromState = entry.getKey();
                List<String> toStates = entry.getValue();
                List<String> newToStates = new ArrayList<>();
                for (String production : toStates) {
                    if (!(production.length() == 1 && Character.isUpperCase(production.charAt(0)))) {
                        newToStates.add(production);
                    }
                }
                updatedRules.put(fromState, newToStates);
            }
        } while (changed);

        // Update the rules with the modified ones
        setRules(updatedRules);
    }

    // epsilon production -> when the right side is empty
    public void removeEpsilonProductions() {
        Map<String, List<String>> updatedRules = new HashMap<>(getRules());

        // Step 1: Identify states with epsilon productions
        Set<String> epsilonStates = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
            if (entry.getValue().contains("")) {
                epsilonStates.add(entry.getKey());
            }
        }

        // Step 2: Remove epsilon productions from grammar rules
        for (String state : epsilonStates) {
            List<String> productions = updatedRules.get(state);
            List<String> updatedProductions = new ArrayList<>(productions);
            updatedProductions.remove("");
            updatedRules.put(state, updatedProductions);
        }

        // Step 3: Replicate productions containing states with epsilon productions
        for (String stateWithEpsilon : epsilonStates) {
            for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
                String fromState = entry.getKey();
                List<String> toStates = entry.getValue();
                List<String> newToStates = new ArrayList<>();
                for (String production : toStates) {
                    if (production.contains(stateWithEpsilon)) {
                        List<String> replicatedProductions = replicateProduction(production, stateWithEpsilon);
                        newToStates.addAll(replicatedProductions);
                    } else {
                        newToStates.add(production);
                    }
                }
                updatedRules.put(fromState, newToStates);
            }
        }

        // Step 4: Generate all possible combinations of productions
        for (Map.Entry<String, List<String>> entry : updatedRules.entrySet()) {
            String fromState = entry.getKey();
            List<String> toStates = entry.getValue();
            Set<String> uniqueToStates = new HashSet<>();
            for (String production : toStates) {
                generateCombinations("", production, 0, uniqueToStates);
            }
            updatedRules.put(fromState, new ArrayList<>(uniqueToStates));
        }

        setRules(updatedRules);
    }

    private void generateCombinations(String prefix, String remaining, int index, Set<String> combinations) {
        if (index == remaining.length()) {
            combinations.add(prefix);
            return;
        }

        char currentChar = remaining.charAt(index);
        if (currentChar == 'ε') {
            generateCombinations(prefix, remaining, index + 1, combinations); // Exclude epsilon
            generateCombinations(prefix + currentChar, remaining, index + 1, combinations); // Include epsilon
        } else {
            generateCombinations(prefix + currentChar, remaining, index + 1, combinations); // Include current character
        }
    }



    // Method to replicate productions
    private List<String> replicateProduction(String production, String stateWithEpsilon) {
        List<String> replicatedProductions = new ArrayList<>();
        int numInstances = (int) production.chars().filter(ch -> ch == stateWithEpsilon.charAt(0)).count();
        for (int i = 0; i < Math.pow(2, numInstances); i++) {
            StringBuilder sb = new StringBuilder(production);
            for (int j = 0; j < numInstances; j++) {
                if ((i & (1 << j)) != 0) {
                    int index = sb.indexOf(stateWithEpsilon);
                    sb.replace(index, index + 1, "");
                }
            }
            replicatedProductions.add(sb.toString());
        }
        return replicatedProductions;
    }

}
