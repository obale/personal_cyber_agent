/**
 * pca-reasoning - to.networld.cyberagent.reasoning.common
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

package to.networld.cyberagent.reasoning.common;

import java.math.BigDecimal;

/**
 * @author Alex Oberhauser
 */
public abstract class GPSHandler {
	
	/**
	 * Used to convert the raw data (from NMEA specification) to the intern used decimal format.
	 * 
	 * @param _nmeaCoordinate The latitude or longitude received from the nmea specification.
	 * @return The coordinate in decimal.
	 */
	public static BigDecimal convertNMEAtoDecimal(double _nmeaCoordinate) {
		int degree = (int) (_nmeaCoordinate / 100);
		double minutes = _nmeaCoordinate - 100 * ((double)degree);
		double total = degree + (minutes / 60);
		return BigDecimal.valueOf(total).setScale(7, BigDecimal.ROUND_HALF_EVEN);
	}
}
