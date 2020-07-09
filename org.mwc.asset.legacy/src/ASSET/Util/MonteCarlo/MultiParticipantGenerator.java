/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Util.MonteCarlo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;

/**
 * list of items in a particular file which may be changed
 */

public final class MultiParticipantGenerator {
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class MultipleGennyTest extends SupportTesting {
		protected FileInputStream stream;

		public MultipleGennyTest(final String val) {
			super(val);
		}

		// TODO FIX-TEST
		public final void NtestLoadVariances() throws XPathExpressionException {
			// create server
			final MultiParticipantGenerator xv = new MultiParticipantGenerator();

			String code_root = System.getProperty("CODE_ROOT");
			System.out.println("root:" + code_root);
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

			Document doc = null;
			try {
				final File file = new File(docPath + "test_variance1.xml");
				assertTrue("can't find data-file", file.exists());
				stream = new FileInputStream(file);
				doc = ScenarioGenerator.readDocumentFrom(stream);
			} catch (final SAXException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			} catch (final FileNotFoundException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}
			xv.loadVariances(doc);

			assertNotNull("managed to load file", doc);

			// ok - check we've loaded our stuff
			assertEquals("load participants", 2, xv._myParticipantVariances.size(), 0);

			// get the first part
			ParticipantVariance pa = xv._myParticipantVariances.get(0);
			assertEquals("got vars for first part", 3, pa.size(), 0);
			XMLVariance xa = pa.itemAt(0);
			assertEquals("got correct variance object", "do it1", xa.getName());
			// now try for the range object itself
			XMLObject op = xa.getObject();
			assertEquals("got correct type", op.getName(), "Value");
			assertTrue("is of attribute type", op instanceof XMLAttribute);

			// //////////////////////////////////////////////////////////
			// first the range object
			// //////////////////////////////////////////////////////////

			// now look into the range itself
			XMLAttribute xmla = (XMLAttribute) op;
			assertTrue("is of range type", xmla.getOperation() instanceof XMLRange);
			XMLRange xr = (XMLRange) xmla.getOperation();

			// check the data settings
			assertEquals("correct min", 1, xr.getMin(), 0);
			assertEquals("correct max", 6, xr.getMax(), 0);
			assertEquals("correct step", 1.5, xr.getStep().doubleValue(), 0);

			// //////////////////////////////////////////////////////////
			// now the choice
			// //////////////////////////////////////////////////////////
			XMLVariance xa2 = pa.itemAt(1);
			assertEquals("got correct variance object", "do it2", xa2.getName());
			// now try for the range object itself
			XMLObject op2 = xa2.getObject();
			assertEquals("got correct type", op2.getName(), "Course");
			assertTrue("is of attribute type", op2 instanceof XMLAttribute);

			// now look into the range itself
			xmla = (XMLAttribute) op2;
			assertTrue("is of choice type", xmla.getOperation() instanceof XMLChoice);
			final XMLChoice xc = (XMLChoice) xmla.getOperation();

			// check the data settings
			assertEquals("got correct num choices", 2, xc.size(), 0);
			assertEquals("first choice correct", "180", xc.get(0));
			assertEquals("first choice correct", "090", xc.get(1));

			// //////////////////////////////////////////////////////////
			// now the xml choice
			// //////////////////////////////////////////////////////////

			xa2 = pa.itemAt(2);
			assertEquals("got correct variance object", "do it3", xa2.getName());
			assertEquals("got correct variance object", "Status/Location/shortLocation", xa2.getId());

			// now try for the range object itself
			op2 = xa2.getObject();
			assertTrue("is of node type", op2 instanceof XMLNode);

			// now look into the range itself
			final XMLNode xmln = (XMLNode) op2;
			assertTrue("is of choice type", xmln.getOperation() instanceof XMLSnippets);
			final XMLSnippets xxc = xmln.getOperation();

			// check the data settings
			assertEquals("got correct num choices", 4, xxc.size(), 0);

			// test the first
			final Node first = xxc.get(0);
			// have a look at it
			assertEquals("first is of correct type", "shortLocation", first.getNodeName());

			final Node second = xxc.get(3);
			// have a look at it
			assertEquals("second is of correct type", "longLocation", second.getNodeName());

			// //////////////////////////////////////////////////////////
			// move onto the second participant
			// //////////////////////////////////////////////////////////
			// get the first part
			pa = xv._myParticipantVariances.get(1);
			assertEquals("got vars for seond part", 1, pa.size(), 0);
			xa = pa.itemAt(0);
			assertEquals("got correct variance object", "do it4", xa.getName());
			// now try for the range object itself
			op = xa.getObject();
			assertEquals("got correct type", op.getName(), "Value");
			assertTrue("is of attribute type", op instanceof XMLAttribute);

			// //////////////////////////////////////////////////////////
			// first the range object
			// //////////////////////////////////////////////////////////

			// now look into the range itself
			xmla = (XMLAttribute) op;
			assertTrue("is of range type", xmla.getOperation() instanceof XMLRange);
			xr = (XMLRange) xmla.getOperation();

			// check the data settings
			assertEquals("correct min", 10, xr.getMin(), 0);
			assertEquals("correct max", 26, xr.getMax(), 0);
			assertEquals("correct step", 2, xr.getStep().doubleValue(), 0);
		}

		// TODO FIX-TEST
		public final void NtestPerformFailedVariances1() {
			final MultiParticipantGenerator genny = new MultiParticipantGenerator();

			// get the file to read in
			String code_root = System.getProperty("CODE_ROOT");
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

			InputStream dataStream = null;
			InputStream varianceStream = null;
			try {
				dataStream = new java.io.FileInputStream(docPath + "test_variance_scenario.xml");
				varianceStream = new java.io.FileInputStream(docPath + "test_variance_FailsToMatch.xml");
			} catch (final FileNotFoundException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			// check it worked
			assertNotNull("managed to load data file", dataStream);
			assertNotNull("managed to load variance file", varianceStream);

			Document thisDocument = null;
			try {
				thisDocument = ScenarioGenerator.readDocumentFrom(dataStream);
			} catch (final SAXException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			assertNotNull("we loaded document ok", thisDocument);

			try {
				genny.loadVariances(ScenarioGenerator.readDocumentFrom(varianceStream));
			} catch (final XPathExpressionException e1) {
				e1.printStackTrace();
			} catch (final SAXParseException e1) {
				e1.printStackTrace();
			} catch (final SAXException e1) {
				e1.printStackTrace();
			}

			assertEquals("loaded vars", 2, genny._myParticipantVariances.size(), 0);

			// now fill the genny
			genny.setDocument(thisDocument);

			ParticipantVariance pa = genny._myParticipantVariances.get(0);

			Exception ex = null;

			try {
				pa.populate(thisDocument);

				// now do it for the second one
				pa = genny._myParticipantVariances.get(1);
				pa.populate(thisDocument);
			} catch (final XMLVariance.IllegalExpressionException e) {
				ex = e;
			} catch (final XMLVariance.MatchingException e) {
				ex = e;
			}

			// check that an exception got thrown
			assertNotNull("check no exceptions thrown", ex);
			assertTrue("it failed to match anything", ex instanceof XMLVariance.MatchingException);
		}

		// TODO FIX-TEST
		public final void NtestPerformFailedVariances2() {
			final MultiParticipantGenerator genny = new MultiParticipantGenerator();

			// get the file to read in
			String code_root = System.getProperty("CODE_ROOT");
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

			InputStream dataStream = null;
			InputStream varianceStream = null;
			try {
				dataStream = new java.io.FileInputStream(docPath + "test_variance_scenario.xml");
				varianceStream = new java.io.FileInputStream(docPath + "test_variance_IllegalExpression.xml");
			} catch (final FileNotFoundException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			// check it worked
			assertNotNull("managed to load data file", dataStream);
			assertNotNull("managed to load variance file", varianceStream);

			Document thisDocument = null;
			try {
				thisDocument = ScenarioGenerator.readDocumentFrom(dataStream);
			} catch (final SAXException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			assertNotNull("we loaded document ok", thisDocument);

			try {
				genny.loadVariances(ScenarioGenerator.readDocumentFrom((varianceStream)));
			} catch (final SAXException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}

			assertEquals("loaded vars", 2, genny._myParticipantVariances.size(), 0);

			// now fill the genny
			genny.setDocument(thisDocument);

			ParticipantVariance pa = genny._myParticipantVariances.get(0);

			Exception ex = null;

			try {
				pa.populate(thisDocument);

				// now do it for the second one
				pa = genny._myParticipantVariances.get(1);
				pa.populate(thisDocument);
			} catch (final XMLVariance.IllegalExpressionException e) {
				ex = e;
			} catch (final XMLVariance.MatchingException e) {
				ex = e;
			}

			// check that an exception got thrown
			assertNotNull("check no exceptions thrown", ex);
			assertTrue("it failed to match anything", ex instanceof XMLVariance.IllegalExpressionException);
		}

		// TODO FIX-TEST
		public final void NtestPerformVariances() throws XPathExpressionException {
			final MultiParticipantGenerator genny = new MultiParticipantGenerator();

			// get the file to read in
			String code_root = System.getProperty("CODE_ROOT");
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

			InputStream dataStream = null;
			InputStream varianceStream = null;
			try {
				dataStream = new java.io.FileInputStream(docPath + "test_variance_scenario.xml");
				varianceStream = new java.io.FileInputStream(docPath + "test_variance1.xml");
			} catch (final FileNotFoundException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			// check it worked
			assertNotNull("managed to load data file", dataStream);
			assertNotNull("managed to load variance file", varianceStream);

			Document thisDocument = null;
			try {
				thisDocument = ScenarioGenerator.readDocumentFrom(dataStream);
			} catch (final SAXException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			assertNotNull("we loaded document ok", thisDocument);

			try {
				genny.loadVariances(ScenarioGenerator.readDocumentFrom(varianceStream));
			} catch (final SAXException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}

			assertEquals("loaded vars", 2, genny._myParticipantVariances.size(), 0);

			// now fill the genny
			genny.setDocument(thisDocument);

			ParticipantVariance pa = genny._myParticipantVariances.get(0);

			boolean worked = true;

			try {
				pa.populate(thisDocument);

				// now do it for the second one
				pa = genny._myParticipantVariances.get(1);
				pa.populate(thisDocument);
			} catch (final XMLVariance.IllegalExpressionException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
				worked = false;
			} catch (final XMLVariance.MatchingException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
				worked = false;
			}

			assertTrue("check no exceptions thrown", worked);

			// so, how does it look now?
			// super.outputThisDocument(thisDocument, "After permutations done");

			// hey, let's see how we got on
			try {
				XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='bravo_001']");
				NodeList nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
				Element thisE = (Element) nl.item(0);
				// SupportTesting.outputThis(thisDocument,"have a look at this!");
				assertEquals("correct name", "bravo_001", thisE.getAttribute("Name"));

				// did we create the last one?
				xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='bravo_004']");
				nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
				thisE = (Element) nl.item(0);
				assertEquals("correct name", "bravo_004", thisE.getAttribute("Name"));

				// //////////////////////////////////////////////////////////
				// now the alphas
				// //////////////////////////////////////////////////////////
				xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='alpha_001']");
				nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
				thisE = (Element) nl.item(0);
				assertEquals("correct name", "alpha_001", thisE.getAttribute("Name"));

				xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='alpha_006']");
				nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
				thisE = (Element) nl.item(0);
				assertEquals("correct name", "alpha_006", thisE.getAttribute("Name"));
			} catch (final Exception e) {
				e.printStackTrace(); // To change body of catch statement use Options |
										// File Templates.
			}
		}

	}

	/**
	 * our type
	 */
	public static final String GENERATOR_TYPE = "MultiParticipantGenerator";

	/**
	 * the list of participant variances we manage
	 */
	Vector<ParticipantVariance> _myParticipantVariances = null;

	/**
	 * our document
	 */
	private Document _myDocument;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	/**
	 * constructor, received a stream containing the list of variances we are going
	 * to manage
	 */
	public MultiParticipantGenerator() {
		_myParticipantVariances = new Vector<ParticipantVariance>(0, 1);
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * constructor, received a stream containing the list of variances we are going
	 * to manage
	 *
	 * @throws XPathExpressionException
	 */
	public MultiParticipantGenerator(final Document doc) throws XPathExpressionException {
		this();

		this.loadVariances(doc);
	}

	public final Document createNewRandomisedPermutation()
			throws XMLVariance.IllegalExpressionException, XMLVariance.MatchingException {

		// take a clone of the current document
		final Document newDoc = (Document) _myDocument.cloneNode(true);

		// check we have a document
		if (newDoc != null) {
			// and run through the participants
			final Iterator<ParticipantVariance> it = _myParticipantVariances.iterator();

			while (it.hasNext()) {
				// so, get this participant
				final ParticipantVariance o = it.next();

				// get the participant to insert it's variances
				o.populate(newDoc);
			}
		}

		return newDoc;
	}

	public final Document getRawDocument() {
		return _myDocument;
	}

	/**
	 * read in the list of variances, and collate them into our list
	 *
	 * @throws XPathExpressionException
	 */
	void loadVariances(final Document document) throws XPathExpressionException {
		XPathExpression xpe = null;
		;
		try {
			xpe = NamespaceContextProvider.createPath("//" + GENERATOR_TYPE);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
			return;
		}

		final Object result = xpe.evaluate(document, XPathConstants.NODESET);

		if (result != null) {
			final NodeList theNodes = (NodeList) result;

			final Element el = (Element) theNodes.item(0);

			if (el != null) {
				// build up our list of variances from this document
				final NodeList lis = el.getElementsByTagName("ParticipantVariance");

				final int len = lis.getLength();
				for (int i = 0; i < len; i++) {
					final Element o = (Element) lis.item(i);

					// create this variance
					final ParticipantVariance xv = new ParticipantVariance(o);

					// and store it
					_myParticipantVariances.add(xv);

				}

			}
		}

	}

	/**
	 * set the document we are going to be changing
	 */
	public final void setDocument(final Document rawDoc) {
		_myDocument = rawDoc;
	}

	/**
	 * set the document which we are going to be changing
	 */
	public final void setDocument(final java.io.InputStream istream) {
		Document thisDocument = null;
		try {
			thisDocument = ScenarioGenerator.readDocumentFrom(istream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}

		// and store it
		setDocument(thisDocument);

		// build up our list of variances from this document
	}

}
