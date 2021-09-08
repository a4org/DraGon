package parser;
import java.io.*;
import lexer.*;
import symbols.*;
import inter.*;

public class Parser {
    private Lexer lex;  // lexical analyzer of this parser
    private Token look; // lookahead token
    Env top = null; // env
    int used = 0;

    public Parser(Lexer l) throws IOException {
        lex = l;
        move();  // find the first lex (lookahead token)
    }

    void move() throws IOException { look = lex.scan(); }
    void error(String s) { throw new Error("near line " + lex.line + ": " + s); }

    void match(int t) throws IOException {
        if( look.tag == t ) move();
        else error("syntax error");
    }

    // Start 
    public void program() throws IOException {  // program -> block
        Stmt s = block();
        int begin = s.newlabel();
        int after = s.newlabel();

        s.emitlabel(begin);  // L1:

        s.gen(begin, after); // 
        s.emitlabel(after);
    }

    Stmt block() throws IOException {          // block -> { decls stmts }
        match('{');
        Env savedEnv = top; 
        top = new Env(top); // create a Env

	// decls just for put id into env table will not emit
        decls(); // there must have a sequence between decls and stmts 

        Stmt s = stmts(); // Seq

        match('}');
        top = savedEnv;                        // cannot be accessed outside the block

        return s; // Seq
    }

    // int i; float[100] j; 
    void decls() throws IOException {          // declaration emit nothing
        while ( look.tag == Tag.BASIC ) {      // which means this lex is a type ( see Type.java )
            Type p = type(); 
            Token tok = look; // int / float
            match(Tag.ID);  // a 
            match(';');

            Id id = new Id((Word)tok, p, used);
            top.put( tok, id );   //  actually decl() will will not print anything
	    // it just put the id (with its type) into the top Env
            used = used + p.width;
        }
    }

    Type type() throws IOException {
        Type p = (Type)look;
        match(Tag.BASIC); // if not a type, print error
        if( look.tag != '[' ) return p; // check whether it is a array
        else return dims(p);                   // return array type
    }

    Type dims(Type p) throws IOException {
        match('[');
        Token tok = look;                      // a number
        match(Tag.NUM);
        match(']');

        if( look.tag == '[' ) {
            p = dims(p);
        }

        return new Array(((Num)tok).value, p); // Array is a child class of Type
    } // End of Declaration


    Stmt stmts() throws IOException {
        if ( look.tag == '}' ) return Stmt.Null;
        else return new Seq(stmt(), stmts());  // seq is used for interation
	// Seq(stmt(), Seq(stmt(), Seq(stmt(), Seq...)))
    }

    // Predictive Parsing
    Stmt stmt() throws IOException {
        Expr x; Stmt s, s1, s2;
        Stmt savedStmt;

        switch( look.tag ) {
            case ';':
                move();
                return Stmt.Null;
            case Tag.IF:
                match(Tag.IF); match('('); x = bool(); match(')');
                s1 = stmt(); // block
                if( look.tag != Tag.ELSE ) {
                    return new If(x, s1);
                }
                match(Tag.ELSE);
                s2 = stmt();
                return new Else(x, s1, s2);
            case Tag.WHILE:
                While whilenode = new While();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = whilenode;
                match(Tag.WHILE); match('('); x = bool(); match(')');
                s1 = stmt();
                whilenode.init(x, s1);
                Stmt.Enclosing = savedStmt;
                return whilenode;
            case Tag.DO:
                Do donode = new Do();
                savedStmt = Stmt.Enclosing; 
                Stmt.Enclosing = donode;

                match(Tag.DO);
                s1 = stmt();
                match(Tag.WHILE); match('('); x = bool(); match(')'); match(';');
                donode.init(s1, x);
                Stmt Enclosing = savedStmt;
                return donode;
            case Tag.BREAK:
                match(Tag.BREAK); match(';');
                return new Break();
            case '{':
                return block();
            default:
                // assign a value 
                return assign();
        }
    }

    Stmt assign() throws IOException {
        Stmt stmt; 
        Token t = look;
        match(Tag.ID);
        Id id = top.get(t);
        if( id == null ) error(t.toString() + " undeclared");  // if not declared before
        if( look.tag == '=' ) {                // S -> id = E
            move();
            stmt = new Set(id, bool());
        }
        else {
            Access x = offset(id);
            match('='); 
            stmt = new SetElem(x, bool());
        }

        match(';');
        return stmt;

    }

    Expr bool() throws IOException {
        Expr x = join();
        while( look.tag == Tag.OR ) {
            Token tok = look; move(); x = new Or(tok, x, join());
        }
        return x;
    }

    Expr join() throws IOException {
        Expr x = equality();
        while( look.tag == Tag.AND ) {
            Token tok = look; move(); x = new And(tok, x, equality());
        }
        return x;
    }

    Expr equality() throws IOException {
        Expr x = rel();
        while( look.tag == Tag.EQ || look.tag == Tag.NE ) {
            Token tok = look; move(); return new Rel(tok, x, expr());
        }
        return x;
    }

    Expr rel() throws IOException {
        Expr x = expr();
        switch( look.tag ) {
            case '<': case Tag.LE: case Tag.GE: case '>':
                Token tok = look; move(); 
                return new Rel(tok, x, expr());
            default:
                return x;
        }
    }


    Expr expr() throws IOException {
        Expr x = term();
        while( look.tag == '-' || look.tag == '+' ) {
            Token tok = look;
            move();
            x = new Arith(tok, x, term()); // key to understand
        }
        return x;
    }

    // Key: (ignore unary)
    // expr -> expr + term // expr - term // term
    // term -> factor * factor // factor / factor // factor
    // factor -> NUM // REAL // TRUE // FALSE // ID // ERROR

    Expr term() throws IOException {
        Expr x = unary();
        while( look.tag == '*' || look.tag == '/' ) {
            Token tok = look; move(); x = new Arith(tok, x, unary() );
        }
        return x;
    }

    Expr unary() throws IOException {
        if( look.tag == '-' ) {
            move(); return new Unary(Word.minus, unary());
        }
        else if( look.tag == '!' ) {
            Token tok = look; move(); return new Not(tok, unary());
        }
        else return factor();
    }

    // jump here to understand (precedence of operators)
    Expr factor() throws IOException {
        Expr x = null;
        switch( look.tag ) {
            case '(':
                move(); x = bool(); match(')'); 
                return x;
            case Tag.NUM:
                x = new Constant(look, Type.Int );    // i = 1;
                move(); 
                return x;
            case Tag.REAL:
                x = new Constant(look, Type.Float );
                move();
                return x;
            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;
            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;
            default:
                error("syntax error");
                return x;

            case Tag.ID:
                Id id = top.get(look);
                if( id == null ) error(look.toString() + " undeclared");
                move();
                if( look.tag != '[' ) return id;
                else return offset(id);
        }
    }

    Access offset(Id a) throws IOException {
        Expr i; Expr w; Expr t1, t2; Expr loc;
        Type type = a.type;
        match('[');
        i = bool();
        match(']');

        type = ((Array)type).of;
        w = new Constant(type.width);
        t1 = new Arith(new Token('*'), i, w);
        loc = t1;

        while( look.tag == '[') {
            match('['); i = bool(); match('[');
            type = ((Array)type).of;
            w = new Constant(type.width);
            t1 = new Arith(new Token('*'), i, w);
            t2 = new Arith(new Token('+'), loc, t1);
            loc = t2;
        }

        return new Access(a, loc, type);
    }
}
