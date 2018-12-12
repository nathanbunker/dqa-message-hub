package org.immregistries.mqe.hub.detection.documentation;

import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Br;
import com.hp.gagawa.java.elements.H2;
import com.hp.gagawa.java.elements.H3;
import com.hp.gagawa.java.elements.H4;
import com.hp.gagawa.java.elements.H5;
import com.hp.gagawa.java.elements.H6;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.P;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Strong;
import com.hp.gagawa.java.elements.Style;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Div;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.immregistries.mqe.validator.detection.DetectionDocumentation.DetectionDocumentationPayload;
import org.immregistries.mqe.vxu.VxuObject;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PDFDocumentDetectionDocumentationSerializer implements DetectionDocumentationSerializer<byte[]> {

	@Override
	public byte[] serialize(DetectionDocumentation documentation) throws IOException, DocumentException, com.lowagie.text.DocumentException {
		Html html = new Html();
		Body body = new Body();
		Head head = new Head();
		Style s = new Style("");
		s.appendChild(new Text(".field-0{ background-color : lightgrey; text-align : center;}"));
		s.appendChild(new Text(".field-1{ background-color : white; text-align : center;}"));
		s.appendChild(new Text(".limit-1{ border-top : 5px solid black !important;}"));
		s.appendChild(new Text(".limit-2{ border-top : 3px solid black !important;}"));
		s.appendChild(new Text(".center{text-align : center;}"));
	    s.appendChild(new Text("body{ font-family: Arial;}"));
	    s.appendChild(new Text("table {width : 100%; border-collapse: collapse;}"));
	    s.appendChild(new Text(".A {background-color : #c7e8c7;}"));
	    s.appendChild(new Text(".W {background-color : #fdefd7;}"));
	    s.appendChild(new Text(".I {background-color : #dadaff;}"));
	    s.appendChild(new Text(".E {background-color : #f9d4d4;}"));
	    s.appendChild(new Text(".table-bordered {border: 1px solid #000000;}.table-bordered th,.table-bordered td {border: 1px solid #000000; padding : 2px;}.table-bordered thead th,.table-bordered thead td { border-bottom-width: 2px;}"));
		H2 mainTilte = new H2().appendText("MQE Detections Documentation");
		body.appendChild(mainTilte);
		Map<String, Map<String, Map<String, List<DetectionDocumentationPayload>>>> detections = documentation.getDetections();
		
		createSection(VxuObject.GENERAL, detections.get(VxuObject.GENERAL.getDescription()), 1, body);
		createSection(VxuObject.MESSAGE_HEADER, detections.get(VxuObject.MESSAGE_HEADER.getDescription()), 2, body);
		createSection(VxuObject.PATIENT, detections.get(VxuObject.PATIENT.getDescription()), 3, body);
		createSection(VxuObject.NEXT_OF_KIN, detections.get(VxuObject.NEXT_OF_KIN.getDescription()), 4, body);
		createSection(VxuObject.VACCINATION, detections.get(VxuObject.VACCINATION.getDescription()), 5, body);
		createSection(VxuObject.OBSERVATION, detections.get(VxuObject.OBSERVATION.getDescription()), 6, body);
		head.appendChild(s);
		html.appendChild(head, body);
		
		return transformMethodTwo(html.write());
	}
	
	public void createSection(VxuObject object, Map<String, Map<String, List<DetectionDocumentationPayload>>> detections, int i, Body body){
		H3 title = new H3().appendChild(new Text(i+". "+object.getDescription()));
		body.appendChild(title);
		if(detections == null || detections.size() == 0) {
			body.appendChild(new Text("No detection under this category."));
		} else {
			int j = 1;
			for(Entry<String, Map<String, List<DetectionDocumentationPayload>>> entry: detections.entrySet()) {
				this.target(entry.getKey(), entry.getValue(), i, j++, body);
			}
		}
	}
	
	public void target(String target, Map<String, List<DetectionDocumentationPayload>> detections, int i, int j, Body body){
		H4 title = new H4().appendChild(new Text(i+"."+j+". "+target));
		body.appendChild(title);
		if(detections == null || detections.size() == 0) {
			body.appendChild(new Text("No detection under this category."));
		} else {
			int k = 1;
			for(Entry<String, List<DetectionDocumentationPayload>> entry: detections.entrySet()) {
				this.severity(entry.getKey(), entry.getValue(), i, j, k++, body);
			}
		}
	}
	
	public void severity(String severity, List<DetectionDocumentationPayload> payloads, int i, int j, int k, Body body){
		H5 title = new H5().setCSSClass(severity).appendChild(new Text(i+"."+j+"."+k+". "+SeverityLevel.findByCode(severity).getLabel()));
		body.appendChild(title);
		if(payloads == null || payloads.size() == 0) {
			body.appendChild(new Text("No detection under this category."));
		} else {
			for(DetectionDocumentationPayload entry: payloads) {
				this.entry(entry, body);
				body.appendChild(new P());
			}
		}
	}
	
	public byte[] transformMethodOne(String html) throws DocumentException, IOException{
		ByteArrayOutputStream _os = new ByteArrayOutputStream();
		Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, _os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,new StringReader(html));
        document.close();    
		_os.close();
		return _os.toByteArray();
	}
	
	public byte[] transformMethodTwo(String html) throws com.lowagie.text.DocumentException, IOException{
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString( html );
	
		renderer.layout();
		ByteArrayOutputStream _os = new ByteArrayOutputStream();
		renderer.createPDF( _os );
		_os.close();
		return _os.toByteArray();
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IOException, DocumentException, com.lowagie.text.DocumentException {
		DetectionDocumentation documentation = DetectionDocumentation.getDetectionDocumentation();
		PDFDocumentDetectionDocumentationSerializer serializer = new PDFDocumentDetectionDocumentationSerializer();
		FileUtils.writeByteArrayToFile(new File("/Users/hnt5/Desktop/doc.pdf"), serializer.serialize(documentation));
	}
	
	private void entry(DetectionDocumentationPayload payload, Body body){
		Table implDetails = new Table().setCSSClass("table table-bordered");
		if(payload.getDetection().getDisplayText() != null && !payload.getDetection().getDisplayText().isEmpty()) {
			implDetails.appendChild(new Tr().setStyle("background-color: #d4d4d4;")
									.appendChild(new Th().setColspan("2")
												.appendChild(new Text("Description"))),
									new Tr()
									.appendChild(new Td().setColspan("2")
												.appendChild(new Span().setStyle("display: block;").appendChild(new Strong().appendChild(new Text(payload.getCode())))
														.appendChild(new Text(" "+payload.getDetection().getDisplayText())))));
		}
		
		if(payload.getDescription() != null && !payload.getDescription().isEmpty()) {
			implDetails.appendChild(new Tr().setStyle("background-color: #d4d4d4;")
									.appendChild(new Th().setColspan("2")
												.appendChild(new Text("Additionnal Documentation"))),
									new Tr()
									.appendChild(new Td().setColspan("2")
												.appendChild(new Text(payload.getDescription()))));
		}
		
		if(payload.getImplementationDetailsPerRule() != null) {
		
			implDetails.appendChild(
														new Tr().setStyle("background-color: #d4d4d4;")
																.appendChild(new Th()
																				.appendChild(new Text("Rule Name")))
																.appendChild(new Th()
																				.appendChild(new Text("Details"))));
			for(Entry<String, String> details: payload.getImplementationDetailsPerRule().entrySet()) {
				implDetails.appendChild(new Tr()
						.appendChild(new Td().appendChild(new Text(details.getKey())))
						.appendChild(new Td().appendChild(new Text(details.getValue()))));
			}
			
		}
		body.appendChild(implDetails);
	}
}
