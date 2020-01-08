import { Component, OnInit, Input } from '@angular/core';
import { ProviderReport } from '../dashboard/report/model';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  report: ProviderReport;

  @Input()
  set data(data: ProviderReport) {
    this.report = data;
  }

  // report: ProviderReport = {
  //   provider: 'Unspecified',
  //   startDate: 1496116800000,
  //   endDate: 1496116800000,
  //   numberOfMessage: 36731,
  //   numberOfErrors: 19,
  //   errors: [
  //     {
  //       severity: 'ERROR',
  //       reportedMessage: 'Vaccination admin date is after patient death date',
  //       hl7LocationList: [
  //         {
  //           line: 0,
  //           segmentId: 'RXA',
  //           segmentSequence: 1,
  //           fieldPosition: 3,
  //           fieldRepetition: 1,
  //           componentNumber: 1,
  //           subComponentNumber: 1,
  //           baseReference: 'RXA-3.1.1',
  //           fieldLoc: 'RXA-3',
  //           abbreviated: 'RXA[1]-3'
  //         }
  //       ],
  //       hl7ErrorCode: {
  //         identifier: '0',
  //         text: '',
  //         nameOfCodingSystem: '',
  //         alternateIdentifier: '',
  //         alternateText: '',
  //         nameOfAlternateCodingSystem: ''
  //       },
  //       applicationErrorCode: {
  //         identifier: '1',
  //         text: 'Illogical Date error',
  //         nameOfCodingSystem: 'HL70533',
  //         alternateIdentifier: 'MQE0253',
  //         alternateText: 'Vaccination admin date is after patient death date',
  //         nameOfAlternateCodingSystem: 'L'
  //       },
  //       exampleMessage: 'MSH|^~\\&|||||20170530103220-0600||VXU^V04^VXU_V04|3SUt-C.01.51.4f|P|2.5.1|\rPID|||3SUt-C.01.51^^^AIRA-TEST^MR||Green^Bennet^Yashodhar^^^^L|Colfax^Melissa|20130522|M||2106-3^White^HL70005|239 Erie Ave^^GR^MI^49519^USA^P||^PRN^PH^^^616^8912503|||||||||2186-5^not Hispanic or Latino^HL70005|||||||20130522|Y|\rPD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20170530|20170530|\rNK1|1|Green^Melissa^^^^^L|MTH^Mother^HL70063|239 Erie Ave^^GR^MI^49519^USA^P|^PRN^PH^^^616^8912503|\rORC|RE||G95I468.3^AIRA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L|\rRXA|0|1|20170530||94^MMRV^CVX|0.5|mL^milliliters^UCUM||00^Administered^NIP001||||||S4121RG||MSD^Merck and Co^MVX||||A|\rRXR|SC^^HL70162|RA^^HL70163|\rOBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V01^Not VFC eligible^HL70064||||||F|||20170530|||VXC40^Eligibility captured at the immunization level^CDCPHINVS|\rOBX|2|CE|30956-7^Vaccine Type^LN|2|94^MMRV^CVX||||||F|\rOBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20100521||||||F|\rOBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20170530||||||F|',
  //       count: 24,
  //       diagnosticMessage: '',
  //       source: 'MQE',
  //       whyToFix: 'The observation value type is not recognized. Please contact your software vendor and request that they review the observation value types that are being sent to ensure that the latest and best types are being sent.',
  //       howToFix: 'The observation value type allows the transmission of additional information that is helpful to understand an immunization event or the patient medical history. Sending the wrong observation value type might cause the IIS to not recognize or understand medically relevant information.',
  //     }
  //   ],
  //   codeIssues: [
  //     {
  //       typeCode: 'MESSAGE_ACCEPT_ACK_TYPE',
  //       typeName: 'Acknowledgement Type',
  //       source: 'MSH-15',
  //       attribute: '',
  //       value: 'SOMETIMES',
  //       count: 12,
  //       exampleMessage: 'MSH|^~\\&|||||20170530103220-0600||VXU^V04^VXU_V04|3SUt-C.02.69.4V|P|2.5.1|||SOMETIMES|\rPID|||3SUt-C.02.69^^^AIRA-TEST^MR||Greenup^Kristy^Tamatha^^^^L|Grant^Iona|20130528|F||2106-3^White^HL70005|103 Iron Ave^^Winn^MI^48896^USA^P||^PRN^PH^^^989^2379242|||||||||2186-5^not Hispanic or Latino^HL70005|\rPD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20170530|20170530|\rNK1|1|Greenup^Iona^^^^^L|MTH^Mother^HL70063|103 Iron Ave^^Winn^MI^48896^USA^P|^PRN^PH^^^989^2379242|\rORC|RE||U38P486.3^AIRA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L|\rRXA|0|1|20170530||03^MMR^CVX|0.5|mL^milliliters^UCUM||00^Administered^NIP001||||||Z7029FR||MSD^Merck and Co^MVX||||A|\rRXR|SC^^HL70162|LA^^HL70163|\rOBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V03^VFC eligible - Uninsured^HL70064||||||F|||20170530|||VXC40^Eligibility captured at the immunization level^CDCPHINVS|\rOBX|2|CE|30956-7^Vaccine Type^LN|2|03^MMR^CVX||||||F|\rOBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20120420||||||F|\rOBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20170530||||||F|',
  //       status: 'Unrecognized',
  //       label: null
  //     }
  //   ],
  //   vaccinationCodes: [
  //     {
  //       typeCode: 'VACCINATION_CVX_CODE',
  //       typeName: 'Vaccination CVX Code',
  //       source: 'RXA-5',
  //       attribute: '00',
  //       value: '94',
  //       count: 24,
  //       exampleMessage: null,
  //       status: 'Valid',
  //       label: 'MMRV'
  //     }
  //   ]
  // };


  constructor() { }

  ngOnInit() {

  }

}
