/**
 * SCFavoriteManager
 */
(function(window) {

	function SCFavoriteManagerImpl(){}
	
	SCFavoriteManagerImpl.prototype = {
	
		url : 'findListMyFavoriteMenu.do'
		
	};
	
	SCFavoriteManagerImpl.prototype.service = function(callbackFn){
		if((this.favoritList && this.favoritList.length > 0)){
			callbackFn && callbackFn(this.favoritList);
			return true;
		}else{
			if(this.favoritList){
				callbackFn && callbackFn(this.favoritList);
				return true;
			}
			return this.ajaxService(this.url, {}, function onSuccess(response){
				this.favoritList = response;
				if(this.favoritList.length > 0){
					callbackFn && callbackFn(this.favoritList);
				}
			}.bind(this)).fail(function() {
				this.favoritList = [];
				console.error("즐겨찾기 데이터 조회중 오류가 발생하였습니다.");
			});
		}
	};
	
	/** Ajax Service */
	SCFavoriteManagerImpl.prototype.ajaxService = function(url, parameter, callbackFn){
		if(typeof parameter == "object"){
			parameter = JSON.stringify(parameter);
		}
		return $.ajax(url, $.extend(SCPreloader.ajaxSettings(), {
			data : parameter	
		})).then(function(response) {
			callbackFn && (callbackFn(response));
		});
	};
	
	SCFavoriteManagerImpl.prototype.getFavoritList = function() {
		return this.favoritList || [];
	};

	SCFavoriteManagerImpl.prototype.setFavoritList = function(favoritList) {
		return this.favoritList = favoritList || [];
	};

	SCFavoriteManagerImpl.prototype.hasMenuFavorite = function(menuCd){
		var list = this.getFavoritList();
		for(var i = 0,len = list.length ; i < len ; i++){
			if(list[i].menu_cd === menuCd){
				return true;
			}
		}
		return false;
	};
	
	/** SCPreloader 인스턴스 생성(on preloader-behavior.js) */
	window.SCFavoriteManager = new SCFavoriteManagerImpl();

}(window));
