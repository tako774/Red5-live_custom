package net.tako774.Red5LiveCustom;

import java.util.HashSet;

import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IStreamPublishSecurity;

public class AuthPublish implements IStreamPublishSecurity {

  private HashSet<String> allowAddresses = null;

  AuthPublish(HashSet<String> allowAddresses) {
    this.allowAddresses = allowAddresses;
  }

  @Override
  public boolean isPublishAllowed(IScope scope, String name, String mode) {
    if (allowAddresses.contains("*")) {
      return true;
    }

    IConnection conn = Red5.getConnectionLocal();
    String address = conn.getRemoteAddress();
    if (allowAddresses.contains(address)) {
      return true;
    }

    conn.close();
    return false;
  }

}
