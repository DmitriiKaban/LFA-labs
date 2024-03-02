# Determinism in Finite Automata. Conversion from NDFA 2 DFA. Chomsky Hierarchy.

### Course: Formal Languages & Finite Automata
### Author: Cravcenco Dmitrii

----


## Objectives:
1. Understand what an automaton is and what it can be used for.

2. Continuing the work in the same repository and the same project, the following need to be added:
   a. Provide a function in your grammar type/class that could classify the grammar based on Chomsky hierarchy.

   b. For this you can use the variant from the previous lab.

3. According to your variant number (by universal convention it is register ID), get the finite automaton definition and do the following tasks:

   a. Implement conversion of a finite automaton to a regular grammar.

   b. Determine whether your FA is deterministic or non-deterministic.

   c. Implement some functionality that would convert an NDFA to a DFA.

   d. Represent the finite automaton graphically (Optional, and can be considered as a __*bonus point*__):

    - You can use external libraries, tools or APIs to generate the figures/diagrams.

    - Your program needs to gather and send the data about the automaton and the lib/tool/API return the visual representation.

Please consider that all elements of the task 3 can be done manually, writing a detailed report about how you've done the conversion and what changes have you introduced. In case if you'll be able to write a complete program that will take some finite automata and then convert it to the regular grammar - this will be **a good bonus point**.


## Implementation description

Firstly I created a method that could classify the grammar based on Chomsky hierarchy. In this method I firstly check if grammar is of the class 0
, to do so, I have to check if there exists at least one transition from terminal state. Then I also check if from state has more than 1
non-terminal symbol, this means that grammar is not of the second type -> it is of the first type.
After checking for 0 type, I check for type III. I use regex to find out if all the transitions belong to
right-hand or left-hand rule. If grammar has all the transitions according to one of the rules then it is
of the type III. After this I check if grammar belongs to the second type also using regex and if it doesn't
belong -> type I.
```
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
```

Then I implemented a method to generate convert FA to regular grammar. I had to define starting symbol,
set of non-terminals, terminals and establish rules according to transitions from FA.

```
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
```
To determine wether FA is deterministic I created the following method. It checks if there are multiple transitions
from one state to another with the same 'with' symbol.
```
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
```

To convert a NDFA to DFA I build the following method.
```
```
To represent a FA graphically I used JavaX. As example of the functionality from the drawing class I
provide you with two functions that draw state and transition.
```
private void drawState(Graphics g, String state, Point position) {
            g.setColor(Color.PINK);
            g.fillOval(position.x - 20, position.y - 20, 40, 40);
            g.setColor(Color.BLACK);
            g.drawOval(position.x - 20, position.y - 20, 40, 40);
            g.drawString(state, position.x - 5, position.y + 5);
        }

        private void drawTransition(Graphics g, FiniteAutomaton.Transition transition) {
            Point fromPosition = statePositions.get(transition.getFromState().toString());
            Point toPosition;

            if (transition.getToState() != null) {
                toPosition = statePositions.get(transition.getToState().toString());
            } else {
                // For transitions to the final state (empty string)
                toPosition = new Point(fromPosition.x + 80, fromPosition.y);
            }

            // Calculate intersection point on the state's circle
            Point intersection = calculateIntersectionPoint(fromPosition, toPosition, 10);

            int labelX = (fromPosition.x + toPosition.x) / 2;
            int labelY = (fromPosition.y + toPosition.y) / 2 - 5;

            // Draw line with arrowhead or loop
            if (transition.getFromState().equals(transition.getToState())) {
                drawSelfLoop(g, fromPosition.x, fromPosition.y, transition.getWithSymbol().toString());
            } else {
                drawArrow(g, fromPosition.x, fromPosition.y, intersection.x, intersection.y);
            }

            g.drawString(transition.getWithSymbol().toString(), labelX, labelY);
        }
```

Here is an example of visualization:

![alt text](https://github.com/DmitriiKaban/LFA-labs/blob/master/LAB1/fa_graphical_representation.png)


## Conclusions and Results
In this laboratory, I successfully implemented a Grammar class to represent formal grammar and performed various tasks
related to grammar analysis and finite automaton conversion. Here's a summary of my key accomplishments:

- Grammar Implementation

I designed a Grammar class that encapsulates the essential elements of formal grammar, including non-terminals, terminals,
a start symbol, and production rules. This class provides a convenient way to manipulate these components.

- String Generation

I implemented a function to generate 5 valid strings from the language expressed by the given grammar. The string generation logic is adaptable based on the rules defined in the grammar.

- Finite Automaton Conversion

I developed functionality to convert an object of type Grammar to an object of type Finite Automaton. This involved creating a FiniteAutomaton class with states, an alphabet, transitions, and a method to check if an input string is accepted.

- String Acceptance by Finite Automaton

I implemented a method in the Finite Automaton class to check if an input string can be obtained via state transitions. The implementation considered the transition rules defined by the grammar and traversed the automaton accordingly.
