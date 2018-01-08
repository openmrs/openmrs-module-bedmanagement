import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('babel-polyfill');
const urlHelper = new UrlHelper();
const i18nIosCodes = require('i18n-iso-639-1');
export default class LocaleHelper {
    fetchLocales = async () => {
        const response = await axios.get(urlHelper.apiBaseUrl() + '/session');
        return {
            localeCode: response.data.locale ? this.getLocaleCode(response.data.locale) : 'en',
            allowedLocales: response.data.allowedLocales ? response.data.allowedLocales : ['en']
        };
    };

    getLocaleCode = (locale) => {
        return locale.toLowerCase().split(/[_-]+/)[0];
    };

    getNativeNameByLocaleCode = (locale) => {
        return i18nIosCodes.getNativeName(locale);
    };
}
