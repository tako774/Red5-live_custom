package net.tako774.Red5LiveCustom;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IStreamPublishSecurity;

public class AuthPublish implements IStreamPublishSecurity {

	private List<String> allowHosts = null;
	AuthPublish(List<String> allowHosts) {
		this.allowHosts = allowHosts;
	}

	@Override
	public boolean isPublishAllowed(IScope scope, String name, String mode) {
		if (allowHosts.contains("*")) {	return true; }

		IConnection conn = Red5.getConnectionLocal();

		String address = conn.getRemoteAddress();
		if (allowHosts.contains(address)) { return true; }

		try {
			String hostname = InetAddress.getByName(address).getHostName();
			if (allowHosts.contains(hostname)) { return true; }
		} catch (UnknownHostException e) {
			// do nothing
		}

		Red5.getConnectionLocal().close();
		return false;
	}

}
