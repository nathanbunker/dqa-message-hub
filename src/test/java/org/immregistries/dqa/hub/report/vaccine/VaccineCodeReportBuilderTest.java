package org.immregistries.dqa.hub.report.vaccine;

import static org.junit.Assert.*;

import org.junit.Test;

public class VaccineCodeReportBuilderTest {

	@Test
	public void test() {
		VaccineCodeReportBuilder b = VaccineCodeReportBuilder.INSTANCE;
		assertNotNull(b.getDefaultVaccineCodesReportType());
	}

}
