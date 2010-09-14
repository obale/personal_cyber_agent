/**
 * PCA Communication
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
 * Written by Alex Oberhauser <alexoberhauser@networld.to>
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

package to.networld.cyberagent.communication.common;

/**
 * Provides constant pairs (namespace and prefix) of OWL/RDF ontologies.
 *
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 */
public interface OntologyHandler {
	public static final String PCA_ACTIONS_NS = "http://pca.networld.to/actions#";
	public static final String PCA_ACTIONS_PREFIX = "pca-actions";

	public static final String PCA_NETWORK_NS = "http://pca.networld.to/network#";
	public static final String PCA_NETWORK_PREFIX = "pca-network";

	/*
	 *************************************************
	 */

	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDF_PREFIX = "rdf";

	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDFS_PREFIX = "rdfs";

	public static final String FOAF_NS = "http://xmlns.com/foaf/0.1/";
	public static final String FOAF_PREFIX = "foaf";

	public static final String WOT_NS = "http://xmlns.com/wot/0.1/index.rdf#";
	public static final String WOT_PREFIX = "wot";

	public static final String GEO_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String GEO_PREFIX = "geo";

	public static final String CONTACT_NS = "http://www.w3.org/2000/10/swap/pim/contact#";
	public static final String CONTACT_PREFIX = "contact";

	public static final String SKOS_NS = "http://www.w3.org/2009/08/skos-reference/skos.rdf#";
	public static final String SKOS_PREFIX = "skos";

	public static final String SKOS_XL_NS = "http://www.w3.org/2009/08/skos-reference/skos-xl.rdf";
	public static final String SKOS_XL_PREFIX = "skos-xl";

	public static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	public static final String DC_PREFIX = "dc";

	public static final String DC_TERMS_NS = "http://purl.org/dc/terms/";
	public static final String DC_TERMS_PREFIX = "dct";

	public static final String DOAP_NS = "http://usefulinc.com/ns/doap#";
	public static final String DOAP_PREFIX = "doap";
}
