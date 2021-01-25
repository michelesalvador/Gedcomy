package gedcomy;

import com.google.gson.*;
import org.folg.gedcom.model.Extensions;
import org.folg.gedcom.model.GedcomTag;
import org.gedml.GedcomParser;
import org.xml.sax.SAXParseException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GedcomTagTypeAdapter implements JsonDeserializer<GedcomTag> {
   private Gson gson;
   //private Map<String,Class> classExtensions;
   //private Map<String,Type> typeExtensions;

   public GedcomTagTypeAdapter() {
      gson = new Gson();
      //classExtensions = new HashMap<String, Class>();
      //typeExtensions = new HashMap<String, Type>();
   }

   /*public void registerExtension(String extensionKey, Class clazz) {
      classExtensions.put(extensionKey, clazz);
   }

   public void registerExtension(String extensionKey, Type type) {
      typeExtensions.put(extensionKey, type);
   }

   public JsonElement serialize(Extensions src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject o = new JsonObject();
      for (Map.Entry<String, Object> entry : src.getExtensions().entrySet()) {
         String key = entry.getKey();
         Object extension = entry.getValue();
         JsonElement elm = (extension instanceof JsonElement ? (JsonElement) extension : gson.toJsonTree(extension));
         o.add(key, elm);
      }
      return o;
   }*/

   //@SuppressWarnings("unchecked")
   public GedcomTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
       if (json.isJsonNull())
           return null;
        
      //JsonObject o = json.getAsJsonObject();
      //List<GedcomTag> tree = new ArrayList<>();
      GedcomTag node = new GedcomTag(null, "pippo", null);
      node.setValue(json.getAsJsonObject().get("head").toString());
     // tree.add((json.toString());
      //return node;
      
     JsonParser parser = new JsonParser();
      JsonObject jsonObject = parser.parse(json.getAsJsonObject().get("head").toString()).getAsJsonObject();

      Set<String> keys = jsonObject.keySet();
	return node;
      
       
      /*for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
         String key = entry.getKey();
         JsonElement jsonElement = entry.getValue();
         Object extension;
         Type type = typeExtensions.get(key);
         if (type != null) {
            extension = gson.fromJson(jsonElement, type);
         }
         else {
            Class clazz = classExtensions.get(key);
            if (clazz != null) {
               extension = gson.fromJson(jsonElement, clazz);
            }
            else {
               extension = jsonElement;
            }
         }
         extensions.put(key, extension);
      }
      return extensions;*/
   }
}
