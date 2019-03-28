package org.immregistries.mqe.hub.submission;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.mqe.hub.rest.MqeHl7TestingController;
import org.springframework.stereotype.Service;

@Service
public class IISSandboxSubmissionImpl implements IISSandboxSubmission {

  private static final Log LOGGER = LogFactory.getLog(MqeHl7TestingController.class);

  @Override
  public String submitMessageToIISSandbox(final String message) throws IOException {
    byte[] postData = getDataString(message).getBytes(StandardCharsets.UTF_8);
    int postDataLength = postData.length;
    URL url = new URL("http://florence.immregistries.org/iis-kernel/pop");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setInstanceFollowRedirects(false);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("charset", "utf-8");
    conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
    conn.setUseCaches(false);
    try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
      wr.write(postData);
    }
    StringBuilder content;

    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(conn.getInputStream()))) {

      String line;
      content = new StringBuilder();

      while ((line = in.readLine()) != null) {
        content.append(line);
        content.append(System.lineSeparator());
      }
    }
    LOGGER.info(content.toString());
    return content.toString();

  }

  private String getDataString(final String message)
      throws UnsupportedEncodingException {
    Map<String, String> params = new HashMap<>();
    params.put("USERID", "Mercy");
    params.put("PASSWORD", "password1234");
    params.put("FACILITYID", "Mercy Healthcare");
    params.put("MESSAGEDATA", message);

    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (first) {
        first = false;
      } else {
        result.append("&");
      }
      result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
      result.append("=");
      result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
    }
    return result.toString();
  }
}
