 
 
import org.fit.layout.storage.example.LinkCrawler;
import org.junit.Before;
import org.junit.Test; 
import org.junit.Ignore; 
 
/** 
 * Tests for {@link Foo}. 
 * 
 * @author user@example.com (John Doe) 
 */ 
public class LinkCrawlerTest { 
 
	LinkCrawler downloader;
	
	public LinkCrawlerTest() {
		downloader = new LinkCrawler();
	}
	
	
	/**
	 * general valid launch
	 */
    @Test
    public void correctRun() { 
    	downloader = new LinkCrawler();
    	downloader.setSeed("http://www.fit.vutbr.cz");
    	downloader.start();
    	org.junit.Assert.assertTrue(downloader.size()>0);
    } 
 
    @Test
    public void invalidRun() {
    	downloader = new LinkCrawler();
    	downloader.start();
    	org.junit.Assert.assertTrue(downloader.size()==0);
    }
    
    @Test
    public void invalidRun2() {
    	downloader = new LinkCrawler();
    	downloader.setSeed("/?saddsa");
    	downloader.start();
    	org.junit.Assert.assertTrue(downloader.size()==0);
    } 
  
    
} 