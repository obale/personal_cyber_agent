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
 * Interface for the action URIs that are used in the SOAPAction field in the
 * HTTP header.
 * 
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 * 
 */
public interface ActionURIHandler {
	public static final String ACTION_PREFIX = OntologyHandler.PCA_ACTIONS_NS;
	
	public static final String REQUEST_ACTION = ACTION_PREFIX + "Request";
	public static final String RESPONSE_ACTION = ACTION_PREFIX + "Response";
	public static final String STATUS_ACTION = ACTION_PREFIX + "Status";
	
	public static final String STORE_ACTION = ACTION_PREFIX + "Store";
	public static final String REMOVE_ACTION = ACTION_PREFIX + "Remove";
	public static final String MODIFY_ACTION = ACTION_PREFIX + "Modify";
}
