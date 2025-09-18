package it.simonvic.giovi.lsp;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

public class GioviLS {

	private static final Logger log = Logger.getLogger("GioviLS");

	public static void main(String[] args) throws IOException {
		var logdir = new File("/tmp/giovils");
		if (!logdir.exists()) {
			logdir.mkdirs();
		}
		var fileHandler = new FileHandler("/tmp/giovils/giovils.log", true);
		fileHandler.setFormatter(new SimpleFormatter());
		log.addHandler(fileHandler);
		log.info("init GioviLS");
		while (true) {
			var server = new GioviLanguageServer();
			try (
				var serverSocket = new ServerSocket(8069);
				var clientSocket = serverSocket.accept();
			) {

				Launcher<LanguageClient> serverLauncher = LSPLauncher.createServerLauncher(
					server,
					clientSocket.getInputStream(),
					clientSocket.getOutputStream());
				server.connect(serverLauncher.getRemoteProxy());

				Future<?> future = serverLauncher.startListening();
				try {
					System.out.println("futureget: " + future.get());
				} catch (InterruptedException | ExecutionException e) {
					log.info(e.getMessage());
				}
			}
		}
	}

}
