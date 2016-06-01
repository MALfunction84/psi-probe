package psiprobe.controllers.certificates;

import org.apache.catalina.connector.Connector;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import psiprobe.controllers.TomcatContainerController;
import psiprobe.model.certificates.Cert;
import psiprobe.model.certificates.ConnectorInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class ListCertificatesController.
 */
public class ListCertificatesController extends TomcatContainerController {

  /** The Constant Logger. */
  private static final Logger logger = LoggerFactory.getLogger(ListCertificatesController.class);

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    ModelAndView modelAndView = new ModelAndView(getViewName());

    try {
      List<Connector> connectors = getContainerWrapper().getTomcatContainer().findConnectors();
      List<ConnectorInfo> infos = getConnectorInfos(connectors);

      for (ConnectorInfo info : infos) {
        List<Cert> certs;

        if (info.getKeystoreType() != null) {
          certs = getCertificates(info.getKeystoreType(), info.getKeystoreFile(), info.getKeystorePass());
          info.setKeyStoreCerts(certs);
        }

        if (info.getTruststoreType() != null) {
          certs = getCertificates(info.getTruststoreType(), info.getTruststoreFile(), info.getTruststorePass());
          info.setTrustStoreCerts(certs);
        }
      }

      modelAndView.addObject("connectors", infos);
    } catch (Exception e) {
      logger.error("There was an exception listing certificates", e);
    }

    return modelAndView;

  }

  /**
   * Gets the certificates.
   *
   * @param storeType the store type
   * @param storeFile the store file
   * @param storePassword the store password
   * @return the certificates
   * @throws Exception the exception
   */
  public List<Cert> getCertificates(String storeType, String storeFile, String storePassword)
      throws Exception {
    KeyStore keyStore;

    // Get key store
    if (storeType != null) {
      keyStore = KeyStore.getInstance(storeType);
    } else {
      keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    }

    // Get password
    char[] password = null;
    if (storePassword != null) {
      password = storePassword.toCharArray();
    }

    // Load key store from file
    try (InputStream storeInput = getStoreInputStream(storeFile)) {
      keyStore.load(storeInput, password);
    } catch (IOException e) {
      logger.error("Error loading store file {}", storeFile, e);
      return null;
    }

    List<Cert> certs = new ArrayList<>();

    Enumeration<String> keystoreAliases = keyStore.aliases();
    while (keystoreAliases.hasMoreElements()) {
      String alias = keystoreAliases.nextElement();

      Certificate[] certificateChains = keyStore.getCertificateChain(alias);

      if (certificateChains != null) {
        for (Certificate certificateChain : certificateChains) {
          X509Certificate x509Cert = (X509Certificate) certificateChain;
          addToStore(certs, alias, x509Cert);
        }
      } else {
        X509Certificate x509Cert = (X509Certificate) keyStore.getCertificate(alias);
        addToStore(certs, alias, x509Cert);
      }
    }
    return certs;
  }

  /**
   * Gets the connector infos.
   *
   * @param connectors the connectors
   * @return the connector infos
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  private List<ConnectorInfo> getConnectorInfos(List<Connector> connectors)
      throws IllegalAccessException, InvocationTargetException {
    List<ConnectorInfo> infos = new ArrayList<>();
    for (Connector connector : connectors) {
      if (!connector.getSecure()) {
        continue;
      }

      ProtocolHandler protocolHandler = connector.getProtocolHandler();

      if (protocolHandler instanceof AbstractHttp11JsseProtocol) {
        AbstractHttp11JsseProtocol<?> protocol = (AbstractHttp11JsseProtocol<?>) protocolHandler;
        if (!protocol.getSecure()) {
          continue;
        }
        infos.add(toConnectorInfo(protocol));
      }
    }
    return infos;
  }

  /**
   * Tries to open a InputStream the same way as
   * {@link org.apache.tomcat.util.file.ConfigFileLoader.getInputStream(String)}
   * 
   * @param path the path of a store file (absolute or relative to CATALINA.BASE), or URI to store
   *        file (absolute or relative to CATALINA.BASE).
   * @return the input stream of the path file
   * @throws IOException if path can not be resolved
   */
  private InputStream getStoreInputStream(String path) throws IOException {
    File file = new File(path);
    if (file.exists()) {
      return new FileInputStream(file);
    }

    File catalinaBaseFolder = new File(System.getProperty("catalina.base"));
    file = new File(catalinaBaseFolder, path);

    if (file.exists()) {
      return new FileInputStream(file);
    }

    URI uri = catalinaBaseFolder.toURI().resolve(path);

    URL url = uri.toURL();

    return url.openConnection().getInputStream();
  }

  /**
   * To connector info.
   *
   * @param protocol the protocol
   * @return the connector info
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  private ConnectorInfo toConnectorInfo(AbstractHttp11JsseProtocol<?> protocol)
      throws IllegalAccessException, InvocationTargetException {
    ConnectorInfo info = new ConnectorInfo();
    BeanUtils.copyProperties(info, protocol);
    info.setName(ObjectName.unquote(info.getName()));
    return info;
  }

  /**
   * Adds the to store.
   *
   * @param certs the certs
   * @param alias the alias
   * @param x509Cert the x509 cert
   */
  private void addToStore(List<Cert> certs, String alias, X509Certificate x509Cert) {
    Cert cert = new Cert();

    cert.setAlias(alias);
    cert.setSubjectDistinguishedName(x509Cert.getSubjectDN().toString());
    cert.setNotBefore(x509Cert.getNotBefore());
    cert.setNotAfter(x509Cert.getNotAfter());
    cert.setIssuerDistinguishedName(x509Cert.getIssuerDN().toString());

    certs.add(cert);
  }

}
