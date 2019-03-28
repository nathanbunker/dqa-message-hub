package org.immregistries.mqe.hub.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * @author jamesabrannan
 * Will send back a more usefull error message, for example:
 * {
 *   "timestamp": 1553738580602,
 *   "status": 500,
 *   "error": "Internal Server Error",
 *   "exception": "org.immregistries.mqe.hub.rest.exception.MessageInputControllerException",
 *   "message": "sender: null: INVALID REQUEST:  VXU is empty",
 *   "path": "/mqe/api/messages/json/notsaved"
 * }
 *
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MessageInputControllerException extends RuntimeException {


  private static final long serialVersionUID = -4639129139826026297L;

  public MessageInputControllerException(String message) {
    super(message);
  }

  public MessageInputControllerException(Throwable cause) {
    super(cause);
  }

  public MessageInputControllerException(String message, Throwable cause) {
    super(message, cause);
  }


}
