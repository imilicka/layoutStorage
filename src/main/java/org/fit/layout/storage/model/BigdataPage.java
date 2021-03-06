package org.fit.layout.storage.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.fit.layout.impl.DefaultBox;
import org.fit.layout.impl.DefaultPage;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;

public class BigdataPage extends DefaultPage {

	
	/**
	 * creates model from the given RDF model statements
	 * @param pageStatements page statements
	 * @param urlString original url
	 * @throws MalformedURLException
	 */
	public BigdataPage(Model pageStatements, String urlString)
			throws MalformedURLException {

		super(new URL(urlString));

		List<DefaultBox> boxes = getAllPageBoxes(pageStatements);
		createDocumentTree(boxes);
	}

	

	/**
	 * conversion all RDF statements into a list of Boxes
	 * 
	 * @param pageStatements
	 * @return list of Boxes that must be converted into a tree
	 */
	private List<DefaultBox> getAllPageBoxes(Model pageStatements) {
		List<DefaultBox> allElements = new ArrayList<>();

		try {

			for (Statement s : pageStatements.filter(null, RDF.TYPE, null)) {
				Model attributes = pageStatements.filter(s.getSubject(), null, null);
				allElements.add(new BigdataBox(attributes));
			}
		} catch (Exception ex) {
		}

		return allElements;
	}

	// ////////////////////////////////////////////////////////////
	// PAGE building
	// ////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param model
	 * @return
	 */
	private void createDocumentTree(List<DefaultBox> elements) {

		// sorting the individuals
		List<DefaultBox> list = sortBySize(elements);

		DefaultBox root = list.get(0);

		this.setHeight(root.getHeight());
		this.setWidth(root.getWidth());

		this.setRoot(root);
		list.remove(list.get(0));

		// process inner elements in the document tree
		appendTo(root, getInnerIndividuals(root, list));
	}

	/**
	 * it sorts page's elements from higher to lower volume
	 * 
	 * @param elements
	 * @return list of individuals
	 */
	public static List<DefaultBox> sortBySize(List<DefaultBox> elements) {

		// list sorting
		Collections.sort(elements, new Comparator<DefaultBox>() {
			public int compare(DefaultBox one, DefaultBox two) {
				int width1 = one.getWidth();
				int height1 = one.getHeight();
				int width2 = two.getWidth();
				int height2 = two.getHeight();

				return (width2 * height2) - (width1 * height1);
			}
		});

		List<DefaultBox> dlb = new ArrayList<>();

		for (DefaultBox db : elements) {

			dlb.add(db);
		}

		return dlb;
		// return elements;
	}

	/**
	 * it appends individual into tree
	 * 
	 * @param appendInd
	 * @param sourceList
	 */
	private void appendTo(DefaultBox appendInd, List<DefaultBox> sourceList) {

		if (sourceList == null || sourceList.size() == 0)
			return;

		// append actual node into tree
		DefaultBox actual = (DefaultBox) sourceList.get(0);
		appendInd.add(actual);
		sourceList.remove(sourceList.get(0));

		if (sourceList.size() == 0)
			return;

		List<DefaultBox> inner = getInnerIndividuals(actual, sourceList);
		List<DefaultBox> outer = getOuterIndividuals(actual, sourceList);

		if (inner.size() > 0)
			appendTo(actual, inner);

		if (outer.size() > 0)
			appendTo(appendInd, outer);
	}

	/**
	 * it returns innner individuals from individualBuffer that are inside the
	 * individual
	 * 
	 * @return
	 */
	private List<DefaultBox> getInnerIndividuals(DefaultBox individual,
			List<DefaultBox> sourceList) {
		List<DefaultBox> listNested = new ArrayList<DefaultBox>();


		for (DefaultBox ind : sourceList) {
			if (isNested(individual, ind)) {
				listNested.add(ind);
			}
		}

		return listNested;
	}

	/**
	 * it returns individuals from individualBuffer that are not inner
	 * 
	 * @param individual
	 * @return
	 */
	private List<DefaultBox> getOuterIndividuals(DefaultBox individual,
			List<DefaultBox> sourceList) {
		List<DefaultBox> listNested = new ArrayList<DefaultBox>();

		for (DefaultBox ind : sourceList) {
			if (!isNested(individual, ind)) {
				listNested.add(ind);
			}
		}

		return listNested;
	}

	/**
	 * detects if in2 is nested in in1
	 * 
	 * @param in1
	 * @param in2
	 * @return
	 */
	private Boolean isNested(DefaultBox in1, DefaultBox in2) {

		int width1 = in1.getWidth();
		int height1 = in1.getHeight();
		int posYAbs1 = in1.getY1();
		int posXAbs1 = in1.getX1();

		int width2 = in2.getWidth();
		int height2 = in2.getHeight();
		int posYAbs2 = in2.getY1();
		int posXAbs2 = in2.getX1();

		if (posXAbs1 <= posXAbs2 && (posXAbs1 + width1) >= (posXAbs2 + width2)
				&& posYAbs1 <= posYAbs2
				&& (posYAbs1 + height1) >= (posYAbs2 + height2))
			return true;
		else
			return false;
	}

}
