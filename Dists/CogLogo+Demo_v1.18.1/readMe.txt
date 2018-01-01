

	CogLogo : an implementation of the Cogniton architecture
	Copyright (C) 2017  SURO François (suro@lirmm.fr)



	1. Setup
	2. The cogniton architecture
	3. Getting started
	4. Command list
	5. Decision Makers




	1 ============== Setup ============== 1

1. Copy the "coglogo" folder to : [NetLogo directory (ex : NetLogo 6.0.2)]/app/extensions/

2. Run "Wolf-Cow.nlogo" and take a look for yourself


	2 ==== The cogniton architecture ==== 2

Based on :
MASQ [Stratulat, T., Ferber, J., & Tranier, J.]
De MASQ à MetaCiv [Ferber, J.& Nigon, J.]

The cogniton represents a cognitive unit (and idea, a beleif, a drive ...) which influences, positively or negatively plans (behaviours) which are defined by a netlogo implementation.

each agent possesse his own cognitons and can modify their weights during the simulation. To calculate the plans weights, the weight of each cogniton is propagated (multiplied) through the influence links to the corresponding plans where all the incoming influences are summed. 

The plan is then selected, either by maximum weight, or by a stochastic decision maker (the plan weight represents the probablility of being selected).

Each cogniton can be activated and deactivated. A plan is avalaible for selection only if he has at least one conditional link (or dependancy link) to an active cogniton

The cogniton architecture also provides culturons, an extension of the cognitons to the AGR principles (Agent-Group-Role [Ferber&Gutknecht2000]). The culturon represents a collective cognitive unit (a cultural element, a norm, a popular opinion ...). a culturon works the exact same way as a cogniton except its weight value is shared between all the agents belonging to the group.
The culturons play the same role in the plan weight calculation, but their influence is multiplied by the agent's participation to the group (and individual variable).


	3 ========= Getting started ========= 3

1. add the line : 
   extensions [CogLogo]

2. to run any command : 
   coglogo:<command>
   ex : coglogo:openEditor

3. to start editing your cognitive schemes run the command :
   coglogo:openEditor

4. the name of the cognitive scheme will determine which breed will be affected, unless you override it in the "linked breeds" panel, case is ignored

5. make sure any plan you declare in the cognitive scheme has a corresponding netLogo procedure with the exact same name

6. call coglogo:reset-simulation in the setup. it resets group count and agent data tracking

7. when creating a turtle in the setup make sure to call coglogo:init-cognitons. never call coglogo:init-cognitons when you hatch a turtle, the new turtle will inherit the cognitons values like any variable.
   ex: create-breed n [
    coglogo:init-cognitons ....]

8. to modify cognitons values , either :
	-call coglogo:set-cogniton-value <"cognitonName"> <value> or coglogo:add-to-cogniton-value <"cognitonName"> <value>
	-call coglogo:feed-back-from-plan <"planName"> <value> which will add the value to the respective cognitons, weighted by the reinforcement links you defined in the model.

9. to use the cogniton based decision process :
   ask breed [run coglogo:choose-next-plan]


	4 ========= Command list ============ 4

	===GENERAL===
OpenEditor : 
opens/close the editor panel

choose-next-plan : 
returns a string, the name of the next plan to run. (ex : run cognitonsfornetlogo:choose-next-plan)

feed-back-from-plan <string planName> <double val> :
propagates val to all the cognitons (or culturons) linked to the plan by a reinforcement link. the value parameter (val) is multiplied by the value of the reinforcement link, the result is added to the value of the cogniton (or culturon)

report-agent-data : 
reports the value of all cognitons and the resulting weights of the plans to our observation panel

reset-simulation : 
to be called in the setup, resets group count and agent data tracking


	==COGNITONS==

init-cognitons : 
must be called for each agent using a cognitive scheme , only during the setup.
  
add-to-cogniton-value <string cognitonName> <double val> : 
adds val to the corresponding cogniton  

set-cogniton-value  <string cognitonName> <double val> : 
sets the value to val of the corresponding cogniton  

get-cogniton-value  <string cognitonName> : 
returns the value of the corresponding cogniton. if the cogniton is not active the function returns 0.

activate-cogniton <string cognitonName> : 
activates a cogniton and sets its value to 0. All plans connected to this cogniton via dependancy links will be avalaible for the next call of choose-next-plan (if they were not already).

deactivate-cogniton <string cognitonName> : 
deactivates a cogniton. All plans connected to this cogniton via dependancy links will be unavalaible for the next call of choose-next-plan if they are not connected to another active cogniton via dependancy links.


	==CULTURONS==
	
create-and-join-group <string groupName> <string roleName> :
create an instance of <groupName> type and join it in the role <roleName>. When joining, the participation (involvement) value is set to 1.0.

join-group <string groupName> <string roleName> <double groupId> : 
join the instance <groupId> of type <groupName> in the role <roleName>. When joining, the participation (involvement) value is set to 1.0.

add-to-participation <string groupName> <double val> :
adds val to the corresponding group involvement.  

set-participation <string groupName> <double val> :
sets the value to val of the corresponding group involvement.

get-group-id <string groupName> : 
returns the group identifier of groupName (a number >= 0) of the agent. if the agent is not in any role of this group type the function returns -1.

get-group-role-id <string groupName> <string roleName> : 
returns the group identifier of groupName (a number >= 0) of the agent if the agent has the role of roleName. if the agent is not in this role of this group type the function returns -1.

leave-group <string groupName> : 
leave the group of groupName (if the agent is not in this type of group, does nothing)

leave-all-groups : 
the agent leaves all his groups

add-to-culturon-value <String groupName> <String culturonName> <Double value> :
add <value> to the culturon <culturonName> of the group type <groupName>

set-culturon-value <String groupName> <String culturonName> <Double value> :
set <value> as the value of the culturon <culturonName> of the group type <groupName>

get-culturon-value <String groupName> <String culturonName> :
return the value of the culturon <culturonName> of the group type <groupName>. if the agent is not in any role of this group type the function returns 0.


	5 ========= Decision Makers ============ 5

MaximumWeight : 
the plan with the maximum weight is selected.

WeightedStochastic :
the weight of each plan represents the probability for the plan to be selected.
if PlanA = 5 and PlanB = 3 , the probability of being selected is : PlanA = 5/8 = 0.625 and PlanB = 3/8 = 0.375
a random function (0,1) is then called to select the plan.

BiasedWeightedStochastic : 
works the same way as the WeightedStochastic, but increase the probability of selecting the better plans accordingly to the bias factor.
the plans are sorted from higher probability to lower, covering the range from 0 to 1 (the highest probablity covers the range from 0 to p , then the next plan from p to p+1 ...)
the random function result is then elevated to the degree of the bias specified (which will bias the random function towards giving values closer to 0)
(a bias of 1 will give the same results as the regular WeightedStochastic, but with a slightly higher computing cost)

