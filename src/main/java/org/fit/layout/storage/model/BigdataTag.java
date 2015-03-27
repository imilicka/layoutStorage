/**
 * BigdataTag.java
 *
 * Created on 27. 3. 2015, 14:43:34 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.impl.DefaultTag;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class BigdataTag extends DefaultTag
{
    private float support;
    
    public BigdataTag(Model attributes)
    {
        super("");
        support = 1.0f;
        for(Statement attribute : attributes ) {
            setAttribute(attribute);
        }
    }
    
    public float getSupport()
    {
        return support;
    }
    
    public void setAttribute(Statement attribute) {
        
        final URI uri = attribute.getPredicate();
        
        if (SEGM.hasName.equals(uri)) {
            setValue(attribute.getObject().stringValue());
        }
        else if (SEGM.hasType.equals(uri)) {
            setType(attribute.getObject().stringValue());
        }
        else if (SEGM.hasSupport.equals(uri)) {
            support = Float.valueOf(attribute.getObject().stringValue());
        }
    }
    
}
