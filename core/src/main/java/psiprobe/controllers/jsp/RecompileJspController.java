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

package psiprobe.controllers.jsp;

import org.apache.catalina.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import psiprobe.controllers.ContextHandlerController;
import psiprobe.model.jsp.Summary;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The Class RecompileJspController.
 *
 * @author Vlad Ilyushchenko
 */
public class RecompileJspController extends ContextHandlerController {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(RecompileJspController.class);

  @Override
  protected ModelAndView handleContext(String contextName, Context context,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    HttpSession session = request.getSession(true);
    Summary summary = (Summary) session.getAttribute(DisplayJspController.SUMMARY_ATTRIBUTE);

    if (request.getMethod().equalsIgnoreCase("post") && summary != null) {
      List<String> names = new ArrayList<>();
      for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
        String name = e.nextElement();
        if ("on".equals(request.getParameter(name))) {
          names.add(name);
        }
      }
      getContainerWrapper().getTomcatContainer().recompileJsps(context, summary, names);
      session.setAttribute(DisplayJspController.SUMMARY_ATTRIBUTE, summary);
    } else if (summary != null && contextName.equals(summary.getName())) {
      String name = ServletRequestUtils.getStringParameter(request, "source", null);
      if (name != null) {
        List<String> names = new ArrayList<>();
        names.add(name);
        getContainerWrapper().getTomcatContainer().recompileJsps(context, summary, names);
        session.setAttribute(DisplayJspController.SUMMARY_ATTRIBUTE, summary);
      } else {
        logger.error("source is not passed, nothing to do");
      }
    }
    return new ModelAndView(new RedirectView(request.getContextPath()
        + ServletRequestUtils.getStringParameter(request, "view", getViewName()) + "?"
        + request.getQueryString()));
  }

}
