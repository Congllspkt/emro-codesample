/**
 * SCLanguageManager
 */
SCLanguageManager = new (function() {
	
	function LanguageManagerImpl() {
	}
	
	LanguageManagerImpl.prototype.parse = function(value) {
		if(!value) {
			return ""
		}
		return SCLanguageManager.parseLocale(value, SCLocaleManager.getLocale());
	};
	
	LanguageManagerImpl.prototype.parseLocale = function(value, locale) {
		return SCLanguageManager.parseLocaleAndDefaultLocale(value, locale, 'ko_KR');
	};
	
	LanguageManagerImpl.prototype.parseLocaleAndDefaultLocale = function(value, locale, defaultLocale) {
		if(!SCLanguageManager.isValidJSON(value)) {
			return value;
		}
		var texts = JSON.parse(value) || {};
		var jsonValue = texts[locale];
		if(!jsonValue) {
			jsonValue = texts[defaultLocale];
		}
		return jsonValue || "";
	};
	
	LanguageManagerImpl.prototype.isValidJSON = function(str) {
		try {
			JSON.parse(str);
			return true;
		} catch(e) {
			return false;
		}
	};
	
	LanguageManagerImpl.prototype.combine = function(obj, displayField, combineField) {
		var displayData = obj[displayField];
		var combineData;
		if(SCLanguageManager.isValidJSON(obj[combineField])) {
			combineData = JSON.parse(obj[combineField]);
		} else {
			combineData = {};
		}
		
		combineData[SCLocaleManager.getLocale()] = displayData;
		obj[combineField] = JSON.stringify(combineData);
	};
	
	LanguageManagerImpl.prototype.combineWithGrid = function(provider, item, obj, displayField, combineField) {
		var displayData = obj[displayField];
		var combineData;
		if(SCLanguageManager.isValidJSON(obj[combineField])) {
			combineData = JSON.parse(obj[combineField]);
		} else {
			combineData = {};
		}
		
		combineData[SCLocaleManager.getLocale()] = displayData;
		var addData = {};
		addData[combineField] = JSON.stringify(combineData);
		provider.setItemAt(item.rowIndex, addData);
	};
	
	return LanguageManagerImpl;
}());