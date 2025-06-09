package CSE_Machine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import Control_Structures.cs_node;


public class CSE {
    private List<List<cs_node>> deltaLists;                          // control structures
    private Stack<cs_node> ControlList = new Stack<cs_node>();        // *C*ontrol
    private Stack<cs_node> StackList = new Stack<cs_node>();          // *S*tack
    private EnvironmentTree envtree = new EnvironmentTree();        // *E*nvironment Tree
    private int curr_env = 0;                                       // current environment number
    private int env_counter = 0;                                    // counter to indicate last created environment number
    private List<Integer> activeEnvNum = new ArrayList<Integer>();  // list of environments currently open
    
    // provide the delta lists when initiaiting a new CSE machine
    public CSE(List<List<cs_node>> deltaLists) {
        this.deltaLists = deltaLists;
    }

    public int getEnvCounter() {
        return env_counter;
    } 

    /*
     * Method to insert a control structure into the Control given the delta number
     */
    public void insertToControl(int delta_num) {
        List<cs_node> delta_i = deltaLists.get(delta_num);
        cs_node delta_cs = new cs_node("delta", delta_num, delta_i);
        this.getControlList().push(delta_cs);
    }

    /*
     * Method to open the control structure into its constituent nodes
     */
    public void expandDelta() {
        cs_node delta_cs = this.getControlList().pop();

        // if its a control structure
        if (delta_cs.getType().equals("delta")) {
            // loop through control structure and push each node into the stack
            List<cs_node> ctrl_struct = delta_cs.getTuple();
            for (int i=0; i< ctrl_struct.size(); i++) {
                this.getControlList().push(ctrl_struct.get(i)); 
            }
        } else {
            // if not a control structure
            throw new EvaluationException("Not a Control Structure - cannot be expanded");
        }
    }

    /*
     * Method to Start up the CSE machine
     */
    private void setupCSE() {
        cs_node parent_env = new cs_node("env", curr_env);              // create env node 0

        this.ControlList.push(parent_env);                              // insert env node to Control
        this.StackList.push(parent_env);                                // insert env node to Stack
        this.envtree.addEnv(curr_env, null, null);   // add the Env node 0 to the Tree (with no parent)
        this.activeEnvNum.add(curr_env);                                // include the 0 into the active env list

        this.insertToControl(0);                              // insert the 0th control structure
        this.expandDelta();                                                 // and expand it
    }

    /*
     * Method to lookup the environment nodes to search for the value of a given variable name 
     */
    public cs_node lookUpEnv(EnvironmentTree envtree, int env_no, String variable) {
        cs_node envVar = envtree.getEnvNode(env_no).getVariable();
        
        // list of variables stored in env node considered 
        List<String> varList = envVar.getLambdavar();

        // if required variable is found
        if (varList.contains(variable)) {
            int idx = varList.indexOf(variable);
            
            // return the corresponding variable stored inside the tuple 
            return envVar.getTuple().get(idx);
        } else {

            // if not found visit parent node and repeat
            int parent_no = envtree.getEnvNode(env_no).getParentEnv().getEnv_no();
            return lookUpEnv(envtree, parent_no, variable);
        }

    }

