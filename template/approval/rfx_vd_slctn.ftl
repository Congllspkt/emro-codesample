<header>
    <h2>RFx 협력사 선정 결재</h2>
    &lt;#if approvalInfo.apvl_docno?? &gt;
    <h4>( 결재 문서번호 : ${approvalInfo.apvl_docno?default("")} )</h4>
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
                <th>상신자</th>
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
                <th>Rfx 제목</th>
                <td>${rfxInfo.rfx_tit?default("")}</td>
            </tr>
            <tr>
                <th>Rfx 번호</th>
                <td>${rfxInfo.rfx_no?default("")}</td>
                <th>구매유형</th>
                <td>${rfxInfo.p2p_purc_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>Rfx 유형</th>
                <td>${rfxInfo.rfx_typ_ccd_nm?default("")}</td>
                <th>Rfx 목적</th>
                <td>${rfxInfo.rfx_purp_ccd_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>선정 유형</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>선정 유형</th>
                <td>${rfxInfo.slctn_typ_ccd_nm?default("")}</td>
                <th>품목 선정 유형</th>
                <td>${rfxInfo.item_slctn_typ_ccd_nm?default("")}</td>
            </tr>
            &lt;#if rfxInfo.rfx_typ_ccd == 'RFP'&gt;
            <tr>
                <th>가격평가 비율</th>
                <td class="tr">${rfxInfo.cbe_ro?default(0)} %</td>
                <th>비가격평가 비율</th>
                <td class="tr">${rfxInfo.npe_ro?default(0)} %</td>
            </tr>
            &lt;/#if&gt;
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
    &lt;#if rfxInfo.item_slctn_typ_ccd == 'DOC' &gt;
    <section>
        &lt;#if rfxInfo.rfx_typ_ccd == 'RFQ'&gt;
        <h3>총액별 협력사순위</h3>
        &lt;#else&gt;
        <h3>종합평가 협력사순위</h3>
        &lt;/#if&gt;
        <!-- ================================= RFP일 경우 (시작) ================================= -->
        &lt;#if rfxInfo.rfx_typ_ccd == 'RFP'&gt;
        <table class="tc">
            <colgroup>
                <col style="width:5%" />
                <col style="width:7%" />
                <col style="width:10%" />
                <col style="width:22%" />
                <col style="width:12%" />
                <col style="width:12%" />
                <col style="width:8%" />
                <col style="width:8%" />
                <col style="width:8%" />
                <col style="width:8%" />
            </colgroup>
            <tr>
                <th>선정 여부</th>
                <th>순위</th>
                <th>협력사 코드</th>
                <th>협력사 명</th>
                <th>투찰 금액</th>
                <th>목표 금액</th>
                <th>평가 합계 점수</th>
                <th>가격평가 점수</th>
                <th>비가격평가 합계 점수</th>
                <th>목표 금액 대비 비율</th>
            </tr>
            &lt;#list vendorRankInfoList as vendorRankInfo&gt;
            <tr>
                &lt;#if vendorRankInfo.slctn_yn == 'Y' &gt;
                <td><img src="ui/assets/img/grid/ico_choice.png"></td>
                &lt;#else&gt;
                <td><img src="ui/assets/img/grid/ico_nonchoice.png"></td>
                &lt;/#if&gt;
                <td>${vendorRankInfo.slctn_rank?default("")}</td>
                <td>${vendorRankInfo.vd_cd?default("")}</td>
                <td>${vendorRankInfo.vd_nm?default("")}</td>
                <td class="tr">${vendorRankInfo.rfx_bid_amt?default(0)}</td>
                <td class="tr">${vendorRankInfo.tgt_amt?default(0)}</td>
                <td class="tr">${vendorRankInfo.eval_ttl_sc?default(0)}</td>
                <td class="tr">${vendorRankInfo.cbe_sc?default(0)}</td>
                <td class="tr">${vendorRankInfo.npe_ttl_sc?default(0)}</td>
                <td class="tr">${vendorRankInfo.tgt_amt_rate?default(0)}</td>
            </tr>
            &lt;/#list&gt;
        </table>
        <!-- ================================= RFP일 경우 (끝) ================================= -->
        &lt;#else&gt;
        <!-- ================================= RFP가 아닐 경우 (시작) ================================= -->
        <table class="tc">
            <colgroup>
                <col style="width:7%" />
                <col style="width:9%" />
                <col style="width:10%" />
                <col style="width:31%" />
                <col style="width:15%" />
                <col style="width:15%" />
                <col style="width:13%" />
            </colgroup>
            <tr>
                <th>선정 유형</th>
                <th>순위</th>
                <th>협력사 코드</th>
                <th>협력사 명</th>
                <th>투찰 금액</th>
                <th>목표 금액</th>
                <th>평가 합계 점수</th>
            </tr>
            &lt;#list vendorRankInfoList as vendorRankInfo&gt;
            <tr>
                &lt;#if vendorRankInfo.slctn_yn == 'Y' &gt;
                <td><img src="ui/assets/img/grid/ico_choice.png"></td>
                &lt;#else&gt;
                <td><img src="ui/assets/img/grid/ico_nonchoice.png"></td>
                &lt;/#if&gt;
                <td>${vendorRankInfo.slctn_rank?default("")}</td>
                <td>${vendorRankInfo.vd_cd?default("")}</td>
                <td>${vendorRankInfo.vd_nm?default("")}</td>
                <td class="tr">${vendorRankInfo.rfx_bid_amt?default(0)}</td>
                <td class="tr">${vendorRankInfo.tgt_amt?default(0)}</td>
                <td class="tr">${vendorRankInfo.tgt_amt_rate?default(0)}</td>
            </tr>
            &lt;/#list&gt;
        </table>
    </section>
    &lt;/#if&gt;
    <!-- ================================= RFP가 아닐 경우 (끝) ================================= -->
    &lt;/#if&gt;


    <section>
        <!-- ================================= 총액일 경우 - 협력사별 견적내역 시작 ================================= -->
        &lt;#if rfxInfo.item_slctn_typ_ccd == 'BYTOT'&gt;


        <h3>협력사 별 견적 내역</h3>
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
                <th rowspan="3">순위</th>
                <th rowspan="3">협력사 명</th>
                <th>품목 코드</th>
                <th>품목 명</th>
                <th>선정 수량</th>
                <th>견적 단가</th>
                <th>견적 금액</th>
            </tr>
            <tr>
                <th>항번</th>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <th>납품장소</th>
                &lt;#else&gt;
                <th>수행장소</th>
                &lt;/#if&gt;
                <th>단위</th>
                <th>품목 제출 단가</th>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <th>납품 요청 일자</th>
                &lt;#else&gt;
                <th>공사 시작 일자</th>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <th>납품 소요 일자</th>
                &lt;#else&gt;
                <th>공사 종료 일자</th>
                &lt;/#if&gt;
                <th colspan="4">규격</th>
            </tr>


            &lt;#list rfxBidsList as rfxBid&gt;
            &lt;#assign rowspan = rowspanList[rfxBid_index] &gt;


            <tr>
                &lt;#if rowspan !=  0 &gt;
                <td rowspan=${rowspan * 3}>${rfxBid.slctn_rank?default("")}</td>
                <td rowspan=${rowspan * 3}>${rfxBid.vd_nm?default("")}</td>
                &lt;/#if&gt;
                <td>${rfxBid.disp_item_cd?default("")}</td>
                <td class="tl">${rfxBid.item_nm?default("")}</td>
                <td class="tr">${rfxBid.rfx_qty?default(0)}</td>
                <td class="tr">${rfxBid.rfx_item_subm_uprc?default(0)}</td>
                <td class="tr">${rfxBid.rfx_item_subm_amt?default(0)}</td>
            </tr>
            <tr>
                <td>${rfxBid.rfx_lno?default("")}</td>
                <td class="tl">${rfxBid.dlvy_plc?default("")}</td>
                <td>${rfxBid.uom_ccd?default("")}</td>
                <td class="tr">${rfxBid.rfx_item_subm_uprc?default(0)}</td>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <td> &lt;#if rfxBid.req_dlvy_dt?? &gt; ${rfxBid.req_dlvy_dt[0..3]?default("")}/${rfxBid.req_dlvy_dt[4..5]?default("")}/${rfxBid.req_dlvy_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;#else&gt;
                <td> &lt;#if rfxBid.const_st_dt?? &gt; ${rfxBid.const_st_dt[0..3]?default("")}/${rfxBid.const_st_dt[4..5]?default("")}/${rfxBid.const_st_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <td class="tr"> ${rfxBid.dlvy_ldtm?default(0)}  </td>
                &lt;#else&gt;
                <td> &lt;#if rfxBid.cntr_exp_dt?? &gt; ${rfxBid.cntr_exp_dt[0..3]?default("")}/${rfxBid.cntr_exp_dt[4..5]?default("")}/${rfxBid.cntr_exp_dt[6..7]?default("")}  &lt;/#if&gt;  </td>
                &lt;/#if&gt;
                <td colspan="4" class="tl">${rfxBid.item_spec?default("")}</td>
            </tr>
            &lt;/#list&gt;
        </table>
        <!-- ================================= 총액일 경우 - 협력사별 견적내역 끝 ================================= -->
        &lt;#else&gt;
        <!-- ================================= 품목일 경우 - 품목별 견적내역 시작 ================================= -->
        <h3>품목별 협력사 순위</h3>
        <table class="tc">
            <colgroup>
                <col style="width:7%" />
                <col style="width:10%" />
                <col style="width:33%" />
                <col style="width:10%" />
                <col style="width:13%" />
                <col style="width:7%" />
                <col style="width:10%" />
                <col style="width:10%" />
            </colgroup>
            <tr>
                <th rowspan="3">항번</th>
                <th rowspan="3">품목 코드</th>
                <th rowspan="3">품목 명</th>
                <th>협력사 코드</th>
                <th>협력사 명</th>
                <th>선정 수량</th>
                <th>품목 제출 단가</th>
                <th>품목 제출 금액</th>
            </tr>
            <tr>
                <th>순위</th>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <th>납품 장소</th>
                &lt;#else&gt;
                <th>수행 장소</th>
                &lt;/#if&gt;
                <th>단위</th>
                <th>목표 단가</th>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <th>납품 요청 일자</th>
                &lt;#else&gt;
                <th>공사 시작 일자</th>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <th>납품 소요 일자</th>
                &lt;#else&gt;
                <th>수행 종료 일자</th>
                &lt;/#if&gt;
                <th colspan="4">규격</th>
            </tr>


            &lt;#list itemVdRankList as itemVdRank&gt;
            &lt;#assign rowspan = rowspanList[itemVdRank_index] &gt;
            <tr>
                &lt;#if rowspan !=  0 &gt;
                <td rowspan=${rowspan * 3}>${itemVdRank.rfx_lno?default("")}</td>
                <td rowspan=${rowspan * 3}>${itemVdRank.disp_item_cd?default("")}</td>
                <td rowspan=${rowspan * 3} class="tl">${itemVdRank.item_nm?default("")}</td>
                &lt;/#if&gt;
                <td>${itemVdRank.vd_cd?default("")}</td>
                <td>${itemVdRank.vd_nm?default("")}</td>
                &lt;#if itemVdRank.slctn_yn_for_aprv == 'Y' &gt;
                <td class="tr" style="background: #d4ebf5;">${itemVdRank.slctn_qty?default(0)}</td>
                &lt;#else&gt;
                <td class="tr">${itemVdRank.slctn_qty?default(0)}</td>
                &lt;/#if&gt;
                <td class="tr">${itemVdRank.rfx_item_subm_uprc?default(0)}</td>
                <td class="tr">${itemVdRank.rfx_item_subm_amt?default(0)}</td>
            </tr>
            <tr>
                <td>${itemVdRank.ranking?default("")}</td>
                <td class="tl">${itemVdRank.dlvy_plc?default("")}</td>
                <td>${itemVdRank.uom_ccd?default("")}</td>
                <td class="tr">${itemVdRank.rfx_trg_uprc?default(0)}</td>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <td> &lt;#if itemVdRank.req_dlvy_dt?? &gt; ${itemVdRank.req_dlvy_dt[0..3]?default("")}/${itemVdRank.req_dlvy_dt[4..5]?default("")}/${itemVdRank.req_dlvy_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;#else&gt;
                <td> &lt;#if itemVdRank.const_st_dt?? &gt; ${itemVdRank.const_st_dt[0..3]?default("")}/${itemVdRank.const_st_dt[4..5]?default("")}/${itemVdRank.const_st_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
                <td class="tr"> ${itemVdRank.dlvy_ldtm?default(0)}  </td>
                &lt;#else&gt;
                <td> &lt;#if itemVdRank.const_exp_dt?? &gt; ${itemVdRank.const_exp_dt[0..3]?default("")}/${itemVdRank.const_exp_dt[4..5]?default("")}/${itemVdRank.const_exp_dt[6..7]?default("")}  &lt;/#if&gt;  </td>
                &lt;/#if&gt;
                <td colspan="4" class="tl">${itemVdRank.item_spec?default("")}</td>
            </tr>
            &lt;/#list&gt;
        </table>
        <!-- ================================= 품목일 경우 - 품목별 견적내역 끝 ================================= -->
        &lt;/#if&gt;
    </section>
</article>