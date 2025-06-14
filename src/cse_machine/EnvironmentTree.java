package CSE_Machine;

import java.util.ArrayList;
import java.util.List;

import Control_Structures.cs_node;


public class EnvironmentTree {
    private List<EnvNode> envList;

    public EnvironmentTree() {
        envList = new ArrayList<EnvNode>();
    }

    /*
     * Adding a new Env node to the Environment Tree
     *      provided the environment number, the variable stored and its parent environment
     */
    public void addEnv(int env_no, cs_node variable, EnvNode parentEnv) {
        EnvNode envNode = new EnvNode(env_no, variable, parentEnv);
        envList.add(envNode);
    }

    /*
     * Function to remove the Environment Node
     */
    public void removeEnv(int env_no){
        for (int i = 0; i < envList.size(); i++) {
            if (envList.get(i).getEnv_no() == env_no) {
                envList.remove(i);
                break;
            }
        }
    }

    /*
     * Function to extract an Environment Node from the tree given the number
     */
    public EnvNode getEnvNode(int env_no) {
        for (int i = 0; i < envList.size(); i++) {
            if (envList.get(i).getEnv_no() == env_no) {
                return envList.get(i);
            }
        }
        throw new EvaluationException("Missing Environment");
    }
    

}