package org.fit.layout.storage;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import com.bigdata.rdf.sail.webapp.client.IPreparedGraphQuery;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;

public class BigdataInterface {

	BigdataConnector bddb;
	Boolean lbs = false;
	String url = "http://localhost:8080/bigdata/sparql";

	public BigdataInterface() throws RepositoryException {
		bddb = new BigdataConnector(this.url, this.lbs);
	}

	public BigdataInterface(String url, Boolean lbs) throws RepositoryException {
		this.url = url;
		this.lbs = lbs;
		bddb = new BigdataConnector(this.url, this.lbs);
	}

	public RepositoryConnection getConnection() {
		return this.bddb.getConnection();
	}

	/**
	 * it returns a list of distinct url pages from the database
	 */
	public List<String> getDistinctUrlPages() {

		List<String> output = new ArrayList<String>();
		URIImpl sourceUrlPredicate = new URIImpl(BoxOnt.sourceUrl.toString());

		try {
			GraphQueryResult result = this.bddb.repo.getRemoteRepository()
					.getStatements(null, sourceUrlPredicate, null, true);// prepareTupleQuery(request);

			// do something with the results
			while (result.hasNext()) {
				Statement bindingSet = result.next();

				String url = bindingSet.getObject().stringValue();

				if (!output.contains(url)) {
					output.add(url);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;

	}

	/**
	 * method gives a list of launches for the specific url
	 * 
	 * @param url
	 *            it defines url of processed site
	 * @return list of specific launches
	 */
	private List<String> getLauncheIds(String url) {
		List<String> output = new ArrayList<String>();

		try {
			// request for all launches of the specific url
			URIImpl sourceUrlPredicate = new URIImpl(
					BoxOnt.sourceUrl.toString());
			ValueFactoryImpl vf = ValueFactoryImpl.getInstance(); // constructor
																	// for the
																	// value
																	// creation
			GraphQueryResult result = bddb.repo.getRemoteRepository()
					.getStatements(null, sourceUrlPredicate,
							vf.createLiteral(url), true); // .getStatements(null,
															// sourceUrlPredicate,
															// vf.createLiteral(url),
															// true);

			// stores all launches into list of string
			while (result.hasNext()) {

				Statement row = result.next();
				String launch = row.getSubject().toString();

				if (!output.contains(launch))
					output.add(launch);
			}

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;
	}

	private BigdataLaunch getLaunchInfo(String launchId) {

		BigdataLaunch bdl = null;

		try {
			URIImpl launchSubject = new URIImpl(launchId);
			GraphQueryResult results = bddb.repo.getRemoteRepository()
					.getStatements(launchSubject, null, null, true); 

			bdl = new BigdataLaunch(results);
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bdl;
	}

	public List<BigdataLaunch> getLaunchesForUrl(String url) {

		List<String> ids = getLauncheIds(url);
		List<BigdataLaunch> launches = new ArrayList<BigdataLaunch>();

		for (String id : ids) {

			BigdataLaunch bdl = getLaunchInfo(id);

			if (bdl != null)
				launches.add(bdl);
		}

		return launches;
	}

	/**
	 * stores page model into bigdata database
	 * 
	 * @param page
	 */
	public void insertPage(Page page) {

		// creates graph representation of RDF triples
		BigdataGraph pgb = new BigdataGraph(page);

		// stores graph of triples into DB
		insertGraph(pgb.getGraph());
	}

	/**
	 * it removes launch
	 * 
	 * @param launchId
	 */
	public void removeLaunch(String launchId) {

		removeLaunchModel(launchId);
		removeLaunchInfo(launchId);

	}

	/**
	 * it removes all launch nodes
	 * 
	 * @param launchDatetime
	 */
	private void removeLaunchModel(String launchDatetime) {

		Model m;
		try {
			m = getLaunchModel(launchDatetime);
			bddb.getConnection().remove(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * it removes launch info
	 * 
	 * @param launchId
	 */
	private void removeLaunchInfo(String launchId) {
		
		try {
			Model m = getLaunchInfoModel(launchId);
			
			bddb.getConnection().remove(m);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * it appends graph into database
	 * 
	 * @param graph
	 */
	public void insertGraph(Graph graph) {
		bddb.addGraph(graph);
	}

	public int getTriplesNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	// TODO
	public Model selectStatements(URI subject, URI predicate, Value object) {

		return null;
	}

	/**
	 * it returns Graph of statements belonging specific launch
	 * 
	 * 
	 * @param launchId
	 * @return
	 * @throws Exception
	 * @deprecated use {@link #getModelForLaunch()} instead.
	 */
	@Deprecated
	public Graph getGraphForLaunch(String launchId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " + "?s ?b ?a . "
				+ "?a app:LaunchDatetime \"" + launchId + "\". "
				+ "?a rdf:type app:Launch  }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		Graph graph = new LinkedHashModel();

		while (gqr.hasNext()) {
			graph.add(gqr.next());
		}

		return graph;
	}

	/**
	 * it builds Model variable (specific type of Graph) for the information
	 * 
	 * 
	 * @param launchId
	 * @return
	 * @throws Exception
	 */
	public Model getLaunchModel(String launchId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " + "?s ?b ?a . "
				+ "?a app:LaunchDatetime \"" + launchId + "\". "
				+ "?a rdf:type app:Launch  }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}
	
	public Model getLaunchInfoModel(String launchId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s app:LaunchDatetime \"" + launchId + "\". "
				+ " }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}

	
	private Model convertGraphQueryResult2Model(GraphQueryResult gqr) throws QueryEvaluationException {
		
		// create a new Model to put statements in
		Model model = new LinkedHashModel();

		while (gqr.hasNext()) {
			model.add(gqr.next());
		}

		return model;
	}
	
	/**
	 * it returns all statements for the specific subject
	 * 
	 * @param subject
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryResult<Statement> getSubjectStatements(Resource subject)
			throws RepositoryException {
		return bddb.connection.getStatements(subject, null, null, true);
	}

	/**
	 * it executes SPARQL query on the databse
	 * 
	 * @param str
	 * @return
	 * @throws QueryEvaluationException
	 */
	public TupleQueryResult executeQuery(String query)
			throws QueryEvaluationException {

		try {

			org.openrdf.query.TupleQuery tq = bddb.repo.getConnection()
					.prepareTupleQuery(QueryLanguage.SPARQL, query);
			return tq.evaluate();

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
