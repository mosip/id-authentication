import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;

/**
 * The Class BioApiTest.
 * 
 * @author Sanjay Murali
 */
public class BioApiTest {
	
	CbeffImpl cbeffUtil = new CbeffImpl();
	
	BioApiImpl bioApiImpl = new BioApiImpl();
	
	List<BIRType> birDataFromXML;
	
	List<BIRType> birDataFromXML2;
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File fXmlFile = new File(classLoader.getResource("applicant_bio_CBEFF.xml").getFile());
		File fXmlFile2 = new File(classLoader.getResource("applicant_bio_CBEFF2.xml").getFile());
		byte[] byteArray = IOUtils.toByteArray(new FileInputStream(fXmlFile.getAbsolutePath()));
		byte[] byteArray2 = IOUtils.toByteArray(new FileInputStream(fXmlFile2.getAbsolutePath()));
		birDataFromXML = cbeffUtil.getBIRDataFromXML(byteArray);
		birDataFromXML2 = cbeffUtil.getBIRDataFromXML(byteArray2);
	}
	
	@Test
	public void arrayEqualsTest() {
		int count = 0;
		for (BIRType birType : birDataFromXML) {
			for (BIRType birType2 : birDataFromXML2) {
				if (Arrays.equals(birType.getBDB(), birType2.getBDB())) {
					count++;
				}
			}
		}
		assertTrue(count > 0);
	}
	
	@Test
	public void checkQualityTest() {
		BIRType birType = birDataFromXML.get(0);
		QualityScore checkQuality = bioApiImpl.checkQuality(birType, null);
		assertEquals(90, checkQuality.getInternalScore());
	}
	
	@Test
	public void matchTest() {
		BIRType birType = birDataFromXML.get(0);
		Score[] match = bioApiImpl.match(birType, birDataFromXML.stream().toArray(BIRType[]::new), null);
		Score highestScore = Arrays.stream(match).max(Comparator.comparing(Score::getInternalScore)).get();
		assertEquals(90,highestScore.getInternalScore());
	}
	
	@Test
	public void compositeMatchTest() {
		CompositeScore compositeMatch = bioApiImpl.compositeMatch(birDataFromXML.stream().toArray(BIRType[]::new), birDataFromXML2.stream().toArray(BIRType[]::new), null);
		for(Score score : compositeMatch.getIndividualScores()) {
			assertEquals(90, score.getInternalScore());
		}
		assertEquals(90, compositeMatch.getInternalScore());
	}
	
	@Test
	public void extractTemplateTest() {
		BIRType birType = birDataFromXML.get(0);
		assertEquals(bioApiImpl.extractTemplate(birType, null), birType);
	}
	
	@Test
	public void segmentTest() {
		BIRType birType = birDataFromXML.get(0);
		BIRType[] bir = new BIRType[1];
		bir[0]= birType;
		BIRType[] segment = bioApiImpl.segment(birType, null);
		assertTrue(segment.length==1);
		assertEquals(segment[0], bir[0]);
	}
}
