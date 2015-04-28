/**
 * ScriptApi.java
 *
 * Created on 28. 4. 2015, 16:40:17 by burgetr
 */
package org.fit.layout.storage.gui;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.fit.layout.api.ScriptObject;
import org.fit.layout.storage.BigdataInterface;
import org.openrdf.repository.RepositoryException;

/**
 * JavaScript application interface for the storage.
 * @author burgetr
 */
public class ScriptApi implements ScriptObject
{
    private BufferedReader rin;
    private PrintWriter wout;
    private PrintWriter werr;
    
    private BigdataInterface bdi;

    
    public ScriptApi()
    {
        
    }
    
    @Override
    public String getName()
    {
        return "storage";
    }
    
    @Override
    public void setIO(Reader in, Writer out, Writer err)
    {
        rin = new BufferedReader(in);
        wout = new PrintWriter(out);
        werr = new PrintWriter(err);
    }
    
    public void connect(String uri)
    {
        try
        {
            bdi = new BigdataInterface(uri, false);
        } 
        catch (RepositoryException e)
        {
            werr.println("Couldn't connect: " + e.getMessage());
        }
    }

}