    /*
     * Main Driver Function of CSE to implement logic
     */
    public void runCSE() {
        setupCSE();                                             // setup CSE

        // while Control is not empty
        while(!this.getControlList().empty()) {
            
            cs_node topCtrlNode = this.getControlList().pop();   // topmost node in control  
            cs_node topStackNode1;                               // topmost node in stack
            cs_node topStackNode2;                               // 2nd topmost node in stack

            cs_node newGammaNode;
            cs_node newlambdaNode;

            cs_node valueItem = topCtrlNode.duplicate();
            
            /* First cheking the topmost Control Node to help decide on the CSE rule to implement */
            switch (topCtrlNode.getType()) {
                
                // CSE Rule 1
                // Stacking Name Variables

                // Integers, Strings, TruthValues, Nil, Dummy, Y* nodes can be pushed to stack directly
                case "INTEGER":
                    this.getStackList().push(valueItem);
                    break;
                
                case "STRING":
                    this.getStackList().push(valueItem);
                    break;
                
                case "TRUTHVALUE":
                    this.getStackList().push(valueItem);
                    break;

                case "NIL":
                    this.getStackList().push(valueItem);
                    break;

                case "DUMMY":
                    this.getStackList().push(valueItem);
                    break;

                case "Y":
                    this.getStackList().push(valueItem);
                    break;

                case "IDENTIFIER":

                    // identifier belongs to an inbuilt function name directly insert
                    if (RPALFunc.checkInBuilt(topCtrlNode.getName())) {
                        this.getStackList().push(valueItem);
                        
                    } else {
                        // get the name of the indentifier variable
                        String varName = topCtrlNode.getName();
                        // lookup the value of the identifier using the environment tree
                        cs_node valueNode = lookUpEnv(envtree, curr_env, varName);
                        // add the value of the identifier to the stack
                        this.getStackList().push(valueNode);
                    }

                    break;

                // CSE Rule 2
                // Stack Lambda into the Stack
                case "lambdaClosure":
                    topCtrlNode.setEnvno(curr_env);                 // mark the current environment onto the lambda node
                    cs_node lambdaNode = topCtrlNode.duplicate();
                    this.StackList.push(lambdaNode);                // push the lambda node with env into stack
                    break;
  
                // CSE Rules 3, 4, 10, 11, 12, 13 (rules with Gamma application)
                case "gamma":
                    topStackNode1 = this.getStackList().pop();

                    // based on type of node on top of stack apply gamma
                    switch (topStackNode1.getType()) {
                        
                        // CSE Rule 3
                        // Apply Rator
                        case "IDENTIFIER":
                            topStackNode2 = this.getStackList().pop();

                            /*
                             * Applying inbuilt functions to the 2nd topmost node in the stack
                             */
                            switch (topStackNode1.getName()) {
                                case "Print":
                                    RPALFunc.Print(topStackNode2);
                                    this.getStackList().push(topStackNode2);
                                    break;

                                case "Conc":
                                    cs_node concOneNode = RPALFunc.ConcOne(topStackNode2);
                                    this.getStackList().push(concOneNode);
                                    break;

                                case "ConcOne":
                                    cs_node node1 = topStackNode1.getTuple().get(0);
                                    cs_node concatNode = RPALFunc.Conc(node1, topStackNode2);
                                    this.getStackList().push(concatNode);
                                    break;
                                
                                case "Stem":
                                    cs_node stemNode = RPALFunc.Stem(topStackNode2);
                                    this.getStackList().push(stemNode);
                                    break;

                                case "Stern":
                                    cs_node sternNode = RPALFunc.Stern(topStackNode2);
                                    this.getStackList().push(sternNode);
                                    break;

                                case "Order":
                                    cs_node numNode = RPALFunc.Order(topStackNode2);
                                    this.getStackList().push(numNode);
                                    break;

                                case "Null":
                                    cs_node nullNode = RPALFunc.Null(topStackNode2);
                                    this.getStackList().push(nullNode);
                                    break;

                                case "Isinteger":
                                    cs_node isIntNode = RPALFunc.Isinteger(topStackNode2);
                                    this.getStackList().push(isIntNode);
                                    break;
                                
                                case "Istruthvalue":
                                    cs_node isTruthNode = RPALFunc.Istruthvalue(topStackNode2);
                                    this.getStackList().push(isTruthNode);
                                    break;
                                
                                case "Isstring":
                                    cs_node isStringNode = RPALFunc.Isstring(topStackNode2);
                                    this.getStackList().push(isStringNode);
                                    break;
                                
                                case "Istuple":
                                    cs_node isTupleNode = RPALFunc.Istuple(topStackNode2);
                                    this.getStackList().push(isTupleNode);
                                    break;
                                
                                case "Isfunction":
                                    cs_node isFunctionNode = RPALFunc.Isfunction(topStackNode2);
                                    this.getStackList().push(isFunctionNode);
                                    break;
                                
                                case "Isdummy":
                                    cs_node isDummyNode = RPALFunc.Isdummy(topStackNode2);
                                    this.getStackList().push(isDummyNode);
                                    break;

                                case "ItoS":
                                    cs_node strNode = RPALFunc.intToStr(topStackNode2);
                                    this.getStackList().push(strNode);
                                    break;
                                
                                default:
                                    break;
                            }
                            break;

                        // CSE Rule 4 & 11
                        // Apply Lambda (to single and multivariable)
                        case "lambdaClosure":
                            // Obtaining Random Value
                            topStackNode2 = this.getStackList().pop();
                            
                            // moving to next environment
                            env_counter++;
                            this.curr_env = env_counter;

                            // creating new environment variable to insert to control-stack
                            cs_node envCSNode = new cs_node("env", env_counter);

                            // clear space in tuple parameter to insert the values of the parameters tracked by lambda
                            topStackNode1.setTuple(new ArrayList<cs_node>());
                            
                            // if the lambda node tracks multiple parameters (formerly a comma node)
                            if (topStackNode1.getLambdavar().size() > 1) {

                                // then insert each value into the tuple parameter of lambda node
                                List<cs_node> tuple1 = topStackNode2.getTuple();
                                for (int i = 0; i < tuple1.size(); i++) {
                                    topStackNode1.getTuple().add(tuple1.get(i));
                                }

                            } else {
                                // else just save the value in the tuple parameter of the lambda node
                                topStackNode1.getTuple().add(topStackNode2);
                            }
                            cs_node valueNode = topStackNode1.duplicate();

                            // create a new Environment node with value saved 
                            this.envtree.addEnv(curr_env, valueNode, this.envtree.getEnvNode(topStackNode1.getEnvno()));
                            
                            // update environment numbers
                            this.activeEnvNum.add(curr_env);
                            this.setCurr_env(curr_env);

                            // push the new environment node
                            this.getControlList().push(envCSNode);
                            this.getStackList().push(envCSNode);

                            // insert the next delta structure
                            int delta_no = topStackNode1.getLambdano();
                            this.insertToControl(delta_no);
                            this.expandDelta();

                            break;

                        // CSE Rule 10
                        // Tuple Selection
                        case "tuple":
                            // get the index of the element to select from tuple
                            topStackNode2 = this.getStackList().pop();
                            int index_i = Integer.parseInt(topStackNode2.getName());

                            // extract tuple
                            List<cs_node> tuple = topStackNode1.getTuple();
                            
                            // selecting the required tuple element
                            cs_node tup_elem = tuple.get(index_i-1);

                            // inserting the selected tuple element
                            this.getStackList().push(tup_elem);
                            
                            break;
                        
                        // CSE Rule 12
                        // Applying Y to lambda
                        case "Y":
                            topStackNode2 = this.getStackList().pop();
                            cs_node etaNode = topStackNode2.duplicate();
                            etaNode.setType("eta");
                            this.getStackList().push(etaNode);
                            break;
                        
                        // CSE Rule 13
                        // Applying f.p.
                        case "eta":
                            // updating the control
                            newGammaNode = new cs_node("gamma", "gamma");
                            // pushing 2 gamma nodes to the control
                            this.getControlList().push(newGammaNode);      
                            this.getControlList().push(newGammaNode);      

                            // updating the stack
                            List<String> varList = topStackNode1.getLambdavar();
                            
                            // creating a new lambda node with env stored
                            newlambdaNode = new cs_node("lambdaClosure", varList, topStackNode1.getLambdano());
                            newlambdaNode.setEnvno(topStackNode1.getEnvno());
                            this.getStackList().push(topStackNode1);        // pushing the eta node back into the stack
                            this.getStackList().push(newlambdaNode);        // pushing the lambda into the stack
                            break;
                    
                        default:
                            break;
                    }
                    break;
                    /* End of Gamma based rules */

                // CSE Rules 5
                // Exit Environment
                case "env":
                    // value node to be reinserted to stack
                    topStackNode1 = this.getStackList().pop();
                    
                    // environment variable found in stack
                    topStackNode2 = this.getStackList().pop();

                    // checking if the environment variables are matching
                    if (topCtrlNode.getType().equals(topStackNode2.getType()) & 
                                        topCtrlNode.getEnvno() == topStackNode2.getEnvno()){
                        this.getStackList().push(topStackNode1);
                        
                        // unless root environment
                        if (this.curr_env != 0) {
                            // remove the env number from the list of active environments
                            this.activeEnvNum.remove((Integer) curr_env);
                            // set the last unclosed environment in active list as current environment
                            this.curr_env = Collections.max(activeEnvNum);   
                        }
                    } else {
                        // if environments did not match put exception
                        throw new EvaluationException("Error in Environments");
                    }
                    break;

                    
                // CSE Rules 6
                // Binary Operators
                case "OPERATOR":
                    // obtain the two operands for the binary operation
                    topStackNode1 = this.getStackList().pop();
                    topStackNode2 = this.getStackList().pop();
                    switch (topCtrlNode.getName()) {
                        case "+":
                            cs_node sumNode = RPALBinaryOps.add(topStackNode1, topStackNode2);
                            this.getStackList().push(sumNode);    
                            break;
                        case "-":
                            cs_node diffNode = RPALBinaryOps.subtract(topStackNode1, topStackNode2);
                            this.getStackList().push(diffNode);    
                            break;
                        case "*":
                            cs_node productNode = RPALBinaryOps.multiply(topStackNode1, topStackNode2);
                            this.getStackList().push(productNode);
                            break;
                        case "/":
                            cs_node quotientNode = RPALBinaryOps.divide(topStackNode1, topStackNode2);
                            this.getStackList().push(quotientNode);
                            break;
                        case "**":
                            cs_node powerNode = RPALBinaryOps.power(topStackNode1, topStackNode2);
                            this.getStackList().push(powerNode);
                            break;
                        case "eq":
                            cs_node isEqual = RPALBinaryOps.isEqual(topStackNode1, topStackNode2);
                            this.getStackList().push(isEqual);
                            break;
                        case "ne":
                            cs_node isNotEqual = RPALBinaryOps.isNotEqual(topStackNode1, topStackNode2);
                            this.getStackList().push(isNotEqual);
                            break;
                        case "ls":
                        case "<":
                            cs_node isLess = RPALBinaryOps.isLessThan(topStackNode1, topStackNode2);
                            this.getStackList().push(isLess);
                            break;
                        case "gr":
                        case ">":
                            cs_node isGreater = RPALBinaryOps.isGreaterThan(topStackNode1, topStackNode2);
                            this.getStackList().push(isGreater);
                            break;
                        case "le":
                        case "<=":
                            cs_node isLessEqual = RPALBinaryOps.isLessEqualThan(topStackNode1, topStackNode2);
                            this.getStackList().push(isLessEqual);
                            break;
                        case "ge":
                        case ">=":
                            cs_node isGreaterEqual = RPALBinaryOps.isGreaterEqualThan(topStackNode1, topStackNode2);
                            this.getStackList().push(isGreaterEqual);
                            break;
                        case "or":
                            cs_node logicOR = RPALBinaryOps.logicOR(topStackNode1, topStackNode2);
                            this.getStackList().push(logicOR);
                            break;
                        case "&":
                            cs_node logicAND = RPALBinaryOps.logicAND(topStackNode1, topStackNode2);
                            this.getStackList().push(logicAND);
                            break;
                        case "aug":
                            cs_node augNode = RPALBinaryOps.augment(topStackNode1, topStackNode2);
                            this.getStackList().push(augNode);
                            break;
                        default:
                            break;
                    }
                    break;

                // CSE Rules 7
                // Unary Operators
                case "not":
                    topStackNode1 = this.getStackList().pop();
                    this.getStackList().push(RPALUnaryOps.logicNot(topStackNode1));
                    break;
                case "neg":
                    topStackNode1 = this.getStackList().pop();
                    this.getStackList().push(RPALUnaryOps.neg(topStackNode1));
                    break;

                // CSE Rules 8
                // Conditional

                case "beta":
                    topStackNode1 = this.getStackList().pop();              // topmost stack element indicating true/false
                    if (topStackNode1.getName().equals("true")) {
                        // insert delta-then
                        this.insertToControl(topCtrlNode.getThenno());      
                        this.expandDelta();
                    } else if (topStackNode1.getName().equals("false")) {
                        // insert delta-else
                        this.insertToControl(topCtrlNode.getElseno());
                        this.expandDelta();
                    }
                    break;
                
                // CSE Rules 9
                // Tuple Formation
                case "tau":
                    // get number of elements in the tuple
                    int n = topCtrlNode.getTauno();
                    
                    // creating the tuple Object to be added into the stack
                    cs_node tuple = new cs_node("tuple", "tuple");
                    tuple.setIsTuple(true);

                    // extracting each of the tuple items from the loop 
                        // and adding to the tuple object
                    for (int i=0; i<n; i++) {
                        cs_node tup_elem = this.getStackList().pop();
                        tuple.getTuple().add(tup_elem.duplicate());
                    }

                    // adding the tuple object to the stack
                    this.getStackList().push(tuple);

                    break;
            
                default:
                    break;
            }

        }

    }
    /*
     * End of runCSE method
     */

    public Stack<cs_node> getControlList() {
        return ControlList;
    }

    public void setControlList(Stack<cs_node> controlList) {
        ControlList = controlList;
    }

    public Stack<cs_node> getStackList() {
        return StackList;
    }

    public void setStackList(Stack<cs_node> stackList) {
        StackList = stackList;
    }

    public int getCurr_env() {
        return curr_env;
    }

    public void setCurr_env(int curr_env) {
        this.curr_env = curr_env;
    }

} 
    