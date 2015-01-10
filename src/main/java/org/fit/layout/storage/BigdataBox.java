package org.fit.layout.storage;

import java.awt.Color;
import java.util.Iterator;

import org.fit.layout.impl.DefaultBox;
import org.fit.layout.model.Box;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;


/**
 * Box representation with RDF attributes initialization
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
	 * creates box element from the given attribute triples
	 * 
	 * @param attributes
	 * @return
	 * 
	 * @deprecated uses old approach of document representation
	 */
	@Deprecated 
	public BigdataBox(Iterator<Statement> attributes) {
		
		setText(""); //there must be set text due to cssbox segmentation
		
		//goes over all statements and sets attributes
		while(attributes.hasNext()) {
			setAttribute(attributes.next());
		}
		
		if (height!=null && width!=null && x!=null && y!=null) {
			setBounds(new Rectangular(x, y, x+width, y+height));
			//setContentBounds(new Rectangular(x, y, x+width, y+height));
			setVisualBounds(new Rectangular(x, y, x+width, y+height));
		}
		
	}
	
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
		
		switch(attribute.getPredicate().toString()) {
		
			case BoxOnt.backgroundColor:
				
				String bgColor = attribute.getObject().stringValue();
				//bgColor = bgColor.substring(1,bgColor.length());
				setBackgroundColor( hex2Rgb( bgColor ) );
				
				break;
			case BoxOnt.backgroundImagePosition:
				break;
			case BoxOnt.backgroundImageUrl:
				break;
			case BoxOnt.color:
				
				String color = attribute.getObject().stringValue();
				//color = color.substring(1, color.length());
				setColor(hex2Rgb(color));
				
				break;
			case BoxOnt.fontDecoration:
				setUnderline(Float.parseFloat( attribute.getObject().stringValue() ));
				break;
			case BoxOnt.fontFamily:
				setFontFamily(attribute.getObject().stringValue());
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
				setTagName(attribute.getObject().stringValue());
				break;
			case BoxOnt.hasText:
				setType(Type.TEXT_CONTENT);
				setText(attribute.getObject().stringValue());
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
