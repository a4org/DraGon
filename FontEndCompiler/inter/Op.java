package inter;
import lexer.*;
import symbols.*;
public class Op extends Expr {
    public Op(Token tok, Type p) { super(tok, p); }
    public Expr reduce() { 
        Expr x = gen(); // return this -> who is ther caller of reduce  (Constant or ID..)
        Temp t = new Temp(type);
        // generate a new temprary value and assign the x to the value
        emit( t.toString() + " = " + x.toString() );
        return t;
    } 
}
