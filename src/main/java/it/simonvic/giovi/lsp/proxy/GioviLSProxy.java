package it.simonvic.giovi.lsp.proxy;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

public class GioviLSProxy {

	private static final Logger log = Logger.getLogger("GioviLS_Proxy");

	public void run()
		throws IOException, InterruptedException, ExecutionException {

		var logdir = new File("/tmp/giovils_proxy");
		if (!logdir.exists()) {
			logdir.mkdirs();
		}
		var fileHandler = new FileHandler("/tmp/giovils_proxy/giovils.log", true);
		fileHandler.setFormatter(new SimpleFormatter());
		log.addHandler(fileHandler);
		log.info("init GioviLS proxy");

		try (var socket = new Socket("127.0.0.1", 8069)) {
			log.info("Connected to real server socket");
			var client = new ProxyClient(null);
			Launcher<LanguageServer> clientLauncher = LSPLauncher.createClientLauncher(
				client,
				socket.getInputStream(),
				socket.getOutputStream());

			log.info("Starting proxy server");
			var server = new ProxyServer(clientLauncher.getRemoteProxy());
			Launcher<LanguageClient> serverLauncher = LSPLauncher.createServerLauncher(
				server,
				System.in,
				System.out);
			server.connect(serverLauncher.getRemoteProxy());

			log.info("listening for real server");
			Future<Void> clientFuture = clientLauncher.startListening();
			//clientFuture.get();

			log.info("Listening for real client");
			Future<?> serverFuture = serverLauncher.startListening();
			serverFuture.get();

		}

	}

}

