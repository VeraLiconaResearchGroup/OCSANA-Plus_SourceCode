package org.compsysmed.ocsana.internal.algorithms.fc;



import java.io.File;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedInteger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;


import org.compsysmed.ocsana.internal.algorithms.fc.AbstractFCAlgorithm;

public class FC extends AbstractFCAlgorithm {
	private static final String NAME = "FC algorithm";
    private static final String SHORTNAME = "FC";
	private NodeHandler nodeHandler;
	
	 @Tunable(description = "Bound FVS discovery",
	            gravity = 350,
	            tooltip="(Unbounded search may take a very long time!")
	   public Boolean useMaxCardinality = true;
	    @Tunable(description = "Maximum number FVSes",
	            gravity = 351,
	            dependsOn = "useMaxCardinality=true")
	   public BoundedInteger maxCardinalityBInt;
    public FC (CyNetwork network) {
    	super(network);
    	maxCardinalityBInt = new BoundedInteger(1, 15, 50, false, false);
    }
   

	public Map<String, List<CyNode>> FVS () {
		Map<String,List<CyNode>> FC =new HashMap<String,List<CyNode>>();
		List<CyNode> fvs=new ArrayList<CyNode>();
		List<CyNode> networknodes = network.getNodeList();
		int maxiter;
		
		if (useMaxCardinality) {
			
			maxiter=maxCardinalityBInt.getValue();
		}
		else {
			maxiter=50;
		}
		
		int looper=0;
		while (looper<maxiter){
			fvs=computeFCs(looper);
			List<CyNode>final_FVS =new ArrayList<CyNode>();
			for (CyNode netnode:networknodes) {
				if (!fvs.contains(netnode)) {
					final_FVS.add(netnode);
				}
			}
			int old = 0;
			int j = 0;
			while (j<FC.size()) {
				List<CyNode> current_set = FC.get("FVS_"+String.valueOf(j+1));
				if (current_set.equals(final_FVS)){
					old++;
				}
				j++;
			}
			if (old==0) {
			FC.put("FVS_"+String.valueOf(FC.size()+1), final_FVS);	
			}
			if (FC.size()==0) {
				FC.put("FVS_1", final_FVS);			
			}
			looper++;
		}
		
			
		
			/*int j = 0;
			if (FC.size()>0) {
				for (j=0;j<FC.size();j++) {
					List<CyNode> currentFVS = FC.get("FVS_"+String.valueOf(j+1));
					int number_true = 0;
					if (currentFVS.equals(final_FVS)){
						number_true+=1;
					}
					//if (number_true==0) {
						FC.put("FVS_"+String.valueOf(FC.size()+1), final_FVS);
					//}
				}
			}
			else {
				FC.put("FVS_1", final_FVS);
			}*/
			
		
		
		//FC.put("FVS_"+String.valueOf(FC.size()+1), final_FVS);
		List<CyNode> sourcenodes = findsourcenodes();
		FC.put("sourcenodes",sourcenodes);
		return FC;
	}
	 public List<CyNode> findsourcenodes(){
		List<CyNode> sourcenodes= new ArrayList<CyNode>();
	 	List<CyNode> networknodes = network.getNodeList();
	 	for (CyNode node:networknodes) {
	 		if (network.getNeighborList(node, Type.INCOMING).size()==0) {
	 			sourcenodes.add(node);
	 		}
	 	}
		return sourcenodes; 
	 }
	 public ArrayList<CyNode> computeFCs(int iter) {
			double T_0 = 0.6;
			double T=T_0;
			double alpha = 0.99;
			int maxFail = 50;
			int nbFail=0;
			List<CyNode> nodeList = network.getNodeList();
			List<CyEdge>edges=network.getEdgeList();

			int N=nodeList.size();
			int maxMvt=5*N;
			List<CyNode> S=new ArrayList<CyNode>();
			ArrayList<CyNode> S_optimal=new ArrayList<CyNode>();
			S.clear();
			S_optimal.clear();
			
			//determine parent and child nodes for each node
			ListMultimap<CyNode, CyNode> child = ArrayListMultimap.create();
			ListMultimap<CyNode, CyNode> parent = ArrayListMultimap.create();
			ArrayList<CyNode> self_loops = new ArrayList<CyNode>();
			ArrayList<CyNode> sink = new ArrayList<CyNode>();
			//ListIterator<CyEdge> listIterator = edges.listIterator(edges.size());
			for (CyEdge edge:edges) {
				
				CyNode source= edge.getSource();
				CyNode target=edge.getTarget();
				
				child.put(source, target);
				parent.put(target,source);
				if (edge.getSource()==edge.getTarget()) {
					self_loops.add(edge.getTarget());
				}
			}
			for (CyNode node:nodeList) {
				if (network.getNeighborList(node, Type.OUTGOING).size()==0) {
		 			sink.add(node);
		 		}
			}
			
			int nbMvt, position_type,candidate_index,N_unnumbered,N_conflict;
			int position;
			int delta_move;
			
			
			Random rando = new Random();
			
			rando.setSeed(iter);
			ArrayList<CyNode> unnumbered= new ArrayList<CyNode>();
			
			List<CyNode> S_trail_head =  new ArrayList<CyNode>();
			List<CyNode> S_trail_tail=  new ArrayList<CyNode>();
			List<CyNode> S_trail = new ArrayList<CyNode>();
			List<CyNode> candidate_incoming_neighbour= new ArrayList<CyNode>();
			List<CyNode> candidate_outgoing_neighbour= new ArrayList<CyNode>();
			List<CyNode> conflict =  new ArrayList<CyNode>();
			ArrayList<CyNode> CV_pos = new ArrayList<CyNode>();
			ArrayList<CyNode> CV_neg = new ArrayList<CyNode>();
			CyNode candidate = null;
					
			for (CyNode nunn :nodeList) {
				if (!self_loops.contains(nunn) | !sink.contains(nunn)){
					unnumbered.add(nunn);
				}
				
			}
			
			N_unnumbered = unnumbered.size();
			
			position=0;
			while (nbFail<maxFail){
				nbMvt =0;
				boolean failure = true;
				while (nbMvt<maxMvt) {					
					candidate_index = rando.nextInt(N_unnumbered);				
					candidate = unnumbered.get(candidate_index);
					position_type = rando.nextInt(2);
					candidate_incoming_neighbour = network.getNeighborList(candidate, CyEdge.Type.INCOMING);
					candidate_outgoing_neighbour = network.getNeighborList(candidate, CyEdge.Type.OUTGOING);
					
					if (position_type==1) {
						position = get_position_minus(candidate_incoming_neighbour,S);
						
					}
					else if (position_type==0) {
						position = get_position_plus(candidate_outgoing_neighbour,S);
						
					}

					
					S_trail=new ArrayList<CyNode> (S);
					if (!S_trail.contains(candidate)) {
						S_trail.add(position-1,candidate);
					}

					
					S_trail_head = S_trail.subList(0, position-1);
					S_trail_tail = S_trail.subList(position, S_trail.size());
					
					conflict.clear();
					if (position_type==1) {
						CV_pos=new ArrayList<CyNode>();
						
						for (CyNode nodetemp:S_trail_head){
							int tempsuid = nodetemp.getSUID().intValue();
							for(CyNode neighbornode:candidate_outgoing_neighbour){
								int neighborsuid = neighbornode.getSUID().intValue();
								if (tempsuid==neighborsuid){
				        		CV_pos.add(nodetemp);		
								}
							}
				        }
				        conflict=CV_pos;
			        
					}
					
					else if (position_type==0) {
						CV_neg=new ArrayList<CyNode>();		
						for (CyNode nodetemp:S_trail_tail){	
							int tempsuid = nodetemp.getSUID().intValue();
							for(CyNode neighbournode:candidate_incoming_neighbour){
								int neighborsuid = neighbournode.getSUID().intValue();
								if (tempsuid==neighborsuid){
									CV_neg.add(nodetemp);
								}
							}
						}
						conflict=CV_neg;					
					}
					
					
					N_conflict = conflict.size();
					
					if (N_conflict>0) {
						for (CyNode conflictn: conflict) {
								S_trail.remove(conflictn);						
								}
							}
				
					delta_move = N_conflict-1;
					
					float tfloat= (float) T;
					if (delta_move<=0 ||Math.exp(-(delta_move)/tfloat)>rando.nextFloat()){
						S=new ArrayList<CyNode>(S_trail);
						unnumbered.remove(candidate);
						if (N_conflict>0) {
							for (CyNode conflictl: conflict) {
								unnumbered.add(conflictl);	
							}						}
						N_unnumbered+=delta_move;
						nbMvt=nbMvt+1;
					
						if (S.size()>S_optimal.size()) {
							S_optimal.clear();
							S_optimal=new ArrayList<CyNode>(S);
							failure=false;
						}
						if (N_unnumbered==0) {
											
							
							return S_optimal;
							
						}
					}

				}
				
				if (failure==true) {
					nbFail+=1;					
				}
				else {
					nbFail=0;
					
				}
				T=(T*alpha);
			}
			
			return S_optimal;
			}
			
			




	public int get_position_plus(List<CyNode> candidate_outgoing_neighbour, List<CyNode> S) {
		int position=1+S.size();
		int i=0;
		for (CyNode node:S) {
			int suid = node.getSUID().intValue();
			for(CyNode neighbornode:candidate_outgoing_neighbour) {
				int neighborsuid = neighbornode.getSUID().intValue();
			if (neighborsuid==suid) {
				position=i+1;
				return position;
			}
			
			}
			
		}
		return position;
	}


	private int get_position_minus(List<CyNode> candidate_incoming_neighbour, List<CyNode> S) {
		int position =1;
		for (int i=S.size()-1;i>=0;i--) {
			CyNode x=S.get(i);
			if (candidate_incoming_neighbour.contains(x)){
				position=i+2;
				return position;
			}
		}
		
		return position;
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

    public String description () {
        return fullName();
    }



}
