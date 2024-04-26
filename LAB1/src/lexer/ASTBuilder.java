package ast;

import lexer.ASTNode;
import lexer.Token;
import lexer.TokenType;

import java.util.List;

public class ASTBuilder {

    public static ASTNode buildAST(List<Token> tokens) {
        ASTNode root = new ASTNode(TokenType.TYPE, "Program");

        for (int i = 0; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            if (currentToken.getType() == TokenType.TYPE) {
                if (i + 4 >= tokens.size()) {
                    throw new IllegalArgumentException("Incomplete declaration at the end of the code");
                }
                ASTNode declarationNode = new ASTNode(TokenType.TYPE, "Declaration");
                declarationNode.setLeftChild(new ASTNode(TokenType.VARIABLE, tokens.get(i + 1).getValue()));
                declarationNode.setRightChild(new ASTNode(TokenType.VALUE, tokens.get(i + 3).getValue()));
                root.setLeftChild(declarationNode);
                i += 4; // Skip to the next line
            } else if (currentToken.getType() == TokenType.VARIABLE) {
                if (i + 5 >= tokens.size()) {
                    throw new IllegalArgumentException("Incomplete expression at the end of the code");
                }
                // Check if there's a semicolon at the end
                if (tokens.get(i + 4).getType() != TokenType.SEMICOLON) {
                    throw new IllegalArgumentException("Missing semicolon at the end of the expression");
                }
                ASTNode operationNode = new ASTNode(tokens.get(i + 1).getType(), "Operation");
                operationNode.setLeftChild(new ASTNode(TokenType.VARIABLE, currentToken.getValue()));
                operationNode.setRightChild(new ASTNode(tokens.get(i + 2).getType(), tokens.get(i + 2).getValue()));
                operationNode.getRightChild().setLeftChild(new ASTNode(TokenType.VALUE, tokens.get(i + 3).getValue()));
                operationNode.getRightChild().setRightChild(new ASTNode(TokenType.VARIABLE, tokens.get(i + 4).getValue()));
                root.setRightChild(operationNode);
                i += 5; // Skip to the next line
            }
        }

        return root;
    }

}
