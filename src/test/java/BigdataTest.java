 
 
import org.fit.layout.storage.BigdataInterface;
import org.junit.Test; 
import org.openrdf.model.Namespace;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
 
/**  
 *  
 */ 
public class BigdataTest { 
 
	
	public BigdataTest() {
		
	}
	
	
	/**
	 * general valid launch
	 * @throws RepositoryException 
	 */
    @Test
    public void correctRun() { 
    	
			try {
				BigdataInterface bdi = new BigdataInterface();		
				
				System.out.println("no problem");
			} catch (RepositoryException e) {
				System.out.println("problem");
			}
			System.out.println("toto je jasne");
    } 
  
    
    /**
	 * general valid launch
	 * @throws RepositoryException 
	 */
    //@Test
    public void correctRun2() { 
    	
			try {
				BigdataInterface bdi = new BigdataInterface();
				
				System.out.println("no problem");
			} catch (RepositoryException e) {
				System.out.println("problem");
			}
			System.out.println("toto je jasne");
    } 
    
} 