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

package com.googlecode.psiprobe.controllers.logs;

import com.googlecode.psiprobe.beans.LogResolverBean;
import com.googlecode.psiprobe.tools.logging.LogDestination;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class ListLogsController.
 *
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class ListLogsController extends ParameterizableViewController {

  /** The error view. */
  private String errorView;
  
  /** The Logger resolver. */
  private LogResolverBean logResolver;

  /**
   * Gets the error view.
   *
   * @return the error view
   */
  public String getErrorView() {
    return errorView;
  }

  /**
   * Sets the error view.
   *
   * @param errorView the new error view
   */
  public void setErrorView(String errorView) {
    this.errorView = errorView;
  }

  /**
   * Gets the Logger resolver.
   *
   * @return the Logger resolver
   */
  public LogResolverBean getLogResolver() {
    return logResolver;
  }

  /**
   * Sets the Logger resolver.
   *
   * @param logResolver the new Logger resolver
   */
  public void setLogResolver(LogResolverBean logResolver) {
    this.logResolver = logResolver;
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    boolean showAll = ServletRequestUtils.getBooleanParameter(request, "apps", false);
    List<LogDestination> uniqueList = logResolver.getLogDestinations(showAll);
    if (uniqueList != null) {
      return new ModelAndView(getViewName()).addObject("logs", uniqueList);
    }
    return new ModelAndView(errorView);
  }

}
