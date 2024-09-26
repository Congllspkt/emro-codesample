(function () {
    /**
     * Flow 차트를 제어하기 위한 Util 함수들의 모음. Behavior
     */
    Polymer.CCFlowBehavior = {

        /**
         * Flow Chart의 현재 상태를 설정한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param flowItem - 현재 상태를 지정할 Flow item HTML 엘리먼트
         */
        setCurrentState: function (flow, flowItem) {
            if(!flowItem) {
                return;
            }

            var prevItems = this.removeStates(flow, "now");
            this.setItemStateBatch(flow, prevItems, "prev");

            this.setItemStateBatch(flow, [flowItem], "now");

            var nextItems = this.getNexts(flow, flowItem);
            this.setItemStateBatch(flow, nextItems, "next");
        },

        /**
         * Flow Chart의 현재 상태를 반환한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @return {NodeList} 현재 진행 중인 HTML Element
         */
        getCurrentState: function (flow) {
            var states = this.getFlowItemsByState(flow, "now");
            return (states.length === 0) ? undefined: states[0];
        },

        /**
         * Flow Chart의 Flow Item들의 상태를 설정한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param flowItems - 현재 상태를 지정할 Flow item들의 배열
         * @param state - 설정할 상태
         */
        setItemStateBatch: function (flow, flowItems, state) {
            if(flowItems && flowItems.length) {
                for(var i=0;i<flowItems.length;i++) {
                    flowItems[i].setAttribute("data-state", state);
                }
            }
        },

        /**
         *
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param objs {Array}
         * @param idField - 객체에서 ID가 들어있는 Field
         * @param stateField - 객체에서 State가 들어있는 Field
         */
        setItemStateEach: function (flow, objs, idField, stateField) {
            idField = idField || "id";
            stateField = stateField || "state";

            if(UT.isArray(objs)) {
                for(var i=0;i<objs.length;i++) {
                    var obj = objs[i];
                    if(UT.isNotEmpty(obj[idField]) && UT.isNotEmpty(obj[stateField])) {
                        var ele = flow.querySelector("*[data-id=" + obj[idField] + "]");
                        if(ele) {
                            ele.setState(obj[stateField]);
                        }
                    }
                }
            }
        },

        /**
         *
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param objs {Array}
         * @param idField - 객체에서 ID가 들어있는 Field
         * @param stateFields - 객체에서 State가 들어있는 Fields
         */
        setItemStateEachColumn: function (flow, objs, idField, stateFields) {
            idField = idField || "id";
            stateFields = stateFields || [];

            if(UT.isArray(objs)) {
                for(var i=0;i<objs.length;i++) {
                    var obj = objs[i];
                    var ele = flow.querySelector("*[data-id=" + obj[idField] + "]");

                    if(!ele || UT.isEmpty(obj[idField])) {
                        continue;
                    }

                    for(var j=0;j<stateFields.length;j++) {
                        var stateField = stateFields[j];
                        if(UT.isNotEmpty(obj[stateField])) {
                            ele.setAttribute("data-" + stateField, obj[stateField]);

                            if(obj[stateField] === "Y") {
								ele.classList.add("assign");
                            }
                        }
                    }

                }
            }
        },

        /**
         * 특정 Flow Item 다음에 진행될 Flow Item들의 엘리먼트 리스트를 반환한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param flowItem - 해당 Flow Item의 다음 프로세스
         * @return {NodeList}
         */
        getNexts: function(flow, flowItem) {
            var nextIds = flowItem.getAttribute("data-next");
            var nextEles = [];

            if(nextIds) {
                var nextIdsArr = nextIds.split(",");
                var queryStr = "";
                for(var i=0;i<nextIdsArr.length;i++) {
                    if(i !== 0) {
                        queryStr += ",";
                    }
                    queryStr += "*[data-id="+nextIdsArr[i]+"]";
                }

                return flow.querySelectorAll(queryStr);
            } else {
                return undefined;
            }
        },

        /**
         * 지정한 상태를 모두 초기화한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @state flowItem - 초기화할 상태
         * @return {NodeList} - 초기화된 엘리먼트
         */
        removeStates: function (flow, state) {
            var stateEles = flow.querySelectorAll("*[data-state="+state+"]");
            for(var i=0;i<stateEles.length;i++) {
                stateEles[i].removeAttribute("data-state");
            }
            return stateEles;
        },

        /**
         * Flow Chart의 Click 이벤트 핸들러. Flow Item에 등록된 Click Event Handler를 호출하는 역할을 한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @state flowItem - 초기화할 상태
         * @return {NodeList} - 초기화된 엘리먼트
         */
        onClickFlow: function (e) {
            if(e.target && e.target.hasAttribute("data-click")) {
                var dataHost = e.target.dataHost;
                var methodName = e.target.getAttribute("data-click");
                this[methodName].call(this, Array.prototype.slice.call(arguments));
            }
        },

        /**
         * Flow Chart 상의 모든 Flow Item들의 엘리먼트 리스트를 반환한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @return {NodeList}
         */
        getFlowItems: function(flow) {
            return flow.querySelectorAll("*[data-id]");
        },

        /**
         * 특정 상태에 있는 엘리먼트 리스트를 반환한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param state - 불러올 상태
         * @return {NodeList}
         */
        getFlowItemsByState: function (flow, state) {
            return flow.querySelectorAll("*[data-state="+state+"]");
        },

        /**
         * Flow Chart 상의 특정 Process ID를 가지고 있는 엘리먼트를 반환한다.
         * @param flow - Flow Chart의 HTML 엘리먼트
         * @param id - Process ID
         * @return {Node}
         */
        getFlowItemById: function (flow, id) {
            return flow.querySelector("*[data-id="+id+"]");
        },
    };
})();