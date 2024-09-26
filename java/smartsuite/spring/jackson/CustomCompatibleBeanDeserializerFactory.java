package smartsuite.spring.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer.Vanilla;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomCompatibleBeanDeserializerFactory extends
		BeanDeserializerFactory {

	public CustomCompatibleBeanDeserializerFactory(
			DeserializerFactoryConfig config) {
		super(config);
	}

	private static final long serialVersionUID = -3124540127638526050L;

	public JsonDeserializer<?> findDefaultDeserializer(
			DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
			throws JsonMappingException {
		Class<?> rawType = type.getRawClass();
		// Object ("untyped"), String equivalents:
		if (rawType == Object.class) {
			return new CustomCompatibleUntypedObjectDeserializer();
		} else {
			return super.findDefaultDeserializer(ctxt, type, beanDesc);
		}
	}

	
	private class CustomCompatibleUntypedObjectDeserializer extends
		UntypedObjectDeserializer {
		
		private static final long serialVersionUID = -3124540127638526050L;

		@Override
		public JsonDeserializer<?> createContextual(
				DeserializationContext ctxt, BeanProperty property)
				throws JsonMappingException {
			if ((_stringDeserializer == null)
					&& (_numberDeserializer == null)
					&& (_mapDeserializer == null)
					&& (_listDeserializer == null)
					&& UntypedObjectDeserializer.class.isAssignableFrom(this
							.getClass())) {
				return CustomCompatibleUntypedDeserializer.std;
			} else {
				return super.createContextual(ctxt, property);
			}
		}

	}

	private static class CustomCompatibleUntypedDeserializer extends Vanilla {

		private static final long serialVersionUID = -667759117289279686L;

		public static final CustomCompatibleUntypedDeserializer std = new CustomCompatibleUntypedDeserializer();

		private final Pattern datePattern = Pattern
				.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}(Z|[+-](\\d{2}:?\\d{2}))?$");

		private final List<DateFormat> dateFormats = Arrays.asList(
				new StdDateFormat(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

		private CustomCompatibleUntypedDeserializer() {}
		
		public Object deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException {
			Object result = super.deserialize(jp, ctxt);
			if (result instanceof String) {
				return stringToDate((String) result);
			}else if(isConvertibleToBigDecimal(result)){
				result = convertToBigDecimal(result);
			}else if(result instanceof Map) {
				result = innerMapToDate((Map) result);
			}else if(result instanceof List && _instanceOfListMap(result)){
				result = convertValuesToString((List) result);
			}
			return result;
		}

	public boolean _instanceOfListMap(Object result){
		if(result instanceof List){
			List list = (List) result;
			if(list.size() == 0){
				return false;
			}
			return list.get(0) instanceof Map;
		}else{
			return false;
		}
	}

		public Date parseDate(String date) throws ParseException {
			ParseException pe = null;
			for (DateFormat f : dateFormats) {
				try {
					return f.parse(date);
				} catch (ParseException e) {
					pe = (pe == null) ? e : pe;
				} catch (IllegalArgumentException e) {
					pe = (pe == null) ? new ParseException(e.getMessage(), 0)
							: pe;
				}
			}
			throw pe;
		}

		protected Object stringToDate(String result) {
			String string = (String) result;
			if (datePattern.matcher(string).matches()) {
				try {
					return parseDate(string);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return result;
		}

		/*protected String numberToString(Object val){
			String result = String.valueOf(val);
			if(result == "null"){
				result = "";
			}
			return result;
		}*/

		public static boolean isConvertibleToBigDecimal(Object value) {
        return value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof BigDecimal;
    }

		protected BigDecimal convertToBigDecimal(Object value) {
        return Optional.ofNullable(value)
                //.filter(v -> v instanceof Number)
				.filter(v -> isConvertibleToBigDecimal(v))
                .map(v -> {
                    if (v instanceof BigDecimal) {
                        return (BigDecimal) v;
                    } else {
                        return new BigDecimal(v.toString());
                    }
                })
                .orElse(null);
    }


		protected  List<Map<Object, Object>> convertValuesToString(List<Map<String, Object>> listOfMaps) {
        return listOfMaps.stream()
				.map(map -> map.entrySet().stream().collect(
				LinkedHashMap::new,
				(m, entry) -> m.put(entry.getKey(), convertToObject(entry.getValue())),
				LinkedHashMap::putAll
		)).collect(Collectors.toList());

		/*
		return listOfMaps.stream().map(map -> map.entrySet().stream().collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    Object value = entry.getValue();
                                    if(isConvertibleToBigDecimal(value)){	//? shot, byte long ...
                                        return convertToBigDecimal(value);
                                    } else {
                                        return value; // No conversion needed
                                    }
                                },(oldValue, newValue) -> newValue, // Merge function to resolve conflicts
                        		HashMap::new
                        ))
                ).collect(Collectors.toList());*/
    }

	public Object convertToObject(Object value){
		if(isConvertibleToBigDecimal(value)){	//? shot, byte long ...
			return convertToBigDecimal(value);
		} else {
			return value; // No conversion needed
		}
	}
		
		protected Object innerMapToDate(Map result) {
			for (Iterator<String> iterator = result.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				Object value = result.get(key);
				if("page".equals(key) || "start".equals(key) || "end".equals(key)) {
					return result;
				}
				if(value instanceof String) {
					result.put(key, stringToDate((String) value));
				}else if(value instanceof Map){
					result.put(key, innerMapToDate((Map) value));
				}else if(isConvertibleToBigDecimal(value)){
					result.put(key, convertToBigDecimal(value));
				}
			}
			return result;
		}

	}
	
}