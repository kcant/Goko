package org.goko.core.gcode.element;

import java.util.ArrayList;
import java.util.List;

import org.goko.core.common.utils.IIdBean;
import org.goko.core.common.utils.Location;

public class GCodeLine implements IIdBean{
	/** Internal identifier of this line */
	private Integer id;
	private Integer lineNumber;
	private List<GCodeWord> words;
	private List<GCodeParameter> parameters;	
	private Location location;
	
	public GCodeLine() {
		this.words 		= new ArrayList<GCodeWord>();
		this.parameters = new ArrayList<GCodeParameter>();
	}

	/**
	 * @return the lineNumber
	 */
	public Integer getLineNumber() {
		return lineNumber;
	}
	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}
	/**
	 * @return the words
	 */
	public List<GCodeWord> getWords() {
		return words;
	}
	/**
	 * @param words the words to set
	 */
	public void setWords(List<GCodeWord> words) {
		this.words = words;
	}
	
	public void addWord(GCodeWord word){
		words.add(word);
	}
	
	public void addWords(List<GCodeWord> lstWord){
		words.addAll(lstWord);
	}
	/**
	 * @return the parameters
	 */
	public List<GCodeParameter> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<GCodeParameter> parameters) {
		this.parameters = parameters;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
}
