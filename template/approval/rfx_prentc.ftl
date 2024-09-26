<header>
    <h2>RFx공고품의서</h2>
    &lt;#if approvalInfo.apvl_docno?? &gt;
    <h4>( 품의번호 : ${approvalInfo.apvl_docno?default("")} )</h4>
    &lt;/#if&gt;
</header>
<article>
    <section>
        <table>
            <colgroup>
                <col style="width:18%"/>
                <col style="width:32%"/>
                <col style="width:18%"/>
                <col style="width:32%"/>
            </colgroup>
            <tr>
                <th>기안일자</th>
                <td>${approvalInfo.apvl_rptg_dttm?default("")}</td>
                <th>기안자</th>
                <td>[${bidNotiInfo.oper_org_cd_nm?default("")}] ${approvalInfo.reg_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>일반 정보</h3>
        <table>
            <colgroup>
                <col style="width:18%"/>
                <col style="width:32%"/>
                <col style="width:18%"/>
                <col style="width:32%"/>
            </colgroup>
            <tr>
                <th>운영조직</th>
                <td>${bidNotiInfo.oper_org_cd_nm?default("")}</td>
                <th>공고번호</th>
                <td>${bidNotiInfo.rfx_prentc_no?default("")}</td>
            </tr>
            <tr>
                <th>공고명</th>
                <td colspan="3">${bidNotiInfo.rfx_prentc_tit?default("")}</td>
            </tr>
            <tr>
                <th>경쟁방식</th>
                <td>${bidNotiInfo.comp_typ_ccd_nm?default("")}</td>
                <th>공고구분</th>
                <td>${bidNotiInfo.rfx_prentc_typ_ccd_nm?default("")}</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>공고 일정</h3>
        <table>
            <colgroup>
                <col style="width:18%"/>
            </colgroup>
            <tr>
                <th>공고시작일시</th>
                <td> &lt;#if bidNotiInfo.rfx_prentc_st_dttm?? &gt; ${bidNotiInfo.rfx_prentc_st_dttm_ymd[0..3]?default("")}/${bidNotiInfo.rfx_prentc_st_dttm_ymd[4..5]?default("")}/${bidNotiInfo.rfx_prentc_st_dttm_ymd[6..7]?default("")}   ${bidNotiInfo.rfx_prentc_st_dttm_hour?default(0)} : ${bidNotiInfo.rfx_prentc_st_dttm_min?default(0)} &lt;/#if&gt; ~ &lt;#if bidNotiInfo.rfx_prentc_clsg_dttm?? &gt; ${bidNotiInfo.rfx_prentc_clsg_dttm_ymd[0..3]?default("")}/${bidNotiInfo.rfx_prentc_clsg_dttm_ymd[4..5]?default("")}/${bidNotiInfo.rfx_prentc_clsg_dttm_ymd[6..7]?default("")}  ${bidNotiInfo.rfx_prentc_clsg_dttm_hour?default("")} : ${bidNotiInfo.rfx_prentc_clsg_dttm_min?default("")} &lt;/#if&gt;</td>
            </tr>
        </table>
    </section>
    <section>
        <h3>공고내용</h3>
        <table>
            <colgroup>
                <col style="width:18%"/>
            </colgroup>
            <tr>
                <th>사업내용</th>
                <td>${(bidNotiInfo.rfx_prentc_biz_expln)!?replace("\n", "<br>")}</td>
            </tr>
            <tr>
                <th>공고내용</th>
                <td>${(bidNotiInfo.rfx_prentc_expln)!?replace("\n", "<br>")}</td>
            </tr>
        </table>
    </section>
    &lt;#if bidNotiInfo.comp_typ_ccd != 'P'&gt;
    <section>
        <h3>협력사정보</h3>
        <table class="tc">
            <colgroup>
                <col style="width:7%"/>
                <col style="width:12%"/>
                <col style="width:44%"/>
                <col style="width:9%"/>
                <col style="width:12%"/>
                <col style="width:14%"/>
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
    &lt;/#if&gt;
</article>