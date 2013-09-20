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
package edu.fcla.daitss.task;

import edu.fcla.daitss.exception.FatalException;

/**
 * @author franco
 * Represents a set of checks to be performed.
 */
public class Executor {
        
    /* sample of data to check */
    protected TaskGenerator sample;
    
    /**
     * @param sample
     */
    public Executor(TaskGenerator sample) {
        this.sample = sample;
    }
    
    /**
     * Perform this task on the set of files in the <code>Task</code>
     * @throws FatalException 
     */
    public void perform() throws FatalException {
        while(this.sample.hasNext()) {
            Task check = this.sample.next();
            check.perform();
        }
    }
}
