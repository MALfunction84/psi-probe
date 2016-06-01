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

package psiprobe.controllers.threads;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import psiprobe.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class KillThreadController.
 *
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class KillThreadController extends ParameterizableViewController {

  /** The replace pattern. */
  private String replacePattern;

  /**
   * Gets the replace pattern.
   *
   * @return the replace pattern
   */
  public String getReplacePattern() {
    return replacePattern;
  }

  /**
   * Sets the replace pattern.
   *
   * @param replacePattern the new replace pattern
   */
  public void setReplacePattern(String replacePattern) {
    this.replacePattern = replacePattern;
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    String threadName = ServletRequestUtils.getStringParameter(request, "thread", null);

    Thread thread = null;
    if (threadName != null) {
      thread = Utils.getThreadByName(threadName);
    }

    if (thread != null) {
      thread.stop();
    }

    String referer = request.getHeader("Referer");
    String redirectUrl;
    if (referer != null) {
      redirectUrl = referer.replaceAll(replacePattern, "");
    } else {
      redirectUrl = request.getContextPath() + getViewName();
    }
    return new ModelAndView(new RedirectView(redirectUrl));
  }

}
