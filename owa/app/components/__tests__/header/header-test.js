import React from 'react';
import {StaticRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import {mount, shallow} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';

import Header from 'components/header';
import UrlHelper from 'utilities/urlHelper';
import messages from 'i18n/messages';

jest.mock('utilities/urlHelper', () => jest.fn());
UrlHelper.mockImplementation(() => ({
    owaPath: () => {
        return '/owa/bedmanagement';
    },
    originPath: () => {
        return '';
    }
}));

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    context: {
        router: {
            history: {
                action: 'PUSH',
                push: jest.fn(),
                replace: jest.fn(),
                go: jest.fn(),
                goBack: jest.fn(),
                goForward: jest.fn(),
                createHref: jest.fn(),
                block: jest.fn(),
                length: 1,
                location: {
                    hash: '',
                    key: 'd0o5f3',
                    pathname: '/owa/bedmanagement/admissionLocations.html',
                    search: ''
                }
            },
            route: {
                location: {
                    hash: '',
                    key: 'd0o5f3',
                    pathname: '/owa/bedmanagement/admissionLocations.html',
                    search: ''
                },
                match: {
                    isExact: true,
                    params: {},
                    path: '/owa/bedmanagement/admissionLocations.html',
                    url: '/owa/bedmanagement/admissionLocations.html'
                }
            }
        },
        intl: intl
    }
};

describe('Header', () => {
    it('renders correctly', () => {
        const header = mount(<Header path="/owa/bedmanagement/admissionLocations.html" />, {context: testData.context});

        expect(header.find('ul').find('li').length).toBe(4);
        expect(
            header
                .find('a.active')
                .text()
                .trim()
        ).toBe('Admission Locations');
        expect(shallowToJson(header)).toMatchSnapshot();
    });
});
