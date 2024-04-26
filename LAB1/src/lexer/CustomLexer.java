package lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomLexer {

    public static List<Token> lex(String code) {
        List<Token> tokens = new ArrayList<>();


        List<String> validTypes = List.of("Barcelona", "RealMadrid", "Chelsea");

        Pattern variablePattern = Pattern.compile("\\b[a-z][a-zA-Z]*\\b");
        Pattern equalsPattern = Pattern.compile("=");
        Pattern valuePattern = Pattern.compile("\\b\\d+\\b");
        Pattern semicolonPattern = Pattern.compile(";");

        String[] lines = code.split("(?<=;)");

        int lineNumber = 1;
        for (String line : lines) {
            List<Token> lineTokens = new ArrayList<>();
            Matcher matcher = getCombinedPattern(validTypes).matcher(line);

            while (matcher.find()) {
                String match = matcher.group();

                if (validTypes.contains(match)) {
                    lineTokens.add(new Token(TokenType.TYPE, match));
                } else if (variablePattern.matcher(match).matches()) {
                    lineTokens.add(new Token(TokenType.VARIABLE, match));
                } else if (equalsPattern.matcher(match).matches()) {
                    lineTokens.add(new Token(TokenType.EQUALS, match));
                } else if (valuePattern.matcher(match).matches()) {
                    lineTokens.add(new Token(TokenType.VALUE, match));
                } else if (semicolonPattern.matcher(match).matches()) {
                    lineTokens.add(new Token(TokenType.SEMICOLON, match));
                } else if (isOperation(match)) {
                    lineTokens.add(new Token(getOperationType(match), match));
                } else {
                    throw new RuntimeException("Unrecognized token '" + match + "' in line " + lineNumber);
                }
            }

            // Check for structure validity
            if (!isValidStructure(lineTokens)) {
                String missingPart = getMissingPart(lineTokens);
                throw new RuntimeException("Error: Invalid code structure in line " + lineNumber +
                        ". " + missingPart + " in line: " + line);
            }

            tokens.addAll(lineTokens);
            lineNumber++;
        }


        return tokens;
    }

    private static boolean isOperation(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private static TokenType getOperationType(String token) {
        return switch (token) {
            case "+" -> TokenType.PLUS;
            case "-" -> TokenType.MINUS;
            case "*" -> TokenType.MULTIPLY;
            case "/" -> TokenType.DIVIDE;
            default -> throw new IllegalArgumentException("Invalid operation token: " + token);
        };
    }

    private static Pattern getCombinedPattern(List<String> validTypes) {
        String validTypesRegex = String.join("|", validTypes);
        String combinedRegex = "\\b(" + validTypesRegex + ")\\b|[A-Za-z][a-zA-Z]*|=|\\b\\d+\\b|[\\-+*/]|;";
        return Pattern.compile(combinedRegex);
    }


    private static boolean isValidStructure(List<Token> tokens) {
        // Check if it's the first structure: TYPE NAME = VALUE;
        if (tokens.size() == 5 &&
                tokens.get(0).getType() == TokenType.TYPE &&
                tokens.get(1).getType() == TokenType.VARIABLE &&
                tokens.get(2).getType() == TokenType.EQUALS &&
                tokens.get(3).getType() == TokenType.VALUE &&
                tokens.get(4).getType() == TokenType.SEMICOLON) {
            return true;
        }

        return (tokens.size() == 4 &&
                (tokens.get(0).getType() == TokenType.VARIABLE || tokens.get(0).getType() == TokenType.VALUE) &&
                (tokens.get(1).getType() == TokenType.PLUS ||
                        tokens.get(1).getType() == TokenType.MINUS ||
                        tokens.get(1).getType() == TokenType.MULTIPLY ||
                        tokens.get(1).getType() == TokenType.DIVIDE) &&
                (tokens.get(2).getType() == TokenType.VARIABLE || tokens.get(2).getType() == TokenType.VALUE) &&
                tokens.get(3).getType() == TokenType.SEMICOLON);
    }


    private static String getMissingPart(List<Token> tokens) {
        StringBuilder missingPart = new StringBuilder(" Token(s) missing: ");
        StringBuilder incorrectPlaceTokens = new StringBuilder(" Token(s) in incorrect place: ");

        if ((!tokens.isEmpty() && tokens.get(0).getType() != TokenType.TYPE)) {
            incorrectPlaceTokens.append("TYPE ");
        }
        if (!Arrays.toString(tokens.stream().map(Token::getType).toArray()).contains("TYPE")) {
            missingPart.append("TYPE ");
        }

        if ((tokens.size() > 1 && tokens.get(1).getType() != TokenType.VARIABLE)) {
            incorrectPlaceTokens.append("VARIABLE ");
        }
        if (!Arrays.toString(tokens.stream().map(Token::getType).toArray()).contains("VARIABLE")) {
            missingPart.append("VARIABLE ");
        }
        if ((tokens.size() > 2 && tokens.get(2).getType() != TokenType.EQUALS)) {
            incorrectPlaceTokens.append("EQUALS ");
        }
        if (!Arrays.toString(tokens.stream().map(Token::getType).toArray()).contains("EQUALS")) {
            missingPart.append("EQUALS ");
        }

        if (!Arrays.toString(tokens.stream().map(Token::getType).toArray()).contains("VALUE")) {
            missingPart.append("VALUE ");
        }
        if ((tokens.size() > 3 && tokens.get(3).getType() != TokenType.VALUE)) {
            incorrectPlaceTokens.append("VALUE ");
        }

        if (!Arrays.toString(tokens.stream().map(Token::getType).toArray()).contains("SEMICOLON")) {
            missingPart.append("SEMICOLON ");
        }
        if ((tokens.size() > 4 && tokens.get(4).getType() != TokenType.SEMICOLON)) {
            incorrectPlaceTokens.append("SEMICOLON");
        }


        String result = missingPart.toString().endsWith(": ") ? "" : missingPart + ".";
        result += incorrectPlaceTokens.toString().endsWith(": ") ? "" : incorrectPlaceTokens + ".";
        return result;
    }

    public static void evaluateAndPrint(String code) {
        CustomLexer lexer = new CustomLexer();
        List<Token> tokens = lexer.lex(code);

        int result = 0;

        for (int i = 0; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            if (currentToken.getType() == TokenType.VARIABLE && i < tokens.size() - 2 &&
                    tokens.get(i + 1).getType() == TokenType.EQUALS &&
                    (tokens.get(i + 2).getType() == TokenType.VALUE || tokens.get(i + 2).getType() == TokenType.VARIABLE)) {
                i += 2;
            } else if (currentToken.getType() == TokenType.VARIABLE || currentToken.getType() == TokenType.VALUE) {
                int operand1;
                if (currentToken.getType() == TokenType.VALUE) {
                    operand1 = Integer.parseInt(currentToken.getValue());
                } else {
                    operand1 = getValueOfVariable(currentToken.getValue(), tokens);
                }
                Token operatorToken = tokens.get(++i); // Move to the operator
                int operand2;
                if (tokens.get(++i).getType() == TokenType.VALUE) {
                    operand2 = Integer.parseInt(tokens.get(i).getValue()); // Next token is the second operand
                } else {

                    operand2 = getValueOfVariable(tokens.get(i).getValue(), tokens);
                }
                switch (operatorToken.getType()) {
                    case PLUS:
                        result = operand1 + operand2;
                        break;
                    case MINUS:
                        result = operand1 - operand2;
                        break;
                    case MULTIPLY:
                        result = operand1 * operand2;
                        break;
                    case DIVIDE:
                        if (operand2 != 0) {
                            result = operand1 / operand2;
                        } else {
                            throw new IllegalArgumentException("Division by zero is not allowed");
                        }
                        break;
                }
                System.out.println("Result of expression: " + result);
            }
        }
    }

    private static int getValueOfVariable(String variableName, List<Token> tokens) {
        for (Token token : tokens) {
            if (token.getType() == TokenType.VARIABLE && token.getValue().equals(variableName)) {

                return Integer.parseInt(tokens.get(tokens.indexOf(token) + 2).getValue());
            }
        }
        throw new IllegalArgumentException("Variable '" + variableName + "' not found");
    }


    private static int evaluateExpression(Token variableToken, Token operatorToken, List<Token> tokens) {

        int startIndex = tokens.indexOf(variableToken) + 2;
        int endIndex = tokens.indexOf(operatorToken) - 1;

        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = startIndex; i <= endIndex; i++) {
            expressionBuilder.append(tokens.get(i).getValue());
        }
        String expression = expressionBuilder.toString();

        // Evaluate the expression
        String[] parts = expression.split("[-+*/]");
        int operand1 = Integer.parseInt(parts[0].trim());
        int operand2 = Integer.parseInt(parts[1].trim());
        int result = 0;

        char operator = expression.replaceAll("[\\d\\s]", "").charAt(0);
        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 != 0) {
                    result = operand1 / operand2;
                } else {
                    throw new IllegalArgumentException("Division by zero is not allowed");
                }
                break;
        }

        return result;
    }


}
