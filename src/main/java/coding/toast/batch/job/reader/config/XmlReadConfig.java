// package coding.toast.batch.job.reader.config;
//
// import com.fasterxml.jackson.core.type.TypeReference;
//
// import java.io.IOException;
// import java.io.PipedInputStream;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpHeaders;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.nio.charset.StandardCharsets;
// import java.time.Duration;
// import java.time.temporal.ChronoUnit;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
//
// //https://golb.hplar.ch/2019/01/java-11-http-client.html
// public class XmlReadConfig {
// 	public static void main(String[] args) {
// 		//
// 		// try (HttpClient client = HttpClient.newHttpClient()) {
// 		//
// 		// 	HttpRequest request = HttpRequest.newBuilder(URI.create("https://jsonplaceholder.typicode.com/posts"))
// 		// 		.timeout(Duration.of(2L, ChronoUnit.SECONDS))
// 		// 		.POST(HttpRequest.BodyPublishers.ofString("""
// 		// 			{
// 		// 				"good":"job",
// 		// 				"nice":"day"
// 		// 			}""")).build();
// 		//
// 		// 	client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
// 		//
// 		// } catch (IOException e) {
// 		// 	throw new RuntimeException(e);
// 		// } catch (InterruptedException e) {
// 		// 	Thread.currentThread().interrupt();
// 		// 	throw new RuntimeException(e);
// 		// }
//
// 		HttpClient httpClient = HttpClient.newBuilder()
// 			.connectTimeout(Duration.of(3L, ChronoUnit.SECONDS))
// 			.version(HttpClient.Version.HTTP_2)
// 			.followRedirects(HttpClient.Redirect.ALWAYS)
// 			.build();
//
// 		HttpRequest request =
// 			HttpRequest.newBuilder(URI.create("https://jsonplaceholder.typicode.com/posts"))
// 				.version(HttpClient.Version.HTTP_2)
// 				.GET()
// 				.build();
//
// 		try (httpClient) {
// 			// HttpResponse<String> response
// 			// 	= httpClient.send(request,
// 			// 		HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
//
// 			HttpResponse<List<UserInfo>> response
// 				= httpClient.send(request,
// 				JsonBodyHandler.jsonBodyHandler(new TypeReference<List<UserInfo>>() {}));
//
// 			int statusCode = response.statusCode();
// 			List<UserInfo> body = response.body();
// 			for (UserInfo userInfo : body) {
// 				System.out.print("\n============ [userId : %s] information =============\n".formatted(userInfo.id()));
// 				System.out.println("userInfo = " + userInfo);
// 			}
// 			// logResult(statusCode / 100, body);
// 		} catch (IOException | InterruptedException e) {
// 			throw new RuntimeException(e);
// 		}
// 	}
//
// 	private static void logResult(int statusGrpCode, Object body) {
// 		switch (statusGrpCode) {
// 			case 2 -> {
// 				System.out.println("success!");
// 			}
// 			case 4 -> {
// 				System.err.println("User Request Error!");
// 			}
// 			case 5 -> {
// 				System.err.println("Server Internal Error!");
// 			}
// 		}
// 		System.out.println("body = " + body);
// 	}
//
// 	private static void shutdown(HttpClient httpClient) {
// 		httpClient.shutdown();// 일단 요청은 더이상 받지 말고, 셧다운 시도
// 		try {
// 			if (!httpClient.awaitTermination(Duration.of(3L, ChronoUnit.SECONDS))) {
// 				httpClient.shutdownNow(); // interrupt 걸어서 shutdown 시도
// 				if (!httpClient.awaitTermination(Duration.of(5L, ChronoUnit.SECONDS))) {
// 					System.err.println("SomeThing is Wrong!");
// 					System.exit(-1);
// 				}
// 			}
// 		} catch (InterruptedException e) {
// 			httpClient.shutdownNow(); // 만약에 현재 shutdown 을 시도하는 쓰레드에 인터럽트가 걸려도 shutdown 을 한다.
// 			Thread.currentThread().interrupt(); // 다시 interrupt 됐다고 flag 를 세팅해준다.
// 		}
// 	}
//
// 	public record UserInfo(String userId, Long id, String title, String body) { }
// }
