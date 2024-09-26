<header>
    <h2>역경매 결과 품의서</h2>
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
        <h3>낙찰자 선정기준</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>총액/품목</th>
                <td>${auctionInfo.item_slctn_typ_ccd_nm?default("")}</td>
                <th>환산통화</th>
                <td>${auctionInfo.cur_ccd?default("")}</td>
            </tr>
        </table>
    </section>
    <!-- ================================================================================================================ -->
    <!-- ================================= 총액일 경우 협력사 순위 테이블 보여짐 (시작) ================================= -->
    &lt;#if auctionInfo.item_slctn_typ_ccd == 'DOC' &gt;
    <section>
        <h3>총액별 협력사순위</h3>
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
                <th>선정</th>
                <th>순위</th>
                <th>협력사코드</th>
                <th>협력사 명</th>
                <th>총액</th>
                <th>목표금액</th>
                <th></th>
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
                <td></td>
            </tr>
            &lt;/#list&gt;
        </table>
    </section>
    <!-- ================================= RFP가 아닐 경우 (끝) ================================= -->
    &lt;/#if&gt;


    <section>
        <!-- ================================= 총액일 경우 - 협력사별 견적내역 시작 ================================= -->
        &lt;#if auctionInfo.item_slctn_typ_ccd == 'DOC'&gt;


        <h3>협력사별 견적내역</h3>
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
                <th>품목코드</th>
                <th>품목명</th>
                <th>선정수량</th>
                <th>견적단가</th>
                <th>견적금액</th>
            </tr>
            <tr>
                <th>항번</th>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <th>납품장소</th>
                &lt;#else&gt;
                <th>수행장소</th>
                &lt;/#if&gt;
                <th>단위</th>
                <th>목표단가</th>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <th>납품요청일</th>
                &lt;#else&gt;
                <th>공사 시작 일자</th>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <th>납품소요일</th>
                &lt;#else&gt;
                <th>공사 종료 일자</th>
                &lt;/#if&gt;
                <th colspan="4">규격</th>
            </tr>


            &lt;#list auctionBidsList as auctionBid&gt;
            &lt;#assign rowspan = rowspanList[auctionQta_index] &gt;


            <tr>
                &lt;#if rowspan !=  0 &gt;
                <td rowspan=${rowspan * 3}>${auctionBid.slctn_rank?default("")}</td>
                <td rowspan=${rowspan * 3}>${auctionBid.vd_nm?default("")}</td>
                &lt;/#if&gt;
                <td>${auctionBid.disp_item_cd?default("")}</td>
                <td class="tl">${auctionBid.item_nm?default("")}</td>
                <td class="tr">${auctionBid.rfx_qty?default(0)}</td>
                <td class="tr">${auctionBid.rfx_item_subm_uprc?default(0)}</td>
                <td class="tr">${auctionBid.rfx_item_subm_amt?default(0)}</td>
            </tr>
            <tr>
                <td>${auctionBid.rfx_lno?default("")}</td>
                <td class="tl">${auctionBid.dlvy_plc?default("")}</td>
                <td>${auctionBid.uom_ccd?default("")}</td>
                <td class="tr">${auctionBid.rfx_trg_uprc?default(0)}</td>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <td> &lt;#if auctionBid.req_dlvy_dt?? &gt; ${auctionBid.req_dlvy_dt[0..3]?default("")}/${auctionBid.req_dlvy_dt[4..5]?default("")}/${auctionBid.req_dlvy_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;#else&gt;
                <td> &lt;#if auctionBid.const_st_dt?? &gt; ${auctionBid.const_st_dt[0..3]?default("")}/${auctionBid.const_st_dt[4..5]?default("")}/${auctionBid.const_st_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <td class="tr"> ${auctionBid.dlvy_ldtm?default(0)}  </td>
                &lt;#else&gt;
                <td> &lt;#if auctionBid.cntr_exp_dt?? &gt; ${auctionBid.cntr_exp_dt[0..3]?default("")}/${auctionBid.cntr_exp_dt[4..5]?default("")}/${auctionBid.cntr_exp_dt[6..7]?default("")}  &lt;/#if&gt;  </td>
                &lt;/#if&gt;
                <td colspan="4" class="tl">${auctionBid.item_spec?default("")}</td>
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
                <th rowspan="3">품목코드</th>
                <th rowspan="3">품목명</th>
                <th>협력사코드</th>
                <th>협력사 명</th>
                <th>선정수량</th>
                <th>견적단가</th>
                <th>견적금액</th>
            </tr>
            <tr>
                <th>순위</th>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <th>납품장소</th>
                &lt;#else&gt;
                <th>수행장소</th>
                &lt;/#if&gt;
                <th>단위</th>
                <th>목표단가</th>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <th>납품요청일</th>
                &lt;#else&gt;
                <th>공사 시작 일자</th>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <th>납품소요일</th>
                &lt;#else&gt;
                <th>공사 종료 일자</th>
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
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
                <td> &lt;#if itemVdRank.req_dlvy_dt?? &gt; ${itemVdRank.req_dlvy_dt[0..3]?default("")}/${itemVdRank.req_dlvy_dt[4..5]?default("")}/${itemVdRank.req_dlvy_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;#else&gt;
                <td> &lt;#if itemVdRank.const_st_dt?? &gt; ${itemVdRank.const_st_dt[0..3]?default("")}/${itemVdRank.const_st_dt[4..5]?default("")}/${itemVdRank.const_st_dt[6..7]?default("")} &lt;/#if&gt; </td>
                &lt;/#if&gt;
            </tr>
            <tr>
                &lt;#if auctionInfo.purc_typ_ccd == 'MT'&gt;
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