/**
 * AnotatorTag.java
 *
 * Created on 4. 2. 2014, 08:57:22 by imilicka
 */
package org.fit.layout.anotator;

import org.fit.layout.impl.DefaultTag;

/**
 * A tag assigned using anotator - training data preparation
 *  
 * @author imilicka
 */
public class AnotatorTag extends DefaultTag
{

    public AnotatorTag(String value)
    {
        super(value);
        setType("FitLayout.AnotatorTag");
    }

}
