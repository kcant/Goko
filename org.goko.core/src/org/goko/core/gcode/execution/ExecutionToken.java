/*******************************************************************************
 * 	This file is part of Goko.
 *
 *   Goko is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Goko is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Goko.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.goko.core.gcode.execution;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.goko.core.common.exception.GkException;
import org.goko.core.common.exception.GkTechnicalException;
import org.goko.core.common.utils.AbstractIdBean;
import org.goko.core.gcode.element.GCodeLine;
import org.goko.core.gcode.element.IGCodeProvider;
import org.goko.core.log.GkLog;

/**
 * Implementation of a {@link IExecutionToken} for execution planner
 *
 * @author PsyKo
 *
 */
public class ExecutionToken<T extends IExecutionTokenState> extends AbstractIdBean implements IExecutionToken<T> {	
	private static final GkLog LOG = GkLog.getLogger(ExecutionToken.class);
	/** The map of state by line Id */
	protected Map<Integer, T> mapExecutionStateById;
	/** The map of lines id by state */
	protected Map<T, List<Integer>> mapLineByExecutionState;
	/** The current command index */
	protected int currentIndex;
	/** Id of the executed GCodeProvider */
	protected WeakReference<IGCodeProvider> gcodeProviderReference;	
	/** Initial state of the lines */
	protected T initialState;
	/** State of the token */
	protected ExecutionState state;
	/**
	 * Constructor
	 * @param provider the provider to build this execution token from
	 * @throws GkException GkException 
	 */
	public ExecutionToken(IGCodeProvider provider, T initState) throws GkException {
		this.gcodeProviderReference = new WeakReference<IGCodeProvider>(provider);
		this.initialState = initState;
		reset();
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken#setLineState(java.lang.Integer, org.goko.core.gcode.execution.IExecutionTokenState)
	 */
	@Override
	public void setLineState(Integer idLine, T state) throws GkException {
		if(!mapExecutionStateById.containsKey(idLine)){
			throw new GkTechnicalException("GCodeLine ["+idLine+"] not found in execution token");	
		}
		
		if(!mapLineByExecutionState.containsKey(state)){
			mapLineByExecutionState.put(state, new ArrayList<Integer>());	
		}
		// remove from previous state list
		T oldState = findLineState(idLine);
		if(oldState != null){
			mapLineByExecutionState.get(oldState).remove(idLine);	
		}
		// Add to new state list
		mapLineByExecutionState.get(state).add(idLine);
		mapExecutionStateById.put(idLine, state);		
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken#getLineState(java.lang.Integer)
	 */
	@Override
	public T getLineState(Integer idLine) throws GkException {
		if(mapExecutionStateById.containsKey(idLine)){
			return mapExecutionStateById.get(idLine);
		}
		throw new GkTechnicalException("GCodeLine ["+idLine+"] not found in execution token");
	}
	
	/**
	 * Equivalent to getLineState except it doesn't throws Exception
	 * @param idLine the id of the line 
	 * @return a state
	 * @throws GkException GkException
	 */
	public T findLineState(Integer idLine) throws GkException {
		if(mapExecutionStateById.containsKey(idLine)){
			return mapExecutionStateById.get(idLine);
		}
		return null;
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken.execution.IGCodeExecutionToken#getNextCommand()
	 */
	@Override
	public GCodeLine getNextLine() throws GkException {
		return getGCodeProvider().getLineAtIndex(currentIndex + 1);
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken.execution.IGCodeExecutionToken#takeNextCommand()
	 */
	@Override
	public GCodeLine takeNextLine() throws GkException {
		currentIndex = currentIndex + 1;
		return getGCodeProvider().getLineAtIndex(currentIndex);
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken#hasMoreLine()
	 */
	@Override
	public boolean hasMoreLine() throws GkException {
		return getLineCount() > currentIndex + 1;
	}

	/**
	 * Returns the number of lines
	 * @return the number of lines
	 */
	public int getLineCount() {		
		try {
			return getGCodeProvider().getLines().size();
		} catch (GkException e) {
			LOG.error(e);
		}
		return 0;
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken#getLineByState(org.goko.core.gcode.execution.IExecutionTokenState)
	 */
	@Override
	public List<GCodeLine> getLineByState(T state) throws GkException {
		List<GCodeLine> result = new ArrayList<GCodeLine>();
		List<Integer> lstId = mapLineByExecutionState.get(state);
		if(CollectionUtils.isNotEmpty(lstId)){
			for (Integer idLine : lstId) {
				result.add(getGCodeProvider().getLine(idLine));
			}
		}
		return result;
	}
	/**
	 * Return the wrapped IGCodeProvider
	 * @return the IGCodeProvider
	 */
	public IGCodeProvider getGCodeProvider(){
		return gcodeProviderReference.get();
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutionToken#reset()
	 */
	@Override
	public void reset() throws GkException {
		this.mapExecutionStateById 	= new HashMap<Integer, T>();		
		this.mapLineByExecutionState = new HashMap<T, List<Integer>>();
		this.mapLineByExecutionState.put(initialState, new ArrayList<Integer>());		
		this.currentIndex = -1;
		this.setState(ExecutionState.IDLE);
		if(CollectionUtils.isNotEmpty(getGCodeProvider().getLines())){
			for (GCodeLine gCodeLine : getGCodeProvider().getLines()) {
				this.mapLineByExecutionState.get(initialState).add(gCodeLine.getId());
				this.mapExecutionStateById.put(gCodeLine.getId(), initialState);
			}
		}
	}

	/**
	 * @return the state
	 */
	public ExecutionState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(ExecutionState state) {
		this.state = state;
	}
}
