package lexer;

import java.util.ArrayList;
import java.util.List;

public class AstNode {
    AstNodeType type;
    String value;
    List<AstNode> children;

    AstNode(AstNodeType type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }
}

