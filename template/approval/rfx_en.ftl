<header>
    <h2>RFX Approval</h2>
    &lt;#if approvalInfo.apvl_docno?? &gt;
    <h4>( Approval request number : ${approvalInfo.apvl_docno?default("")} )</h4>
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
                <th>Proposal Date</th>
                <td>${approvalInfo.apvl_rptg_dttm?default("")}</td>
                <th>Drafter</th>
                <td>[${rfxInfo.oper_org_cd_nm?default("")}] ${approvalInfo.reg_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>General Infomation</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>OperationOrg</th>
                <td>${rfxInfo.oper_org_cd_nm?default("")}</td>
                <th>RFX Title</th>
                <td>${rfxInfo.rfx_tit?default("")}</td>
            </tr>
            <tr>
                <th>RFX Number</th>
                <td>${rfxInfo.rfx_no?default("")}</td>
                <th>Purchase type</th>
                <td>${rfxInfo.p2p_purc_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>RFX Type</th>
                <td>${rfxInfo.rfx_typ_ccd_nm?default("")}</td>
                <th>PO Creation Type</th>
                <td>${rfxInfo.rfx_purp_ccd_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>Schedule</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>RFx Duration</th>
                <td> &lt;#if rfxInfo.rfx_st_dttm?? &gt; ${rfxInfo.rfx_st_dttm_ymd[0..3]?default("")}/${rfxInfo.rfx_st_dttm_ymd[4..5]?default("")}/${rfxInfo.rfx_st_dttm_ymd[6..7]?default("")}  ${rfxInfo.rfx_st_dttm_hour?default(0)} : ${rfxInfo.rfx_st_dttm_min?default(0)} &lt;/#if&gt; ~  &lt;#if rfxInfo.rfx_clsg_dttm?? &gt; ${rfxInfo.rfx_clsg_dttm_ymd[0..3]?default("")}/${rfxInfo.rfx_clsg_dttm_ymd[4..5]?default("")}/${rfxInfo.rfx_clsg_dttm_ymd[6..7]?default("")} ${rfxInfo.rfx_clsg_dttm_hour?default("")} : ${rfxInfo.rfx_clsg_dttm_min?default("")}  &lt;/#if&gt; </td>
                <th>Open DateTime</th>
                <td> &lt;#if rfxInfo.open_dt?? &gt; ${rfxInfo.open_dttm_ymd[0..3]?default("")}/${rfxInfo.open_dttm_ymd[4..5]?default("")}/${rfxInfo.open_dttm_ymd[6..7]?default("")}  ${rfxInfo.open_dt_hour?default("")} : ${rfxInfo.open_dt_min?default("")} &lt;/#if&gt; </td>
            </tr>
        </table>
    </section>
    <section>
        <h3>Proceeding method</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>Competition Type</th>
                <td>${rfxInfo.comp_typ_ccd_nm?default("")}</td>
                <th>Selection Type</th>
                <td>${rfxInfo.slctn_typ_ccd_nm?default("")}</td>
            </tr>
            <tr>
                <th>Item Selection Type</th>
                <td>${rfxInfo.item_slctn_typ_ccd_nm?default("")}</td>
                <th>CostStructure Use Y/N</th>
                <td>${rfxInfo.coststr_use_yn_str?default("")}</td>
            </tr>
            <tr>
                <th>Briefing Y/N</th>
                <td>${rfxInfo.bfg_yn_str?default("")}</td>
                <th>Presentation Y/N</th>
                <td>${rfxInfo.presn_yn_str?default("")}</td>
            </tr>
            <tr>
                <th>Partial Bidding Permission Y/N</th>
                <td>${rfxInfo.prtl_bid_perm_yn_str?default("")}</td>
                &lt;#if rfxInfo.rfx_typ_ccd == 'RFP'&gt;
                <th>Evaluation Ratio</th>
                <td>Price : ${rfxInfo.cbe_ro?default("")} % , Non-price : ${rfxInfo.npe_ro?default("")} % ( Vendor Public Y/N : ${rfxInfo.npe_ro_vd_pub_yn?default("")} )</td>
                &lt;#else&gt;
                <th></th>
                <td></td>
                &lt;/#if&gt;
            </tr>
        </table>
    </section>
    <section>
        <h3>Contract Condition</h3>
        <table>
            <colgroup>
                <col style="width:18%" />
                <col style="width:32%" />
                <col style="width:18%" />
                <col style="width:32%" />
            </colgroup>
            <tr>
                <th>Classification of internal/external materials</th>
                <td>${rfxInfo.domovrs_div_ccd_nm?default("")}</td>
                <th>CompensationOfDeferment Ratio</th>
                <td>${rfxInfo.dfrm_ro?default(0)} / 1000</td>
            </tr>
            <tr>
                <th>Currency</th>
                <td>${rfxInfo.cur_ccd?default("")}</td>
                <th>Contractual period</th>
                <td>&lt;#if rfxInfo.cntr_st_dt?? &gt; ${rfxInfo.cntr_st_dt[0..3]?default("")}/${rfxInfo.cntr_st_dt[4..5]?default("")}/${rfxInfo.cntr_st_dt[6..7]?default("")} &lt;/#if&gt; ~ &lt;#if rfxInfo.cntr_exp_dt?? &gt; ${rfxInfo.cntr_exp_dt[0..3]?default("")}/${rfxInfo.cntr_exp_dt[4..5]?default("")}/${rfxInfo.cntr_exp_dt[6..7]?default("")} &lt;/#if&gt;</td>
            </tr>
            <tr>
                <th>PaymentMethod/Explanation</th>
                <td colspan="3">${rfxInfo.pymtmeth_ccd_nm?default("")} ${rfxInfo.pymtmeth_expln?default("")}</td>
            </tr>
            <tr>
                <th>DeliveryMethod/Explanation</th>
                <td colspan="3">${rfxInfo.dlvymeth_ccd_nm?default("")} ${rfxInfo.dlvymeth_expln?default("")}</td>
            </tr>
            &lt;#if rfxInfo.rfx_buyer_rmk??&gt;
            <tr>
                <td class='staticcell5'>Buyer Remark</td>
                <td class='contentcell' colspan="2">${(rfxInfo.rfx_buyer_rmk)!?replace("\n", "<br>")}
                </td>
            </tr>
            &lt;/#if&gt;
        </table>
    </section>

    <!-- =================================구매유형이 물품일 경우 물품정보 테이블 시작 ================================= -->
    &lt;#if rfxInfo.purc_typ_ccd == 'QTY'&gt;
    <section>
        <h3>Item Information</h3>
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
                <th rowspan="3">Index</th>
                <th>Item Code</th>
                <th>Item Name</th>
                <th>Quantity</th>
                <th>Requested unit price</th>
                <th>Target UnitPrice</th>
            </tr>
            <tr>
                <th>LineNumber</th>
                <th>Item Specification</th>
                <th>UOM</th>
                <th>Request Amount</th>
                <th>Purchase Group</th>
            </tr>
            <tr>
                <th>Request Delivery Date</th>
                <th colspan="4">Delivery Place</th>
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
        <h3>Construction/service Information</h3>
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
                <th rowspan="3">Index</th>
                <th>Item Code</th>
                <th colspan="2">Item Name</th>
                <th>Quantity</th>
                <th>Requested unit price</th>
                <th>Target UnitPrice</th>
            </tr>
            <tr>
                <th>LineNumber</th>
                <th colspan="2">Item Specification</th>
                <th>UOM</th>
                <th>Request Amount</th>
                <th>Purchase Group</th>
            </tr>
            <tr>
                <th>Construction Start Date</th>
                <th>Construction Expiration Date</th>
                <th colspan="4">수행장소</th>
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
        <h3>Vendor Information</h3>
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
                <th>Vendor Code</th>
                <th>Vendor Name</th>
                <th>Vendor PersonInCharge Name</th>
                <th>Vendor PersonInCharge Telephone</th>
                <th>Vendor PersonInCharge Email</th>
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