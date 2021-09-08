package inter;
import symbols.*;
public class If extends Stmt {
    Expr expr; Stmt stmt;
    public If(Expr x, Stmt s) {
	// bool(), stmt()
        expr = x;
        stmt = s;
        if( expr.type != Type.Bool ) expr.error("boolean required in if");
    }

    public void gen(int b, int a) {
	// gen(begin, after)
        int label = newlabel();
        expr.jumping(0, a); // if false goto a 
        emitlabel(label);
        stmt.gen(label, a);
    }
}
