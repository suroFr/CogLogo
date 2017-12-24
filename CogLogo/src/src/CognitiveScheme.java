//    CogLogo : an implementation of the Cogniton architecture
//    Copyright (C) 2017  SURO François (suro@lirmm.fr)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.


package src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.nlogo.api.Agent;

public class CognitiveScheme {
	public String Name = "turtles";
	public long modified = System.currentTimeMillis();;
	public ArrayList<Cogniton> cognitons = new ArrayList<>();
	public ArrayList<String> cognitonNames = new ArrayList<>();

	public HashMap<String, Color> planColors = new HashMap<>();	
	
	public ArrayList<String> groupNames = new ArrayList<String>();
	public ArrayList<GroupDescription> groupDescriptions = new ArrayList<GroupDescription>();
	public double groupIdCounter = 0;
	public HashMap<Double, GroupInstance> groupInstances = new HashMap<>();

	public double DecisionMakerBias = 1;
	public String myDecisionMaker = DecisionMakers[0];
	public static final String[] DecisionMakers  = new String[] {"MaximumWeight", "WeightedStochastic", "BiasedWeightedStochastic"};

	public ArrayList<String> linkedBreeds = new ArrayList<>();
	
	
	public CognitiveScheme() {	}
	public CognitiveScheme(String name) {Name = name;}
	
	public Cogniton addCogniton(String n, boolean b)
	{
		Cogniton cog = new Cogniton(n);
		cog.starting = b;
		cognitons.add(cog);
		cognitonNames.add(n);
		modified = System.currentTimeMillis();
		return cog;		
	}

	public Cogniton addCogniton(String n, boolean b, double val) {
		Cogniton c = addCogniton(n,b);
		c.startingValue = val;
		return c;
	}

	public void addInfluenceLink(String nCog, double value, String nPlan)
	{
		cognitons.get(cognitonNames.indexOf(nCog)).influencelinks.put(nPlan, value);
		modified = System.currentTimeMillis();
	}
	
	public void removeInfluenceLink(String nCog, String nPlan)
	{
		cognitons.get(cognitonNames.indexOf(nCog)).influencelinks.remove(nPlan);
		modified = System.currentTimeMillis();
	}

	public void addDependencyLink(String nCog, String nPlan)
	{
		if(!cognitons.get(cognitonNames.indexOf(nCog)).dependencyLinks.contains(nPlan))
			cognitons.get(cognitonNames.indexOf(nCog)).dependencyLinks.add(nPlan);	
		modified = System.currentTimeMillis();
	}

	public void removeDependencyLink(String nCog, String nPlan)
	{
		cognitons.get(cognitonNames.indexOf(nCog)).dependencyLinks.remove(nPlan);	
		modified = System.currentTimeMillis();
	}

	public void addReinforcementLink(String nCog, double value, String nPlan) {
		cognitons.get(cognitonNames.indexOf(nCog)).feedBackLinks.put(nPlan, value);
		modified = System.currentTimeMillis();
	}
	
	public void removeReinforcementLink(String nCog, String nPlan)
	{
		cognitons.get(cognitonNames.indexOf(nCog)).feedBackLinks.remove(nPlan);
		modified = System.currentTimeMillis();
	}
	
	public void renameCogniton(Cogniton cogniton, String text) {
		if(cognitons.get(cognitonNames.indexOf(text)) == null && cognitons.get(cognitonNames.indexOf(cogniton.Name)) != null)
		{
			cognitonNames.remove(cogniton.Name);
			cognitons.remove(cogniton);
			cogniton.Name = text;
			cognitonNames.add(text);
			cognitons.add(cogniton);
			modified = System.currentTimeMillis();;
		}
	}
	public void removeAllInfluenceLink(String nCog) {
		cognitons.get(cognitonNames.indexOf(nCog)).influencelinks.clear();
	}	
	
	public void removeAllDependencyLink(String nCog) {
		cognitons.get(cognitonNames.indexOf(nCog)).dependencyLinks.clear();
	}

	public void removeAllReinforcementLink(String nCog) {
		cognitons.get(cognitonNames.indexOf(nCog)).feedBackLinks.clear();
	}

	public void removeCogniton(Cogniton cogniton) {
		cognitons.remove(cogniton);
		cognitonNames.remove(cogniton.Name);
		modified = System.currentTimeMillis();;
	}	
	
	public int getCognitonVariableOffset(String br, String cog)
	{
		return CognitonExtension.getBreedVariableOffset(br) + cognitonNames.indexOf(cog);
	}
	//// GROUPS ROLES

	public boolean addGroup(String grp)
	{
		if(!groupNames.contains(grp))
		{
			groupNames.add(grp);
			groupDescriptions.add(new GroupDescription(grp));
			return true;
		}
		return false;
	}	
	
