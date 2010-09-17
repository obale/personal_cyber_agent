/**
 * pca-reasoning - to.networld.cyberagent.reasoning.persistent
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Alex Oberhauser <alexoberhauser@networld.to>
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
 * All Rights Reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>
 */

package to.networld.cyberagent.reasoning.persistent;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * @author Alex Oberhauser
 * @author Corneliu Stanciu Valentin
 */
public class RepositoryHandler {
	private static RepositoryHandler instance = null;
	private final Repository repos;
	private ValueFactory valueFactory;
	private RepositoryConnection connection;
	
	private RepositoryHandler() throws IOException {
		Properties prop = new Properties();
		prop.load(RepositoryHandler.class.getClassLoader().getResourceAsStream("to/networld/cyberagent/reasoning/default.properties"));
		String dataDir = prop.getProperty("pca.persistent.datadir");
		this.repos = new SailRepository(new MemoryStore(new File(dataDir)));
	}
	
	public void init() throws RepositoryException {
		this.repos.initialize();
		this.valueFactory = this.repos.getValueFactory();
		this.connection = this.repos.getConnection();
		this.connection.setAutoCommit(true);
	}
	
	public void populateAndDump() throws RDFParseException, RepositoryException, IOException {
        URI meFOAF = this.valueFactory.createURI("http://devnull.networld.to/foaf.rdf");
        URI interest = this.valueFactory.createURI("http://xmlns.com/foaf/0.1/interest");
        Literal someLiteral = this.valueFactory.createLiteral("Artifical Intelligence");
        
        this.connection.add(meFOAF, interest, someLiteral); 
	}
	
	public void clean() throws RepositoryException {
		this.connection.commit();
		this.connection.close();
		this.repos.shutDown();
	}
	
	public static RepositoryHandler newInstance() throws IOException {
		if ( instance == null ) instance = new RepositoryHandler();
		return instance;
	}
}
