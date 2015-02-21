package org.fit.layout.storage;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.AreaOnt;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
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
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import com.bigdata.gom.gpo.GPO;
import com.bigdata.rdf.sail.webapp.client.IPreparedGraphQuery;


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
	 * unimplemented
	 * @param newNamespace
	 */
	public void addNamespace(String newNamespace) {
		bddb.addNamespace(newNamespace);
	}

	/**
	 * 
	 * @return
	 * @throws RepositoryException
	 * @unimplemented in Bigdata framework
	 */
	public RepositoryResult<Namespace> getAllNamespaces() throws RepositoryException {
		return bddb.getAllNamespaces();
	}
	
	/**
	 * it returns a list of distinct url pages from the database
	 */
	public List<String> getDistinctUrlPages() {

		List<String> output = new ArrayList<String>();
		URIImpl sourceUrlPredicate = new URIImpl(BoxOnt.sourceUrl.toString());

		try {
			GraphQueryResult result = this.bddb.repo.getRemoteRepository()
					.getStatements(null, sourceUrlPredicate, null, true);

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

	public List<BigdataPageInfo> getPagesForUrl(String url) {

		List<String> ids = getPageIds(url);
		List<BigdataPageInfo> pagesInfos = new ArrayList<BigdataPageInfo>();

		for (String id : ids) {

			BigdataPageInfo bdl = getPageInfo(id);

			if (bdl != null)
				pagesInfos.add(bdl);
		}

		return pagesInfos;
	}

	/**
	 * stores page model into bigdata database
	 * 
	 * @param page
	 */
	public void insertPage(Page page) {
		
		// creates graph representation of RDF triples
		BigdataBoxModelBuilder pgb = new BigdataBoxModelBuilder(page);

		// stores graph of triples into DB
		insertGraph(pgb.getGraph());
	}

	
	public void insertAreaTree(AreaTree atree, String url) {
		
		try {
			URIImpl page = getPageNode(url);
		
			BigdataAreaModelBuilder buildingModel = new BigdataAreaModelBuilder(atree, page, url);
			insertGraph(buildingModel.getGraph());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/**
	 * it removes launch
	 * 
	 * @param pageId
	 */
	public void removePage(String pageId) {

		removePageBoxModel(pageId);
		//removePageAreaModel(pageId);
		removePageInfo(pageId);

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
	 * it builds Model variable (specific type of Graph) for the information
	 * 
	 * 
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public Model getPageBoxModelFromTimestamp(String pageId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " + "?s ?b ?a . "
				+ "?a app:LaunchDatetime \"" + pageId + "\". "
				+ "?a rdf:type app:Page  }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}
	
	/*
	 * get page box model from the unique page ID
	 * 
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public Model getPageBoxModelFromNode(String pageId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " 
				+ "?s app:belongsTo <"+pageId+">}";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}
	
	
	/**
	 * it returns area model
	 * @param areaTreeId
	 * @return
	 * @throws Exception
	 */
	public Model getPageAreaModel(URIImpl areaTreeId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX seg: <http://www.fit.vutbr.cz/~imilicka/public/ontology/segmentation.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type seg:Area . "
				+ "?s seg:isPartOf <" + areaTreeId.toString() + "> }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}
	
	/**
	 * gets all area models for specific url
	 * @param url
	 * @throws Exception 
	 */
	public List<String> getPageAreaModels(String url) throws Exception {
		
		List<String> output = new ArrayList<String>();

		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Page. "
				+ "?s app:sourceUrl \"" + url + "\" }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository().prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();
		
		
		URIImpl hasAreaTree = new URIImpl(AreaOnt.hasAreaTree);
		while( gqr.hasNext() ) {
			Statement stmt = gqr.next();
			if( stmt.getPredicate().equals(hasAreaTree) ) {
				output.add(stmt.getObject().stringValue());
			}
		}
		
		return output;
	}
	
	
	public Model getLaunchInfoModel(String pageId) throws Exception {
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s app:LaunchDatetime \"" + pageId + "\". "
				+ " }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
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
	
	
	public void clearRDFDatabase() {
		
		try {
			Update upd = bddb.repo.getConnection().prepareUpdate(QueryLanguage.SPARQL, "DELETE WHERE { ?s ?p ?o }");
			upd.execute();
			
		} catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
			e.printStackTrace();
		}
		
	}


	
	/**
	 * gets page node from specific page url
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public URIImpl getPageNode(String url) throws Exception {
		
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Page . " 
				+ "?s app:sourceUrl \"" + url + "\" } ";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		String res = null;
		
		// gets page node
		while(gqr.hasNext()) {
			Statement stm = gqr.next();
			res = stm.getSubject().stringValue();
		}
		
		return new URIImpl(res);
	}
	
	
	//PRIVATE =========================================
	
	/**
	 * it removes all launch nodes
	 * 
	 * @param launchDatetime
	 */
	private void removePageBoxModel(String launchDatetime) {

		Model m;
		try {
			m = getPageBoxModelFromTimestamp(launchDatetime);
			bddb.getConnection().remove(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * it removes launch info
	 * 
	 * @param pageId
	 */
	private void removePageInfo(String pageId) {
		
		try {
			Model m = getLaunchInfoModel(pageId);
			bddb.getConnection().remove(m);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * converstion from GraphQueryResult info Model
	 * @param gqr
	 * @return
	 * @throws QueryEvaluationException
	 */
	private Model convertGraphQueryResult2Model(GraphQueryResult gqr) throws QueryEvaluationException {
		
		// create a new Model to put statements in
		Model model = new LinkedHashModel();

		while (gqr.hasNext()) {
			model.add(gqr.next());
		}

		return model;
	}

	/**
	 * method gives a list of launches for the specific url
	 * 
	 * @param url
	 *            it defines url of processed site
	 * @return list of specific launches
	 */
	private List<String> getPageIds(String url) {
		List<String> output = new ArrayList<String>();

		try {
			// request for all launches of the specific url
			URIImpl sourceUrlPredicate = new URIImpl(BoxOnt.sourceUrl.toString());
			ValueFactoryImpl vf = ValueFactoryImpl.getInstance(); // constructor
																	// for the
																	// value
																	// creation
			GraphQueryResult result = bddb.repo.getRemoteRepository()
					.getStatements(null, sourceUrlPredicate, vf.createLiteral(url), true); 

			// stores all launches into list of string
			while (result.hasNext()) 
			{
				Statement row = result.next();
				String page = row.getSubject().toString();

				if (!output.contains(page))
					output.add(page);
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

	/**
	 * gets page info
	 * @param pageId
	 * @return
	 */
	private BigdataPageInfo getPageInfo(String pageId) {

		BigdataPageInfo bdl = null;

		try {
			URIImpl page = new URIImpl(pageId);
			GraphQueryResult results = bddb.repo.getRemoteRepository()
					.getStatements(page, null, null, true); 

			bdl = new BigdataPageInfo(results);
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bdl;
	}

	/**
	 * it appends graph into database
	 * 
	 * @param graph
	 */
	private void insertGraph(Graph graph) {
		bddb.addGraph(graph);
	}


}
