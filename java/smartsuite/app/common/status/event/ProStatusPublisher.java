package smartsuite.app.common.status.event;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProStatusPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(ProStatusPublisher.class);
    @Inject
    ApplicationEventPublisher applicationEventPublisher;


    public void cancelRfxResult(Map<String, Object> param){
         CustomSpringEvent docIdsEvent = CustomSpringEvent.toCompleteEvent("cancelRfxResult",param);
         applicationEventPublisher.publishEvent(docIdsEvent);
    }

    public void closeRfxItemResult(Map<String, Object> param){
         CustomSpringEvent docIdsEvent = CustomSpringEvent.toCompleteEvent("closeRfxItemResult",param);
         applicationEventPublisher.publishEvent(docIdsEvent);
    }

    public List<Map<String, Object>> findListReferenceDoc(Map<String, Object> param) {
        List<Map<String,Object>> docList = Lists.newArrayList();
        if(param == null) {
            return Collections.emptyList();
        }

        String appType = (String) param.get("appType");

        ReferenceDocImpl[] appDocs = (ReferenceDocImpl[]) ReferenceDoc.getEnumConstant(appType);
        if(appDocs != null){
            //현재 업무(appType) 테이블에 필드에 들어있는  다른 업무들의 item의 id를 가져온다.
            CustomSpringEvent docIdsEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocIdsFrom"+appType,param);
            applicationEventPublisher.publishEvent(docIdsEvent);
            Map<String,Object> idsParam = (Map<String, Object>) docIdsEvent.getResult();
            if(idsParam != null){
                param.putAll(idsParam);
            }

            //appType == appName FK

            //document로 나타낼 정의되어있는 리스트를 호출한다.
             for(ReferenceDocImpl appDoc: appDocs) {
                 String appName = appDoc.getName();
                 param.put("sortkey",appDoc.getValue());
                 CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocFrom"+appDoc.getName(),param);
                 applicationEventPublisher.publishEvent(completeEvent);

                 List<Map<String,Object>> result = (List<Map<String, Object>>) completeEvent.getResult();

                if(!CollectionUtils.isEmpty(result)){
                        docList = Stream.concat(docList.stream(), result.stream())
                                //.sorted(Comparator.comparing(map -> (String) map.get("sortKey")))
                                .sorted(new Comparator<Map<String, Object>>() {
                                        @Override
                                        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                                            // 비교하고자 하는 속성에 따라 정렬 기준을 변경할 수 있습니다.
                                            String key1 = (String) map1.getOrDefault("sortKey","");
                                            String key2 = (String) map2.getOrDefault("sortKey","");
                                            return key1.compareTo(key2);
                                        }})
                                .collect(Collectors.toList());
                }
             }
        }
        return docList;
    }

    public List<Map<String,Object>> findListReferenceDocFromPRByPrItemIds(Map<String, Object> param) {
       CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocFromPRByPrItemIds",param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object>> result = (List<Map<String, Object>>) completeEvent.getResult();
        return result;
    }

    public List<Map<String,Object>> findRfxItemIdsByRfxId(Map<String, Object> param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findRfxItemIdsByRfxId",param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object>> result = (List<Map<String, Object>>) completeEvent.getResult();
        return result;
    }

    public List<Map<String,Object>> findListPoByRfxItemIds(Map<String, Object> param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListPoByRfxItemIds",param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object>> result = (List<Map<String, Object>>) completeEvent.getResult();
        return result;
    }


