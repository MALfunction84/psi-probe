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

package psiprobe.controllers.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.tanukisoftware.wrapper.WrapperManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class ThreadDumpController.
 *
 * @author Vlad Ilyushchenko
 */
public class ThreadDumpController extends ParameterizableViewController {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(ThreadDumpController.class);

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    boolean done = false;
    try {
      Class.forName("org.tanukisoftware.wrapper.WrapperManager");
      logger.info("ThreadDump requested by {}", request.getRemoteAddr());
      WrapperManager.requestThreadDump();
      done = true;
    } catch (ClassNotFoundException e) {
      logger.info("WrapperManager not found. Do you have wrapper.jar in the classpath?");
      logger.trace("", e);
    }
    return new ModelAndView(getViewName(), "done", done);
  }

}
