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

package src.editor;

import java.util.ArrayList;

import org.nlogo.api.Agent;


public class AgentInfo{
	public Long id = new Long(0);
	String breed = "";
	ArrayList<Double> cognitonValues;	
	ArrayList<Double> culturonsGRPID;	
	ArrayList<String> culturonsROLE;	
	ArrayList<Double> culturonsVALUE;
	Agent agent;
	public String cogScheme;
	
	public AgentInfo(long id2, String breedname, String cs, ArrayList<Double> values, ArrayList<Double> GRPID,
			ArrayList<String> ROLE, ArrayList<Double> VALUE, Agent ag) {
		id = id2;
		breed = breedname;
		cognitonValues = values;
		culturonsGRPID = GRPID;
		culturonsROLE = ROLE;
		culturonsVALUE = VALUE;
		agent = ag;
		cogScheme = cs;
	}
	
	@Override
	public String toString() {
		return breed + " - " + id;
	}
	
	@Override
	public int hashCode(){

        return id.intValue();
    }

	@Override
	public boolean equals(Object obj) {
		Long idIn = ((AgentInfo)obj).id;
		if(id == idIn)
			return true;
		return false;
	}
}