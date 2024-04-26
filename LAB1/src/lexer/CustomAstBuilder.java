package lexer;

import java.util.List;
import java.util.Stack;

public class CustomAstBuilder {

    public static AstNode buildAst(List<Token> tokens) {
        AstNode programNode = new AstNode(AstNodeType.PROGRAM, "Program");

        // Initialize a stack to keep track of the current context (e.g., line or expression)
        Stack<AstNode> contextStack = new Stack<>();
        contextStack.push(programNode); // Start with the program node as the context

        for (Token token : tokens) {
            switch (token.type()) {
                case TYPE:

                    if (contextStack.peek().type != AstNodeType.LINE) {
                        AstNode lineNode = new AstNode(AstNodeType.LINE, "Line");
                        contextStack.peek().children.add(lineNode); // Add it to the current context
                        contextStack.push(lineNode); // Set the new line node as the current context
                    }

                    // Create a type declaration node
                    AstNode typeDeclarationNode = new AstNode(AstNodeType.TYPE_DECLARATION, token.value());
                    contextStack.peek().children.add(typeDeclarationNode);
                    break;
                case VARIABLE:

                    if (contextStack.peek().type != AstNodeType.LINE) {
                        AstNode lineNode = new AstNode(AstNodeType.LINE, "Line");
                        contextStack.peek().children.add(lineNode); // Add it to the current context
                        contextStack.push(lineNode); // Set the new line node as the current context
                    }

                    if (!contextStack.peek().children.isEmpty() && contextStack.peek().children.get(0).type == AstNodeType.TYPE_DECLARATION) {
                        // Create a variable usage (expression) node
                        AstNode variableDeclarationNode = new AstNode(AstNodeType.VARIABLE_DECLARATION, token.value());
                        contextStack.peek().children.add(variableDeclarationNode);
                    } else {

                        AstNode variableUseNode = new AstNode(AstNodeType.VARIABLE_USE, token.value());
                        contextStack.peek().children.add(variableUseNode);
                    }
                    break;
                case EQUALS:
                    // Create an assignment node
                    AstNode assignmentNode = new AstNode(AstNodeType.ASSIGNMENT, "=");
                    contextStack.peek().children.add(assignmentNode);
                    break;
                case VALUE:
                    // Create a value node
                    AstNode valueNode = new AstNode(AstNodeType.VALUE, token.value());
                    contextStack.peek().children.add(valueNode);
                    break;
                case PLUS:
                case MINUS:
                case MULTIPLY:
                case DIVIDE:
                    // Create an operation node
                    AstNode operationNode = new AstNode(AstNodeType.OPERATION, token.value());
                    contextStack.peek().children.add(operationNode);
                    break;
                case SEMICOLON:
                    // End the current line context
                    contextStack.pop();
                    AstNode newLineNode = new AstNode(AstNodeType.LINE, "Line");
                    contextStack.peek().children.add(newLineNode);
                    contextStack.push(newLineNode);
                    break;
                // Handle other token types as needed
                default:
                    throw new RuntimeException("Unrecognized token: " + token.value());
            }
        }

        return programNode;
    }


    public static void printAst(AstNode node, int depth) {
        String indent = "  ".repeat(depth);

        boolean isLastLineEmpty = node.type == AstNodeType.LINE && node.children.isEmpty();

        if (!isLastLineEmpty) {
            System.out.println(indent + node.type + ": " + node.value);
        }

        for (AstNode child : node.children) {
            printAst(child, depth + 1);
        }
    }

}
