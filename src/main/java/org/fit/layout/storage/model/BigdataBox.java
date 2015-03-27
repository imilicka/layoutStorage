package org.fit.layout.storage.model;

import java.awt.Color;

import org.fit.layout.impl.DefaultBox;
import org.fit.layout.model.Box;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BOX;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;


/**
 * Box representation created from RDF statements
 * 
 * @author milicka
 *
 */
public class BigdataBox extends DefaultBox implements Box {

	Integer width = null;
	Integer height = null;
	Integer x = null;
	Integer y = null;
	
	
	/**
	 * it builds CSSBox BOX representation from the given RDF statements
	 * @param attributes
	 */
	public BigdataBox(Model attributes) {
		
		//there must be set default text
		setText(""); 
		
		//goes over all statements and sets attributes
		for(Statement attribute : attributes ) {
			setAttribute(attribute);
		}
		
		if(height!=null)
			System.out.println("Velikost height "+height);
		
		
		if (height!=null && width!=null && x!=null && y!=null) {
			setBounds(new Rectangular(x, y, x+width, y+height));
			//setContentBounds(new Rectangular(x, y, x+width, y+height));
			setVisualBounds(new Rectangular(x, y, x+width, y+height));
		}
	}
	
	
	public void setAttribute(Statement attribute) {
		
		final URI uri = attribute.getPredicate();
	    
		if (BOX.backgroundColor.equals(uri)) {
			
			String bgColor = attribute.getObject().stringValue();
			//bgColor = bgColor.substring(1,bgColor.length());
			setBackgroundColor( hex2Rgb( bgColor ) );
			
		}
		else if (BOX.backgroundImagePosition.equals(uri)) {
		}
		else if (BOX.backgroundImageUrl.equals(uri)) {
		}
		else if (BOX.color.equals(uri)) {
			
			String color = attribute.getObject().stringValue();
			//color = color.substring(1, color.length());
			setColor(hex2Rgb(color));
			
		}
		else if (BOX.underline.equals(uri)) {
			setUnderline(Float.parseFloat( attribute.getObject().stringValue() ));
		}
        else if (BOX.lineThrough.equals(uri)) {
            setUnderline(Float.parseFloat( attribute.getObject().stringValue() ));
        }
		else if (BOX.fontFamily.equals(uri)) {
			setFontFamily(attribute.getObject().stringValue());
		}
		else if (BOX.fontSize.equals(uri)) {
			setFontSize(Float.parseFloat( attribute.getObject().stringValue() ));
		}
		else if (BOX.fontStyle.equals(uri)) {
			setFontStyle(Float.parseFloat( attribute.getObject().stringValue() ));
		}
		else if (BOX.fontWeight.equals(uri)) {
			setFontWeight(Float.parseFloat( attribute.getObject().stringValue() ));
		}
		else if (BOX.hasBottomBorder.equals(uri)) {
			setBottomBorder(Integer.parseInt( attribute.getObject().stringValue() ));
		}
		else if (BOX.hasLeftBorder.equals(uri)) {
			setLeftBorder(Integer.parseInt( attribute.getObject().stringValue() ));
		}
		else if (BOX.hasRightBorder.equals(uri)) {
			setRightBorder(Integer.parseInt( attribute.getObject().stringValue() ));
		}
		else if (BOX.hasTopBorder.equals(uri)) {
			setTopBorder(Integer.parseInt( attribute.getObject().stringValue() ));
		}
		else if (BOX.hasText.equals(uri)) {
			setType(Type.TEXT_CONTENT);
			setText(attribute.getObject().stringValue());
		}
		else if (BOX.height.equals(uri)) {
			height = Integer.parseInt( attribute.getObject().stringValue() );
		}
		else if (BOX.width.equals(uri)) {
			width = Integer.parseInt( attribute.getObject().stringValue() );
		}
		else if (BOX.positionX.equals(uri)) {
			x = Integer.parseInt( attribute.getObject().stringValue() );
		}	
		else if (BOX.positionY.equals(uri)) {
			y = Integer.parseInt( attribute.getObject().stringValue() );
		}
	}  
	
	
	/** 
	 *  
	 * @param colorStr e.g. "#FFFFFF" 
	 * @return  
	 */ 
	private Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	} 
}
