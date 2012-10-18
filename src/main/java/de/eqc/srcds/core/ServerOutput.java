/**
 * This file is part of the Source Dedicated Server Controller project.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or
 * combining it with srcds-controller (or a modified version of that library),
 * containing parts covered by the terms of GNU General Public License,
 * the licensors of this Program grant you additional permission to convey
 * the resulting work. {Corresponding Source for a non-source form of such a
 * combination shall include the source code for the parts of srcds-controller
 * used as well as that of the covered work.}
 *
 * For more information, please consult:
 *    <http://www.earthquake-clan.de/srcds/>
 *    <http://code.google.com/p/srcds-controller/>
 */
package de.eqc.srcds.core;

import java.util.Collection;

/**
 * The started process may produce output on the console. This interface
 * provides access to the history
 * 
 * @author Holger Cremer
 */
public interface ServerOutput {

    /**
     * Get the maximum size of the history.
     * 
     * @return the maximum line count.
     */
    int getMaxHistorySize();

    /**
     * Gets a (unmodifiable) collection of strings with the last (up to
     * {@link #getMaxHistorySize()}) output lines.
     * 
     * @return
     */
    Collection<String> getLastLog();

    /**
     * Adds an observer to get notice about every new line of process output.
     * 
     * @param o
     */
    void registerOnLogObserver(ProcessOutputObserver observer);

    /**
     * Unregisters an observer.
     * 
     * @param o
     */
    void unRegisterOnLogObserver(ProcessOutputObserver observer);
    
    /**
     * Unregisters all observers.
     * 
     */
    void unRegisterAllOnLogObservers();
}