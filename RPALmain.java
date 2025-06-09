import java.io.File;
import java.util.ArrayList;
import java.util.List;

import CSE_Machine.CSE;
import Control_Structures.ControlStructures;
import Control_Structures.cs_node;
import Parser.AST;
import Parser.ParseTree;
import Lex_Analyzer.LexicalAnalyzer;
import Lex_Analyzer.Token;

public class RPALmain {
    public static void main(String[] args) throws Exception {

        // Lexical Analysis
        File file = new File(args[0]);


        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(file);
        ArrayList<Token> tokenList = lexicalAnalyzer.getTokenList();

        // Parsin
        ParseTree parser = new ParseTree(tokenList);

        // AST Generation
        AST tree = parser.buildAst();

        
        // Standardize Tree
        tree.standardize();

        // Control Structures
        ControlStructures ctrlstruct = new ControlStructures();
        ctrlstruct.genControlStructures(tree.getRoot());
        List<List<cs_node>> deltc_struct = ctrlstruct.getCS();

        // CSE
        CSE cse_m = new CSE(deltc_struct);
        cse_m.runCSE();
        System.out.println();
    }
}