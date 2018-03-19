package org.immregistries.dqa.hub.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immregistries.dqa.validator.report.codes.CollectionBucket;

public class CodeCollectionMap {
	private Map<String,List<CollectionBucket>> map = new HashMap<>();

	public Map<String, List<CollectionBucket>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<CollectionBucket>> map) {
		this.map = map;
	}
}
