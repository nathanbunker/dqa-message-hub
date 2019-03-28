package org.immregistries.mqe.hub.submission;

import java.io.IOException;

public interface IISSandboxSubmission {
  /* Sam 3/27 I always create interfaces, makes testing and mocking easier */

  String submitMessageToIISSandbox(String message) throws IOException;

}
