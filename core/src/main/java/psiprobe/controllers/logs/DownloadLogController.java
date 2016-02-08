/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */

package psiprobe.controllers.logs;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import psiprobe.Utils;
import psiprobe.tools.logging.LogDestination;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class DownloadLogController.
 *
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class DownloadLogController extends LogHandlerController {

  @Override
  protected ModelAndView handleLogFile(HttpServletRequest request, HttpServletResponse response,
      LogDestination logDest) throws Exception {

    boolean compressed = "true".equals(ServletRequestUtils.getStringParameter(request, "compressed"));
    
    File file = logDest.getFile();
    logger.info("Sending " + file + (compressed ? " compressed" : "") + " to " + request.getRemoteAddr() + "("
        + request.getRemoteUser() + ")");
    if (compressed) {
      Utils.sendCompressedFile(request, response, file);
    } else {
      Utils.sendFile(request, response, file);
    }
    return null;
  }

}
