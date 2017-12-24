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


package src.myutils;

import java.util.HashMap;
import src.editor.AgentInfo;

public class myHashset
{
	HashMap<Long, AgentInfo> map = new HashMap<>();
	
	synchronized public boolean add(AgentInfo e) {
		map.put(e.id, e);
		return true;
	}
	
	synchronized public void clear() {
		map.clear();
	}

	public Object[] toArray() {
		synchronized (this) {
			return map.values().toArray();			
		}
	}
	
	public AgentInfo[] toAgentInfoArray() {
		synchronized (this) {
			AgentInfo[] ret = new AgentInfo[map.keySet().size()];
			int i = 0;
			for(Long l : map.keySet())
			{
				ret[i] = map.get(l);
				i++;
			}
			return ret;			
		}
	}
	
	public String[] toStringArray() {
		synchronized (this) {
			String[] ret = new String[map.values().size()];
			for(int i = 0 ; i < map.values().size(); i++)
				ret[i] = ((AgentInfo)map.values().toArray()[i]).toString();
			return  ret;			
		}
	}

	public int size() {
		
		return map.size();
	}

	public AgentInfo get(Long id) {
		return map.get(id);
	}
}
