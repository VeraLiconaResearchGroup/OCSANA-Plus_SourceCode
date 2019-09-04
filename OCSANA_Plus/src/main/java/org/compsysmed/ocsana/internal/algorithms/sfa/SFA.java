package org.compsysmed.ocsana.internal.algorithms.sfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.TreeMap;

//import static org.junit.Assert.assertNotNull;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import org.compsysmed.ocsana.internal.algorithms.AbstractOCSANAAlgorithm;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.work.Tunable;
import org.ejml.alg.dense.mult.MatrixVectorMult;
import org.ejml.data.MatrixIterator;
import org.ejml.simple.SimpleMatrix;

import org.la4j.Matrix;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;

import org.cytoscape.model.CyRow;
public class SFA 
	extends AbstractSFAAlgorithm {
	private static final String NAME = "Singal Flow Analysis";
	private static final String SHORTNAME = "SFA";
  
    public SFA(CyNetwork network) {
        super(network);
    }
	
	public Map<CyNode, Double> computesfa(Set<CyNode> sources, Set<CyNode> targets) {
		// 1. process data
		List<CyNode> nodes = network.getNodeList();
		int totalnodecount = network.getNodeList().size();
        SimpleMatrix A = new SimpleMatrix(totalnodecount, totalnodecount);
        Map<CyNode,Integer> n2i =new HashMap<CyNode,Integer>();
        int k=0;
        for (CyNode node:nodes) {
        	n2i.put(node, k);
        	k++;
        }
        List<CyEdge> edges = network.getEdgeList();
        CyTable edgeTable=network.getDefaultEdgeTable();
        // 2. create adjacency matrix with signs
       
		
		for (CyEdge edge:edges) {
			CyRow edgeRow=edgeTable.getRow(edge.getSUID());
			CyColumn signColumn = edgeTable.getColumn("interaction");
			String interaction=edgeRow.get(signColumn.getName(),signColumn.getType()).toString();
			if (interaction.equals("activates")) {
				CyNode source = edge.getSource();
				CyNode target = edge.getTarget();
				Integer source_index = n2i.get(source);
				Integer target_index = n2i.get(target);
				A.set(target_index,source_index, 1);
			}
			if (interaction.equals("inhibits")) {
				CyNode source = edge.getSource();
				CyNode target = edge.getTarget();
				Integer source_index = n2i.get(source);
				Integer target_index = n2i.get(target);
				A.set(target_index,source_index, -1);
			}	
		}
		// 3. initalize stuff (prepare exact solution)
        double alpha = 0.5;
        int lim_iter = 1000;
        boolean apply_weight_norm = true;
        
        
        
        SimpleMatrix W =new SimpleMatrix(A);
        //apply weight matrix normalization
        Double[] Dc=new Double[totalnodecount];
        for (int i=0;i<totalnodecount;i++) {
        	SimpleMatrix col = A.extractVector(false, i);
        	MatrixIterator coliter = col.iterator(false, 0, 0, col.numRows()-1, col.numCols()-1);
        	double totedge = 0;
        	double sqrt = 0;
        	while(coliter.hasNext()){
        		double edgeval = Math.abs(coliter.next());     		
        		totedge=(totedge+edgeval);		
        	}
        	if (totedge==0) {
    			totedge=1;	
    		}  
        	sqrt = 1/Math.sqrt(totedge);
			Dc[i]=sqrt;
			 	
        }
       
        
        Double[] dDr=new Double[totalnodecount];
        SimpleMatrix Dr = new SimpleMatrix(totalnodecount, 1);
        for (int i=0;i<totalnodecount;i++) {
        	SimpleMatrix row = A.extractVector(true, i);
        	MatrixIterator rowiter = row.iterator(true, 0, 0, row.numRows()-1, row.numCols()-1);
        	double totedge = 0;
        	double sqrt = 0;
        	while(rowiter.hasNext()){
        		double edgeval = Math.abs(rowiter.next());      		
        		totedge=totedge+edgeval;      		
        	}
        	if (totedge==0) {
    			totedge=1;
    		}
        	sqrt = 1/Math.sqrt(totedge);
        	//Dr[i][0]=sqrt;   
        	Dr.set(i, sqrt);
        	dDr[i]=sqrt;
        }
       
       //weight matrix normalization
       
        double[] dr = Arrays.stream(dDr).mapToDouble(x -> x == null ? 0.0 : x.doubleValue()).toArray();
        double[] dc = Arrays.stream(Dc).mapToDouble(x -> x == null ? 0.0 : x.doubleValue()).toArray();
        SimpleMatrix drdag =SimpleMatrix.diag(dr);
        SimpleMatrix dcdag=SimpleMatrix.diag(dc);
        
        W = W.mult(dcdag);
        //W = np.multiply(W, np.mat(Dr).T)
        W=drdag.mult(W);
        
        
        
        
        SimpleMatrix alphaW = W.scale(alpha);
        
        //(1-alpha)
        
        SimpleMatrix M0=SimpleMatrix.identity(W.numRows()).minus(alphaW);
        
        //SimpleMatrix M = InvertMatrix.invert(M0, true).mul(1-alpha);
        SimpleMatrix M= M0.invert().scale(1-alpha);
		// 4. create basal activity matrix
        int n = A.numRows();
        SimpleMatrix b=new SimpleMatrix(n,1);
		// 5. set basal activitiesNOT WORKING
        for (CyNode source:sources) {
        	b.set(nodes.indexOf(source), 1);
        }
        for (CyNode target:targets) {
        	b.set(nodes.indexOf(target), -1);
        }
		// 6. propogate exact
        // 7. if self weight matrix invalidated -prepare exact
     	// 8. return M weight matrix;
        
        SimpleMatrix Mdot = M.mult(b);
		
        Map<CyNode,Double> finalmap =new HashMap<CyNode,Double>();
    	MatrixIterator xiter = Mdot.iterator(true, 0, 0, Mdot.numRows()-1, Mdot.numCols()-1);
    	while( xiter.hasNext()){
    		Double valuem = xiter.next();
    		int valueindex = xiter.getIndex();
    		for(Entry<CyNode, Integer> entry : n2i.entrySet()) {
    			 if (entry.getValue().equals(valueindex)){
    				 finalmap.put(entry.getKey(), valuem);
    				 
    			 }
    		 }
        		
        }
	return finalmap;
	}
 

	@Override
	public String fullName() {
		return NAME;
	}

	@Override
	public String shortName() {
		return SHORTNAME;
	}

	@Override
	public String description() {
        StringBuilder result = new StringBuilder(fullName());

        result.append(" (");


        result.append(")");
        return result.toString();
   }
	@Override
    public void cancel () {
        super.cancel();
        
    }


}