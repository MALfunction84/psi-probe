package psiprobe.model;

import org.junit.Test;

import com.codebox.bean.JavaBeanTester;

import psiprobe.model.DataSourceInfo;

/**
 * The Class DataSourceInfoTest.
 */
public class DataSourceInfoTest {

  /**
   * Javabean tester.
   */
  @Test
  public void javabeanTester() {
    JavaBeanTester.builder(DataSourceInfo.class).loadData().test();
  }

}
