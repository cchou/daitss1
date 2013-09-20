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
package edu.fcla.daitss.file.distributed;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.task.Executor;
import edu.fcla.daitss.task.TaskManager;
import edu.fcla.daitss.util.Informer;

public class IntegrityTaskManager extends TaskManager {
		
	public IntegrityTaskManager(Collection c) throws FatalException{
		String methodName = "IntegrityTaskManager(Collection)";
        DBConnection cnn = DBConnection.getConnection();
        this.e = new Executor(
				new IntegrityTaskGenerator(cnn, c));
        try {
            cnn.close();
        }catch (SQLException e) {
            Informer.getInstance().warning(this,
                    methodName,
                    "Error closing database connection: " + e.getMessage(),
                    cnn.toString());
        }        
        cnn = null;
    }
	
	public static void main(String[] args) throws FatalException {						
		Collection c = new Vector();
		new IntegrityTaskManager(c).process();
	}
}
