/**
 * pca-reasoning - to.networld.cyberagent.reasoning
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

package to.networld.cyberagent.reasoning.infogathering;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import to.networld.cyberagent.reasoning.common.ComponentConfig;
import to.networld.cyberagent.reasoning.persistent.RepositoryHandler;
import to.networld.scrawler.common.Ontologies;

/**
 * Extracts and stores the information related to an foaf:Agent entity.
 * The information are gained from the SOAP header part.
 * 
 * @author Alex Oberhauser
 */
public class MetaInformation {
	private final SOAPHeader header;
	
	public MetaInformation(SOAPHeader _header) {
		this.header = _header;
	}
	
	/**
	 * Converts a Node object to the String representation.
	 * 
	 * @param node A XML node gained from a SOAP message
	 * @return The representation of the given node and all sub nodes as String.
	 */
	private static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).warn("Not able to transform SOAPElement to String.");
			return "";
		}
		return sw.toString();
	}
	
	/**
	 * Stores the information related to a foaf:Agent subject (gained from the
	 * SOAP header) to the RDF repository.
	 * 
	 * @throws IOException
	 */
	public synchronized void store() throws IOException {
		RepositoryHandler reposHandler = new RepositoryHandler();
		try {
			reposHandler.init();
			Iterator<?> iter = this.header.getChildElements(new QName(Ontologies.foafURI, "Agent"));
			while ( iter.hasNext() ) {
				SOAPElement foafAgent = (SOAPElement)iter.next();
				/*
				 * Remove unused namespace declarations. 
				 */
				foafAgent.removeNamespaceDeclaration("SOAP-ENV");
				foafAgent.removeNamespaceDeclaration("SOAP-SEC");
				Node node = (Node)foafAgent;
				InputStream is = new ByteArrayInputStream(nodeToString(node).getBytes());
				String foafURLStr = foafAgent.getAttribute("rdf:about");
				reposHandler.addRDFStream(is, foafURLStr, RDFFormat.RDFXML);
			}
		} catch (RDFParseException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (RepositoryException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} finally {	
			try {
				reposHandler.clean();
			} catch (RepositoryException e) {
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			}
		}
	}
	
}
