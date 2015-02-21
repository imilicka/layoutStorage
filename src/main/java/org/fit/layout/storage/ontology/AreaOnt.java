package org.fit.layout.storage.ontology;

public class AreaOnt {

	/**
	 * <p>
	 * The namespace of the vocabulary as a string
	 * </p>
	 */
	public static final String NS = "http://www.fit.vutbr.cz/~imilicka/public/ontology/segmentation.owl#";

	/**
	 * <p>
	 * The namespace of the vocabulary as a string
	 * </p>
	 * 
	 * @see #NS
	 */
	public static String getURI() {
		return NS;
	}


	public static final String Area = NS + "Area";
	public static final String hasAreaTree = NS + "hasAreaTree";
	public static final String hasTag = NS + "hasTag";
	public static final String isPartOf = NS + "isPartOf";
	public static final String parentModel = NS + "parentModel";
	

}
