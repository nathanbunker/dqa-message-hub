package org.immregistries.mqe.hub.code;

import java.util.ArrayList;
import java.util.List;
import org.immregistries.codebase.client.CodeMapBuilder;
import org.immregistries.codebase.client.generated.Codeset;
import org.immregistries.codebase.client.reference.CodesetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/codes")
public class CodebaseController {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      CodebaseController.class);

  @RequestMapping(value = "/{type}", method = RequestMethod.GET)
  public Codeset getNdcList(@PathVariable("type") String codesetType) {
    LOGGER.info("NdcController getNdcList");
    List<Codeset> list = CodeMapBuilder.INSTANCE.getDefaultCodeMap().getCodesets();
    for (Codeset c : list) {
      if (codesetType.equals(c.getType())) {
        return c;
      }
    }
    return null;
  }


  @RequestMapping(value = "", method = RequestMethod.GET)
  public List<MqeCodeType> getCodeList() {
    LOGGER.info("NdcController getNdcList");
    List<MqeCodeType> t = new ArrayList<>();
    for (CodesetType ct : CodesetType.values()) {
        t.add(new MqeCodeType(ct));
    }
    return t;
  }

}
