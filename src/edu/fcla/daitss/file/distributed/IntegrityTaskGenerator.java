/*
 * DAITSS Copyright (C) 2007 University of Florida
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package edu.fcla.daitss.file.distributed;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import edu.fcla.daitss.bitstream.Link;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.file.Descriptor;
import edu.fcla.daitss.task.TaskGenerator;
import edu.fcla.daitss.task.Task;

/**
 * @author franco
 *
 */
public class IntegrityTaskGenerator implements TaskGenerator {
    
    private final DBConnection con;
	private final Collection col; 
	private final Iterator colIter; 
	
    /**
     * This Task implementation checks integrity of Distributed objects
     * with respect to preservation level. Distributed objects are best 
     * represented as a graph since they may include circular references.
     * Using a node (represented as the Distriubted object passed in the 
     * constructor) as an entry point to the graph, the state of all other 
     * nodes belonging to the Distributed object is checked. If the 
     * preservation level of any node differs from the preservation level 
     * of the entry point, an association is made between the Distributed 
     * object and the SevereElement, 'PRES_LEVEL_CONFLICT', in the  
     * DATA_FILE_SEVERE_ELEMENT Table where it can be accessed by normal 
     * Reporter methods. 
     * 
     * @author chris
     */
	class IntegrityTask implements Task {

		/**  
		 * The Distributed object root used as an entry to the Distributed 
		 * object graph
		 */
		private final Distributed root;
		
		/**
		 * Stores all nodes visited as Distributed objects. 
		 * Used to detect and short-circuit cycles in the 
		 * Distributed object graph.
		 */
		private final Collection nodes;
		
		
		/**
		 * Constructor. Takes a local root of a Distributed object
		 * as an argument. 
		 * 
		 * @param dObj
		 */
		public IntegrityTask(Distributed dObj) {
			 this.root = dObj;
			 this.nodes = new Vector();			 		
		}
		
		/**
		 * 
		 */
        public void perform() throws FatalException {
			// add the root node to the nodes Collection
			// so that cycles can be detected
			this.nodes.add(this.root);
			checkChildPresLevel(this.root);
		}
		
		/**
		 * Recursively check the preservation level of all children
		 * of the root node. if a child's preservation level differs
		 * from the preservation level of the root, record the conflict
		 * in the database
		 * 
		 * @param node
		 * @return false, when an integrity problem has been detected
		 * @throws FatalException
		 */
		private boolean checkChildPresLevel(Distributed node) 
			throws FatalException {
			
			// only check integrity of content files, not descriptors
			if (node instanceof Descriptor || ((DataFile)node).isDescriptor()) {
				// if df is a descriptor, exit early
				return true;
			}
					
			Collection links = node.getLinks();
			Iterator i = links.iterator();
			while (i.hasNext()) {
				// get the Link object
				Link l = (Link)i.next();
				
				// make sure this was a successful link. if not, move to 
				// the next one. Unsuccessful links have been recorded 
				// elsewhere
				if (l.getStatus().equals(Link.STATUS_IGNORED) 
						|| l.getStatus().equals(Link.STATUS_BROKEN)) {
					continue;
				}												
				// the link has a status of Link.STATUS_SUCCESSFUL
				// or Link.STATUS_UNKNOWN
				
				// get the DataFile representing the link
				DataFile df = node.getDfFromLinkAlias(l.getLinkAlias());												
				
				// if no DataFile exists for this link, then there is 
				// an integrity problem
				// if the DataFile's preservation level isn't the same as
				// the root's preservation level, then there is an 
				// integrity problem
				if (df == null || !root.getPresLevel().equals(df.getPresLevel())) {
					// make sure a real data file was returned, if not 
					// the disctributed object has lost integrity
					// the preservation levels don't match, this is 
					// considered a conflict
					// only one preservation level conflict can be stored
					// per DataFile (due to database primary key constraints),
					// just exit now.
					return false;
				}
				// check for cycles. if this object has already been 
				// stored in this.nodes, then a cycle has been found,
				// so just continue with the next iteration
				if (df instanceof Distributed) {
					if (this.nodes.contains((Distributed)df)) {
						continue;
					}

					// add the object to the nodes collection to 
					// check for cycles in future iterations
					this.nodes.add((Distributed)df);
					// if the child DataFile is also a Distributed object, 
					// check its children recursively
					if (!checkChildPresLevel((Distributed)df)) {
						return false;
					}

				}					
			}	
			
			return true;
		}
    }
    
    
    /**
     * Constructor.
     * @param con A connection to a database that will store output of tasks created
     * by this Generator
     * @param c A collection of DataFile objects to be checked
     */
    IntegrityTaskGenerator(DBConnection con, Collection col) {
        // set the database connection
		this.con = con;
		// initialize Collection instance member
		this.col = new Vector();
		// add only Distributed files from the input collection to the
		// instance member collection (which is the one that will be 
		// iterated through).
		Iterator i = col.iterator();
		while (i.hasNext()) {
			DataFile df = (DataFile)i.next();
			if (df instanceof Distributed) {
				this.col.add(df);
			}
		}
		// set the Iterator instance member to the Iterator of the newly
		// created Collection instance member, not the Collection argument's
		// Iterator
		this.colIter = this.col.iterator();
	}
    
    public Task next() throws FatalException {
        return new IntegrityTask((Distributed)this.colIter.next());
    }

    public boolean hasNext() {
		return this.colIter.hasNext();
	}


}
