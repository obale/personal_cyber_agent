/**
 * pca-reasoning - to.networld.cyberagent.reasoning.persistent
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Alex Oberhauser <oberhauseralex@networld.to>
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
import java.io.InputStream;

import net.jcip.annotations.NotThreadSafe;

import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import to.networld.cyberagent.common.config.Configuration;
import to.networld.cyberagent.reasoning.common.ComponentConfig;

/**
 * Repository Handler that uses the Sesame RDF Repository. If the connection with a
 * remote repository fails the fallback solution is to use a local data store.
 * 
 * @author Alex Oberhauser
 * @author Corneliu Stanciu Valentin
 */
@NotThreadSafe
public class RepositoryHandler {
	private Repository repos;
	private ValueFactory valueFactory;
	private RepositoryConnection connection;
	
	public RepositoryHandler() throws IOException {
		Configuration config = Configuration.newInstance();
		String remoteRepos = config.getValue("reasoning.persistent.remoteurl");
		String remoteDB = config.getValue("reasoning.persistent.remotedb");
		try {
			RemoteRepositoryManager remRepManager = RemoteRepositoryManager.getInstance(remoteRepos);
			this.repos = remRepManager.getRepository(remoteDB);
			remRepManager.shutDown();
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("Connected to the remote repository '" + remoteRepos + "' with database '" + remoteDB + "'");
		} catch (Exception e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).warn("[!!!] Failed to connect to the remote repository '" + remoteRepos + "' with database '" + remoteDB + "'!");
			String dataDir = config.getValue("reasoning.persistent.datadir");
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).warn("[!!!] Using instead the local repository '" + dataDir + "'");
			this.repos = new SailRepository(new MemoryStore(new File(dataDir)));
		}
	}
	
	public synchronized void init() throws RepositoryException {
		this.repos.initialize();
		this.valueFactory = this.repos.getValueFactory();
		this.connection = this.repos.getConnection();
		this.connection.setAutoCommit(true);
	}
	
	public void addTriple(Resource _subject, URI _predicate, Value _object) throws RepositoryException {
		this.connection.add(_subject, _predicate, _object);
	}
	
	public void addRDFStream(InputStream _rdfStream, String _baseURI, RDFFormat _format) throws RDFParseException, RepositoryException, IOException {
		this.connection.add(_rdfStream, _baseURI, _format);
	}
	
	public void removeTripe(Resource _subject, URI _predicate, Value _object) throws RepositoryException {
		this.connection.remove(_subject, _predicate, _object);
	}
	
	public TupleQueryResult executeSPARQLQuery(String _query) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		TupleQuery tuple = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, _query);
		return tuple.evaluate();
	}
	
	public RepositoryResult<Statement> getStatements(Resource _subject, URI _predicate, Value _object, boolean _includeInferred) throws RepositoryException {
		return this.connection.getStatements(_subject, _predicate, _object, _includeInferred);
	}
	
	public ValueFactory getValueFactory() {
		return this.valueFactory;
	}
	
	public synchronized void clean() throws RepositoryException {
		if ( this.connection != null ) {
			this.connection.close();
		}
		if ( this.repos != null )
			this.repos.shutDown();
	}
}
