package org.immregistries.mqe.hub.detection.documentation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.validator.detection.DetectionDocumentation;
import org.immregistries.mqe.validator.detection.DetectionDocumentation.DetectionDocumentationPayload;
import org.immregistries.mqe.vxu.VxuField;
import org.immregistries.mqe.vxu.VxuObject;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.H2;
import com.hp.gagawa.java.elements.H3;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Style;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;

@Service
public class PDFDetectionDocumentationSerializer implements DetectionDocumentationSerializer<byte[]> {

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
	
		createSection(VxuObject.GENERAL, documentation, 1, body);
		createSection(VxuObject.MESSAGE_HEADER, documentation, 2, body);
		createSection(VxuObject.PATIENT, documentation, 3, body);
		createSection(VxuObject.NEXT_OF_KIN, documentation, 4, body);
		createSection(VxuObject.VACCINATION, documentation, 5, body);
		createSection(VxuObject.OBSERVATION, documentation, 6, body);
		head.appendChild(s);
		html.appendChild(head, body);
		
		return transformMethodTwo(html.write());
	}
	
	public void createSection(VxuObject object, DetectionDocumentation documentation, int i, Body body){
		H3 title = new H3().appendChild(new Text(i+". "+object.getDescription()));
		Table table = new Table();
		table.setCSSClass("table-bordered");
		table.appendChild(tableHeader());
		Tbody tbody =new Tbody();
		for(Tr tr : rows(documentation, object.getDescription(), documentation.getDetections().get(object.getDescription()))){
			tbody.appendChild(tr);
		}
		table.appendChild(tbody);
		body.appendChild(title);
		body.appendChild(table);
	}
	
	public byte[] transformMethodOne(String html) throws DocumentException, IOException{
		ByteArrayOutputStream _os = new ByteArrayOutputStream();
		Document document = new Document();
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, _os);
        // step 3
        document.open();
        // step 4
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,new StringReader(html));
        // step 5
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
	
//	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IOException, DocumentException, com.lowagie.text.DocumentException {
//		DetectionDocumentation documentation = DetectionDocumentation.getDetectionDocumentation();
//		HTMLDetectionDocumentationSerializer serializer = new HTMLDetectionDocumentationSerializer();
//		FileUtils.writeByteArrayToFile(new File("/Users/hnt5/Desktop/doc.pdf"), serializer.serialize(documentation));
//	}
	
	private List<Tr> rows(DetectionDocumentation documentation, String o, Map<String, Map<String, List<DetectionDocumentationPayload>>> detections){
		List<Tr> rows = new ArrayList<>();
		int i = 0;
		for(String field : detections.keySet()){
			i++;
			Tr row = new Tr();
			row.setCSSClass("limit-1");
			int fspan = documentation.sizeForField(o, field);
			Td first = new Td().appendChild(new Text(field)).setRowspan(fspan > 1 ? fspan+"" : null);
			first.setCSSClass("field-"+(i%2));
			boolean fieldAggregate = true;
			row.appendChild(first);
			for(String severity : detections.get(field).keySet()){
				Tr _row;
				if(fieldAggregate){
					_row = row;
					fieldAggregate = false;
				}
				else {
					_row = new Tr();
					_row.setCSSClass("limit-2");
				}
				int sspan = documentation.sizeForSeverity(o, field, severity);
				Td second = new Td().appendChild(new Text(severity)).setRowspan(sspan > 1 ? sspan+"" : null).setCSSClass(severity+" center");
				
				_row.appendChild(second);
				boolean severityAggregate = true;
				for(DetectionDocumentationPayload payload : detections.get(field).get(severity)){
					Tr __row;
					if(severityAggregate){
						__row = _row;
						severityAggregate = false;
					}
					else {
						__row = new Tr();
						
					}
					__row.appendChild(
							new Td().appendChild(new Text(payload.getDetection().getMqeMqeCode())).setStyle("width : 50px;"),
							new Td().appendChild(new Text(payload.getDetection().getDisplayText()))
									.appendChild(new Span().appendChild(new Text(payload.getDescription()))),
							new Td().appendChild(new Text(payload.isActive() ? "Active" : "Not Active")));
					rows.add(__row);
				}
			}
		}
		return rows;
	}
	
	private Thead tableHeader(){
		return new Thead().appendChild(
				new Th().appendChild(new Text("Target")),
				new Th().appendChild(new Text("Severity")),
				new Th().appendChild(new Text("Code")),
				new Th().appendChild(new Text("Details")),
				new Th().appendChild(new Text("Status")));
	}

}
