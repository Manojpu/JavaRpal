package CSE_Machine;

import Control_Structures.cs_node;


public class EnvNode {
    
    private int env_no;             // environment number 
    private cs_node variable;        // the variable/s and its corresponding value/s
    private EnvNode parentEnv;      // the parent environment node

    public EnvNode(int env_no, cs_node variable, EnvNode parentEnv) {
        this.env_no = env_no;
        this.variable = variable;
        this.parentEnv = parentEnv;
    }

    public int getEnv_no() {
        return env_no;
    }

    public void setEnv_no(int env_no) {
        this.env_no = env_no;
    }

    public cs_node getVariable() {
        return variable;
    }

    public void setEnv_variable(cs_node env_variable) {
        this.variable = env_variable;
    }

    public EnvNode getParentEnv() {
        return parentEnv;
    }

    public void setParentEnv(EnvNode parentEnv) {
        this.parentEnv = parentEnv;
    }
    
}