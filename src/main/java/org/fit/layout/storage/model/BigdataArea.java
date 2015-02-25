package org.fit.layout.storage.model;

import java.awt.Color;

import org.fit.layout.impl.DefaultArea;
import org.fit.layout.impl.DefaultTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;


/**
 * it builds Area objects from RDF statements
 * 
 * @author milicka
 *
 */
public class BigdataArea extends DefaultArea implements Area {

	
	Integer width = null;
	Integer height = null;
	Integer x = null;
	Integer y = null;


	/**
	 * it builds CSSBox BOX representation from the given RDF statements
	 * @param attributes
	 * @param r is not used for model loading, but it has to be used due to 
	 */
	public BigdataArea(Model attributes) {
		super(new Rectangular());	//it is not valid because
		
		//goes over all statements and sets attributes
		for(Statement attribute : attributes ) {
			setAttribute(attribute);
		}
		
		if(height!=null)
			System.out.println("Velikost height "+height);
		
		
		if (height!=null && width!=null && x!=null && y!=null) {
			setBounds(new Rectangular(x, y, x+width, y+height));
		}
	}
	
	
	public void setAttribute(Statement attribute) {
		
		switch(attribute.getPredicate().toString()) {
		
			case BoxOnt.backgroundColor:
				
				String bgColor = attribute.getObject().stringValue();
				setBackgroundColor( hex2Rgb( bgColor ) );
				
				break;
			case BoxOnt.backgroundImagePosition:
				break;
			case BoxOnt.backgroundImageUrl:
				break;
			case BoxOnt.fontDecoration:
				setUnderline(Float.parseFloat( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.fontSize:
				setFontSize(Float.parseFloat( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.fontStyle:
				setFontStyle(Float.parseFloat( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.fontWeight:
				setFontWeight(Float.parseFloat( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.hasBottomBorder:
				setBottomBorder(Integer.parseInt( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.hasLeftBorder:
				setLeftBorder(Integer.parseInt( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.hasRightBorder:
				setRightBorder(Integer.parseInt( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.hasTopBorder:
				setTopBorder(Integer.parseInt( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.hasTag:
				
				addTag(new DefaultTag("TODO", attribute.getObject().stringValue()) , 0); //TODO set tag type appropriately
				break;
			case BoxOnt.height:
				height = Integer.parseInt( attribute.getObject().stringValue() );
				break;
			case BoxOnt.width:
				width = Integer.parseInt( attribute.getObject().stringValue() );
				break;
			case BoxOnt.positionX:
				x = Integer.parseInt( attribute.getObject().stringValue() );
				break;	
			case BoxOnt.positionY:
				y = Integer.parseInt( attribute.getObject().stringValue() );
				break;		
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
