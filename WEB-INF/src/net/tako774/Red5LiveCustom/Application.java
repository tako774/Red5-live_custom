package net.tako774.Red5LiveCustom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

public class Application extends ApplicationAdapter {

  private int maxConnections = 5;
  private List<String> allowPublishHosts;

  private void _loadProperty() {
    Properties conf = new Properties();
    InputStream inputStream = null;

    try {
      inputStream = Application.class.getClassLoader().getResourceAsStream("application.properties");
      conf.load(inputStream);

      int _maxConnections = Integer.parseInt(conf.getProperty("maxConnections"));
      if (_maxConnections > 0) {
        maxConnections = _maxConnections;
      }

      String[] _allowPublishHostStrs = conf.getProperty("allowPublishHosts").split(",");
      for (int i = 0; i < _allowPublishHostStrs.length; i++) {
        _allowPublishHostStrs[i] = _allowPublishHostStrs[i].trim();
      }
      allowPublishHosts = Arrays.asList(_allowPublishHostStrs);

    } catch (IOException e) {
      log.warn(e.toString(), e.getStackTrace().toString());
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          log.warn(e.toString(), e.getStackTrace().toString());
        }
      }
    }

  }

  @Override
  public boolean appStart(IScope app) {

    log.info("LiveCustom appStart");

    _loadProperty();
    log.info("maxConnections: " + maxConnections);
    log.info("allowPublishHosts: " + allowPublishHosts.toString());

    registerStreamPublishSecurity(new AuthPublish(allowPublishHosts));

    return true;
  }

  @Override
  public boolean appConnect(IConnection conn, Object[] params) {
    log.info("LiveCustom appConnect");

    IScope appScope = conn.getScope();
    log.debug("App connect called for scope: {}", appScope.getName());

    int currentClientSize = getClients().size();
    log.info("Current Clients: " + currentClientSize);
    if (currentClientSize > maxConnections) {
      log.warn("Client Connection is over Max (" + maxConnections + ")");
      rejectClient("Client Connection is over Max (" + maxConnections + ")");
      return false;
    }

    return true;
  }
}
