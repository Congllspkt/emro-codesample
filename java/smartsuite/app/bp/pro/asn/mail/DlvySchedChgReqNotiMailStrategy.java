package smartsuite.app.bp.pro.asn.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.pro.asn.repository.DlvySchedMailRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class DlvySchedChgReqNotiMailStrategy implements MailStrategy {

    @Inject
    DlvySchedMailRepository dlvySchedMailRepository;

    @Override
    public String getEmailTemplateId() {
        return "DLVY_SCHED_CHG_REQ_NOTI_MAIL";
    }

    @Override
    public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws ParseException {

        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        String dlvySchedChgReqRsn = (String) data.get("dlvy_sched_chg_req_rsn");
        Map<String, Object> userInfo = (Map<String, Object>) data.get("userInfo");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        items.stream().forEach(item -> {
            try {
                item.put("po_qty", numberFormat.format(new BigDecimal(item.get("po_qty").toString())));
                item.put("dlvy_qty", numberFormat.format(new BigDecimal(item.get("dlvy_qty").toString())));
                if(item.get("req_qty") != null) item.put("req_qty", numberFormat.format(new BigDecimal(item.get("req_qty").toString())));

                Date dlvyDt = inputFormat.parse((String) item.get("dlvy_dt"));
                item.put("dlvy_dt", outputFormat.format(dlvyDt));

                if(item.get("req_dt") != null)  {
                    Date reqDt = inputFormat.parse((String) item.get("req_dt"));
                    item.put("req_dt", outputFormat.format(reqDt));
                }

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        // 내용 데이터
        Map<String, Object> contentData = Maps.newHashMap();
        contentData.put("dlvySchedChgReqRsn", dlvySchedChgReqRsn);
        contentData.put("userInfo", userInfo);
        contentData.put("items", items);
        contentData.put("poNos", items.stream().map(item -> item.get("po_no").toString().concat("\n")).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        contentData.put("itemCds", items.stream().map(item -> item.get("item_cd").toString().concat("\n")).filter(Objects::nonNull).distinct().collect(Collectors.toList()));

        // 수신자
        Map paramForReceiver = Maps.newHashMap();
        paramForReceiver.put("usr_ids", items.stream().map(item -> item.get("regr_id")).filter(Objects::nonNull).distinct().collect(Collectors.toList()));

        List<Map> temp = dlvySchedMailRepository.findReceiversByUsrId(paramForReceiver);
        List<TemplateMailData.Receiver> receivers = Lists.newArrayList();
        for(Map usr : (List<Map>) temp) {
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
