(function () {
    /**
     * 워크플레이스 메뉴이동 기능을 제공하기 위한 Util 함수들의 모음. Behavior
     */
    Polymer.CCWorkplaceBehavior = {

        /**
         * 메뉴코드로 메뉴 정보를 return 한다.
         * @param menuCd - 이동할 메뉴 코드
         */
		getMenuInfo: function(menuCd) {
			var menuInfo = SCMenuManager.getMenuNode(menuCd);
			return menuInfo;
		},

        /**
         * 모듈을 MDI에서 initialized 하고 paramenter를 전달한다.
         * @param module - 메뉴 모듈
         * @return data - 모듈에 전달할 parameter
         */
		workplaceAppIdByDetailId: function(module,data){
			var me = this;
			var param = {app_id: data.task_uuid, task_uuid: data.task_uuid};
			
			if(data.menu_cd === "PRO10130") {	// 구매요청접수
				param.prcs_cd = "PR_ITEM";
			} else if(data.menu_cd === "INV14000") {	// 기성현황
				var datSrcLogicNm = UT.isString(data.dat_src_logic_nm) ? data.dat_src_logic_nm.toUpperCase() : null;
				if(datSrcLogicNm.indexOf("ASN") > -1) {			// 기성대기인 경우 AR
					param.prcs_cd = "ASN";
				} else if(datSrcLogicNm.indexOf("GR") >  -1) {	// 기성임시저장인 경우 GR
					param.prcs_cd = "GR";
				} else {
					param.prcs_cd = "PO";
				}
			}
			module.params = param;
			module.initialized();
		},

        /**
         * Flow Chart의 Flow Item들의 상태를 설정한다.
         * @param menuCd - 메뉴 코드
         * @param appData - 메뉴 모듈 실행 시 전달할 파라미터
         */
        createTaskModule: function (menuCd, appData) {
			var menuInfo = this.getMenuInfo(menuCd);
			var me = this;
			var data = { menu_cd : menuCd, task_uuid : appData.task_uuid,dat_src_logic_nm : appData.dat_src_logic_nm};
			MDIUT.createWindow(menuCd, menuInfo.menu_nm, menuInfo.menu_url, function(module) {
				me.workplaceAppIdByDetailId(module,data);
			});
        },
		
    };
})();