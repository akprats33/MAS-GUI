package mas.globalSchedulingproxy.agent;

import jade.core.AID;
import javax.swing.SwingUtilities;
import mas.globalSchedulingproxy.goal.GSASendNegotitationGoal;
import mas.globalSchedulingproxy.goal.QueryJobGoal;
import mas.globalSchedulingproxy.gui.GSAproxyGUI;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;

public class GlobalSchedulingAgent extends AbstractGlobalSchedulingAgent{

	private static final long serialVersionUID = 1L;
	public static GSAproxyGUI mygui;
	private Logger log;
	private BeliefBase bfBase;

	public static void addCompletedJob(job j) {
		if(mygui != null) {
			mygui.addCompletedJob(j);
		}
	}

	@Override
	protected void init() {
		super.init();		

		log = LogManager.getLogger();

		Capability bCap=  new BasicCapability();
		addCapability(bCap);

		AID bba = AgentUtil.findBlackboardAgent(this);
		
		this.bfBase = bCap.getBeliefBase();
		bfBase.updateBelief( ID.GlobalScheduler.BeliefBaseConst.blackboardAgent, bba);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mygui = new GSAproxyGUI(GlobalSchedulingAgent.this);
			}
		});

	}

	public void negotiateJob(job myJob) {
		log.info("GSA - Sending negotitaion job : " + myJob);
		bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.Current_Negotiation_Job, myJob);
		
		addGoal(new GSASendNegotitationGoal());
	}

	public static void showQueryResponse(JobQueryObject response) {
		
	}

	public void queryJob(job job) {
		bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.GSAqueryJob, job);
		
		addGoal(new QueryJobGoal());
	}
}
