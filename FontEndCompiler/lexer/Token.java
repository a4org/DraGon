package lexer;
// the base class
public class Token {
    // a token just with a tag
    public final int tag;
    public Token(int t) { tag = t; }
    public String toString() {return "" + (char)tag;}
}
