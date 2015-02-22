package org.fit.layout.storage;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.AreaOnt;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
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
	

	
	
	
	//box tree functions ===========================================================	
	
	/**
	 * it returns a list of distinct source urls in database
	 * 
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

	/**
	 * gets all pages URI
	 * @return
	 * @throws Exception
	 */
	public List<String> getAllPageIds() throws Exception {
		
		List<String> output = new ArrayList<String>();
		URIImpl pageType = new URIImpl(BoxOnt.Page.toString());

		try {
			GraphQueryResult result = this.bddb.repo.getRemoteRepository()
					.getStatements(null, RDF.TYPE, pageType, true);

			// do something with the results
			while (result.hasNext()) {
				Statement bindingSet = result.next();

				String url = bindingSet.getSubject().stringValue();

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
	 * method gives a list of pageIDs for the specific url
	 * 
	 * @param url
	 *            it defines url of processed site
	 * @return list of specific launches
	 */
	public List<String> getPageIdsForUrl(String url) {
		
		List<String> output = new ArrayList<String>();

		try {
			// request for all launches of the specific url
			URIImpl sourceUrlPredicate = new URIImpl(BoxOnt.sourceUrl.toString());
			ValueFactoryImpl vf = ValueFactoryImpl.getInstance(); 
																	
			GraphQueryResult result = bddb.repo.getRemoteRepository()
					.getStatements(null, sourceUrlPredicate, vf.createLiteral(url), true); 

			// stores all launches into list of string
			while (result.hasNext()) 
			{
				Statement row = result.next();
				String page = row.getSubject().toString();

				System.out.println("output "+page);
				
				if (!output.contains(page))
					output.add(page);
			}

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}
	
	/**
	 * stores page model into bigdata database
	 * 
	 * @param page
	 */
	public void insertPageBoxModel(Page page) {
		
		// creates graph representation of RDF triples
		BigdataBoxModelBuilder pgb = new BigdataBoxModelBuilder(page);

		// stores graph of triples into DB
		insertGraph(pgb.getGraph());
	}

	/**
	 * it removes page
	 * 
	 * @param pageId
	 */
	public void removePage(String pageId) {

		removePageModel(pageId);
		removePageInfo(pageId);
	}

	/**
	 * it builds Model variable (specific type of Graph) for the information
	 * 
	 * 
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public Model getBoxModelForTimestamp(String timestamp) throws Exception {
		
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " + "?s ?b ?a . "
				+ "?a app:LaunchDatetime \"" + timestamp + "\". "
				+ "?a rdf:type app:Page  }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}
	
	/*
	 * gets page box model from the unique page ID
	 * 
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public Model getBoxModelForPageId(String pageId) throws Exception {
		
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
	 * loads page info - sourceUrl, launchDateTime
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public Model getPageInfo(String pageId) throws Exception {
		
		URIImpl page = new URIImpl(pageId);
		
		GraphQueryResult result = null;
		try {
			result = this.bddb.repo.getRemoteRepository().getStatements(page, null, null, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertGraphQueryResult2Model(result);
	}
	
	
	
	
	//AREA tree functions ===========================================================
	
	
	/**
	 * it returns area model
	 * @param areaTreeId
	 * @return
	 * @throws Exception
	 */
	public Model getAreaModelForAreaTreeId(String areaTreeId) throws Exception {
		
		String query = "PREFIX app: <http://www.mws.cz/render.owl#>"
				+ "PREFIX seg: <http://www.fit.vutbr.cz/~imilicka/public/ontology/segmentation.owl#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type seg:Area . "
				+ "?s seg:isPartOf <" + areaTreeId + "> }";

		IPreparedGraphQuery pgq = bddb.repo.getRemoteRepository()
				.prepareGraphQuery(query);
		GraphQueryResult gqr = pgq.evaluate();

		return convertGraphQueryResult2Model(gqr);
	}
	
	/**
	 * gets all area models for specific url
	 * @param pageId
	 * @throws Exception 
	 */
	public List<String> getAreaTreeIdsForPageId(String pageId) throws Exception {
		
		List<String> output = new ArrayList<String>();
		URIImpl hasAreaTree = new URIImpl(AreaOnt.hasAreaTree.toString());
		URIImpl page = new URIImpl(pageId);

		try {
			GraphQueryResult result = this.bddb.repo.getRemoteRepository()
					.getStatements(page, hasAreaTree, null, true);

			while (result.hasNext()) {
				Statement bindingSet = result.next();
				String url = bindingSet.getObject().stringValue();

				if (!output.contains(url))
					output.add(url);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	/**
	 * inserts area tree to specific pageId
	 * @param atree
	 * @param pageId
	 */
	public void insertAreaTree(AreaTree atree, URIImpl pageId) {
		
		try {
			String actualUrl = pageId.toString().substring(0, pageId.toString().lastIndexOf("#"));
			
			BigdataAreaModelBuilder buildingModel = new BigdataAreaModelBuilder(atree, pageId, actualUrl);
			insertGraph(buildingModel.getGraph());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	//others =========================================================================

	
	
	/**
	 * gets all statements for the specific subject
	 * (gets all triples for specific node)
	 * 
	 * @param subject
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryResult<Statement> getSubjectStatements(Resource subject)
			throws RepositoryException {
		RepositoryResult<Statement> stm = bddb.connection.getStatements(subject, null, null, true);
		
		return stm;
	}
	
	/**
	 * gets model with all attributes
	 * 
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public Model getSubjectModel(Resource subject) throws Exception {
		
		GraphQueryResult gqr = this.bddb.repo.getRemoteRepository().getStatements(subject, null, null, true);
		Model m = convertGraphQueryResult2Model(gqr);
		return m;
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


	

	
	//PRIVATE =========================================
	
	/**
	 * removes page model with its area trees
	 * 
	 * @param pageId
	 */
	private void removePageModel(String pageId) {

		//remove page model
		try {
			Model m;
			m = getBoxModelForPageId(pageId);
			bddb.getConnection().remove(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//load all area trees
		List<String> areaTreeModels = null;
		try {
			areaTreeModels = getAreaTreeIdsForPageId(pageId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//removes all area trees
		for(String areaTreeId: areaTreeModels) {
			try {
				Model mat = getAreaModelForAreaTreeId(areaTreeId);
				bddb.getConnection().remove(mat);	
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * it removes page info
	 * 
	 * @param pageId
	 */
	private void removePageInfo(String pageId) {
		
		try {
			Model m = getPageInfo(pageId);
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
	 * it appends graph into database
	 * 
	 * @param graph
	 */
	private void insertGraph(Graph graph) {
		bddb.addGraph(graph);
	}


}