	public void removeGroup(String grp)
	{
		if(groupNames.contains(grp))
		{
			groupDescriptions.remove(groupNames.indexOf(grp));
			groupNames.remove(grp);
		}
	}
	
	public boolean addRole(String grp, String rl)
	{
		if(groupNames.contains(grp))
			return groupDescriptions.get(groupNames.indexOf(grp)).addRole(rl);
		return false;
	}		
	
	public void removeRole(String grp, String rl)
	{
		if(groupNames.contains(grp))
			groupDescriptions.get(groupNames.indexOf(grp)).removeRole(rl);
	}
	
	public Cogniton addCulturon(String grp, String rl,String cult)
	{
		if(groupNames.contains(grp))
			return groupDescriptions.get(groupNames.indexOf(grp)).addCulturon(rl,cult);
		return null;				
	}
	
	public Cogniton addCulturon(String grp, String rl,String cult, double d) {
		Cogniton ret = addCulturon(grp, rl, cult);
		if(ret != null)
			ret.startingValue = d;
		return ret;
	}
	
	public void removeCulturon(String grp, String rl,String n)
	{
		if(groupNames.contains(grp))
			groupDescriptions.get(groupNames.indexOf(grp)).removeCulturon(rl,n);
	}
	
	public boolean checkGroupRoleTypeExists(String group, String role)
	{
		if(groupNames.contains(group) && groupDescriptions.get(groupNames.indexOf(group)).checkRoleExist(role))
			return true;
		CognitonExtension.myConsole.println("WARNING : the group < "+group+" > or role < " +role+ " > does not exist in the cognitive scheme : "+Name );
		return false;		
	}

	public boolean checkGroupTypeExists(String group)
	{
		if(groupNames.contains(group))
			return true;
		CognitonExtension.myConsole.println("WARNING : the group < "+group+" > does not exist in the cognitive scheme : "+Name );
		return false;		
	}
	
	public ArrayList<Cogniton> getCulturonsForGroupRole(String g, String r) {
		
		if(groupNames.contains(g))
			return groupDescriptions.get(groupNames.indexOf(g)).getCulturonsForRole(r);
		else
			return null;
	}
	
	public ArrayList<String> getRolesFromGroup(String g) {
		return groupDescriptions.get(groupNames.indexOf(g)).getRoleNames();
	}
	
	public Cogniton getCulturon(String grp, String rol, String nam)
	{
		if(groupNames.contains(grp))
			return groupDescriptions.get(groupNames.indexOf(grp)).getCulturon(rol, nam);
		else
			return null;
	}

	public void addCulturonInfluenceLink(String grp, String rol, String nam, double value, String nPlan) {
		getCulturon( grp, rol, nam).influencelinks.put(nPlan, value);
		modified = System.currentTimeMillis();
	}
	public void addCulturonDependencyLink(String grp, String rol, String nam, String nPlan) {
		getCulturon( grp, rol, nam).dependencyLinks.add(nPlan);	
		modified = System.currentTimeMillis();
	}
	
	public void addCulturonReinforcementLink(String grp, String rol, String nam, double value, String nPlan) {
		getCulturon( grp, rol, nam).feedBackLinks.put(nPlan, value);
		modified = System.currentTimeMillis();
	}
	
	public void removeAllCulturonInfluenceLink(String grp, String rol,String nam) {
		getCulturon( grp, rol, nam).influencelinks.clear();
		modified = System.currentTimeMillis();
	}	
	
	public void removeAllCulturonDependencyLink(String grp, String rol,String nam) {
		getCulturon( grp, rol, nam).dependencyLinks.clear();
		modified = System.currentTimeMillis();
	}
	
	public void removeAllCulturonReinforcementLink(String grp, String rol,String nam) {
		getCulturon( grp, rol, nam).feedBackLinks.clear();
		modified = System.currentTimeMillis();
	}	
	
	public void renameCulturon(String grp, String rol, Cogniton cog, String cname) {
		
		if(groupNames.contains(grp))
			groupDescriptions.get(groupNames.indexOf(grp)).renameCulturon(rol,cog,cname);
	}

	public double generateGroupId()
	{
		double ret = groupIdCounter;
		groupIdCounter+=1.0;
		return ret;
	}

	public int getCulturonOffset(String br)
	{
		return CognitonExtension.getBreedVariableOffset(br) + cognitonNames.size();
	}

	public int getGroupVariableOffset(String br,String grp) {
		return getCulturonOffset(br) + (groupNames.indexOf(grp)*3);
	}

	public double createGroupInstance(String gr) {
		if(groupNames.contains(gr))
		{
			double id = generateGroupId();
			groupInstances.put(id, new GroupInstance(id,gr));
			return id;
		}
		return -1.0;
	}
	
