package lexer;
public class Num extends Token {
    // integer + tag
    // number (integer)
    public final int value;
    public Num(int v) { super(Tag.NUM); value = v; } // super(Tag.NUM) means a Num project will have a tag NUM
    public String toString() {return "" + value;}
}
