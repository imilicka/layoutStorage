package org.fit.layout.storage.example;

import java.awt.Dimension;

import java.io.IOException;
import java.net.MalformedURLException;

import java.util.List;

import org.fit.layout.cssbox.CSSBoxTreeBuilder;
import org.fit.layout.model.Page;
import org.fit.layout.storage.BigdataInterface;
import org.fit.layout.storage.BigdataLaunchInfo;
import org.fit.layout.storage.BigdataPage;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.xml.sax.SAXException;

import com.bigdata.rdf.axioms.Axioms;
import com.bigdata.rdf.graph.util.GraphLoader;
import com.bigdata.rdf.internal.constraints.BNodeBOp;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailFactory;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.spo.SPO;


public class BigDataTesting {

	public static void main(String[] args) throws QueryEvaluationException {

		String urlString = "http://cssbox.sourceforge.net/";
		//String urlString = "http://www.mws.cz/";
		
		BigdataInterface bdi = null;
		
		
		try {
			
			
            CSSBoxTreeBuilder build = new CSSBoxTreeBuilder(new Dimension(800, 900));
            try {
				build.parse(urlString);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Page page = build.getPage();
            
            
			//example 1 - it inserts openRDF graph into database
            bdi = new BigdataInterface("http://pcuifs2.fit.vutbr.cz:8080/bigdata/sparql",false);
			bdi.insertPage( page );
			
			
			//example 2
			List<String> listURL = bdi.getDistinctUrlPages();
			for(String url : listURL) {
				System.out.println("URL:"+url);
			}
			
			
			//example 3
			List<BigdataLaunchInfo> listLaunch = bdi.getLaunchesForUrl(urlString);
			
			for(BigdataLaunchInfo launchInfo : listLaunch) {
				System.out.println("URL Launch:"+launchInfo.getDate());
				
				Model launch = bdi.getLaunchModel(launchInfo.getDate());
				page = new BigdataPage(launch, urlString );
			}
			
			

			
		} catch (RepositoryException e2) {
			e2.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
