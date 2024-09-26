<header>
    <h2>역경매진행품의서</h2>
    &lt;#if approvalInfo.apvl_docno?? &gt;
    <h4>( 품의번호 : ${approvalInfo.apvl_docno?default("")} )</h4>
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
                <th>기안일자</th>
                <td>${approvalInfo.apvl_rptg_dttm?default("")}</td>
                <th>기안자</th>
                <td>[${auctionInfo.oper_org_cd_nm?default("")}] ${approvalInfo.reg_nm?default("")}</td>
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
                <td>${auctionInfo.oper_org_cd_nm?default("")}</td>
                <th>역경매명</th>
                <td>${auctionInfo.rfx_tit?default("")}</td>
            </tr>
            <tr>
                <th>역경매번호/차수</th>
                <td>${auctionInfo.rfx_no?default("")} / ${auctionInfo.rfx_rnd?default("")}</td>
                <th>구매유형</th>
                <td>${auctionInfo.p2p_purc_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>Rfx 유형</th>
                <td>${auctionInfo.rfx_typ_ccd_nm?default("")}</td>
                <th>역경매결과유형</th>
                <td>${auctionInfo.rfx_purp_ccd_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>역경매일정</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col/>
            </colgroup>
            <tr>
                <th>역경매기간</th>
                <td> &lt;#if auctionInfo.rfx_st_dttm?? &gt; ${auctionInfo.rfx_st_dttm_ymd[0..3]?default("")}/${auctionInfo.rfx_st_dttm_ymd[4..5]?default("")}/${auctionInfo.rfx_st_dttm_ymd[6..7]?default("")}  : ${auctionInfo.rfx_st_dttm_hour?default(0)} : ${auctionInfo.rfx_st_dttm_min?default(0)} &lt;/#if&gt; ~  &lt;#if auctionInfo.rfx_clsg_dttm?? &gt; ${auctionInfo.rfx_clsg_dttm_ymd[0..3]?default("")}/${auctionInfo.rfx_clsg_dttm_ymd[4..5]?default("")}/${auctionInfo.rfx_clsg_dttm_ymd[6..7]?default("")} : ${auctionInfo.rfx_clsg_dttm_hour?default("")} : ${auctionInfo.rfx_clsg_dttm_min?default("")}  &lt;/#if&gt; </td>
            </tr>
        </table>
    </section>
    <section>
        <h3>역경매진행방식</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>경쟁방식</th>
                <td>${auctionInfo.comp_typ_ccd_nm?default("")}</td>
                <th>역경매유형</th>
                <td>${auctionInfo.slctn_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>총액/품목</th>
                <td>${auctionInfo.item_slctn_typ_ccd_nm?default("")}</td>
                <th>최소입찰단위</th>
                <td>${auctionInfo.coststr_use_yn_str?default("")}</td>
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
                <th>내/외자구분</th>
                <td>${auctionInfo.domovrs_div_ccd_nm?default("")}</td>
                <th>지체상금</th>
                <td>${auctionInfo.dfrm_ro?default(0)} /1000</td>
            </tr>
            <tr>
                <th>역경매 통화</th>
                <td>${auctionInfo.cur_ccd?default("")}</td>
                <th>계약기간</th>
                <td>&lt;#if auctionInfo.cntr_st_dt?? &gt; ${auctionInfo.cntr_st_dt[0..3]?default("")}/${auctionInfo.cntr_st_dt[4..5]?default("")}/${auctionInfo.cntr_st_dt[6..7]?default("")} &lt;/#if&gt; ~ &lt;#if auctionInfo.cntr_exp_dt?? &gt; ${auctionInfo.cntr_exp_dt[0..3]?default("")}/${auctionInfo.cntr_exp_dt[4..5]?default("")}/${auctionInfo.cntr_exp_dt[6..7]?default("")} &lt;/#if&gt;</td>
            </tr>
            <tr>
                <th>지급조건</th>
                <td colspan="3">${auctionInfo.pymtmeth_ccd_nm?default("")} ${auctionInfo.pymtmeth_expln?default("")}</td>
            </tr>
            <tr>
                <th>인도조건</th>
                <td colspan="3">${auctionInfo.dlvymeth_ccd_nm?default("")} ${auctionInfo.dlvymeth_expln?default("")}</td>
            </tr>
            <#if auctionInfo.rfx_buyer_rmk??>
                <tr>
                    <td class='staticcell5'>내부공유 및 담당의견</td>
                    <td class='contentcell' colspan="3">${(auctionInfo.rfx_buyer_rmk)!?replace("\n", "<br>")}
                    </td>
                </tr>
            </#if>
        </table>
    </section>

    <!-- =================================구매유형이 물품일 경우 물품정보 테이블 시작 ================================= -->
    &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
    <section>
        <h3>품의내역</h3>
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
                <th>품목코드</th>
                <th>품목명</th>
                <th>요청수량</th>
                <th>요청단가</th>
                <th></th>
            </tr>
            <tr>
                <th>항번</th>
                <th>규격</th>
                <th>단위</th>
                <th>요청금액</th>
                <th>구매그룹</th>
            </tr>
            <tr>
                <th>납기일자</th>
                <th colspan="4">납품장소</th>
            </tr>
            &lt;#list rfxItems as rfxItem&gt;
            <tr>
                <td rowspan="3">${rfxItem_index+1}</td>
                <td>${rfxItem.disp_item_cd?default("")}</td>
                <td class="tl">${rfxItem.item_nm?default("")}</td>
                <td class="tr">${rfxItem.rfx_qty?default("")}</td>
                <td class="tr">${rfxItem.rfx_req_uprc?default("")}</td>
                <td class="tr"></td>
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
        <h3>품의내역</h3>
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
                <th>품목코드</th>
                <th colspan="2">품목명</th>
                <th>요청수량</th>
                <th>요청단가</th>
                <th>목표단가</th>
            </tr>
            <tr>
                <th>항번</th>
                <th colspan="2">규격</th>
                <th>단위</th>
                <th>요청금액</th>
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

    <section>
        <h3>협력사정보</h3>
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
                <th>협력사코드</th>
                <th>협력사 명</th>
                <th>협력사담당자</th>
                <th>협력사전화번호</th>
                <th>협력사담당자 Email</th>
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
</article>