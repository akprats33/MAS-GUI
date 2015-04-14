package mas.localSchedulingproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.database.OperationItemId;
import mas.machineproxy.gui.MachineGUI;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 *
 * this picks a job from the queue and sends it to the machine for processing
 */

public class EnqueueBatchPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private ArrayList<Batch> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	private Batch comingBatch;
	private OperationDataBase operationdb;
	private MachineGUI gui;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance pInstance) {

		bfBase = pInstance.getBeliefBase();
		ACLMessage msg = ((MessageGoal)pInstance.getGoal()).
				getMessage();

		try {
			comingBatch = (Batch) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		this.operationdb = (OperationDataBase) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.operationDatabase).
				getValue();
		
		gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();

		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		//		log.info(comingJob.getBidWinnerLSA());
		if(comingBatch.getWinnerLSA().equals(myAgent.getAID())) {
			log.info("Adding the batch" + comingBatch.getBatchId()+  " to queue of agent, " +
					myAgent.getLocalName());

			OperationItemId id = new OperationItemId(comingBatch.getCurrentOperationType(), comingBatch.getCustomerId());
			
			comingBatch.setCurrentOperationProcessingTime(operationdb.getOperationInfo(id).getProcessingTime());
		
			jobQueue.add(comingBatch);
			/**
			 * update the belief base
			 */
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue, jobQueue);	

			if(gui != null) {
				gui.addBatchToQueue(comingBatch);
			}
		}
	}
}
