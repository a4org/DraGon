package inter;
public class Seq extends Stmt { // child class of Stmt (sequence of stmt) 
    // Seq = (stmt, Seq)
    Stmt stmt1; Stmt stmt2;
    public Seq(Stmt s1, Stmt s2) { stmt1 = s1; stmt2 = s2; }
    // Code generator
    public void gen(int b, int a) {
        if ( stmt1 == Stmt.Null ) stmt2.gen(b, a); 
        else if ( stmt2 == Stmt.Null ) stmt1.gen(b, a); // only generate one stmt
        else {
            int label = newlabel();
            stmt1.gen(b, label);
            emitlabel(label);
            stmt2.gen(label,a);
        }
    }
}
