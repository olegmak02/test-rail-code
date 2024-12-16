package client.testrail.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import client.testrail.model.Links;
import client.testrail.model.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class PageDeserializer extends StdDeserializer<Page> {

    public static String field = "objects";
    public static Class type = Object.class;
    public static Object supplement = Collections.emptyList();

    public PageDeserializer() {
        this(null);
    }

    public PageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Page deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        int offset = node.get("offset").asInt();
        int limit = node.get("limit").asInt();
        int size = node.get("size").asInt();
        JsonNode links = node.get("links");
        String next = links.get("next").isNull() ? null : links.get("next").asText();
        String prev = links.get("prev").isNull() ? null : links.get("prev").asText();
        ArrayNode objects = (ArrayNode) node.get(field);
        List list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModules(new CaseModule(), new FieldModule(), new UnixTimestampModule());
        for(int i = 0; i < objects.size(); i++) {
            JsonNode element = objects.get(i);
            list.add(mapper.reader(type).with(new InjectableValues.Std().addValue(type.toString(), supplement)).readValue(element.toString()));
        }
        Page page = new Page();
        page.limit = limit;
        page.offset = offset;
        page.size = size;
        page._links = new Links();
        page._links.next = next;
        page._links.prev = prev;
        page.objects = list;
        return page;
    }
}
