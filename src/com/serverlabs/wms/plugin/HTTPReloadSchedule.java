package com.serverlabs.wms.plugin;

import java.util.List;
import java.util.Map;
import java.io.OutputStream;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.http.HTTProvider2Base;
import com.wowza.wms.http.IHTTPRequest;
import com.wowza.wms.http.IHTTPResponse;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.plugin.streampublisher.ServerListenerStreamPublisher;
import com.wowza.wms.server.Server;
import com.wowza.wms.stream.publish.Stream;
import com.wowza.wms.vhost.IVHost;

public class HTTPReloadSchedule extends HTTProvider2Base {
	private static String MODULE_NAME = "[ReloadSchedule]";

	public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp) {
		if (!doHTTPAuthentication(vhost, req, resp))
			return;

		final String PROP_NAME_PREFIX = "streamPublisher";

		req.parseBodyForParams(true);

		Map<String, List<String>> params = req.getParameterMap();

		String action = "";
		String app = "";
		String report = "Nothing to do";

		ServerListenerStreamPublisher streamPublisher;
		IApplicationInstance appInstance = null;

		streamPublisher = (ServerListenerStreamPublisher) Server.getInstance().getProperties().get(ServerListenerStreamPublisher.PROP_STREAMPUBLISHER);

		if (params.containsKey("action"))
			action = params.get("action").get(0);

		if (params.containsKey("app")) {
			app = params.get("app").get(0);
		}

		if (vhost.applicationExists(app)) {
			appInstance = vhost.getApplication(app).getAppInstance("_definst_");
		}

		if (action.equalsIgnoreCase("load")) {
			try {
				streamPublisher.loadSchedule(appInstance);
				report = " Schedule loaded for app [" + app + "]";
				WMSLoggerFactory.getLogger(null).info(MODULE_NAME + report);
			} catch (Exception e) {
				e.printStackTrace();
				report = " Schedule load ERROR for app [" + app + "] ";
				WMSLoggerFactory.getLogger(null).error(MODULE_NAME + report + e.toString());
			}
		} else if (action.equalsIgnoreCase("unload")) {
			@SuppressWarnings("unchecked")
			Map<String, Stream> streams = (Map<String, Stream>) appInstance.getProperties().remove(PROP_NAME_PREFIX + "Streams");
			if (streams != null) {
				try {
					for (Stream stream : streams.values()) {
						streamPublisher.shutdownStream(appInstance, (com.wowza.wms.stream.publish.Stream) stream);
					}
					streams.clear();
					report = " Schedule unloaded for app [" + app + "]";
					WMSLoggerFactory.getLogger(null).info(MODULE_NAME + report);
				} catch (Exception e) {
					e.printStackTrace();
					report = " Schedule unload ERROR for app [" + app + "] ";
					WMSLoggerFactory.getLogger(null).error(MODULE_NAME + report + e.toString());
				}
			} else {
				report = " No streams found for app [" + app + "]. Nothing to do";
				WMSLoggerFactory.getLogger(null).error(MODULE_NAME + report);
			}
		}

		String retStr = "<html><head><title>Wowza Schedule Reloader</title></head><body><h1>Wowza Schedule Reloader</h1>" + report + "</body></html>";
		try {
			OutputStream out = resp.getOutputStream();
			byte[] outBytes = retStr.getBytes();
			out.write(outBytes);
		} catch (Exception e) {
			e.printStackTrace();
			WMSLoggerFactory.getLogger(null).error(MODULE_NAME + " ERROR: " + e.toString());
		}
	}
}