	public double createAndJoinGroupInstance(String gr, String rl , Agent idA) {
		if(groupNames.contains(gr))
		{
			double id = generateGroupId();
			groupInstances.put(id, new GroupInstance(id,gr,rl,idA));
			return id;			
		}
		return -1.0;
	}

	public Double getCulturonValueForGroupInstance(Double groupId, String cogName) {
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			return g.getCulturonValue(cogName);
		return 0.0;
	}
	
	public void setCulturonValueForGroupInstance(Double groupId, String cogName, double value) {
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			g.setCulturonValue(cogName,value);	
	}
	
	public void addToCulturonValueForGroupInstance(Double groupId, String cogName, double value) {
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			g.addToCulturonValue(cogName, value);
	}
	
	public void feedBackToGroupInstance(Double groupId, String planName, double value) {
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			g.feedBackFromPlan(planName,value);
	}
	
	public boolean joinGroup(Double groupId, String role, Agent agentId)
	{
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			return g.joinGroup(role,agentId);
		return false;
	}

	public void leaveGroup(Double groupId, Agent agentId)
	{
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			if(g.leaveGroup(agentId))
					groupInstances.remove(groupId);
	}
	
	public boolean amIInGroup(Double groupId, Agent agentId)
	{
		GroupInstance g = groupInstances.get(groupId);
		if(g!=null)
			return g.isAgentInGroup(agentId);
		return false;
	}


	////CLASSES

	public class Cogniton{
		public String Name = "defaultCogniton";
		public String customTag = "";
		public HashMap<String, Double> influencelinks = new HashMap<>();
		public HashMap<String, Double> feedBackLinks = new HashMap<>();
		public ArrayList<String> dependencyLinks = new ArrayList<>();
		public Color customColor = Color.GRAY;
		public boolean starting = false;
		public boolean isCulturon = false;
		public double startingValue = 0.0;
		
		public Cogniton(String name)
		{
			Name = name;
		}

		public Color getDisplayColor() {
			return customColor;
		}

		public void setDisplayColor(Color c) {
			customColor = c;
		}

		public double getFeedBackCoefficientForPlan(String string) {
			if(feedBackLinks.get(string)!=null)
				return feedBackLinks.get(string);
			else
				return 0.0;
		}
	}
	
	public class GroupDescription{
		public ArrayList<ArrayList<Cogniton>> culturons = new ArrayList<ArrayList<Cogniton>>();
		public ArrayList<ArrayList<String>> culturonNames = new ArrayList<ArrayList<String>>();
		public ArrayList<String> roleNames = new ArrayList<String>();
		String groupName;
		
		public GroupDescription(String n)
		{
			groupName = n;
		}

		public void renameCulturon(String rol, Cogniton cog, String cname) {
			
			if(getCulturon(rol, cname) == null && getCulturon(rol, cog.Name) != null)
			{
				culturons.get(roleNames.indexOf(rol)).remove(cog);
				culturonNames.get(roleNames.indexOf(rol)).remove(cog.Name);
				cog.Name = cname;
				culturons.get(roleNames.indexOf(rol)).add(cog);
				culturonNames.get(roleNames.indexOf(rol)).add(cog.Name);
				modified = System.currentTimeMillis();;
			}
		}

		public Cogniton getCulturon(String r, String nam) {
			if(roleNames.contains(r) && culturonNames.get(roleNames.indexOf(r)).contains(nam))
				return culturons.get(roleNames.indexOf(r)).get(culturonNames.get(roleNames.indexOf(r)).indexOf(nam));
			return null;
		}

		public Cogniton getCulturon(String nam) {
			for(int i = 0 ; i < culturons.size(); i++)
				for(int j = 0 ; j < culturons.get(i).size(); j++)
					if(culturons.get(i).get(j).Name.equals(nam))
						return culturons.get(i).get(j);			
			return null;
		}

		public ArrayList<String> getRoleNames() {
			return roleNames;
		}

		public ArrayList<Cogniton> getCulturonsForRole(String r) {
			if(roleNames.contains(r))
				return culturons.get(roleNames.indexOf(r));
			return null;
		}

		public boolean checkRoleExist(String role) {
			return roleNames.contains(role);
		}

		public void removeCulturon(String rl, String n) {
			if(roleNames.contains(rl))
			{
				if(culturonNames.get(roleNames.indexOf(rl)).contains(n))
				{
					culturons.get(roleNames.indexOf(rl)).remove(culturonNames.get(roleNames.indexOf(rl)).indexOf(n));
					culturonNames.get(roleNames.indexOf(rl)).remove(n);					
					modified = System.currentTimeMillis();
				}	
			}
		}

