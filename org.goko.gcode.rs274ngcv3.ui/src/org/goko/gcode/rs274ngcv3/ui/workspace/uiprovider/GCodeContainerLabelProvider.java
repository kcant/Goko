/**
 *
 */
package org.goko.gcode.rs274ngcv3.ui.workspace.uiprovider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;
import org.goko.core.gcode.element.IGCodeProvider;
import org.goko.core.gcode.element.validation.IValidationTarget;
import org.goko.core.gcode.rs274ngcv3.element.IModifier;


/**
 * @author PsyKo
 * @date 31 oct. 2015
 */
public class GCodeContainerLabelProvider extends LabelProvider implements IStyledLabelProvider {
	private final ImageDescriptor warningImageDescriptor;
	private final ImageDescriptor errorImageDescriptor;
	
	/**
	 * 
	 */
	public GCodeContainerLabelProvider() {
		super();
		warningImageDescriptor = ResourceManager.getPluginImageDescriptor("org.goko.gcode.rs274ngcv3.ui", "resources/icons/warn_ovr.png");
		errorImageDescriptor   = ResourceManager.getPluginImageDescriptor("org.goko.gcode.rs274ngcv3.ui", "resources/icons/error_ovr.png");
	}


	/** (inheritDoc)
	 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
	 */
	@Override
	public StyledString getStyledText(Object element) {
		if(element instanceof IGCodeProvider){
			return new StyledString(((IGCodeProvider) element).getCode());
		}else if(element instanceof GCodeContainerUiProvider){
			StyledString styleString = new StyledString();
			styleString.append("GCode ");
			return styleString;
		}else if(element instanceof IModifier){
			StyledString styleString = new StyledString();
			styleString.append(((IModifier<?>) element).getModifierName());
			return styleString;
		}
		return null;
	}


	/** (inheritDoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if(element instanceof GCodeContainerUiProvider){
			return ResourceManager.getPluginImage("org.goko.gcode.rs274ngcv3.ui", "resources/icons/resource_persp.gif");
		}else if(element instanceof IGCodeProvider){
			Image image = ResourceManager.getPluginImage("org.goko.gcode.rs274ngcv3.ui", "resources/icons/document-attribute-g.png");
			IGCodeProvider provider = (IGCodeProvider) element;			
			if(provider.isLocked()){
				return ResourceManager.getPluginImage("org.goko.gcode.rs274ngcv3.ui", "resources/icons/lock.png");
			}
			image = decorateValidationTarget(image, provider);
			return image;
		}else if(element instanceof IModifier){
			if(((IModifier<?>) element).isEnabled()){
				return ResourceManager.getPluginImage("org.goko.gcode.rs274ngcv3.ui", "resources/icons/modifier-on.png");
			}else{
				return ResourceManager.getPluginImage("org.goko.gcode.rs274ngcv3.ui", "resources/icons/modifier-off.png");
			}
		}
		return super.getImage(element);
	}
	
	protected Image decorateValidationTarget(Image image, IValidationTarget target){
		if(target.hasErrors()){
			return new DecorationOverlayIcon(image, errorImageDescriptor, IDecoration.BOTTOM_RIGHT).createImage();
		}else if(target.hasWarnings()){
			return new DecorationOverlayIcon(image, warningImageDescriptor, IDecoration.BOTTOM_RIGHT).createImage();
		}
		return image;
	}
}