<header>
    <h2>협상 업체 선정 결재</h2>
    <#if approvalInfo.aprv_docno?? >
    <h4>( 결재 문서번호 : ${approvalInfo.apvl_docno?default("")} )</h4>
    </#if>
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
                <th>결재 상신 일시</th>
                <td>${approvalInfo.apvl_rptg_dttm?default("")}</td>
                <th>결재 상신자</th>
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
                <th>RFX 제목</th>
                <td>${rfxInfo.rfx_tit?default("")}</td>
            </tr>
            <tr>
                <th>RFX 번호</th>
                <td>${rfxInfo.rfx_no?default("")}</td>
                <th>구매 유형</th>
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
        <h3>낙찰자 선정기준</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>낙찰자선정방식</th>
                <td>${rfxInfo.slctn_typ_ccd_nm?default("")}</td>
                <th>품목 선정 유형</th>
                <td>${rfxInfo.item_slctn_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>가격평가</th>
                <td class="tr">${rfxInfo.cbe_ro?default(0)} %</td>
                <th>비가격평가</th>
                <td class="tr">${rfxInfo.npe_ro?default(0)} %</td>
            </tr>
            <tr>
                <th>통화</th>
                <td>${rfxInfo.cur_ccd?default("")}</td>
                <th></th>
                <td></td>
            </tr>
        </table>
    </section>
<!-- ================================================================================================================ -->
<!-- ================================= 총액일 경우 협력사 순위 테이블 보여짐 (시작) ================================= -->
    <section>
        <h3>종합평가 협력사 순위</h3>
        <!-- ================================= RFP일 경우 (시작) ================================= -->
        <table class="tc">
            <colgroup>
                <col style="width:5%" />
                <col style="width:7%" />
                <col style="width:10%" />
                <col style="width:22%" />
                <col style="width:12%" />
                <col style="width:12%" />
                <col style="width:12%" />
            </colgroup>
            <tr>
                <th>선정 여부</th>
                <th>선정 순위</th>
                <th>협력사 코드</th>
                <th>협력사 명</th>
                <th>RFX 투찰 금액</th>
                <th>협상 금액</th>
                <th>목표 금액</th>
            </tr>
            <#list vendorRankInfoList as vendorRankInfo>
            <tr>
                <#if vendorRankInfo.slctn_yn == 'Y' >
                <td><img src="ui/assets/img/grid/ico_choice.png"></td>
                <#else>
                <td><img src="ui/assets/img/grid/ico_nonchoice.png"></td>
                </#if>
                <td>${vendorRankInfo.slctn_rank?default("")}</td>
                <td>${vendorRankInfo.vd_cd?default("")}</td>
                <td>${vendorRankInfo.vd_nm?default("")}</td>
                <td class="tr">${vendorRankInfo.rfx_bid_amt?default(0)}</td>
                <td class="tr">${vendorRankInfo.nego_amt?default("")}</td>
                <td class="tr">${vendorRankInfo.tgt_amt?default(0)}</td>
            </tr>
            </#list>
        </table>
    </section>

    <section>
        <h3>협력사별 견적 상세</h3>
        <table class="tc">
            <colgroup>
                <col style="width:7%" />
                <col style="width:12%" />
                <col style="width:10%" />
                <col style="width:37%" />
                <col style="width:10%" />
                <col style="width:12%" />
                <col style="width:12%" />
            </colgroup>
            <tr>
                <th rowspan="3">선정 순위</th>
                <th rowspan="3">협력사 명</th>
                <th>품목 코드</th>
                <th>품목 명</th>
                <th>선정 수량</th>
                <th>협상 품목 단가</th>
                <th>협상 품목 금액</th>
            </tr>
            <tr>
                <th>항번</th>
                <th>납품 장소</th>
                <th>UOM</th>
                <th>RFX 품목 제출 단가</th>
                <th>요청 납품 일자</th>
            </tr>
            <tr>
                <th>납품 리드타임</th>
                <th colspan="4">규격</th>
            </tr>
        <#list rfxBidsList as rfxBid>
            <#assign rowspan = rowspanList[rfxBid_index] >
            <tr>
                <#if rowspan !=  0 >
                <td rowspan=${rowspan * 3}>${rfxBid.slctn_rank?default("")}</td>
                <td rowspan=${rowspan * 3}>${rfxBid.vd_nm?default("")}</td>
                </#if>
                <td>${rfxBid.disp_item_cd?default("")}</td>
                <td class="tl">${rfxBid.disp_item_nm?default("")}</td>
                <td class="tr">${rfxBid.rfx_qty?default(0)}</td>
                <td class="tr">${rfxBid.nego_item_uprc?default("")}</td>
                <td class="tr">${rfxBid.nego_item_amt?default("")}</td>
            </tr>
            <tr>
                <td>${rfxBid.rfx_lno?default("")}</td>
                <td class="tl">${rfxBid.dlvy_plc?default("")}</td>
                <td>${rfxBid.uom_ccd?default("")}</td>
                <td class="tr">${rfxBid.rfx_trg_uprc?default(0)}</td>
                <td> <#if rfxBid.req_dlvy_dt?? > ${rfxBid.req_dlvy_dt[0..3]?default("")}/${rfxBid.req_dlvy_dt[4..5]?default("")}/${rfxBid.req_dlvy_dt[6..7]?default("")} </#if> </td>
            </tr>
            <tr>
                <td class="tr"> ${rfxBid.dlvy_ldtm?default(0)}  </td>
                <td colspan="4" class="tl">${rfxBid.item_spec?default("")}</td>
            </tr>
        </#list>
        </table>
    </section>
</article>