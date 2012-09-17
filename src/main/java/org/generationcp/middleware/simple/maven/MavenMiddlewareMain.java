package org.generationcp.middleware.simple.maven;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Lot;


public class MavenMiddlewareMain{
    public static void main(String[] args) {
        //set db connection properties
        DatabaseConnectionParameters localDbParameters = 
            new DatabaseConnectionParameters("localhost", "3306", "ibdbv1_sample_local", "user", "password");
        
        DatabaseConnectionParameters centralDbParameters = 
            new DatabaseConnectionParameters("localhost", "3306", "ibdb_rice_20120405", "user", "password");
        
        //create the ManagerFactory
        ManagerFactory factory = new ManagerFactory(localDbParameters, centralDbParameters);
        
        //get the DataManager object you want to use
        GermplasmDataManager manager = factory.getGermplasmDataManager();
        
        //use the function needed from the manager
        try{
            System.out.println("Retrieving the pedigree tree of 50533...");
            GermplasmPedigreeTree tree = manager.generatePedigreeTree(Integer.valueOf(50533), 4);
            
            //print the result on the console
            if(tree != null){
                System.out.println("Printing the pedigree tree of 50533...");
                printNode(tree.getRoot(), 1);
            }
        }catch(QueryException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
        
        //call method for inserting data into the db
        InventoryDataManager inventoryManager = factory.getInventoryDataManager();
        
        try{
            //create object to store
            Lot lot = new Lot();
            lot.setComments("sample added lot");
            lot.setEntityId(new Integer(50533));
            lot.setEntityType("GERMPLSM");
            lot.setLocationId(new Integer(9001));
            lot.setScaleId(new Integer(1538));
            lot.setSource(null);
            lot.setStatus(new Integer(0));
            lot.setUserId(new Integer(1));

            //call method to store object in db
            int added = inventoryManager.addLot(lot);
            
            if(added == 1){
                System.out.println("Lot has been saved successfull.");
            }
        } catch(QueryException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
        
        factory.close();
    }
    
    private static void printNode(GermplasmPedigreeTreeNode node, int level) {
        StringBuffer tabs = new StringBuffer();

        for (int ctr = 1; ctr < level; ctr++) {
            tabs.append("\t");
        }

        String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        System.out.println(tabs.toString() + node.getGermplasm().getGid() + " : " + name);

        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
            printNode(parent, level + 1);
        }
    }
}
