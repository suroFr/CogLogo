package src.myutils;

import java.util.Collection;

import org.nlogo.agent.Turtle;
import org.nlogo.api.AgentException;
import org.nlogo.api.LogoException;

import src.CognitiveScheme;
import src.CognitonExtension;

public class GroupInstanceScrubber {

	
	public static double checkAndUpdateAgentGroupVariables(double groupId, Turtle t, CognitiveScheme cs, String breedName, String grpName)
	{
		if(groupId != -1 && !cs.amIInGroup(groupId, t))
		{
			try {
				t.setVariable(cs.getGroupVariableOffset(breedName,grpName), -1.0);
				t.setVariable(cs.getGroupVariableOffset(breedName,grpName)+1, "_NULL_");
				t.setVariable(cs.getGroupVariableOffset(breedName,grpName)+2, 0.0);	
			} catch (LogoException | AgentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		return groupId;
	}
	
	public static double checkAndUpdateAgentGroupVariables(double groupId, Turtle t, CognitiveScheme cs, String breedName, int grpIndex) {
		if(groupId != -1 && !cs.amIInGroup(groupId, t))
		{
			try {
				t.setVariable(cs.getCulturonOffset(breedName)+(grpIndex*3), -1.0);
				t.setVariable(cs.getCulturonOffset(breedName)+(grpIndex*3)+1, "_NULL_");
				t.setVariable(cs.getCulturonOffset(breedName)+(grpIndex*3)+2, 0.0);	
			} catch (LogoException | AgentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		return groupId;
	}

	public static void scrub(Collection<CognitiveScheme> values) {
		if(values != null)
			for(CognitiveScheme cs : values)
			{
				Double [] GIDS = new Double[cs.groupInstances.keySet().size()];
				GIDS = cs.groupInstances.keySet().toArray(GIDS);
				for(Double giId : GIDS)
				{
					for(int i = 0 ; i < cs.groupInstances.get(giId).agents.size(); i++)
					{
						if(!CognitonExtension.modelContext.world().turtles().contains(cs.groupInstances.get(giId).agents.get(i)))
						{
							//CognitonExtension.myConsole.println("agent removed");
							cs.groupInstances.get(giId).agents.remove(i);
							i--;
						}
					}
					if(cs.groupInstances.get(giId).agents.size() == 0)
					{
						cs.groupInstances.remove(giId);
						//CognitonExtension.myConsole.println("group removed");
					}
				}
			}
	}

	
}
