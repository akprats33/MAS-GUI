package mas.globalSchedulingproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.globalSchedulingproxy.gui.GSANegotiateProxyGUI;
import mas.jobproxy.Batch;
import mas.jobproxy.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class NegotiateViaGuiPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private Batch JobUnderNegotiation;
	private Logger log = LogManager.getLogger();
	private String replyWith = null;
	
	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		
		try {
			this.JobUnderNegotiation = (Batch)((MessageGoal)(PI.getGoal())).
					getMessage().getContentObject();
			
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		
		log.info("Negotiation job for GSA : " + JobUnderNegotiation);
		replyWith = ((MessageGoal)PI.getGoal()).getMessage().getReplyWith();
	}

	@Override
	public void action() {
		if(this.JobUnderNegotiation != null) {
			GSANegotiateProxyGUI negotiation = new GSANegotiateProxyGUI(
					(GlobalSchedulingAgent)myAgent, this.JobUnderNegotiation);
			GlobalSchedulingAgent.weblafgui.addNegotiationBid(this.JobUnderNegotiation);
			
		}
	}
}
