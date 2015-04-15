/**
 * BigdataLogicalArea.java
 *
 * Created on 27. 3. 2015, 14:52:08 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.impl.DefaultLogicalArea;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class BigdataLogicalArea extends DefaultLogicalArea
{

    
    public BigdataLogicalArea(Model attributes)
    {
        for(Statement attribute : attributes ) {
            setAttribute(attribute);
        }
    }
    
    public void setAttribute(Statement attribute) {
        
        final URI uri = attribute.getPredicate();
        
        if (SEGM.hasText.equals(uri)) {
            setText(attribute.getObject().stringValue());
        }
    }

}
