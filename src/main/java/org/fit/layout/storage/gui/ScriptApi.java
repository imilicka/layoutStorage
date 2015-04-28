/**
 * ScriptApi.java
 *
 * Created on 28. 4. 2015, 16:40:17 by burgetr
 */
package org.fit.layout.storage.gui;

import org.fit.layout.api.ScriptObject;

/**
 * JavaScript application interface for the storage.
 * @author burgetr
 */
public class ScriptApi implements ScriptObject
{

    @Override
    public String getName()
    {
        return "storage";
    }
    
    public void connect(String uri)
    {
        //TODO
    }

}
