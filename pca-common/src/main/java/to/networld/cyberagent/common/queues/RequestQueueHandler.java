/**
 * PCA Common
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

package to.networld.cyberagent.common.queues;

import javax.xml.soap.SOAPMessage;

import to.networld.cyberagent.common.data.IPPackage;

/**
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 *
 */
public class RequestQueueHandler extends QueueHandler<SOAPMessage> {
	private static QueueHandler<IPPackage> instance = null;
	
	private RequestQueueHandler() {}

	public static QueueHandler<IPPackage> newInstance() {
		if ( instance == null ) instance = new QueueHandler<IPPackage>(); 
		return instance;
	}
}
