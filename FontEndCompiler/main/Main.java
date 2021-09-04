package main;
import lexer.*;
import parser.*;
import java.io.*;

public class Main {
    public static void main(String [] args) throws IOException {
        Lexer lex = new Lexer();
        Parser parse = new Parser(lex);
	// Start from here
        parse.program();
        System.out.write('\n');
    }
}
