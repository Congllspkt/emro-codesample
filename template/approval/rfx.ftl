<header>
    <h2>RFX 진행 품의서</h2>
    &lt;#if approvalInfo.apvl_docno?? &gt;
    <h4>( 품의 번호 : ${approvalInfo.apvl_docno?default("")} )</h4>
    &lt;/#if&gt;
</header>
<article>
    <section>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>기안 일자</th>
                <td>${approvalInfo.apvl_rptg_dttm?default("")}</td>
                <th>기안자</th>
                <td>[${rfxInfo.oper_org_cd_nm?default("")}] ${approvalInfo.reg_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>일반 정보</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>운영조직</th>
                <td>${rfxInfo.oper_org_cd_nm?default("")}</td>
                <th>RfX 제목</th>
                <td>${rfxInfo.rfx_tit?default("")}</td>
            </tr>
            <tr>
                <th>RFX 번호</th>
                <td>${rfxInfo.rfx_no?default("")}</td>
                <th>구매유형</th>
                <td>${rfxInfo.p2p_purc_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>RFX 유형</th>
                <td>${rfxInfo.rfx_typ_ccd_nm?default("")}</td>
                <th>RFX 목적</th>
                <td>${rfxInfo.rfx_purp_ccd_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>일정</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>RFx 기간</th>
                <td> &lt;#if rfxInfo.rfx_st_dttm?? &gt; ${rfxInfo.rfx_st_dttm_ymd[0..3]?default("")}/${rfxInfo.rfx_st_dttm_ymd[4..5]?default("")}/${rfxInfo.rfx_st_dttm_ymd[6..7]?default("")}  ${rfxInfo.rfx_st_dttm_hour?default(0)} : ${rfxInfo.rfx_st_dttm_min?default(0)} &lt;/#if&gt; ~  &lt;#if rfxInfo.rfx_clsg_dttm?? &gt; ${rfxInfo.rfx_clsg_dttm_ymd[0..3]?default("")}/${rfxInfo.rfx_clsg_dttm_ymd[4..5]?default("")}/${rfxInfo.rfx_clsg_dttm_ymd[6..7]?default("")} ${rfxInfo.rfx_clsg_dttm_hour?default("")} : ${rfxInfo.rfx_clsg_dttm_min?default("")}  &lt;/#if&gt; </td>
                <th>개찰 일시</th>
                <td> &lt;#if rfxInfo.open_dt?? &gt; ${rfxInfo.open_dttm_ymd[0..3]?default("")}/${rfxInfo.open_dttm_ymd[4..5]?default("")}/${rfxInfo.open_dttm_ymd[6..7]?default("")}  ${rfxInfo.open_dt_hour?default("")} : ${rfxInfo.open_dt_min?default("")} &lt;/#if&gt; </td>
            </tr>
        </table>
    </section>
    <section>
        <h3>진행방식</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>경쟁 유형</th>
                <td>${rfxInfo.comp_typ_ccd_nm?default("")}</td>
                <th>선정 유형</th>
                <td>${rfxInfo.slctn_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>품목 선정 유형</th>
                <td>${rfxInfo.item_slctn_typ_ccd_nm?default("")}</td>
                <th>CostStructure(RFX) 사용 여부</th>
                <td>${rfxInfo.coststr_use_yn_str?default("")}</td>
            </tr>
            <tr>
                <th>제안설명회 여부</th>
                <td>${rfxInfo.bfg_yn_str?default("")}</td>
                <th>제안발표회 여부</th>
                <td>${rfxInfo.presn_yn_str?default("")}</td>
            </tr>
            <tr>
                <th>부분 투찰 허용 여부</th>
                <td>${rfxInfo.prtl_bid_perm_yn_str?default("")}</td>
                &lt;#if rfxInfo.rfx_typ_ccd == 'RFP'&gt;
                <th>평가 비율</th>
                <td>가격 : ${rfxInfo.cbe_ro?default("")} % , 비가격 : ${rfxInfo.npe_ro?default("")} % ( 협력사 공개여부 : ${rfxInfo.npe_ro_vd_pub_yn?default("")} )</td>
                &lt;#else&gt;
                <th></th>
                <td></td>
                &lt;/#if&gt;
            </tr>
        </table>
    </section>
    <section>
        <h3>계약 조건</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>내외자 구분</th>
                <td>${rfxInfo.domovrs_div_ccd_nm?default("")}</td>
                <th>지체상금 비율</th>
                <td>${rfxInfo.dfrm_ro?default(0)} / 1000</td>
            </tr>
            <tr>
                <th>통화</th>
                <td>${rfxInfo.cur_ccd?default("")}</td>
                <th>계약 기간</th>
                <td>&lt;#if rfxInfo.cntr_st_dt?? &gt; ${rfxInfo.cntr_st_dt[0..3]?default("")}/${rfxInfo.cntr_st_dt[4..5]?default("")}/${rfxInfo.cntr_st_dt[6..7]?default("")} &lt;/#if&gt; ~ &lt;#if rfxInfo.cntr_exp_dt?? &gt; ${rfxInfo.cntr_exp_dt[0..3]?default("")}/${rfxInfo.cntr_exp_dt[4..5]?default("")}/${rfxInfo.cntr_exp_dt[6..7]?default("")} &lt;/#if&gt;</td>
            </tr>
            <tr>
                <th>지급방법/설명</th>
                <td colspan="3">${rfxInfo.pymtmeth_ccd_nm?default("")} ${rfxInfo.pymtmeth_expln?default("")}</td>
            </tr>
            <tr>
                <th>납품방법/설명</th>
                <td colspan="3">${rfxInfo.dlvymeth_ccd_nm?default("")} ${rfxInfo.dlvymeth_expln?default("")}</td>
            </tr>
            &lt;#if rfxInfo.rfx_buyer_rmk??&gt;
            <tr>
                <td class='staticcell5'>구매사 비고</td>
                <td class='contentcell' colspan="2">${(rfxInfo.rfx_buyer_rmk)!?replace("\n", "<br>")}
                </td>
            </tr>
            &lt;/#if&gt;
        </table>
    </section>

    <!-- =================================구매유형이 물품일 경우 물품정보 테이블 시작 ================================= -->
    &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
    <section>
        <h3>품목 정보</h3>
        <table class="tc">
            <colgroup>
                <col style="width:7%" />
                <col style="width:12%" />
                <col style="width:44%" />
                <col style="width:9%" />
                <col style="width:14%" />
                <col style="width:14%" />
            </colgroup>
            <tr>
                <th rowspan="3">순번</th>
                <th>품목 코드</th>
                <th>품목 명</th>
                <th>수량</th>
                <th>요청 단가</th>
                <th>목표 단가</th>
            </tr>
            <tr>
                <th>항번</th>
                <th>품목 규격</th>
                <th>UOM</th>
                <th>요청 금액</th>
                <th>구매그룹</th>
            </tr>
            <tr>
                <th>요청 납품 일자</th>
                <th colspan="4">납품 장소</th>
            </tr>
            &lt;#list rfxItems as rfxItem&gt;
            <tr>
                <td rowspan="3">${rfxItem_index+1}</td>
                <td>${rfxItem.disp_item_cd?default("")}</td>
                <td class="tl">${rfxItem.item_nm?default("")}</td>
                <td class="tr">${rfxItem.rfx_qty?default("")}</td>
                <td class="tr">${rfxItem.rfx_req_uprc?default("")}</td>
                <td class="tr">${rfxItem.rfx_trg_uprc?default(0)}</td>
            </tr>
            <tr>
                <td>${rfxItem.rfx_lno?default("")}</td>
                <td class="tl">${rfxItem.item_spec?default("")}</td>
                <td>${rfxItem.uom_ccd?default("")}</td>
                <td class="tr">${rfxItem.rfq_amt?default("")}</td>
                <td>${rfxItem.purc_grp_cd?default("")} ${rfxItem.purc_grp_nm?default("")}</td>
            </tr>
            <tr>
                <td>
                    &lt;#if rfxItem.req_dlvy_dt?? &gt;
                    ${rfxItem.req_dlvy_dt[0..3]?default("")}/${rfxItem.req_dlvy_dt[4..5]?default("")}/${rfxItem.req_dlvy_dt[6..7]?default("")}
                    &lt;/#if&gt;
                </td>
                <td colspan="4" class="tl">${rfxItem.dlvy_plc?default("")}</td>
            </tr>
            &lt;/#list&gt;
        </table>
    </section>
    <!-- =================================구매유형이 물품일 경우 물품정보 테이블 끝 ================================= -->
    &lt;#else&gt;
    <!-- =================================구매유형이 공사용역일 경우 물품정보 테이블 시작 ================================= -->
    <section>
        <h3>공사/용역 정보</h3>
        <table class="tc">
            <colgroup>
                <col style="width:7%" />
                <col style="width:12%" />
                <col style="width:12%" />
                <col style="width:32%" />
                <col style="width:9%" />
                <col style="width:14%" />
                <col style="width:14%" />
            </colgroup>
            <tr>
                <th rowspan="3">순번</th>
                <th>품목 코드</th>
                <th colspan="2">품목 명</th>
                <th>수량</th>
                <th>요청 단가</th>
                <th>목표 단가</th>
            </tr>
            <tr>
                <th>항번</th>
                <th colspan="2">품목 규격</th>
                <th>UOM</th>
                <th>요청 금액</th>
                <th>구매그룹</th>
            </tr>
            <tr>
                <th>공사 시작 일자</th>
                <th>공사 종료 일자</th>
                <th colspan="4">수행 장소</th>
            </tr>
            &lt;#list rfxItems as rfxItem&gt;
            <tr>
                <td rowspan="3">${rfxItem_index+1}</td>
                <td>${rfxItem.disp_item_cd?default("")}</td>
                <td colspan="2" class="tl">${rfxItem.item_nm?default("")}</td>
                <td class="tr">${rfxItem.rfx_qty?default("")}</td>
                <td class="tr">${rfxItem.rfx_req_uprc?default("")}</td>
                <td class="tr">${rfxItem.rfx_trg_uprc?default(0)}</td>
            </tr>
            <tr>
                <td>${rfxItem.rfx_lno?default("")}</td>
                <td colspan="2" class="tl">${rfxItem.item_spec?default("")}</td>
                <td>${rfxItem.uom_ccd?default("")}</td>
                <td class="tr">${rfxItem.rfq_amt?default("")}</td>
                <td>${rfxItem.purc_grp_cd?default("")} ${rfxItem.purc_grp_nm?default("")}</td>
            </tr>
            <tr>
                <td>&lt;#if rfxItem.const_st_dt?? &gt;${rfxItem.const_st_dt[0..3]?default("")}/${rfxItem.const_st_dt[4..5]?default("")}/${rfxItem.const_st_dt[6..7]?default("")} &lt;/#if&gt; </td>
                <td>&lt;#if rfxItem.const_exp_dt?? &gt;${rfxItem.const_exp_dt[0..3]?default("")}/${rfxItem.const_exp_dt[4..5]?default("")}/${rfxItem.const_exp_dt[6..7]?default("")} &lt;/#if&gt;</td>
                <td colspan="4" class="tl">${rfxItem.dlvy_plc?default("")}</td>
            </tr>
            &lt;/#list&gt;
        </table>
    </section>
    &lt;/#if&gt;
    <!-- =================================구매유형이 공사용역일 경우 물품정보 테이블 끝 ================================= -->
    &lt;#if rfxInfo.comp_typ_ccd != 'P'&gt;
    <section>
        <h3>협력사 정보</h3>
        <table class="tc">
            <colgroup>
                <col style="width:7%" />
                <col style="width:12%" />
                <col style="width:44%" />
                <col style="width:9%" />
                <col style="width:12%" />
                <col style="width:14%" />
            </colgroup>
            <tr>
                <th>No.</th>
                <th>협력사 코드</th>
                <th>협력사 명</th>
                <th>협력사 담당자 명</th>
                <th>협력사 담당자 전화</th>
                <th>협력사 담당자 이메일</th>
            </tr>
            &lt;#list vendorInfoList as vendorInfo&gt;
            <tr>
                <td>${vendorInfo_index+1}</td>
                <td>${vendorInfo.disp_vd_cd?default("")}</td>
                <td class="tl">${vendorInfo.vd_nm?default("")}</td>
                <td>${vendorInfo.vd_pic_nm?default("")}</td>
                <td>${vendorInfo.vd_pic_tel?default("")}</td>
                <td class="tl">${vendorInfo.vd_pic_eml?default("")}</td>
            </tr>
            &lt;/#list&gt;
        </table>
    </section>
    &lt;/#if&gt;
</article>