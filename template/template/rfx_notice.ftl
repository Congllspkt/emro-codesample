<style type="text/css">
    .rfx_table {overflow:hidden; height:505px; overflow-y:auto;padding-right:10px;}/*--20200625--*/
    .notice_wrap {padding:30px 30px 0;box-sizing: border-box;}/*--20200625--*/
    .notice_wrap section {margin-bottom: 20px;}
    .notice_wrap h3 {color: #000;font-weight: 500;font-size: 15px;line-height: 1;letter-spacing: -.5px;padding-left: 5px;padding-bottom: 10px;}
    .notice_wrap h3:before {content: '' ;display: inline-block;width: 3px;height: 15px;border-radius: 1px;background: #6171ea;margin-right: 10px;vertical-align: top;}
    .notice_wrap h3>a {display: inline-block;float: right;color: #666;font-size: 13px;font-weight: 600;}
    .notice_wrap h3>a:hover{text-decoration:underline;}/*--20200630--*/
    .notice_wrap table {width: 100%;border-collapse: collapse;font-size: 13px;color: #666;}
    .notice_wrap table th, .notice_wrap table td {border-top: 1px solid #ddd;border-bottom: 1px solid #ddd;padding: 10px 20px;box-sizing: border-box;}
    .notice_wrap table th {background: #f5f5f5;font-weight: 500;color: #222;text-align: left;vertical-align: middle;}
    .notice_wrap table td li {list-style: disc;padding: 2px 0;margin-left: 20px;}
    .notice_wrap table td li>a:hover {color: #6171ea; text-decoration: underline;}/*--20200630--*/
    .notice_wrap table td p {padding: 2px 0;}
    .notice_wrap table.brdtb th, .notice_wrap table.brdtb td {border: 1px solid #ddd;}
    .button_area {text-align: center; margin-top:20px}
    .button_area a {display: inline-block;background: #252b7a;border-radius: 5px;color: #fff;font-size: 13px;font-weight: 600;text-align: center;padding: 7px 10px;}
    .button_area a:hover {background: #1b2167}
    .txct {text-align: center !important;}
    .txrg {text-align: right !important;}
</style>

<div class="notice_wrap"><!--20200625-->
    <div class="rfx_table">
        <!-- 일반 정보 -->
        <section>
            <h3>일반 정보</h3>
            <table>
                <caption>입찰공고 일반 정보-view 테이블</caption>
                <colgroup>
                    <col width="20%">
                    <col width="30%">
                    <col width="20%">
                    <col width="35%">
                </colgroup>
                <tbody>
                <tr>
                    <th>RFx번호 / 차수</th>
                    <td>${rfxHdData.rfx_no} / ${rfxHdData.rfx_rnd} </td>
                    <th>RFx명</th>
                    <td>${rfxHdData.rfx_tit}</td>
                </tr>
                <tr>
                    <th>구매유형</th>
                    <td>${rfxHdData.p2p_purc_typ_ccd_nm!}</td>
                    <th>RFx유형</th>
                    <td>${rfxHdData.rfx_typ_ccd_nm!}</td>
                </tr>
                </tbody>
            </table>
        </section>
        <!-- RFx일정 -->
        <section>
            <h3>RFx일정</h3>
            <table>
                <caption>입찰공고 RFx일정-view 테이블</caption>
                <colgroup>
                    <col width="20%">
                    <col width="*">
                </colgroup>
                <tbody>
                <tr>
                    <th>RFx기간</th>
                    <td colspan="3">${rfxHdData.rfx_period!}</td>
                </tr>
                <tr>
                    <th>개찰일시</th>
                    <td colspan="3">${rfxHdData.open_dttm!}</td>
                </tr>
                </tbody>
            </table>
        </section>
        <!-- RFx진행방식 -->
        <section>
            <h3>RFx진행방식</h3>
            <table>
                <caption>입찰공고 RFx진행방식-view 테이블</caption>
                <colgroup>
                    <col width="20%">
                    <col width="30%">
                    <col width="20%">
                    <col width="30%">
                </colgroup>
                <tbody>
                <tr>
                    <th>경쟁방식</th>
                    <td>${rfxHdData.comp_typ_ccd_nm!}</td>
                    <th>평가비율</th>
                    <td>가격 : ${rfxHdData.cbe_ro!"0"}%, 비가격 : ${rfxHdData.npe_ro!"0"}%</td>
                </tr>
                <tr>
                    <th>총액/품목</th>
                    <td>${rfxHdData.item_slctn_typ_ccd_nm!}</td>
                    <th>협력사선정방식</th>
                    <td>${rfxHdData.slctn_typ_ccd_nm!}</td>
                </tr>
                <tr>
                    <th>현장설명회여부</th>
                    <td>${rfxHdData.bfg_yn!}</td>
                    <th>원가구성항목설정여부</th>
                    <td>${rfxHdData.coststr_use_yn!}</td>
                </tr>
                <tr>
                    <th>부분입찰가능여부</th>
                    <td>${rfxHdData.prtl_bid_perm_yn!}</td>
                    <th>제안발표여부</th>
                    <td>${rfxHdData.presn_yn!}</td>
                </tr>
                </tbody>
            </table>
        </section>
        <!-- 현장설명회/제안발표정보 -->
        <section>
            <h3>현장설명회/제안발표정보</h3>
            <table>
                <caption>입찰공고 현장설명회/제안발표정보-view 테이블</caption>
                <colgroup>
                    <col width="20%">
                    <col width="30%">
                    <col width="20%">
                    <col width="30%">
                </colgroup>
                <tbody>
                <tr>
                    <th>현장설명회일자</th>
                    <td>${rfxHdData.bfg_dttm!}</td>
                    <th>현장설명회장소</th>
                    <td>${rfxHdData.bfg_plc!}</td>
                </tr>
                <tr>
                    <th>담당자</th>
                    <td colspan="3">${rfxHdData.bfg_pic_nm!}</td>
                </tr>
                <tr>
                    <th>제안발표기간</th>
                    <td>${rfxHdData.presn_period!}</td>
                    <th>제안발표담당자</th>
                    <td>${rfxHdData.presn_pic_nm!}</td>
                </tr>
                </tbody>
            </table>
        </section>
        <!-- 계약 조건 -->
        <section>
            <h3>계약 조건</h3>
            <table>
                <caption>입찰공고 계약 조건-view 테이블</caption>
                <colgroup>
                    <col width="20%">
                    <col width="30%">
                    <col width="20%">
                    <col width="30%">
                </colgroup>
                <tbody>
                <tr>
                    <th>내/외자구분</th>
                    <td>${rfxHdData.domovrs_div_ccd_nm!}</td>
                    <th>지체상금</th>
                    <td>${rfxHdData.dfrm_ro!}</td>
                </tr>
                <tr>
                    <th>RFx통화</th>
                    <td colspan="3">${rfxHdData.cur_ccd_nm!}</td>
                </tr>
                <tr>
                    <th>지급조건/지급조건상세</th>
                    <td colspan="3">${rfxHdData.pymtmeth_ccd_nm!} / ${rfxHdData.pymtmeth_expln!}</td>
                </tr>
                <tr>
                    <th>인도조건/인도조건상세</th>
                    <td colspan="3">${rfxHdData.dlvymeth_ccd_nm!} / ${rfxHdData.dlvymeth_expln!}</td>
                </tr>
                <tr>
                    <th>계약기간</th>
                    <td colspan="3">${rfxHdData.cntr_period!}</td>
                </tr>
                </tbody>
            </table>
        </section>
        <!-- 첨부파일 -->
        <#if attData??>
            <section>
                <h3>첨부파일 <a onclick="downloadZipFile(this)">파일다운로드</a></h3>
                <table>
                    <caption>입찰공고 첨부파일-view 테이블</caption>
                    <colgroup>
                        <col width="20%">
                        <col width="*">
                    </colgroup>
                    <tbody>
                    <tr>
                        <th>구매자첨부</th>
                        <td>
                            <ul id="att_list">
                                <#list attData as item>
                                    <li><a data-arg="${item.athf_uuid}" onclick="downloadAttFile(this)">${item.athf_orig_nm}</a></li>
                                </#list>
                            </ul>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </section>
        </#if>
        <!-- 특이사항 -->
        <section>
            <h3>특이사항</h3>
            <table>
                <caption>입찰공고 첨부파일-view 테이블</caption>
                <colgroup>
                    <col width="20%">
                    <col width="*">
                </colgroup>
                <tbody>
                <tr>
                    <th>협력사공유</th>
                    <td>
                        ${rfxHdData.rfx_vd_rmk!}
                    </td>
                </tr>
                </tbody>
            </table>
        </section>
        <#if rfxDtData??>
            <!-- 품목정보 -->
            <section>
                <h3>품목정보</h3>
                <table class="brdtb">
                    <caption>입찰공고 품목정보-view 테이블</caption>
                    <colgroup>
                        <col width="15%;">
                        <col width="40%">
                        <col width="15%">
                        <col width="15%">
                        <col width="15%">
                    </colgroup>
                    <tbody>
                    <tr>
                        <th class="txct">품목코드</th>
                        <th class="txct">품목명</th>
                        <th class="txct">규격</th>
                        <th class="txct">단위</th>
                        <th class="txct">수량</th>
                    </tr>
                    <#list rfxDtData as item>
                        <tr>
                            <td class="txct">${item.item_cd!}</td>
                            <td>${item.item_nm!}</td>
                            <td class="txct">${item.item_spec!}</td>
                            <td class="txct">${item.uom_ccd_nm!}</td>
                            <td class="txrg">${item.rfx_qty!}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </section>
        </#if>
    </div>
</div>
<div class="button_area">
    <a href="javascript:onShowList()" alt="목록">목록</a>
</div>