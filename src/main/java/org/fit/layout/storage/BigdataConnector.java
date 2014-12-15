package org.fit.layout.storage;




import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;


public class BigdataConnector {
	String endpointUrl;
	Boolean lbs;
	//BigdataSailRemote
	RepositoryConnection connection;
	BigdataSailRemoteRepository repo;
	

	/******************** getters/setters ************************/
	/**
	 * it returns connection
	 * @return
	 */
	public RepositoryConnection getConnection() 
	{
		return connection;
	}
	
	
	/**
	 * it redefines parameters and creates connection to server 
	 * @param endpoint
	 * @param lbs
	 * @throws RepositoryException
	 */
	public BigdataConnector(String endpoint, Boolean lbs) throws RepositoryException 
	{
		this.endpointUrl = endpoint;
		this.lbs = lbs;
		this.createConnection();
	}
	

	/**
	 * it creates db connection
	 * @throws RepositoryException
	 */
	private void createConnection() throws RepositoryException 
	{
		this.repo = new BigdataSailRemoteRepository(this.endpointUrl, this.lbs);
		this.connection = repo.getConnection();
	}

	
	/**
	 * it adds single tripple
	 * @param s
	 * @param p
	 * @param o
	 * @throws RepositoryException
	 */
	public void add(Resource s, URI p, Value o) 
	{
		try {
			
			Statement stmt = new StatementImpl(s, p, o);
			this.connection.add(stmt);
			this.connection.commit();
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * it executes sparql and returns tupy
	 * @param queryString
	 * @return
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException 
	 */
	public TupleQueryResult executeQuery(String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		try {
			TupleQuery query = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult tqr = query.evaluate();
        	return tqr;
		}
		catch(Exception ex) {
			System.out.println("nastala chyba");
		}
		return null;
	}

	
	/**
	 * it stores graph of statements into bigdata
	 * @param graph
	 */
	public void addGraph(Graph graph) 
	{
		
		try {
			
			this.connection.add(graph);
			this.connection.commit();
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
