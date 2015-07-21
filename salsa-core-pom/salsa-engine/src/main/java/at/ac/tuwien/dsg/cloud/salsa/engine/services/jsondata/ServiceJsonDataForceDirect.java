/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceJsonDataForceDirect {
	
	List<Node> nodes = new ArrayList<>();
	List<Link> links = new ArrayList<>();
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        int nodeIndexCount=0;
	
	public class Node{		
		String name;
		String group;
                String state;
                String type;
		public Node(String name, String group, String type, String state){
			this.name = name;
			this.group = group;
                        this.state = state;
                        this.type = type;
		}
	}
	
	public class Link{
		int source;
		int target;
		String type;
		public Link(int source, int target, String type){
			this.source = source;
			this.target = target;
			this.type = type;	
		}
	}
	
	public void addNode(String name, String group, String type, String state){                
		this.nodes.add(new Node(name,group,type,state));
                nodeIndexMap.put(name, nodeIndexCount);
                nodeIndexCount+=1;
	}
	
	public void addLink(String source, String target, String type){
		this.links.add(new Link(nodeIndexMap.get(source), nodeIndexMap.get(target), type));
	}
}
