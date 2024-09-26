package smartsuite.app.sp.pro.asn.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.pro.asn.repository.DlvySchedMailRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;
import smartsuite.app.sp.pro.asn.repository.SpDlvySchedMailRepository;

import javax.inject.Inject;
import javax.mail.Message;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class DlvySchedNotiMailStrategy implements MailStrategy {

    @Inject
    SpDlvySchedMailRepository spDlvySchedMailRepository;

    @Override
    public String getEmailTemplateId() {
        return "DLVY_SCHED_NOTI_MAIL";
    }

    @Override
    public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws ParseException {

        List<Map<String, Object>> dlvyScheds = (List<Map<String, Object>>) data.get("dlvyScheds");
        Map<String, Object> userInfo = (Map<String, Object>) data.get("userInfo");


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        dlvyScheds.stream().forEach(item -> {
            try {
                item.put("po_qty", numberFormat.format(new BigDecimal(item.get("po_qty").toString())));
                item.put("dlvy_qty", numberFormat.format(new BigDecimal(item.get("dlvy_qty").toString())));
                Date dlvyDt = inputFormat.parse((String) item.get("dlvy_dt"));
                item.put("dlvy_dt", outputFormat.format(dlvyDt));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        // 내용 데이터
        Map<String, Object> contentData = Maps.newHashMap();
        contentData.put("dlvyScheds", dlvyScheds);
        contentData.put("userInfo", userInfo);
        contentData.put("poNos", dlvyScheds.stream().map(item -> item.get("po_no").toString().concat("\n")).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        contentData.put("itemCds", dlvyScheds.stream().map(item -> item.get("item_cd").toString().concat("\n")).filter(Objects::nonNull).distinct().collect(Collectors.toList()));



        // 수신자
        Map<String, Object> tempMap = Maps.newHashMap();
//        tempMap.put("gr_pic_id", );
        // 수정 필요함!!
        tempMap.put("gr_pic_ids", dlvyScheds.stream().map(item -> item.get("gr_pic_id").toString()).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        List<Map<String, Object>> grPicUserInfo = (List<Map<String, Object>>) spDlvySchedMailRepository.findReceiversByUsrId(tempMap);

        List<TemplateMailData.Receiver> receivers = Lists.newArrayList();

        for(Map<String, Object> usr : (List<Map<String, Object>>) grPicUserInfo) {
            TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
                    Message.RecipientType.TO,
                    (String) usr.get("eml"),
                    (String) usr.get("usr_nm"));
            receivers.add(receiver);
        }

        TemplateMailData templateMailData = TemplateMailData.getInstance(
                null,
                receivers,
                null,
                null,
                contentData
        );

        return templateMailData;
    }
}