/*
    public Map<String, Object> findListReferenceDocFromPR(Map<String, Object> param) {
        List<Map<String,Object>> docList = Lists.newArrayList();
        if(param == null) {
            return Collections.emptyMap();
        }

        String appType = (String) param.get("appType");


        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocFromPR"+"by"+appType,param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object> result = completeEvent.getResult();
        docList = Stream.concat(docList.stream(), result.stream()
        .collect(Collectors.toList());


        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocFromRFX"+"by"+appType,param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object> result = completeEvent.getResult();
        docList = Stream.concat(docList.stream(), result.stream()
        .collect(Collectors.toList());

        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocFromCONTRACT"+"by"+appType,param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object> result = completeEvent.getResult();
        docList = Stream.concat(docList.stream(), result.stream()
        .collect(Collectors.toList());


        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListReferenceDocFromAUCTION"+"by"+appType,param);
        applicationEventPublisher.publishEvent(completeEvent);
        List<Map<String,Object> result = completeEvent.getResult();
        docList = Stream.concat(docList.stream(), result.stream()
        .collect(Collectors.toList());
    }*/


    enum ReferenceDoc {
    PR("PR", PRReferenceDoc.class),
        UPCR("UPCR", UPCRReferenceDoc.class),
    RFX("RFX", RFXReferenceDoc.class),
    PO("PO", POReferenceDoc.class),
    CONTRACT("CONTRACT", CONTRACTReferenceDoc.class),
    ASN("ASN", ASNReferenceDoc.class),
        AUCTION ("AUCTION", RFXReferenceDoc.class),
    GR("GR",GRReferenceDoc.class);

    private final String value;
    private final Class<? extends Enum<?>> enumClass;

    ReferenceDoc(String value, Class<? extends Enum<?>> enumClass) {
            this.value = value;
            //this.enumClass = (Class<? extends Enum<?>>) enumClass.asSubclass(Enum.class);
            this.enumClass = enumClass;
        }

        public String getValue() {
            return value;
        }
		/*
		 * public static <T extends Enum<T>> T getEnumConstant(String name) { return
		 * Enum.valueOf(enumClass.asSubclass(Enum.class), name); }
		 */

        public static Enum<?>[] getEnumConstant(String type) {
            for (ReferenceDoc doc : values()) {
                if (doc.getValue().equals(type)) {
                    return doc.enumClass.getEnumConstants();
                }
            }
            LOG.info("Unknown ReferenceDoc type: " + type);
            //throw new IllegalArgumentException("Unknown ReferenceDoc type: " + type);
            return null;
        }
}
    interface ReferenceDocImpl{
    	String getValue();
    	String getName();
    }

    enum PRReferenceDoc implements ReferenceDocImpl{
        PR("A"), RFX("AA"), AUCTION("B"), CONTRACT("C"), PO("D"), ASN("E"), GR("F");

        private final String value;

        PRReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        public static String getNameByValue(String value) {
            for (PRReferenceDoc doc : values()) {
                if (doc.value.equals(value)) {
                    return doc.name();
                }
            }
            throw new IllegalArgumentException("No enum constant with value: " + value);
        }

		@Override
		public String getName() {
			return this.name();
		}

    }

    enum UPCRReferenceDoc implements ReferenceDocImpl{
        PR("A"),UPCR("B"), RFX("C"), AUCTION("D"), CONTRACT("E"), PO("F"), ASN("G"), GR("H");

        private final String value;

        UPCRReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        public static String getNameByValue(String value) {
            for (UPCRReferenceDoc doc : values()) {
                if (doc.value.equals(value)) {
                    return doc.name();
                }
            }
            throw new IllegalArgumentException("No enum constant with value: " + value);
        }

		@Override
		public String getName() {
			return this.name();
		}

    }

    enum RFXReferenceDoc implements ReferenceDocImpl{
        PR("A"), UPCR("AAA"), RFX("AA"), CONTRACT("C"), PO("B"), ASN("C"), GR("D");

        private final String value;

        RFXReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum CONTRACTReferenceDoc implements ReferenceDocImpl{
        PR("A"),UPCR("AA"), RFX("B"), CONTRACT("C");

        private final String value;

        CONTRACTReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum POReferenceDoc implements ReferenceDocImpl{
        PR("A"),UPCR("AA"), RFX("B"), AUCTION("C"),CONTRACT("D"),PO("DD"),ASN("E"),GR("F");

        private final String value;

        POReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum ASNReferenceDoc implements ReferenceDocImpl{
        PR("A"), UPCR("AA"),RFX("B"), PO("C"),ASN("D"),GR("E");

        private final String value;

        ASNReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum GRReferenceDoc implements ReferenceDocImpl{
        PR("A"), UPCR("AA"),RFX("B"), PO("C"),ASN("D"),GR("E");

        private final String value;

        GRReferenceDoc(String value) {
            this.value = value;

        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getName() {
            return this.name();
        }
    }
}
