package inter;
public class Stmt extends Node { // child class of Node
    public Stmt() { }
    public static Stmt Null = new Stmt();
    public void gen(int b, int a) {} // will not use this function (int stmt)
    // normally: see gen function in different nodes (ARITH, IF, BREAK, WHILE...)
    int after = 0;
    public static Stmt Enclosing = Stmt.Null;
}
