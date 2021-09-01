package symbols;
import lexer.*;
public class Array extends Type {
    public Type of;      // Element Type
    public int size = 1; // Element number
    public Array(int sz, Type p) {
        super("[]", Tag.INDEX, sz*p.width); size = sz; of = p;
    }
    public String toString() { return "[" + size + "] " + of.toString(); }
}
