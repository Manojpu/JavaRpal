JFLAGS = -g
JC = javac
JAR = jar
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

# List all the Java source files here
CLASSES = \
	Lex_Analyzer/LexicalAnalyzer.java \
	Lex_Analyzer/Token.java \
	Parser/AST.java \
	Parser/LeafNode.java \
	Parser/Node.java \
	Parser/ParseException.java \
	Parser/ParseTree.java \
	Control_Structures/ControlStructures.java \
	Control_Structures/CSNode.java \
	CSE_Machine/CSE.java \
	CSE_Machine/EnvironmentTree.java \
	CSE_Machine/EnvNode.java \
	CSE_Machine/EvaluationException.java \
	CSE_Machine/RPALFunc.java \
	CSE_Machine/RPALBinaryOps.java \
	CSE_Machine/RPALUnaryOps.java \
	RPALmain.java

# Target for building the classes
default: classes

# Rule for compiling Java source files into classes
classes: $(CLASSES:.java=.class)


test:
	RPALmain Test_Programs/rpal_01 > output.01
	diff output.01 rpal_test_programs/output01.test

	RPALmain Test_Programs/add > output.01
	diff output.01 rpal_test_programs/add_out
	
	RPALmain Test_Programs/conc.1 > output.03
	diff output.03 rpal_test_programs/conc.1_out

	## lambda closure
	RPALmain Test_Programs/defns.1 > output.04
	diff output.04 rpal_test_programs/defns.1_out

	RPALmain Test_Programs/fn1 > output.05
	diff output.05 rpal_test_programs/fn1_out

	RPALmain Test_Programs/fn2 > output.06
	diff output.06 rpal_test_programs/fn2_out

	RPALmain Test_Programs/fn3 > output.07
	diff output.07 rpal_test_programs/fn3_out

	RPALmain Test_Programs/ftst > output.08
	diff output.08 rpal_test_programs/ftst_out

	RPALmain Test_Programs/infix > output.09
	diff output.09 rpal_test_programs/infix_out

	RPALmain Test_Programs/infix2 > output.10
	diff output.10 rpal_test_programs/infix2_out

	RPALmain Test_Programs/pairs1 > output.11
	diff output.11 rpal_test_programs/pairs1_out

	RPALmain Test_Programs/pairs2 > output.12
	diff output.12 rpal_test_programs/pairs2_out

	RPALmain Test_Programs/pairs3 > output.13
	diff output.13 rpal_test_programs/pairs3_out

	RPALmain Test_Programs/picture > output.14
	diff output.14 rpal_test_programs/picture_out

	RPALmain Test_Programs/towers > output.15
	diff output.15 rpal_test_programs/towers_out

	RPALmain Test_Programs/vectorsum > output.16
	diff output.16 rpal_test_programs/vectorsum_out

test2: 
	RPALmain Test_Programs/rpal_01 > output.01
	diff output.01 rpal_test_programs/output01.test


check: default test


clean:
	find -name "*.class" -exec $(RM) {} +