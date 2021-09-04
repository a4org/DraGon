package lexer;
public class Real extends Token {
    // float + tag
    // floating number
    public final float value;
    public Real(float v) { super(Tag.REAL); value = v; }
    public String toString() { return "" + value; }
}
