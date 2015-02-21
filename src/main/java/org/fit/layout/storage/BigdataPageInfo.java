package org.fit.layout.storage;

import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;


/**
 * Class represents a specific launch info
 * 
 * @author milicka
 *
 */
public class BigdataPageInfo {

	private String date;
	private String id;
	private String url;
	
	
	public BigdataPageInfo(GraphQueryResult statements) throws QueryEvaluationException {
		
		
		while(statements.hasNext() ) {
			Statement st = statements.next();
			
			id = st.getSubject().toString();
			
			
			switch(st.getPredicate().toString()) {
			
				case BoxOnt.sourceUrl:
					this.url = st.getObject().stringValue();
					break;
			
				case BoxOnt.LaunchDatetime:
					this.date = st.getObject().stringValue();
					break;
			}
		}
		
	}
	
	public String getId() {
		return id;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getUrl() {
		return url;
	}
	
}
