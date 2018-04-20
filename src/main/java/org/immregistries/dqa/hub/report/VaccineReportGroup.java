package org.immregistries.dqa.hub.report;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VaccineReportGroup {
	
	  /* These age ranges are inclusive of the lower bound, and exclusive of upper bound*/
	  //HepB("Hep B", "132", "146", "21", "94"),
	  ADENOVIRUS("Adenovirus", "143", "54", "55", "82"),
	  DTAP("DTaP", "28", "20", "106", "107", "146", "170", "110", "50", "120", "130", "132", "01", "22", "102"),
	  HEPA("Hep A", "84", "31", "85", "104"),
	  HEPB("Hep B", "146", "110", "132", "102", "189", "08", "42", "43", "44", "45", "58"),
	  HERPES("Herpes", "60"),
	  HIB("Hib", "146", "170", "50", "120", "132", "22", "102", "47", "46", "49", "48", "17", "51"),
	  HIV("HIV", "61"),
	  HPV("HPV", "118", "62", "137", "165"),
	  IMMUNEGLOBULIN("Immune Globulin", "86", "14", "87"),
	  INFLUENZA("Influenza", "160", "151", "123", "135", "153", "171", "186", "158", "150", "161", "166", "111", "149", "155", "185", "141", "140", "144", "15", "168", "88", "16"),
	  JAPENESEENCEPHALITIS("Japenese Encephalitis", "134", "39", "129"),
	  JUNINVIRUS("Junin Virus", "63"),
	  LEPROSY("Leprosy", "65"),
	  LIESHMAIASIS("Lieshmaiasis", "64"),
	  LYMEDISEASE("Lyme Disease", "66"),
	  MALARIA("Malaria", "67"),
	  MEASLES("Measles", "05"),
	  MELANOMA("Melanoma", "68"),
	  MENING("Mening", "108", "163", "162", "164", "103", "148", "147", "136", "114", "32", "167"),
	  MMR("MMR", "04", "03", "94"),
	  MUMPS("Mumps", "07", "38"),
	  PARAINFLUENZA("Parainfluenza", "69"),
	  PCV("PCV", "177", "133", "100", "152"),
	  PERTUSSIS("Pertussis", "11"),
	  PLAGUE("Plague", "23"),
	  POLIO("Polio", "146", "170", "110", "120", "130", "132", "10", "02", "179", "178", "182", "89"),
	  PPSV23("PPSV23", "33", "109"),
	  QFEVER("Q Fever", "70"),
	  RABIES("Rabies", "175", "176", "40", "18", "90"),
	  RHEUMATICFEVER("Rheumatic Fever", "72"),
	  RHOD("RHo(D)", "159", "157", "156"),
	  RIFTVALLEYFEVER("Rift Valley Fever", "73"),
	  RIG("RIG", "34"),
	  ROTAVIRUS("Rotavirus", "119", "116", "74", "122"),
	  RSV("RSV", "71", "93", "145"),
	  RUBELLA("Rubella", "06", "38"),
	  SMALLPOX("Smallpox", "75", "105", "79"),
	  STAPHYLOCOCCUSBACTERIOLYSATE("Staphylococcus Bacterio Lysate", "76"),
	  TDAP("Tdap", "138", "09", "113", "139", "115"),
	  TETANUS("Tetanus", "180", "35", "142", "112"),
	  TICKBORNEENCEPHALITIS("Tick-Borne Encephalitis", "77"),
	  TIG("TIG", "13"),
	  TUBERCULINSKINTEST("Tuberculin Skin Test", "98", "95", "96", "97"),
	  TULAREMIA("Tularemia", "78"),
	  TYPHOID("Typhoid", "25", "41", "53", "91", "101"),
	  TYPHUS("Typhus", "131"),
	  VARICELLAIMMUNEGLOBIN("Varicella Immune Globin", "36", "117"),
	  VARICELLA("Varicella", "94", "21"),
	  VENEZUELANEQUINEENCEPHALITIS("Venezuelan Equine Encephalitis", "81", "80", "92"),
	  YELLOWFEVER("Yellow Fever", "37", "183", "184"),
	  ZOSTER("Zoster", "121", "187", "188"),
	  UNKNOWN("Unknown");
	
	  String label;
	  String[] cvxList;
	  
	  public String[] getCvxList() {
		return cvxList;
	}

	@JsonValue
	public String getLabel() {
		return label;
	}

	  static Map<String, VaccineReportGroup> map = new HashMap<>();
	  static {
		  for (VaccineReportGroup vrg : VaccineReportGroup.values()) {
			  for (String cvx : vrg.cvxList) {
				  map.put(cvx, vrg);
			  }
		  }
	  }
	  
	  private VaccineReportGroup(String label, String... cvx) {
		this.label = label;
		this.cvxList = cvx;
	  }
	  
	  public static VaccineReportGroup get(String cvx) {
		VaccineReportGroup v = map.get(cvx);
		return v != null ? v : VaccineReportGroup.UNKNOWN;
	  }
}
