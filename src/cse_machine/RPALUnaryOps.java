package CSE_Machine;

import Control_Structures.cs_node;

public class RPALUnaryOps {
    
    /*
     * RPAL function for NOT operator 
     */
    public static cs_node logicNot(cs_node node){
        if (node.getType().equals("TRUTHVALUE")) {
            if (node.getName().equals("true")) {
                return new cs_node("TRUTHVALUE", "false");
            } else {
                return new cs_node("TRUTHVALUE","true");
            }
        } else {
            throw new EvaluationException("Not a TruthValue type");
        }
    }
    
    /*
     * RPAL function for negative operator
     */
    public static cs_node neg(cs_node node){
        if (node.getType().equals("INTEGER")) {
            int num = Integer.parseInt(node.getName());
            node.setName(String.valueOf(-num));
            return node;
        } else {
            throw new EvaluationException("Not an INTEGER type");
        }
    }

}