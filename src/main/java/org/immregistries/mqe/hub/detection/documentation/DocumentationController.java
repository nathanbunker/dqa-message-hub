package org.immregistries.mqe.hub.detection.documentation;

import java.io.IOException;

import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;

@RequestMapping(value = "/documentation")
@RestController
public class DocumentationController {

	@Autowired
	PDFDetectionDocumentationSerializer pdfSerializer;
	
	@RequestMapping(value = "pdf", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getDocumentationPDF() throws NoSuchFieldException, SecurityException, IOException, DocumentException, com.lowagie.text.DocumentException{
		DetectionDocumentation documentation = DetectionDocumentation.getDetectionDocumentation();
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = "mqe_detections_documentation.pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    ResponseEntity<byte[]> response = new ResponseEntity<>(this.pdfSerializer.serialize(documentation), headers, HttpStatus.OK);
	    return response;
	}
	
	@RequestMapping(value = "json", method = RequestMethod.GET)
	public ResponseEntity<DetectionDocumentation> getDocumentationJSON() throws NoSuchFieldException, SecurityException {
	    ResponseEntity<DetectionDocumentation> response = new ResponseEntity<>(DetectionDocumentation.getDetectionDocumentation(), HttpStatus.OK);
	    return response;
	}
}
