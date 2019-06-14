package org.immregistries.mqe.hub.detection.documentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hub.detection.documentation.DocumentationTableRow.Details;
import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.immregistries.mqe.validator.detection.DetectionDocumentation.DetectionDocumentationPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;

@RequestMapping(value = "/api/documentation")
@RestController
public class DocumentationController {

	@Autowired
	PDFTableDetectionDocumentationSerializer pdfTableSerializer;
	
	@Autowired
	PDFDocumentDetectionDocumentationSerializer pdfDocumentSerializer;

	@Autowired
	DetectionDocumentation documentation;
	
	@RequestMapping(value = "pdf", method = RequestMethod.GET, params = { "type" })
	public ResponseEntity<byte[]> getDocumentationPDF(@RequestParam("type") String type) throws NoSuchFieldException, SecurityException, IOException, DocumentException, com.lowagie.text.DocumentException{
		// Configure Response Metadata
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = "mqe_detections_documentation.pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

	    // Is the export type defined ?
	    boolean isDefType = type != null && !type.isEmpty() && type.toLowerCase().equals("document") || type.toLowerCase().equals("table");

		byte[] bytes;

		// Serialize the documentation
	    if(isDefType && type.toLowerCase().equals("table")) {
	    	bytes = this.pdfTableSerializer.serialize(documentation);
		} else {
	    	bytes = this.pdfDocumentSerializer.serialize(documentation);
		}

	    // Create HTTP Response
	    ResponseEntity<byte[]> response = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	    return response;
	}
	
	@RequestMapping(value = "json", method = RequestMethod.GET)
	public ResponseEntity<DetectionDocumentation> getDocumentationJSON() throws NoSuchFieldException, SecurityException {
	    ResponseEntity<DetectionDocumentation> response = new ResponseEntity<>(this.documentation, HttpStatus.OK);
	    return response;
	}
	
	@RequestMapping(value = "table", method = RequestMethod.GET)
	public ResponseEntity<List<DocumentationTableRow>> getDocumentationTable() throws NoSuchFieldException, SecurityException { List<DocumentationTableRow> doc = new ArrayList<>();
		
		Map<String, Map<String, Map<String, List<DetectionDocumentationPayload>>>> detections = documentation.getDetections();
		
		for(String target: detections.keySet()) {	 
			Map<String, Map<String, List<DetectionDocumentationPayload>>> targetV = detections.get(target);
			for(String field: targetV.keySet()) {	 
				Map<String, List<DetectionDocumentationPayload>> severityV = targetV.get(field);
				for(String severity: severityV.keySet()) {	 
					List<DetectionDocumentationPayload> pV = severityV.get(severity);
					for(DetectionDocumentationPayload payload: pV) {
						List<Details> details = null;
						if(payload.getImplementationDetailsPerRule() != null && payload.getImplementationDetailsPerRule().size() > 0) {
							details = new ArrayList<>();
							for(Entry<String, String> entries: payload.getImplementationDetailsPerRule().entrySet()) {
								details.add(new Details(entries.getKey(), entries.getValue()));
							}
						}
						doc.add(new DocumentationTableRow(payload.getDetection().getMqeMqeCode(), payload.getDetection().getDisplayText(), payload.isActive(), field, target, SeverityLevel.findByCode(severity).getLabel(), details));
					}
				}
			}
		}
	    ResponseEntity<List<DocumentationTableRow>> response = new ResponseEntity<>(doc, HttpStatus.OK);
	    return response;
	}
}
