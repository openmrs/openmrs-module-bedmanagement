import React from 'react';
import {mount, shallow} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {IntlProvider} from 'react-intl';

import LocaleList from 'components/locale/localeList';
import messages from 'i18n/messages';
require('components/__mocks__/location-mock');

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        allowedLocales: ['en', 'es', 'fr', 'it'],
        localeCode: 'en'
    },
    context: {
        store: {
            setState: jest.fn()
        },
        intl: intl
    }
};

describe('LocaleList', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        mock.onPost('https://192.168.33.10/openmrs/ws/rest/v1/session').reply(200, {});
    });

    it('renders correctly', () => {
        const languageList = shallow(
            <LocaleList allowedLocales={testData.props.allowedLocales} localeCode={testData.props.localeCode} />,
            {context: testData.context}
        );

        expect(languageList.find('a').length).toBe(4);
        expect(shallowToJson(languageList)).toMatchSnapshot();
    });

    it('should change language', () => {
        const spyOnSetLanguage = jest.spyOn(LocaleList.prototype, 'setLanguage');
        const languageList = mount(
            <LocaleList allowedLocales={testData.props.allowedLocales} localeCode={testData.props.localeCode} />,
            {context: testData.context}
        );

        languageList
            .find('.locale')
            .at(1)
            .simulate('click');
        expect(spyOnSetLanguage).toHaveBeenCalledWith('es');
    });
});
