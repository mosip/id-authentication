import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.cbeffutil.entity.BIR;

/**
 * The Class BioApiTest.
 * 
 * @author Sanjay Murali
 */
@Ignore
public class BioApiTest {
	
	CbeffImpl cbeffUtil = new CbeffImpl();
	
	BioApiImpl bioApiImpl = new BioApiImpl();
	
	List<BIR> birDataFromXML;
	
	List<BIR> birDataFromXML2;
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File fXmlFile = new File(classLoader.getResource("applicant_bio_CBEFF.xml").getFile());
		File fXmlFile2 = new File(classLoader.getResource("applicant_bio_CBEFF2.xml").getFile());
		byte[] byteArray = IOUtils.toByteArray(new FileInputStream(fXmlFile.getAbsolutePath()));
		byte[] byteArray2 = IOUtils.toByteArray(new FileInputStream(fXmlFile2.getAbsolutePath()));
		birDataFromXML = cbeffUtil.convertBIRTypeToBIR(cbeffUtil.getBIRDataFromXML(byteArray));
		birDataFromXML2 = cbeffUtil.convertBIRTypeToBIR(cbeffUtil.getBIRDataFromXML(byteArray2));
	}
	
	@Test
	public void arrayEqualsTest() {
		int count = 0;
		for (BIR BIR : birDataFromXML) {
			for (BIR BIR2 : birDataFromXML2) {
				if (Arrays.equals(BIR.getBdb(), BIR2.getBdb())) {
					count++;
				}
			}
		}
		assertTrue(count > 0);
	}
	
	@Test
	public void checkQualityTest() {
		BIR BIR = birDataFromXML.get(0);
		QualityScore checkQuality = bioApiImpl.checkQuality(BIR, null);
		assertEquals(90, checkQuality.getInternalScore());
	}
	
	@Test
	public void matchTest() {
		BIR BIR = birDataFromXML.get(0);
		Score[] match = bioApiImpl.match(BIR, birDataFromXML.stream().toArray(BIR[]::new), null);
		Score highestScore = Arrays.stream(match).max(Comparator.comparing(Score::getInternalScore)).get();
		assertEquals(90,highestScore.getInternalScore());
	}
	
	@Test
	public void compositeMatchTest() {
		CompositeScore compositeMatch = bioApiImpl.compositeMatch(birDataFromXML.stream().toArray(BIR[]::new), birDataFromXML2.stream().toArray(BIR[]::new), null);
		for(Score score : compositeMatch.getIndividualScores()) {
			assertEquals(90, score.getInternalScore());
		}
		assertEquals(90, compositeMatch.getInternalScore());
	}
	
	@Test
	public void extractTemplateTest() {
		BIR BIR = birDataFromXML.get(0);
		assertEquals(bioApiImpl.extractTemplate(BIR, null), BIR);
	}
	
	@Test
	public void segmentTest() {
		BIR BIR = birDataFromXML.get(0);
		BIR[] bir = new BIR[1];
		bir[0]= BIR;
		BIR[] segment = bioApiImpl.segment(BIR, null);
		assertTrue(segment.length==1);
		assertEquals(segment[0], bir[0]);
	}
}
