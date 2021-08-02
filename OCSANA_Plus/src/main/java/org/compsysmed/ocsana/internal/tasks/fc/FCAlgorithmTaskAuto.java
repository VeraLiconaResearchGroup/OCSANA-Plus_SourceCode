/**
 * Task to run path-finding algorithm in OCSANA
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks.fc;

import java.net.URISyntaxException;
// Java imports
import java.util.*;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
// Cytoscape imports
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.util.BoundedInteger;
import org.lappsgrid.pycaller.PyCallerException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;


public class FCAlgorithmTaskAuto extends AbstractTask implements ObservableTask  {
	public FCAlgorithmTaskAuto() {
	    	super();
    	}
    @ProvidesTitle
	public String getTitle() { return "computes FC"; }
    
	@Tunable(description="Network to compute FC on", required=true)
	  public CyNetwork network;
	@Tunable(description="node name column", required=true)
	  public String column;



    
    private String FC_string;

    
    @Override
    public void run (TaskMonitor arg0) throws Exception {
    	NodeHandler nodehandler=new NodeHandler(network);
    	nodehandler.setNodeNameColumn(network.getDefaultNodeTable().getColumn(column));
        Map<String, List<CyNode>> FC = FVS(network);
        List<CyNode> sourcenodes = FC.get("sourcenodes");

        FC_string = "Source Nodes: ";
        for (CyNode node:sourcenodes) {
     	   String nodename=nodehandler.getNodeName(node);
     	   FC_string=FC_string+nodename+"\t";
        }

        int numberFVS=FC.size();
        List<CyNode> FVS;
		if (numberFVS<3) {
        	FVS=FC.get("FVS_1");
        	if ((FVS).size()==0) {
        		FC_string=FC_string+"\nno FVSes identified";	
        	}else {
        		FC_string = FC_string+"\nFVS_1: ";
        		for (CyNode node:FVS) {
	            	String nodename = nodehandler.getNodeName(node);
	            	FC_string=FC_string+nodename+"\t";
	            }	
        	}
        	
        } else {
	        for (int i = 1;i<numberFVS;i++) {
	        	FC_string = FC_string+"\nFVS_"+String.valueOf(i)+": ";
	        	FVS=FC.get("FVS_"+String.valueOf(i));
	        	for (CyNode node:FVS) {
	            	String nodename = nodehandler.getNodeName(node);
	            	FC_string=FC_string+nodename+"\t";
	            }
	        }
        }
       
    }

	 private Map<String, List<CyNode>> FVS(CyNetwork network) {
		 Map<String,List<CyNode>> FC =new HashMap<String,List<CyNode>>();
		 
	 
			List<CyNode> fvs=new ArrayList<CyNode>();
			List<CyNode> networknodes = network.getNodeList();

			int maxiter;

			maxiter=50;

			
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		if (type.equals(String.class)) {
			return (R) (FC_string.toString());
		} 
		else {
			return null;
		}
	}


}
