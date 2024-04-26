package lexer;

// AST Node Types
enum AstNodeType {
    PROGRAM,
    LINE,
    TYPE_DECLARATION,
    VARIABLE_DECLARATION,
    VARIABLE_USE,
    ASSIGNMENT,
    VALUE,
    OPERATION,
    EXPRESSION
}