package CSE_Machine;

import java.util.ArrayList;
import java.util.Collections;

import Control_Structures.cs_node;



public class RPALFunc {

    /*
     * Static Method to check if the Identifier is an in-built function in RPAL
     */
    public static boolean checkInBuilt(String name) {
        ArrayList<String> functionNames = new ArrayList<String>();

        Collections.addAll(functionNames, "Print","Stem","Stern","Conc","Order","Null",
                "Isinteger","Istruthvalue","Isstring","Istuple","Isfunction","Isdummy","ItoS");

        return functionNames.contains(name);
    }

    /*
     * Static Method to Print Values 
     */
    public static void Print(cs_node node) {
        
        // checking the type of the node to print the nodes
        switch (node.getType()) {

            // for integers, strings, truthvalues and nil
            case "INTEGER":
            case "STRING":
            case "TRUTHVALUE":
            case "NIL":
                // if getName string contains escape characters, print them accordingly
                if (node.getName().contains("\\n")) {
                    node.setName(node.getName().replace("\\n", "\n"));
                }
                if(node.getName().contains("\\t")) {
                    node.setName(node.getName().replace("\\t", "\t"));
                }
                System.out.print(node.getName());       // print the value of the node
                break;
        
            // for printing the tuple
            case "tau":
            case "tuple":
                System.out.print("(");
                for (int i = 0; i < node.getTuple().size(); i++) {
                    System.out.print(node.getTuple().get(i).getName());
                    if (i != node.getTuple().size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print(")");
                break;
            
            // for lambda nodes
            case "lambdaClosure":
                // print like [lambda closure: x: 2]
                System.out.print("[lambda closure: ");
                for (int i = 0; i < node.getLambdavar().size(); i++) {
                    System.out.print(node.getLambdavar().get(i));       
                    /*
                     * Have to check how it should be printed for many lambda variables
                     */
                }
                System.out.print(": ");
                System.out.print(node.getLambdano());
                System.out.print("]");
                break;
            
            default:
                System.out.println(); // check this
                break;
        }        
    }

    /*
     * RPAL Function to return first character of String
     */
    public static cs_node Stem(cs_node node) {
        if (node.getType().equals("STRING")) {
            cs_node newNode = node.duplicate();
            newNode.setName(newNode.getName().substring(0,1));
            return newNode;
        } else {
            // throw exception if argument not string
            throw new EvaluationException("Argument is not a string");
        }
    }

    /*
     * RPAL Function to return substring without the first character 
     */
    public static cs_node Stern(cs_node node) {
        if (node.getType().equals("STRING")) {
            cs_node newNode = node.duplicate();
            newNode.setName(newNode.getName().substring(1));
            return newNode;
        } else {
            // throw exception if argument not string
            throw new EvaluationException("Argument is not a string");
        }
    }

    /*
     * RPAL Function to conduct the first step in Concatenating
     */
    public static cs_node ConcOne(cs_node node1) {
        if (node1.getType().equals("STRING")) {
            cs_node concOne = new cs_node("IDENTIFIER","ConcOne");
            concOne.getTuple().add(node1);
            return concOne;
        } else {
            // throw exception if argument not string
            throw new EvaluationException("Argument is not a string");
        }
    }

    /*
     * RPAL Function to conduct the second the step in Concatenating
     */
    public static cs_node Conc(cs_node node1, cs_node node2) {
        // node1 was already checked for a string type in previous ConcOne method
        if (node2.getType().equals("STRING")) {
            String conc = node1.getName().concat(node2.getName());
            return new cs_node("STRING", conc);
        } else {
            // throw exception if argument not string
            throw new EvaluationException("Argument is not a string");
        }
    }

    /*
     * RPAL function to obtain the number of elements in the tuple
     */
    public static cs_node Order(cs_node tupleNode) {
        if (tupleNode.getIsTuple()) {
            int num = tupleNode.getTuple().size();
            return new cs_node("INTEGER", String.valueOf(num));
        } else {
            // throw exception if argument not tuple type
            throw new EvaluationException("Attempt to find the order of a non-tuple");
        }
    }

    /*
     * RPAL Function to check if the Tuple is NIL
     */
    public static cs_node Null(cs_node tupleNode) {
        if (tupleNode.getTuple().size() == 0 && tupleNode.getIsTuple()) {
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    /*
     * Code to check Type of the variable
     */
    // Checking if type integer
    public static cs_node Isinteger(cs_node node) {
        if (node.getType().equals("INTEGER")) {
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    // checking is type truthvalue
    public static cs_node Istruthvalue(cs_node node) {
        if (node.getType().equals("TRUTHVALUE")) {
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    // checking if type string
    public static cs_node Isstring(cs_node node) {
        if (node.getType().equals("STRING")) {
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    // checking if type tuple
    public static cs_node Istuple(cs_node node) {
        if (node.getIsTuple()) { 
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    // checking if type Function
    public static cs_node Isfunction(cs_node node) {
        if (node.getType().equals("FUNCTION")) { 
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    // checking if type Dummy
    public static cs_node Isdummy(cs_node node) {
        if (node.getType().equals("DUMMY")) {
            return new cs_node("TRUTHVALUE", "true");
        } else {
            return new cs_node("TRUTHVALUE", "false");
        }
    }

    // converting the node from integer to string
    public static cs_node intToStr(cs_node intNode) {
        if (intNode.getType().equals("INTEGER")) {
            cs_node strNode = intNode.duplicate();
            strNode.setType("STRING");
            return strNode;
        } else {
            throw new EvaluationException("Argument is not an Integer");
        }
    }

    
}