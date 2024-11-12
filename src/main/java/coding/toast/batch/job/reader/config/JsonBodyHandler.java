package coding.toast.batch.job.reader.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

	private final ObjectMapper mapper = new ObjectMapper()
											.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	private Class<T> type;
	private TypeReference<T> typeRef;
	
	public static <T> JsonBodyHandler<T> jsonBodyHandler(final Class<T> type) {
		return new JsonBodyHandler<>(type);
	}
	
	public static <T> JsonBodyHandler<T> jsonBodyHandler(final TypeReference<T> typeRef) {
		return new JsonBodyHandler<>(typeRef);
	}
	
	public JsonBodyHandler(Class<T> type) {
		this.type = type;
	}
	
	public JsonBodyHandler(TypeReference<T> typeRef) {
		this.typeRef = typeRef;
	}
	
	@Override
	public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
		return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8)
			, s -> {
				try {
					// typeRef has priority!
					if (typeRef != null) {
						return mapper.readValue(s, typeRef);
					} else {
						return mapper.readValue(s, type);
					}
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			});
	}
}
