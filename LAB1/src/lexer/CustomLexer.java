package lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomLexer {

    public static List<Token> lex(String code) {
        List<Token> tokens = new ArrayList<>();

        // Define valid types
        List<String> validTypes = List.of("Barcelona", "RealMadrid", "Chelsea");

        // Define regex patterns for the tokens
        Pattern variablePattern = Pattern.compile("\\b[a-z][a-zA-Z]*\\b");
        Pattern equalsPattern = Pattern.compile("=");
        Pattern valuePattern = Pattern.compile("\\b\\d+\\b");
        Pattern semicolonPattern = Pattern.compile(";");

        // Split the input code into lines
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

    private static Pattern getCombinedPattern(List<String> validTypes) {
        String validTypesRegex = String.join("|", validTypes);
        String combinedRegex = "\\b(" + validTypesRegex + ")\\b|[A-Za-z][a-zA-Z]*|=|\\b\\d+\\b|;";
        return Pattern.compile(combinedRegex);
    }

    private static boolean isValidStructure(List<Token> tokens) {
        // Check if the token sequence represents a valid structure: type varName = varValue;
        return tokens.size() == 5 &&
                tokens.get(0).getType() == TokenType.TYPE &&
                tokens.get(1).getType() == TokenType.VARIABLE &&
                tokens.get(2).getType() == TokenType.EQUALS &&
                tokens.get(3).getType() == TokenType.VALUE &&
                tokens.get(4).getType() == TokenType.SEMICOLON;
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

}
