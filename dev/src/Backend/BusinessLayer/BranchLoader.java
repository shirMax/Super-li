package Backend.BusinessLayer;

import Backend.PersistenceLayer.BranchesDAO;
import Backend.PersistenceLayer.CreateBranches;

import java.util.List;

public class BranchLoader {
    private BranchesDAO branches=new BranchesDAO();

    public BranchLoader() {
        new CreateBranches();
    }

    public List<String> getAllBranches() throws Exception {
        return branches.getAll();
    }

    public void addNewBranch(String branchAddress,String area) throws Exception {
        if(!checkBranchExists( branchAddress))
            branches.addBranch(branchAddress,area);
        else
            throw new IllegalArgumentException("branch "+branchAddress+" already exists");
    }

    public boolean checkBranchExists(String branchAddress) throws Exception {
        return branches.checkIfBranchExists(branchAddress);
    }

    public void removeBranch(String branchName) throws Exception {
        branches.removeBranch(branchName);
    }
}
