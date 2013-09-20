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
/*
 * Created on Jun 7, 2005
 */
package edu.fcla.daitss.task;

import edu.fcla.daitss.ArchiveWorker;
import edu.fcla.daitss.exception.FatalException;

public abstract class TaskManager extends ArchiveWorker {
		
	protected Executor e = null;
	
	public void fail() {
		// unimplemented until needed
	}

	public void error() {
		// unimplemented until needeed
	}
 
	public void process() throws FatalException {
		e.perform();
	}

}
