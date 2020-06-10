package org.immregistries.mqe.hub.report;

import org.apache.commons.lang3.StringUtils;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;
import org.immregistries.mqe.validator.engine.codes.CodeRepository;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.codes.CodeCollection;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.vxu.VxuField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CodeCollectionService {

    private final CodeRepository codeRepo = CodeRepository.INSTANCE;

    public CodeCollectionMap getEvaluatedCodeFromMetrics(MqeMessageMetrics allDaysMetrics) {
        CodeCollection senderCodes = allDaysMetrics.getCodes();
        Map<String, List<CollectionBucket>> map = new TreeMap<>();
        for (CollectionBucket cb : senderCodes.getCodeCountList()) {
            VxuField field = VxuField.getByName(cb.getTypeCode());
            CodesetType t = field.getCodesetType();
            if (t == null) {
                throw new RuntimeException(
                        "well...  this is embarrassing. there's a field with no type: " + field);
            }
            cb.setSource(field.getHl7Locator());
            cb.setTypeName(t.getDescription());
            Code c = codeRepo.getCodeFromValue(cb.getValue(), t);

            if (c != null) {
                if (c.getCodeStatus() != null && StringUtils.isNotBlank(c.getCodeStatus().getStatus())) {
                    String status = c.getCodeStatus().getStatus();
                    cb.setStatus(status);
                } else {
                    cb.setStatus("Unrecognized");
                }
                String description = c.getLabel();
                cb.setLabel(description);
            } else {
                cb.setStatus("Unrecognized");
            }

            List<CollectionBucket> list = map.get(cb.getTypeName());

            if (list == null) {
                list = new ArrayList<>();
                list.add(cb);
                map.put(cb.getTypeName(), list);
            } else {
                // we want to aggregate, and ignore the attributes, so we have to add them up,
                // since they're separate in the database.
                boolean found = false;
                for (CollectionBucket bucket : list) {
                    if (bucket.getTypeName().equals(cb.getTypeName())
                            && bucket.getValue().equals(cb.getValue())
                            && bucket.getSource().equals(cb.getSource())) {
                        bucket.setCount(bucket.getCount() + cb.getCount());
                        found = true;
                    }
                }

                if (!found) {
                    list.add(cb);

                }
            }

        }

        CodeCollectionMap cm = new CodeCollectionMap();
        cm.setMap(map);
        return cm;
    }
}
