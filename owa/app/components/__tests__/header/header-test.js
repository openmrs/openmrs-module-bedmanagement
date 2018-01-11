import React from 'react';
import { StaticRouter } from 'react-router-dom';
import {mount} from 'enzyme';
import { shallowToJson } from 'enzyme-to-json';

import Header from 'components/header';
import UrlHelper from 'utilities/urlHelper';

jest.mock('utilities/urlHelper', () => jest.fn());
UrlHelper.mockImplementation(
    () => ({
        owaPath: () => {
            return '/owa/bedmanagement';
        },
        originPath: () => {
            return '';
        }
    })
);

describe('Header', () => {
    it('renders correctly', () => {
        const header = mount(
            <StaticRouter context={{}}>
                <Header path="/owa/bedmanagement/admissionLocations.html" />
            </StaticRouter>
        );

        expect(header.find('ul').find('li').length).toBe(4);
        expect(header.find('a.active').text().trim()).toBe('Admission Locations');
        expect(shallowToJson(header)).toMatchSnapshot();
    });
});