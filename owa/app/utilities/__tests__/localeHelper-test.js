import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import LocaleHelper from 'utilities/localeHelper';

const localeHelper = new LocaleHelper();
require('babel-polyfill');
require('components/__mocks__/location-mock');
describe('LocaleHelper', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            sessionId: '0680FE18902BDA7BBCB4AF824747D6FB',
            authenticated: true,
            user: {
                uuid: 'c1c21e11-3f10-11e4-adec-0800271c1b75'
            },
            locale: 'en',
            allowedLocales: ['en', 'es', 'fr', 'it', 'pt_BR']
        };

        mock.onGet('https://192.168.33.10/openmrs/ws/rest/v1/session').reply(200, data);
    });

    it('Should return valid values', async () => {
        const data = await localeHelper.fetchLocales();
        expect(data.localeCode).toEqual('en');
        expect(data.allowedLocales).toEqual(['en', 'es', 'fr', 'it', 'pt_BR']);
    });

    it('Should return valid language by locale code', async () => {
        expect(localeHelper.getNativeNameByLocaleCode('pt')).toEqual('Português');
        expect(localeHelper.getNativeNameByLocaleCode('en')).toEqual('English');
        expect(localeHelper.getNativeNameByLocaleCode('it')).toEqual('Italiano');
    });
});
