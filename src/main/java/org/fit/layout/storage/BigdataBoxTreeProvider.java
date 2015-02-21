/**
 * CSSBoxTreeProvider.java
 *
 * Created on 27. 1. 2015, 15:14:55 by burgetr
 */
package org.fit.layout.storage;

import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.fit.layout.cssbox.CSSBoxTreeBuilder;
import org.fit.layout.impl.BaseBoxTreeProvider;
import org.fit.layout.model.Page;
import org.fit.layout.storage.model.BigdataPage;
import org.openrdf.model.Model;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.xml.sax.SAXException;

/**
 * A box tree provider implementation based on BigdataDB 
 * 
 * @author milicka
 */
public class BigdataBoxTreeProvider extends BaseBoxTreeProvider
{
    private URL url2;
    private URIImpl pageId;
   
    
    private final String[] paramNames = { "url2", "pageId" };
    private final ValueType[] paramTypes = { ValueType.STRING, ValueType.STRING };

    public BigdataBoxTreeProvider()
    {
		setUrl2("http://localhost:8080/bigdata/sparql");
		setPageId("http://www.idnes.cz#20150220070323");
    }
    
    public BigdataBoxTreeProvider(URL url2, URIImpl pageId)
    {
        this.url2 = url2;
        this.pageId = pageId;
    }

    
    public String getId()
    {
        return "FitLayout.Bigdata";
    }

   
    public String getName()
    {
        return "Bigdata loader";
    }

    
    public String getDescription()
    {
        return "Uses the Bigdata RDF DB for obtaining the box tree.";
    }

    
    public String[] getParamNames()
    {
        return paramNames;
    }

    
    public ValueType[] getParamTypes()
    {
        return paramTypes;
    }

    public URL getUrl2()
    {
        return url2;
    }

    public void setUrl2(URL url)
    {
        this.url2 = url;
    }
    
    public void setUrl2(String url2)
    {
        try {
            this.url2 = new URL(url2);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url2);
        }
    }

    public URIImpl getPageId()
    {
        return pageId;
    }

    public void setPageId(URIImpl pageId)
    {
        this.pageId = pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = new URIImpl(pageId);
    }

    
    public Page getPage() 
    {
    	try {
			BigdataInterface bdi = new BigdataInterface(url2.toString(), false);
			
			//Model m = bdi.getPageBoxModelFromNode(pageId.toString());
			Model m = bdi.getPageBoxModelFromNode(pageId.toString());
			
			BigdataPage bdmb = new BigdataPage(m, "http://www.idnes.cz");
			return bdmb;
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/*
        CSSBoxTreeBuilder build = new CSSBoxTreeBuilder(new Dimension(width, height));
        try {
            build.parse(url);
            return build.getPage();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
            return null;
        }
        */
    	return null;
    }

}