		public Cogniton addCulturon(String rl, String cult) {
			if(roleNames.contains(rl))
			{
				if(!culturonNames.get(roleNames.indexOf(rl)).contains(cult))
				{
					Cogniton cog = getCulturon(cult);
					if(cog == null)
					{
						cog = new Cogniton(cult);
						cog.isCulturon = true;
						cog.customTag = groupName + ">" + rl;
					}
					else
						cog.customTag = groupName;	
					
					culturonNames.get(roleNames.indexOf(rl)).add(cult);
					culturons.get(roleNames.indexOf(rl)).add(cog);
					modified = System.currentTimeMillis();
					return cog;
				}				
			}
			return null;
		}

		public void removeRole(String rl) {
			if(roleNames.contains(rl))
			{
				culturons.remove(roleNames.indexOf(rl));
				culturonNames.remove(roleNames.indexOf(rl));
				roleNames.remove(rl);				
			}
		}

		public boolean addRole(String rl) {

			if(!roleNames.contains(rl))
			{
				roleNames.add(rl);
				culturons.add(new ArrayList<>());
				culturonNames.add(new ArrayList<>());
				return true;
			}
			return false;
		}

		public String toFileString() {
			String ret = "";
			ret += "GRP\n" + groupName + "\n";
			for (int r = 0; r < roleNames.size(); r++) {
				ret += "ROL\n" + roleNames.get(r) + "\n";
				for (Cogniton c : culturons.get(r)) {
					ret +="COG\n" + c.Name + "\n" + Integer.toString(c.customColor.getRGB()) + "\n"+ Double.toString(c.startingValue) + "\n";
					ret += "INFLUENCE\n";
					for (String inf : c.influencelinks.keySet())
						ret += inf + "=" + c.influencelinks.get(inf).toString() + "\n";
					ret +="DEPENDENCY\n";
					for (String dep : c.dependencyLinks)
						ret += dep + "\n";
				}
			}
			return ret;
		}

		public boolean renameRole(String rl, String newRl) {
			if(roleNames.contains(rl))
			{
				ArrayList<Cogniton> cult = culturons.get(roleNames.indexOf(rl));
				ArrayList<String> cultName = culturonNames.get(roleNames.indexOf(rl));
				roleNames.remove(rl);
				culturons.remove(cult);
				culturonNames.remove(cultName);
				roleNames.add(newRl);
				culturons.add(cult);
				culturonNames.add(cultName);
				for(Cogniton c: cult)
					c.customTag = groupName + ">" + newRl;
				return true;
			}
			return false;
		}		
	}
	
	public class GroupInstance{
		double id;
		String type;
		HashMap<String, Double> cultValues = new HashMap<>();
		public ArrayList<Agent> agents = new ArrayList<>();
		GroupDescription gd;
		public GroupInstance(double idin, String t)
		{
			id = idin;
			type = t;
			gd = groupDescriptions.get(groupNames.indexOf(t));
			for(String r : gd.getRoleNames())
				for(Cogniton c : gd.getCulturonsForRole(r))
					cultValues.put(c.Name, c.startingValue);
			
		}

		public synchronized void feedBackFromPlan(String planName, double value) {
			Double val;
			for(String cname : cultValues.keySet())
			{
				val  = cultValues.get(cname);
				cultValues.put(cname, val + (value * gd.getCulturon(cname).getFeedBackCoefficientForPlan(planName)));
			}
		}

		public GroupInstance(double idin,String type, String role, Agent agentId)
		{
			this(idin,type);
			joinGroup(role, agentId);
		}
		
		public synchronized void addToCulturonValue(String cogName, double value) {
			Double val = cultValues.get(cogName);
			if(val != null)
				cultValues.put(cogName,val + value);			
		}

		public synchronized void setCulturonValue(String cogName, double value) {
			if(cultValues.get(cogName) != null)
				cultValues.put(cogName, value);
		}

		
		public boolean leaveGroup(Agent agentId) {
			agents.remove(agentId);
			if(agents.size() == 0)
				return true;
			return false;
		}

		public boolean joinGroup(String role, Agent agentId) {
			agents.add(agentId);
			return true;
		}

		public Double getCulturonValue(String cogName) {
			if(cultValues.get(cogName)!=null)
				return cultValues.get(cogName);
			return 0.0;
		}	
		
		public boolean isAgentInGroup(Agent id)
		{
			return agents.contains(id);
		}
	}

	public boolean renameRole(String gr, String rl, String newRl) {
		if(groupNames.contains(gr))
			return groupDescriptions.get(groupNames.indexOf(gr)).renameRole(rl,newRl);
		return false;
	}
	public boolean renameGroup(String gr, String newName) {
		if(groupNames.contains(gr))
		{
			GroupDescription gd = groupDescriptions.get(groupNames.indexOf(gr));
			groupNames.remove(gr);
			groupDescriptions.remove(gd);
			gd.groupName = newName;
			groupNames.add(newName);
			groupDescriptions.add(gd);
			return true;
		}
		return false;
	}

}
